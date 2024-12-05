import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public RegisterServlet() {
        // Default constructor
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        JsonObject jsonResponse;

        try {
            Connection conn = DBConnection.getConnection();

            // Check if username already exists
            String checkUserSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(checkUserSql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // Username exists
                jsonResponse = Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Username already exists.")
                        .build();
                out.print(jsonResponse);
                rs.close();
                ps.close();
                conn.close();
                return;
            }
            rs.close();
            ps.close();

            // Insert new user
            String insertUserSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            ps = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, password);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userID = generatedKeys.getInt(1);
                    jsonResponse = Json.createObjectBuilder()
                            .add("status", "success")
                            .add("message", "Registration successful.")
                            .add("userID", userID)
                            .add("username", username)
                            .build();
                } else {
                    jsonResponse = Json.createObjectBuilder()
                            .add("status", "error")
                            .add("message", "Failed to retrieve user ID.")
                            .build();
                }
                generatedKeys.close();
            } else {
                jsonResponse = Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Registration failed.")
                        .build();
            }

            ps.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            jsonResponse = Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Database driver not found.")
                    .build();
            e.printStackTrace();
        } catch (SQLException e) {
            jsonResponse = Json.createObjectBuilder()
                    .add("status", "error")
                    .add("message", "Database error.")
                    .build();
            e.printStackTrace();
        }

        out.print(jsonResponse);
    }
}

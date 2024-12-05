import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        JsonObject jsonResponse = new JsonObject();

        try {
            Connection conn = DBConnection.getConnection();

            // Check if username already exists
            String checkUserSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(checkUserSql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Username exists
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Username already exists.");
                out.print(gson.toJson(jsonResponse));
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
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Registration successful.");
                    jsonResponse.addProperty("userID", userID);
                    jsonResponse.addProperty("username", username);
                } else {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Failed to retrieve user ID.");
                }
                generatedKeys.close();
            } else {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Registration failed.");
            }

            ps.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database error.");
            e.printStackTrace();
        }

        out.print(gson.toJson(jsonResponse));
    }
}

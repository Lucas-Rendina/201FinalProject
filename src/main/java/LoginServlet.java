import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class LoginServ
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public LoginServlet() {
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
            String checkUserSql = "SELECT user_id, password FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(checkUserSql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                int userID = rs.getInt("user_id");

                if (storedPassword.equals(password)) { // Success
                    jsonResponse = Json.createObjectBuilder()
                            .add("status", "success")
                            .add("userID", userID)
                            .add("username", username)
                            .build();
                } else { // Incorrect password
                    jsonResponse = Json.createObjectBuilder()
                            .add("status", "error")
                            .add("message", "Incorrect username or password.")
                            .build();
                }
            } else { // Incorrect username
                jsonResponse = Json.createObjectBuilder()
                        .add("status", "error")
                        .add("message", "Incorrect username or password.")
                        .build();
            }

            rs.close();
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

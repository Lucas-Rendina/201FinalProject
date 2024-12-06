import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
<<<<<<< HEAD

=======
    
>>>>>>> refs/remotes/origin/main
    public RegisterServlet() {
        super();
    }
<<<<<<< HEAD

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

=======
    
>>>>>>> refs/remotes/origin/main
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
<<<<<<< HEAD

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

=======
>>>>>>> refs/remotes/origin/main
        JsonObject jsonResponse = new JsonObject();
        
        // Get parameters from request
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Basic validation
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "All fields are required.");
            out.print(gson.toJson(jsonResponse));
            return;
        }
        
        // Password match validation
        if (!password.equals(confirmPassword)) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Passwords do not match.");
            out.print(gson.toJson(jsonResponse));
            return;
        }
        
        // Email format validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Invalid email format.");
            out.print(gson.toJson(jsonResponse));
            return;
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Check if username already exists
            String checkUserSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
<<<<<<< HEAD
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
            String insertUserSql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(insertUserSql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userID = generatedKeys.getInt(1);
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Registration successful.");
                    jsonResponse.addProperty("userID", userID);
                    jsonResponse.addProperty("username", username);
=======
                String existingUsername = rs.getString("username");
                String existingEmail = rs.getString("email");
                
                if (existingUsername.equals(username)) {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Username already exists.");
>>>>>>> refs/remotes/origin/main
                } else {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Email already registered.");
                }
                
                rs.close();
                checkStmt.close();
                conn.close();
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            rs.close();
            checkStmt.close();
            
            // Insert new user
            String insertSql = "INSERT INTO users (username, pw, fName, lName, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, "DefaultFirst"); // Add first name input to form or use default
            insertStmt.setString(4, "DefaultLast");  // Add last name input to form or use default
            insertStmt.setString(5, email);
            
            int rowsAffected = insertStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Registration successful!");
                    jsonResponse.addProperty("userId", userId);
                    jsonResponse.addProperty("username", username);
                    
                    // Set session attributes
                    request.getSession().setAttribute("userId", userId);
                    request.getSession().setAttribute("username", username);
                }
                generatedKeys.close();
            } else {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Registration failed. Please try again.");
            }
            
            insertStmt.close();
            conn.close();
            
        } catch (ClassNotFoundException e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "An unexpected error occurred.");
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("register.html");
    }
}






<<<<<<< Updated upstream
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

=======
>>>>>>> Stashed changes
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
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    public LoginServlet() {
        super();
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
        
        // Get parameters from request
        String username = request.getParameter("username");
        String password = request.getParameter("password");
<<<<<<< Updated upstream

        try {
            Connection conn = DBConnection.getConnection();
            String checkUserSql = "SELECT user_id, password FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(checkUserSql);
=======
        
        // Basic validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Username and password are required.");
            out.print(gson.toJson(jsonResponse));
            return;	
        }
        
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("Database connection successful");
            
            // Check user credentials
            String loginSql = "SELECT userID, username FROM users WHERE username = ? AND pw = ?";
            PreparedStatement ps = conn.prepareStatement(loginSql);
>>>>>>> Stashed changes
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
<<<<<<< Updated upstream
                String storedPassword = rs.getString("password");
                int userID = rs.getInt("user_id");
                
                if (storedPassword.equals(password)) { 
                    // Login success
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Login successful.");
                    jsonResponse.addProperty("userID", userID);
                    jsonResponse.addProperty("username", username);
                } else {
                    // Incorrect password
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Incorrect username or password.");
                }
            } else {
                // Username not found
=======
                // Login successful
                int userID = rs.getInt("userID");
                String fetchedUsername = rs.getString("username");
                
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("userID", userID);
                session.setAttribute("username", fetchedUsername);
                
                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Login successful!");
                jsonResponse.addProperty("userID", userID);
                jsonResponse.addProperty("username", fetchedUsername);
                jsonResponse.addProperty("redirect", "marketplace.html");
            } else {
                // Login failed
>>>>>>> Stashed changes
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Invalid username or password.");
            }
            
            rs.close();
            ps.close();
            conn.close();
<<<<<<< Updated upstream
        } catch (ClassNotFoundException e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
=======
            
        } catch (ClassNotFoundException e) {
>>>>>>> Stashed changes
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
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userID") != null) {
            response.sendRedirect("marketplace.html");
        } else {
            response.sendRedirect("login.html");
        }
    }
}
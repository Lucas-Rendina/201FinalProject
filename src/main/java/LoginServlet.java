import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        // Default constructor
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        JsonObject jsonResponse = new JsonObject();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
    	System.out.println(username+" "+password);
        try {
            Connection conn = DBConnection.getConnection();
            String checkUserSql = "SELECT userID, pw FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(checkUserSql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("pw");
                int userID = rs.getInt("userID");
                
                if (storedPassword.equals(password)) { 
                    // Login success
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Login successful.");
                    jsonResponse.addProperty("userID", userID);
                    jsonResponse.addProperty("username", username);
                    HttpSession session = request.getSession();
    				session.setAttribute("username", username);
    				
                } else {
                    // Incorrect password
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Incorrect username or password.");
                    System.out.println(password+"!="+storedPassword);
                }
            } else {
                // Username not found
            	System.out.println("uname nf"+username);
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Incorrect username or password.");
            }

            rs.close();
            ps.close();
            conn.close();
        }  catch (SQLException e) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database error.");
            e.printStackTrace();
        }

        out.print(gson.toJson(jsonResponse));
    }
}

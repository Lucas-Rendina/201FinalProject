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
import com.google.gson.JsonArray;

@WebServlet("/AccountServlet")
public class AccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        
        if ("checkLogin".equals(action)) {
            boolean isLoggedIn = (session != null && session.getAttribute("userID") != null);
            jsonResponse.addProperty("loggedIn", isLoggedIn);
            System.out.println("Login status check: " + isLoggedIn);
            System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
            System.out.println("UserID: " + (session != null ? session.getAttribute("userID") : "null"));
        }
        else if ("getUserInfo".equals(action)) {
            if (session != null && session.getAttribute("userID") != null) {
                try {
                    Connection conn = DBConnection.getConnection();
                    String sql = "SELECT username, email FROM users WHERE userID = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, (Integer) session.getAttribute("userID"));
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        jsonResponse.addProperty("username", rs.getString("username"));
                        jsonResponse.addProperty("email", rs.getString("email"));
                    }
                    
                    rs.close();
                    ps.close();
                    conn.close();
                } catch (Exception e) {
                    jsonResponse.addProperty("error", "Database error: " + e.getMessage());
                }
            }
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();
        
        String action = request.getParameter("action");
        
        if ("signout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            jsonResponse.addProperty("status", "success");
        }
        
        out.print(new Gson().toJson(jsonResponse));
    }
}
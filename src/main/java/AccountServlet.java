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
        else if ("getEnrolledCourses".equals(action)) {
            if (session != null && session.getAttribute("userID") != null) {
                try {
                    Connection conn = DBConnection.getConnection();
                    String sql = "SELECT s.courseCode, s.professor, s.stime, s.contact " +
                                 "FROM relation r " +
                                 "JOIN schedule s ON r.schedID = s.schedID " +
                                 "WHERE r.userID = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, (Integer) session.getAttribute("userID"));
                    ResultSet rs = ps.executeQuery();
                    
                    JsonArray coursesArray = new JsonArray();
                    while (rs.next()) {
                        JsonObject course = new JsonObject();
                        course.addProperty("courseCode", rs.getString("courseCode"));
                        course.addProperty("professor", rs.getString("professor"));
                        course.addProperty("stime", rs.getString("stime"));
                        course.addProperty("contact", rs.getString("contact"));
                        coursesArray.add(course);
                    }
                    
                    jsonResponse.add("courses", coursesArray);
                    
                    rs.close();
                    ps.close();
                    conn.close();
                } catch (Exception e) {
                    jsonResponse.addProperty("error", "Database error: " + e.getMessage());
                    e.printStackTrace(); // Add this for debugging
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
        } else if("delete".equals(action)) {
        //	System.out.println("Here");
        	HttpSession session = request.getSession(false);
        	
        	if(session != null && session.getAttribute("userID") != null) {
        		int userID = (Integer) session.getAttribute("userID");
        		
        		try {
        			Connection conn = DBConnection.getConnection();
        			
        			//System.out.println("Happened");
        			
        			String command = "DELETE FROM users WHERE userID = ?";
        			PreparedStatement deleteQuery = conn.prepareStatement(command);
        			
        			deleteQuery.setInt(1, userID);
        			
        			int userFound = deleteQuery.executeUpdate();
        			deleteQuery.close();
        			
        			System.out.println("userFound: " + userFound);
        			
        			if(userFound > 0) {
        				conn.commit();
        				//System.out.println("CANADA");
        				jsonResponse.addProperty("status", "success");
        				jsonResponse.addProperty("message", "Account has been deleted");
        				
        				session.invalidate();
        			} else {
        				System.out.println("ERROR");
        				conn.rollback();
        				jsonResponse.addProperty("status", "error");
        				jsonResponse.addProperty("message", "Failed to delete account");
        			}
        		}
        		
        		catch(SQLException e) {
        			jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Database error: " + e.getMessage());
                    e.printStackTrace();
        		}
        		
        		catch(ClassNotFoundException e) {
        			jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Class not found: " + e.getMessage());
                    e.printStackTrace();
        		}
        	}
        	
        } else if ("removeCourse".equals(action)) {
            HttpSession session = request.getSession(false);
            int userID = (Integer) session.getAttribute("userID");
            String courseCode = request.getParameter("courseCode");
            
            try {
                Connection conn = DBConnection.getConnection();
                String querySql = "SELECT schedID FROM schedule WHERE courseCode = ?";
                PreparedStatement queryPs = conn.prepareStatement(querySql);
                queryPs.setString(1, courseCode);
                ResultSet rs = queryPs.executeQuery();

                if (rs.next()) {
                    int schedID = rs.getInt("schedID");

                    // Remove the course from the relation table
                    String deleteSql = "DELETE FROM relation WHERE userID = ? AND schedID = ?";
                    PreparedStatement deletePs = conn.prepareStatement(deleteSql);
                    deletePs.setInt(1, userID);
                    deletePs.setInt(2, schedID);

                    int rowsAffected = deletePs.executeUpdate();

                    if (rowsAffected > 0) {
                        jsonResponse.addProperty("status", "success");
                        jsonResponse.addProperty("message", "Course removed successfully.");
                    } else {
                        jsonResponse.addProperty("status", "error");
                        jsonResponse.addProperty("message", "Course not found or not enrolled.");
                    }

                    deletePs.close();
                } else {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Course not found.");
                }

                rs.close();
                queryPs.close();
                conn.close();
            } catch(SQLException e) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Database error: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
        }
        
		out.print(new Gson().toJson(jsonResponse));
	}
}
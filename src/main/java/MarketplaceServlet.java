import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/MarketplaceServlet")
public class MarketplaceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<String> COURSE_CODES = Arrays.asList(
        "CSCI101", "CSCI201", "CSCI270", "MATH225", "PHYS151", 
        "CHEM105", "BISC120", "WRIT150", "PSYC100", "ECON203"
    );
    private static final List<String> PROFESSORS = Arrays.asList(
        "Dr. Smith", "Dr. Johnson", "Dr. Williams", "Dr. Brown",
        "Dr. Jones", "Dr. Garcia", "Dr. Miller", "Dr. Davis",
        "Dr. Rodriguez", "Dr. Martinez"
    );
    private static final List<String> TIME_SLOTS = Arrays.asList(
        "MWF 9:00-10:20", "MWF 10:30-11:50", "TTH 9:30-10:50",
        "TTH 11:00-12:20", "MWF 13:00-14:20", "TTH 14:00-15:20",
        "MWF 15:00-16:20", "TTH 15:30-16:50", "MW 17:00-18:20",
        "TTH 17:00-18:20"
    );
    
    public MarketplaceServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        List<Course> courses = new ArrayList<>();
        
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT DISTINCT courseCode, professor, stime, contact FROM schedule";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Course course = new Course();
                course.setCourseCode(rs.getString("courseCode"));
                course.setProfessor(rs.getString("professor"));
                course.setStime(rs.getString("stime"));
                course.setContact(rs.getString("contact"));
                courses.add(course);
            }
            
            rs.close();
            ps.close();
            conn.close();
            
        } catch (Exception e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Database error: " + e.getMessage());
            out.print(gson.toJson(jsonResponse));
            e.printStackTrace();
            return;
        }
        
        out.print(gson.toJson(courses));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();

        HttpSession session = request.getSession();
        Integer userID = (Integer) session.getAttribute("userID");
        
        if (userID == null) {
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "User not logged in");
            out.print(gson.toJson(jsonResponse));
            return;
        }

        String action = request.getParameter("action");
        if ("add".equals(action)) {
            String courseCode = request.getParameter("courseCode");
            try {
                Connection conn = DBConnection.getConnection();
                
                // Insert into schedule table
                String insertSql = "INSERT INTO schedule (userID, courseCode, professor, stime, contact) " +
                                 "SELECT ?, courseCode, professor, stime, contact " +
                                 "FROM schedule " +
                                 "WHERE courseCode = ? AND NOT EXISTS (SELECT 1 FROM schedule WHERE userID = ? AND courseCode = ?)";
                
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setInt(1, userID);
                ps.setString(2, courseCode);
                ps.setInt(3, userID);
                ps.setString(4, courseCode);
                
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Course added successfully");
                } else {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Course already exists in schedule or does not exist");
                }
                
                ps.close();
                conn.close();
                
            } catch (Exception e) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Database error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    class Course {
        private String courseCode;
        private String professor;
        private String stime;
        private String contact;
        
        // Getters and setters
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getProfessor() { return professor; }
        public void setProfessor(String professor) { this.professor = professor; }
        public String getStime() { return stime; }
        public void setStime(String stime) { this.stime = stime; }
        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
    }
}
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
    
    private List<Course> generateCourses() {
        List<Course> courses = new ArrayList<>();
        Random random = new Random();
        
        for (String courseCode : COURSE_CODES) {
            Course course = new Course();
            course.setCourseCode(courseCode);
            course.setProfessor(getRandomElement(PROFESSORS));
            course.setStime(getRandomElement(TIME_SLOTS));
            course.setContact("prof@university.edu");
            courses.add(course);
        }
        
        return courses;
    }
    
    private <T> T getRandomElement(List<T> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        
        List<Course> courses = generateCourses();
        
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
        if ("addCourse".equals(action)) {
            // Retrieve form data
            String courseName = request.getParameter("courseName");
            String professor = request.getParameter("professor");
            String[] days = request.getParameterValues("days");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            boolean tradeable = Boolean.parseBoolean(request.getParameter("tradeable"));
            String price = request.getParameter("price");

            try {
                Connection conn = DBConnection.getConnection();
                String querySql = "SELECT email FROM users WHERE userID = ?";
                PreparedStatement queryPs = conn.prepareStatement(querySql);
                queryPs.setInt(1, userID);
                ResultSet rs = queryPs.executeQuery();
    
                String contact = null;
                if (rs.next()) {
                    contact = rs.getString("email");
                } else {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "User email not found.");
                    out.print(gson.toJson(jsonResponse));
                    rs.close();
                    queryPs.close();
                    conn.close();
                    return;
                }
                rs.close();
                queryPs.close();
                String dayc="";
                for(int i=0;i<days.length;i++) {
                	if(days[i].equalsIgnoreCase("Monday")) {
                		dayc+="M";
                	}
                	if(days[i].equalsIgnoreCase("Tuesday")) {
                		dayc+="T";
                	}
                	if(days[i].equalsIgnoreCase("Wednesday")) {
                		dayc+="W";
                	}
                	if(days[i].equalsIgnoreCase("Thursday")) {
                		dayc+="Th";
                	}
                	if(days[i].equalsIgnoreCase("Friday")) {
                		dayc+="F";
                	}
                }
                
                
                // Insert course into the schedule table
                String insertSql = "INSERT INTO schedule (userID, courseCode, professor, stime, contact) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(insertSql);
                ps.setInt(1, userID);
                ps.setString(2, courseName);
                ps.setString(3, professor);
                ps.setString(4, dayc+" "+startTime+"-"+endTime); 
                System.out.println(contact);
                ps.setString(5, contact);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    jsonResponse.addProperty("status", "success");
                    jsonResponse.addProperty("message", "Course added to schedule successfully.");
                } else {
                    jsonResponse.addProperty("status", "error");
                    jsonResponse.addProperty("message", "Failed to add course to schedule.");
                }

                ps.close();
                conn.close();
            } catch (SQLException e) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Database error: " + e.getMessage());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/ScheduleServlet")
public class ScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        JsonObject jsonResponse = new JsonObject();

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

            // Insert course into the schedule table
            String insertSql = "INSERT INTO schedule (courseName, professor, days, startTime, endTime, tradeable, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertSql);
            ps.setString(1, courseName);
            ps.setString(2, professor);
            ps.setString(3, String.join(",", days)); // Store days as a comma-separated string
            ps.setString(4, startTime);
            ps.setString(5, endTime);
            ps.setBoolean(6, tradeable);
            ps.setString(7, price);

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
        }

        out.print(gson.toJson(jsonResponse));
    }
} 
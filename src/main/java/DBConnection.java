import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
   
	
	//db.properties wasn't working for me, we can just each add our own shit here
	private static String URL= "jdbc:mysql://localhost:3306/coursemarket";
	private static String USER = "root";
	private static String PASSWORD= "root";

//	static {
//		// Load properties when the class is first loaded
//		Properties props = new Properties();
//		try (InputStream input = new FileInputStream("db.properties")) {
//			props.load(input);
//			URL = props.getProperty("db.url");
//			USER = props.getProperty("db.user");
//			PASSWORD = props.getProperty("db.password");
//		} catch (IOException e) {
//			e.printStackTrace();
//			// Handle the error: for example, rethrow as RuntimeException or handle gracefully
//		}
//	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}

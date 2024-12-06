import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
<<<<<<< HEAD
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        // Load properties when the class is first loaded
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("db.properties")) {
            props.load(input);
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error: for example, rethrow as RuntimeException or handle gracefully
        }
    }
=======
	
	//db.properties wasn't working for me, we can just each add our own shit here
    private static String URL= "jdbc:mysql://localhost:3306/COURSEMARKET";
    private static String USER = "root";
    private static String PASSWORD= "";

//    static {
//        Properties props = new Properties();
//        try {
//            // Use class loader to find the properties file in the classpath
//            InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
//            if (input == null) {
//                // Fallback to WEB-INF path
//                input = DBConnection.class.getClassLoader().getResourceAsStream("../webapp/WEB-INF/db.properties");
//            }
//            props.load(input);
//            URL = props.getProperty("db.url");
//            USER = props.getProperty("db.user");
//            PASSWORD = props.getProperty("db.password");
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Set default values as fallback
//            URL = "jdbc:mysql://localhost:3306/COURSEMARKET";
//            USER = "root";
//            PASSWORD = "root";
//        }
//    }
>>>>>>> refs/remotes/origin/main

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

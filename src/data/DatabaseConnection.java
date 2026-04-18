package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	private static final String SERVER = "localhost";
	private static final int PORT = 3306;
	private static final String DATABASE = "library_db";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";
	
	public static Connection getConnection() {
		try {
			String url = "jdbc:mariadb://" + SERVER + ":" + PORT + "/" + DATABASE;
			return DriverManager.getConnection(url, USERNAME, PASSWORD);
			
		} catch (SQLException e) {
			System.out.println("Database connection failed: " + e.getMessage());
			return null;
		}
	}
	
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Failed to close connection: " + e.getMessage());
			}
		}
	}

}

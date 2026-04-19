package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

<<<<<<< HEAD
=======
/*
 Description:
 Centralized utility class responsible for creating and closing
 JDBC Connection objects to the MariaDB library_db database. All
 Manager classes obtain their connections through this class so
 that credentials and JDBC URL construction are defined in exactly  one place.

Inputs: none at call time; connection constants are defined below
and can be edited to match each developer's local setup.
Processing: loads the MariaDB JDBC driver, builds the JDBC URL,
and returns a live Connection. Wraps SQLExceptions into
RuntimeExceptions so callers do not need to handle low-
level driver failures at every call site.
Outputs: an open java.sql.Connection ready for use.
 
NOTE: Before running the application, update USERNAME and PASSWORD
below to match your local MariaDB installation, and make sure
the mariadb-java-client JAR is on the classpath.
 */
>>>>>>> project-branch-1
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

    // ---- Connection constants ----
    private static final String SERVER   = "localhost";
    private static final int    PORT     = 3306;
    private static final String DATABASE = "library_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";          // <-- update for setup

    private static final String URL =
            "jdbc:mariadb://" + SERVER + ":" + PORT + "/" + DATABASE;

    /* Prevent instantiation - this class only exposes static helpers. */
    private DatabaseConnection() {}

    /*Open and return a new database connection. Callers are responsible
     for closing the connection (or use closeConnection() below).*/
    public static Connection getConnection() {
        try {
            // Explicitly load the driver
            // JDBC but makes classpath issues easier to spot.
            Class.forName("org.mariadb.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MariaDB JDBC driver not found on classpath. "
                  + "Make sure mariadb-java-client-*.jar is in the lib/ folder "
                  + "and added to the project's build path.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to connect to database at " + URL
                  + " - check server is running and credentials are correct.", e);
        }
    }

    /* Safely close a connection, ignoring nulls and swallowing close errors. */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Warning: failed to close DB connection: " + e.getMessage());
            }
        }
    }
}

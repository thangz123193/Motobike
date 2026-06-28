package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBContext - opens a JDBC connection to SQL Server.
 *
 * NOTE for NetBeans setup:
 *  - Copy mssql-jdbc-x.x.x.jre11.jar (or jre17) into the project's lib/ folder
 *  - Add it to Project Properties -> Libraries -> Compile, and also mark it
 *    "Package" so it gets copied into WEB-INF/lib on deploy.
 *  - Make sure SQL Server is running with TCP/IP enabled on port 1433
 *    (SQL Server Configuration Manager -> Protocols -> TCP/IP -> Enabled)
 *    and SQL Server Authentication mode is enabled (not Windows-only).
 */
public class DBContext {

    // ==== Change these 4 values to match your own SQL Server instance ====
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=MotorbikeRepairShopDB;"
            + "encrypt=true;trustServerCertificate=true;";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "sa";
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public Connection connection;

    public DBContext() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Always close the connection when a DAO method is done with it.
     * Call this in a finally block (DAOs in this project do that for you).
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Quick manual test: run this main method directly in NetBeans
     * to confirm your DB connection string/credentials are correct
     * before building the rest of the app.
     */
    public static void main(String[] args) {
        DBContext db = new DBContext();
        if (db.getConnection() != null) {
            System.out.println("Connected to SQL Server successfully!");
        } else {
            System.out.println("Connection FAILED. Check URL/username/password and that SQL Server is running.");
        }
        db.closeConnection();
    }
}

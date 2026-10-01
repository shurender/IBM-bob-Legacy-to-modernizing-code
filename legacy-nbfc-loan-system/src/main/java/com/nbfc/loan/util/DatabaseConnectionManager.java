package com.nbfc.loan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connections to the SQLite database.
 * This is a simple connection utility - not a full connection pool.
 * Typical for legacy enterprise applications of the 2008-2012 era.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class DatabaseConnectionManager {

    private static final String DRIVER_CLASS = "org.sqlite.JDBC";
    private static final String DB_URL_PREFIX = "jdbc:sqlite:";

    private static String databasePath = null;

    static {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC Driver not found. " +
                    "Please ensure sqlite-jdbc is in the classpath.", e);
        }
    }

    /**
     * Private constructor - utility class should not be instantiated.
     */
    private DatabaseConnectionManager() {
    }

    /**
     * Sets the database file path. Should be called during application startup.
     *
     * @param path the file system path to the SQLite database file
     */
    public static synchronized void setDatabasePath(String path) {
        databasePath = path;
    }

    /**
     * Returns the configured database path.
     *
     * @return database file path
     */
    public static String getDatabasePath() {
        return databasePath;
    }

    /**
     * Opens and returns a new JDBC connection to the SQLite database.
     * Caller is responsible for closing the connection.
     *
     * @return a new JDBC Connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        if (databasePath == null || databasePath.trim().isEmpty()) {
            throw new SQLException("Database path has not been configured. " +
                    "Call setDatabasePath() before requesting a connection.");
        }
        String url = DB_URL_PREFIX + databasePath;
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(true);
        return conn;
    }

    /**
     * Quietly closes a connection, ignoring any exceptions.
     * Useful in finally blocks.
     *
     * @param conn the connection to close (may be null)
     */
    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Intentionally swallowed - legacy pattern
                System.err.println("[WARN] Failed to close database connection: " + e.getMessage());
            }
        }
    }
}

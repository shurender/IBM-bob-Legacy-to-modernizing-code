package com.nbfc.loan.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Application lifecycle listener that initializes the SQLite database
 * on application startup.
 *
 * Registered in web.xml as a context listener.
 * Creates the database schema if it does not already exist.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.log("[NBFC] Application starting up. Initializing database...");

        try {
            // Determine database file location (in app home directory or temp dir)
            String appHome = System.getProperty("user.home");
            String dbPath = appHome + File.separator + "nbfc_loan.db";

            context.log("[NBFC] Database path: " + dbPath);

            // Configure the connection manager
            DatabaseConnectionManager.setDatabasePath(dbPath);

            // Initialize schema
            initializeSchema(context);

            context.log("[NBFC] Database initialization complete.");

        } catch (Exception e) {
            context.log("[NBFC] FATAL: Database initialization failed: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize NBFC database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("[NBFC] Application shutting down.");
    }

    /**
     * Creates database tables if they do not exist.
     */
    private void initializeSchema(ServletContext context) throws SQLException {
        Connection conn = null;
        Statement stmt  = null;

        try {
            conn = DatabaseConnectionManager.getConnection();
            stmt = conn.createStatement();

            // Create loan_applications table
            String createLoanApplications =
                "CREATE TABLE IF NOT EXISTS loan_applications (" +
                "    id                INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "    application_ref   TEXT NOT NULL, " +
                "    customer_name     TEXT NOT NULL, " +
                "    age               INTEGER NOT NULL, " +
                "    monthly_income    REAL NOT NULL, " +
                "    employment_type   TEXT NOT NULL, " +
                "    credit_score      INTEGER NOT NULL, " +
                "    existing_emi      REAL NOT NULL DEFAULT 0, " +
                "    loan_amount       REAL NOT NULL, " +
                "    loan_tenure       INTEGER NOT NULL, " +
                "    decision          TEXT NOT NULL, " +
                "    decision_code     TEXT NOT NULL, " +
                "    decision_reason   TEXT, " +
                "    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP " +
                ")";

            stmt.execute(createLoanApplications);
            context.log("[NBFC] Table 'loan_applications' ready.");

            // Create eligibility_rules table (reference data)
            String createEligibilityRules =
                "CREATE TABLE IF NOT EXISTS eligibility_rules (" +
                "    id                INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "    rule_name         TEXT NOT NULL, " +
                "    rule_description  TEXT, " +
                "    min_value         REAL, " +
                "    max_value         REAL, " +
                "    is_active         INTEGER DEFAULT 1, " +
                "    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP " +
                ")";

            stmt.execute(createEligibilityRules);
            context.log("[NBFC] Table 'eligibility_rules' ready.");

            // Seed eligibility rules if not already seeded
            seedEligibilityRules(stmt, context);

        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { /* ignored */ }
            }
            DatabaseConnectionManager.closeQuietly(conn);
        }
    }

    /**
     * Inserts default eligibility rule reference data if the table is empty.
     */
    private void seedEligibilityRules(Statement stmt, ServletContext context) throws SQLException {
        // Check if rules already exist
        java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM eligibility_rules");
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        rs.close();

        if (count == 0) {
            context.log("[NBFC] Seeding eligibility rules...");

            String[] rules = {
                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('MIN_AGE', 'Minimum applicant age in years', 21, NULL)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('MAX_AGE', 'Maximum applicant age in years', NULL, 60)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('MIN_CREDIT_SCORE', 'Minimum credit score required for auto-approval', 700, NULL)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('BORDERLINE_CREDIT_SCORE', 'Credit score range requiring manual review', 650, 699)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('REJECT_CREDIT_SCORE', 'Credit score below which application is auto-rejected', NULL, 649)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('MIN_MONTHLY_INCOME', 'Minimum monthly income in INR', 25000, NULL)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('MAX_DEBT_TO_INCOME_RATIO', 'Maximum allowed debt-to-income ratio (as decimal)', NULL, 0.50)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('BORDERLINE_DEBT_TO_INCOME', 'Debt-to-income ratio flagging for manual review', 0.40, 0.50)",

                "INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES " +
                "('MAX_LOAN_TO_INCOME_RATIO', 'Maximum loan amount as multiple of annual income', NULL, 10)",
            };

            for (String rule : rules) {
                stmt.execute(rule);
            }

            context.log("[NBFC] Eligibility rules seeded successfully.");
        }
    }
}

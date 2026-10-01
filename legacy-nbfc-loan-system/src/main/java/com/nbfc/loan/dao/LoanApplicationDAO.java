package com.nbfc.loan.dao;

import com.nbfc.loan.model.LoanApplication;
import com.nbfc.loan.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the loan_applications table.
 * Handles all CRUD operations using plain JDBC.
 *
 * This class follows the traditional DAO pattern common in enterprise
 * Java applications of the 2008-2012 era.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class LoanApplicationDAO {

    private static final String INSERT_SQL =
        "INSERT INTO loan_applications " +
        "(application_ref, customer_name, age, monthly_income, employment_type, " +
        " credit_score, existing_emi, loan_amount, loan_tenure, " +
        " decision, decision_code, decision_reason) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_SQL =
        "SELECT id, application_ref, customer_name, age, monthly_income, employment_type, " +
        "       credit_score, existing_emi, loan_amount, loan_tenure, " +
        "       decision, decision_code, decision_reason, created_at " +
        "FROM loan_applications WHERE id = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT id, application_ref, customer_name, age, monthly_income, employment_type, " +
        "       credit_score, existing_emi, loan_amount, loan_tenure, " +
        "       decision, decision_code, decision_reason, created_at " +
        "FROM loan_applications ORDER BY created_at DESC";

    private static final String SELECT_RECENT_SQL =
        "SELECT id, application_ref, customer_name, age, monthly_income, employment_type, " +
        "       credit_score, existing_emi, loan_amount, loan_tenure, " +
        "       decision, decision_code, decision_reason, created_at " +
        "FROM loan_applications ORDER BY created_at DESC LIMIT ?";

    private static final String COUNT_SQL =
        "SELECT COUNT(*) FROM loan_applications";

    /**
     * Saves a new loan application to the database.
     *
     * @param application the application to save
     * @return the generated database ID
     * @throws SQLException if the insert fails
     */
    public Long save(LoanApplication application) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn  = DatabaseConnectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1,  application.getApplicationReference());
            pstmt.setString(2,  application.getCustomerName());
            pstmt.setInt(3,     application.getAge());
            pstmt.setDouble(4,  application.getMonthlyIncome());
            pstmt.setString(5,  application.getEmploymentType());
            pstmt.setInt(6,     application.getCreditScore());
            pstmt.setDouble(7,  application.getExistingEmi());
            pstmt.setDouble(8,  application.getLoanAmount());
            pstmt.setInt(9,     application.getLoanTenure());
            pstmt.setString(10, application.getDecision());
            pstmt.setString(11, application.getDecisionCode());
            pstmt.setString(12, application.getDecisionReason());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting loan application failed, no rows affected.");
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Inserting loan application failed, no ID obtained.");
            }

        } finally {
            closeQuietly(generatedKeys);
            closeQuietly(pstmt);
            DatabaseConnectionManager.closeQuietly(conn);
        }
    }

    /**
     * Finds a loan application by its primary key.
     *
     * @param id the database ID
     * @return the LoanApplication or null if not found
     * @throws SQLException if the query fails
     */
    public LoanApplication findById(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn  = DatabaseConnectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }
            return null;

        } finally {
            closeQuietly(rs);
            closeQuietly(pstmt);
            DatabaseConnectionManager.closeQuietly(conn);
        }
    }

    /**
     * Returns all loan applications ordered by creation date descending.
     *
     * @return list of all applications
     * @throws SQLException if the query fails
     */
    public List<LoanApplication> findAll() throws SQLException {
        Connection conn = null;
        Statement stmt  = null;
        ResultSet rs    = null;

        List<LoanApplication> list = new ArrayList<LoanApplication>();

        try {
            conn = DatabaseConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs   = stmt.executeQuery(SELECT_ALL_SQL);

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            DatabaseConnectionManager.closeQuietly(conn);
        }
    }

    /**
     * Returns the N most recent loan applications.
     *
     * @param limit maximum number of records to return
     * @return list of recent applications
     * @throws SQLException if the query fails
     */
    public List<LoanApplication> findRecent(int limit) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs    = null;

        List<LoanApplication> list = new ArrayList<LoanApplication>();

        try {
            conn  = DatabaseConnectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_RECENT_SQL);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } finally {
            closeQuietly(rs);
            closeQuietly(pstmt);
            DatabaseConnectionManager.closeQuietly(conn);
        }
    }

    /**
     * Returns the total count of loan applications in the database.
     *
     * @return total application count
     * @throws SQLException if the query fails
     */
    public int countAll() throws SQLException {
        Connection conn = null;
        Statement stmt  = null;
        ResultSet rs    = null;

        try {
            conn = DatabaseConnectionManager.getConnection();
            stmt = conn.createStatement();
            rs   = stmt.executeQuery(COUNT_SQL);

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } finally {
            closeQuietly(rs);
            closeQuietly(stmt);
            DatabaseConnectionManager.closeQuietly(conn);
        }
    }

    /**
     * Maps a ResultSet row to a LoanApplication bean.
     */
    private LoanApplication mapRow(ResultSet rs) throws SQLException {
        LoanApplication app = new LoanApplication();

        app.setId(rs.getLong("id"));
        app.setApplicationReference(rs.getString("application_ref"));
        app.setCustomerName(rs.getString("customer_name"));
        app.setAge(rs.getInt("age"));
        app.setMonthlyIncome(rs.getDouble("monthly_income"));
        app.setEmploymentType(rs.getString("employment_type"));
        app.setCreditScore(rs.getInt("credit_score"));
        app.setExistingEmi(rs.getDouble("existing_emi"));
        app.setLoanAmount(rs.getDouble("loan_amount"));
        app.setLoanTenure(rs.getInt("loan_tenure"));
        app.setDecision(rs.getString("decision"));
        app.setDecisionCode(rs.getString("decision_code"));
        app.setDecisionReason(rs.getString("decision_reason"));

        String createdAtStr = rs.getString("created_at");
        if (createdAtStr != null) {
            try {
                app.setCreatedAt(new java.util.Date(rs.getTimestamp("created_at").getTime()));
            } catch (Exception e) {
                app.setCreatedAt(new java.util.Date());
            }
        }

        return app;
    }

    // JDBC Resource cleanup utilities

    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    private void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }
}

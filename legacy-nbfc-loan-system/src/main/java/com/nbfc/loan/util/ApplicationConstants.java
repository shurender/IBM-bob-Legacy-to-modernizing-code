package com.nbfc.loan.util;

/**
 * Application-wide constants for the NBFC Loan Eligibility System.
 * Centralizes magic numbers and string literals used across the codebase.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public final class ApplicationConstants {

    // Prevent instantiation
    private ApplicationConstants() {
    }

    // -----------------------------------------------
    // Eligibility Rules - Credit Score
    // -----------------------------------------------

    /** Minimum credit score for automatic approval */
    public static final int CREDIT_SCORE_MIN_ELIGIBLE       = 700;

    /** Credit score below which application is automatically rejected */
    public static final int CREDIT_SCORE_MAX_REJECTED       = 649;

    /** Lower bound of the borderline credit score range */
    public static final int CREDIT_SCORE_BORDERLINE_LOW     = 650;

    /** Upper bound of the borderline credit score range */
    public static final int CREDIT_SCORE_BORDERLINE_HIGH    = 699;

    // -----------------------------------------------
    // Eligibility Rules - Age
    // -----------------------------------------------

    /** Minimum applicant age */
    public static final int AGE_MINIMUM                     = 21;

    /** Maximum applicant age */
    public static final int AGE_MAXIMUM                     = 60;

    // -----------------------------------------------
    // Eligibility Rules - Income
    // -----------------------------------------------

    /** Minimum monthly income required (INR) */
    public static final double MIN_MONTHLY_INCOME           = 25000.0;

    /** Monthly income considered borderline (close to minimum) */
    public static final double BORDERLINE_MONTHLY_INCOME    = 30000.0;

    // -----------------------------------------------
    // Eligibility Rules - Debt to Income
    // -----------------------------------------------

    /** Maximum allowed debt-to-income ratio for auto-approval */
    public static final double MAX_DEBT_TO_INCOME_RATIO     = 0.50;

    /** Debt-to-income ratio above which application is flagged for review */
    public static final double BORDERLINE_DEBT_TO_INCOME    = 0.40;

    // -----------------------------------------------
    // Eligibility Rules - Loan Amount
    // -----------------------------------------------

    /** Maximum loan amount as a multiple of annual income */
    public static final double MAX_LOAN_TO_ANNUAL_INCOME    = 10.0;

    /** Loan amount as a multiple of annual income that triggers review */
    public static final double BORDERLINE_LOAN_TO_INCOME    = 8.0;

    // -----------------------------------------------
    // EMI Calculation
    // -----------------------------------------------

    /** Default annual interest rate for EMI estimation (%) */
    public static final double DEFAULT_INTEREST_RATE        = 12.0;

    // -----------------------------------------------
    // Decision Codes
    // -----------------------------------------------

    public static final String DECISION_ELIGIBLE            = "ELIGIBLE";
    public static final String DECISION_NOT_ELIGIBLE        = "NOT_ELIGIBLE";
    public static final String DECISION_REVIEW_REQUIRED     = "REVIEW_REQUIRED";

    // -----------------------------------------------
    // Employment Types
    // -----------------------------------------------

    public static final String EMPLOYMENT_FULL_TIME         = "FULL_TIME";
    public static final String EMPLOYMENT_PART_TIME         = "PART_TIME";
    public static final String EMPLOYMENT_SELF_EMPLOYED     = "SELF_EMPLOYED";
    public static final String EMPLOYMENT_CONTRACT          = "CONTRACT";
    public static final String EMPLOYMENT_UNEMPLOYED        = "UNEMPLOYED";

    // -----------------------------------------------
    // JSP Attribute Keys
    // -----------------------------------------------

    public static final String ATTR_LOAN_APPLICATION        = "loanApplication";
    public static final String ATTR_ELIGIBILITY_DECISION    = "eligibilityDecision";
    public static final String ATTR_VALIDATION_RESULT       = "validationResult";
    public static final String ATTR_APPLICATION_LIST        = "applicationList";
    public static final String ATTR_ERROR_MESSAGE           = "errorMessage";

    // -----------------------------------------------
    // JSP View Paths
    // -----------------------------------------------

    public static final String VIEW_RESULT                  = "/WEB-INF/jsp/result.jsp";
    public static final String VIEW_ERROR                   = "/WEB-INF/jsp/error.jsp";
    public static final String VIEW_APPLICATION_LIST        = "/WEB-INF/jsp/applicationList.jsp";
    public static final String VIEW_APPLICATION_DETAIL      = "/WEB-INF/jsp/applicationDetail.jsp";
}

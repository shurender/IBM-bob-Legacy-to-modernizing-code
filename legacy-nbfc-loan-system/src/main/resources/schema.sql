
-- NBFC Loan Eligibility System - Database Schema
-- SQLite Database: nbfc_loan.db
-- Version: 1.0 | 2010
--
-- Note: This schema is auto-executed by DatabaseInitializer on startup.
-- This file is provided for reference and documentation only.
-- =====================================================================

-- Main loan applications table
CREATE TABLE IF NOT EXISTS loan_applications (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    application_ref   TEXT NOT NULL,
    customer_name     TEXT NOT NULL,
    age               INTEGER NOT NULL,
    monthly_income    REAL NOT NULL,
    employment_type   TEXT NOT NULL,
    credit_score      INTEGER NOT NULL,
    existing_emi      REAL NOT NULL DEFAULT 0,
    loan_amount       REAL NOT NULL,
    loan_tenure       INTEGER NOT NULL,
    decision          TEXT NOT NULL,         -- ELIGIBLE | NOT_ELIGIBLE | REVIEW_REQUIRED
    decision_code     TEXT NOT NULL,         -- APPROVED | REJECTED | MANUAL_REVIEW
    decision_reason   TEXT,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Eligibility rules reference table
CREATE TABLE IF NOT EXISTS eligibility_rules (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    rule_name         TEXT NOT NULL,
    rule_description  TEXT,
    min_value         REAL,
    max_value         REAL,
    is_active         INTEGER DEFAULT 1,
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Seed eligibility rules
INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('MIN_AGE', 'Minimum applicant age in years', 21, NULL);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('MAX_AGE', 'Maximum applicant age in years', NULL, 60);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('MIN_CREDIT_SCORE', 'Minimum credit score for auto-approval', 700, NULL);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('BORDERLINE_CREDIT_SCORE', 'Credit score range requiring manual review', 650, 699);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('REJECT_CREDIT_SCORE', 'Credit score below which application is auto-rejected', NULL, 649);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('MIN_MONTHLY_INCOME', 'Minimum monthly income in INR', 25000, NULL);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('MAX_DEBT_TO_INCOME_RATIO', 'Maximum allowed debt-to-income ratio (as decimal)', NULL, 0.50);

INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value)
VALUES ('MAX_LOAN_TO_INCOME_RATIO', 'Maximum loan as multiple of annual income', NULL, 10);

-- Sample query: view all applications with decisions
-- SELECT id, customer_name, credit_score, loan_amount, decision, created_at
-- FROM loan_applications
-- ORDER BY created_at DESC;

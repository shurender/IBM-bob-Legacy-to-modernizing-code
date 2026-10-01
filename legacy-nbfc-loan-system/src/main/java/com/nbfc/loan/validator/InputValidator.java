package com.nbfc.loan.validator;

import com.nbfc.loan.model.ValidationResult;
import com.nbfc.loan.util.ApplicationConstants;

/**
 * Validates raw form input before creating a LoanApplication object.
 * Checks for missing fields, invalid formats, and out-of-range values.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class InputValidator {

    /**
     * Validates all loan application form fields.
     *
     * @param customerName   applicant's full name
     * @param ageStr         applicant's age as string
     * @param incomeStr      monthly income as string
     * @param employmentType employment type code
     * @param creditScoreStr credit score as string
     * @param existingEmiStr existing EMI obligation as string
     * @param loanAmountStr  requested loan amount as string
     * @param loanTenureStr  loan tenure in months as string
     * @return ValidationResult containing any errors
     */
    public ValidationResult validate(String customerName, String ageStr,
                                     String incomeStr, String employmentType,
                                     String creditScoreStr, String existingEmiStr,
                                     String loanAmountStr, String loanTenureStr) {

        ValidationResult result = new ValidationResult();

        // Validate Customer Name
        validateCustomerName(customerName, result);

        // Validate Age
        validateAge(ageStr, result);

        // Validate Monthly Income
        validateMonthlyIncome(incomeStr, result);

        // Validate Employment Type
        validateEmploymentType(employmentType, result);

        // Validate Credit Score
        validateCreditScore(creditScoreStr, result);

        // Validate Existing EMI
        validateExistingEmi(existingEmiStr, result);

        // Validate Loan Amount
        validateLoanAmount(loanAmountStr, result);

        // Validate Loan Tenure
        validateLoanTenure(loanTenureStr, result);

        return result;
    }

    private void validateCustomerName(String customerName, ValidationResult result) {
        if (customerName == null || customerName.trim().isEmpty()) {
            result.addError("Customer Name is required.");
            return;
        }
        if (customerName.trim().length() < 2) {
            result.addError("Customer Name must be at least 2 characters.");
        }
        if (customerName.trim().length() > 100) {
            result.addError("Customer Name must not exceed 100 characters.");
        }
    }

    private void validateAge(String ageStr, ValidationResult result) {
        if (ageStr == null || ageStr.trim().isEmpty()) {
            result.addError("Age is required.");
            return;
        }
        try {
            int age = Integer.parseInt(ageStr.trim());
            if (age < 1 || age > 120) {
                result.addError("Age must be a valid number between 1 and 120.");
            }
        } catch (NumberFormatException e) {
            result.addError("Age must be a valid whole number.");
        }
    }

    private void validateMonthlyIncome(String incomeStr, ValidationResult result) {
        if (incomeStr == null || incomeStr.trim().isEmpty()) {
            result.addError("Monthly Income is required.");
            return;
        }
        try {
            double income = Double.parseDouble(incomeStr.trim());
            if (income < 0) {
                result.addError("Monthly Income cannot be negative.");
            }
            if (income > 10000000) {
                result.addError("Monthly Income value seems unreasonably high. Please verify.");
            }
        } catch (NumberFormatException e) {
            result.addError("Monthly Income must be a valid number.");
        }
    }

    private void validateEmploymentType(String employmentType, ValidationResult result) {
        if (employmentType == null || employmentType.trim().isEmpty()) {
            result.addError("Employment Type is required.");
            return;
        }
        String[] validTypes = {
            ApplicationConstants.EMPLOYMENT_FULL_TIME,
            ApplicationConstants.EMPLOYMENT_PART_TIME,
            ApplicationConstants.EMPLOYMENT_SELF_EMPLOYED,
            ApplicationConstants.EMPLOYMENT_CONTRACT,
            ApplicationConstants.EMPLOYMENT_UNEMPLOYED
        };
        boolean found = false;
        for (String t : validTypes) {
            if (t.equals(employmentType.trim())) {
                found = true;
                break;
            }
        }
        if (!found) {
            result.addError("Please select a valid Employment Type.");
        }
    }

    private void validateCreditScore(String creditScoreStr, ValidationResult result) {
        if (creditScoreStr == null || creditScoreStr.trim().isEmpty()) {
            result.addError("Credit Score is required.");
            return;
        }
        try {
            int score = Integer.parseInt(creditScoreStr.trim());
            if (score < 300 || score > 900) {
                result.addError("Credit Score must be between 300 and 900.");
            }
        } catch (NumberFormatException e) {
            result.addError("Credit Score must be a valid whole number.");
        }
    }

    private void validateExistingEmi(String existingEmiStr, ValidationResult result) {
        if (existingEmiStr == null || existingEmiStr.trim().isEmpty()) {
            result.addError("Existing EMI is required. Enter 0 if none.");
            return;
        }
        try {
            double emi = Double.parseDouble(existingEmiStr.trim());
            if (emi < 0) {
                result.addError("Existing EMI cannot be negative.");
            }
        } catch (NumberFormatException e) {
            result.addError("Existing EMI must be a valid number.");
        }
    }

    private void validateLoanAmount(String loanAmountStr, ValidationResult result) {
        if (loanAmountStr == null || loanAmountStr.trim().isEmpty()) {
            result.addError("Requested Loan Amount is required.");
            return;
        }
        try {
            double amount = Double.parseDouble(loanAmountStr.trim());
            if (amount <= 0) {
                result.addError("Requested Loan Amount must be greater than zero.");
            }
            if (amount > 100000000) {
                result.addError("Requested Loan Amount exceeds the maximum allowed limit.");
            }
        } catch (NumberFormatException e) {
            result.addError("Requested Loan Amount must be a valid number.");
        }
    }

    private void validateLoanTenure(String loanTenureStr, ValidationResult result) {
        if (loanTenureStr == null || loanTenureStr.trim().isEmpty()) {
            result.addError("Loan Tenure is required.");
            return;
        }
        try {
            int tenure = Integer.parseInt(loanTenureStr.trim());
            if (tenure < 6) {
                result.addError("Loan Tenure must be at least 6 months.");
            }
            if (tenure > 360) {
                result.addError("Loan Tenure cannot exceed 360 months (30 years).");
            }
        } catch (NumberFormatException e) {
            result.addError("Loan Tenure must be a valid whole number of months.");
        }
    }
}

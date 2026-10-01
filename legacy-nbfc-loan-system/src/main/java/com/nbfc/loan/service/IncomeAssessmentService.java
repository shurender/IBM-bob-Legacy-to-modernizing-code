package com.nbfc.loan.service;

import com.nbfc.loan.model.IncomeAssessment;
import com.nbfc.loan.util.ApplicationConstants;

/**
 * Service responsible for evaluating the income profile of a loan applicant.
 * Calculates EMI affordability, debt-to-income ratio, and loan-to-income ratio.
 *
 * Rules:
 *   Monthly income < 25,000          → FAIL
 *   Monthly income 25,000-30,000     → REVIEW (borderline)
 *   Debt-to-income >= 50%            → FAIL
 *   Debt-to-income 40%-50%           → REVIEW
 *   Loan-to-annual-income > 10x      → FAIL
 *   Loan-to-annual-income 8x-10x     → REVIEW
 *   All checks clear                 → PASS
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class IncomeAssessmentService {

    /**
     * Assesses the income profile of an applicant.
     *
     * @param monthlyIncome   gross monthly income (INR)
     * @param existingEmi     total existing monthly EMI obligations (INR)
     * @param loanAmount      requested loan amount (INR)
     * @param loanTenure      loan tenure in months
     * @param employmentType  employment type code
     * @return IncomeAssessment with computed ratios and status
     */
    public IncomeAssessment assess(double monthlyIncome, double existingEmi,
                                   double loanAmount, int loanTenure,
                                   String employmentType) {

        IncomeAssessment assessment = new IncomeAssessment();
        assessment.setMonthlyIncome(monthlyIncome);
        assessment.setExistingEmi(existingEmi);
        assessment.setRequestedLoanAmount(loanAmount);
        assessment.setLoanTenureMonths(loanTenure);
        assessment.setEmploymentType(employmentType);

        // Calculate estimated new EMI using simple flat-rate approximation
        double estimatedNewEmi = calculateEstimatedEmi(loanAmount, loanTenure,
                ApplicationConstants.DEFAULT_INTEREST_RATE);
        assessment.setEstimatedNewEmi(estimatedNewEmi);

        // Total EMI after this loan
        double totalEmi = existingEmi + estimatedNewEmi;
        assessment.setTotalEmiAfterLoan(totalEmi);

        // Disposable income
        double disposable = monthlyIncome - totalEmi;
        assessment.setDisposableIncome(disposable);

        // Debt-to-income ratio (total EMI / monthly income)
        double dtiRatio = (monthlyIncome > 0) ? (totalEmi / monthlyIncome) : 1.0;
        assessment.setDebtToIncomeRatio(dtiRatio);

        // Loan-to-income ratio (loan amount / annual income)
        double annualIncome = monthlyIncome * 12;
        double ltiRatio = (annualIncome > 0) ? (loanAmount / annualIncome) : 999.0;
        assessment.setLoanToIncomeRatio(ltiRatio);

        // Determine assessment status
        determineStatus(assessment, monthlyIncome, dtiRatio, ltiRatio);

        return assessment;
    }

    /**
     * Applies business rules to set the assessment status and remark.
     */
    private void determineStatus(IncomeAssessment assessment,
                                  double monthlyIncome,
                                  double dtiRatio,
                                  double ltiRatio) {

        StringBuilder failReasons   = new StringBuilder();
        StringBuilder reviewReasons = new StringBuilder();

        // Rule 1: Minimum income check
        if (monthlyIncome < ApplicationConstants.MIN_MONTHLY_INCOME) {
            failReasons.append("Monthly income of ").append(monthlyIncome)
                       .append(" is below the minimum requirement of ")
                       .append(ApplicationConstants.MIN_MONTHLY_INCOME).append(". ");
        } else if (monthlyIncome < ApplicationConstants.BORDERLINE_MONTHLY_INCOME) {
            reviewReasons.append("Monthly income is close to the minimum threshold. ");
        }

        // Rule 2: Debt-to-income ratio check
        if (dtiRatio >= ApplicationConstants.MAX_DEBT_TO_INCOME_RATIO) {
            failReasons.append("Total debt-to-income ratio of ")
                       .append(String.format("%.1f", dtiRatio * 100))
                       .append("% exceeds the maximum allowed 50%. ");
        } else if (dtiRatio >= ApplicationConstants.BORDERLINE_DEBT_TO_INCOME) {
            reviewReasons.append("Debt-to-income ratio of ")
                         .append(String.format("%.1f", dtiRatio * 100))
                         .append("% is high and requires review. ");
        }

        // Rule 3: Loan-to-annual-income ratio check
        if (ltiRatio > ApplicationConstants.MAX_LOAN_TO_ANNUAL_INCOME) {
            failReasons.append("Requested loan amount exceeds 10 times annual income. ");
        } else if (ltiRatio > ApplicationConstants.BORDERLINE_LOAN_TO_INCOME) {
            reviewReasons.append("Loan amount is high relative to annual income. ");
        }

        // Determine final status
        if (failReasons.length() > 0) {
            assessment.setAssessmentStatus(IncomeAssessment.STATUS_FAIL);
            assessment.setAssessmentRemark(failReasons.toString().trim());
        } else if (reviewReasons.length() > 0) {
            assessment.setAssessmentStatus(IncomeAssessment.STATUS_REVIEW);
            assessment.setAssessmentRemark(reviewReasons.toString().trim());
        } else {
            assessment.setAssessmentStatus(IncomeAssessment.STATUS_PASS);
            assessment.setAssessmentRemark("Income profile meets eligibility criteria.");
        }
    }

    /**
     * Calculates estimated monthly EMI using reducing balance formula.
     *
     * EMI = P * r * (1+r)^n / ((1+r)^n - 1)
     * where P = principal, r = monthly rate, n = tenure months
     *
     * @param principal    loan principal amount
     * @param tenureMonths loan tenure in months
     * @param annualRate   annual interest rate as percentage
     * @return estimated monthly EMI
     */
    public double calculateEstimatedEmi(double principal, int tenureMonths, double annualRate) {
        if (principal <= 0 || tenureMonths <= 0) return 0;

        double monthlyRate = annualRate / (12 * 100);

        if (monthlyRate == 0) {
            return principal / tenureMonths;
        }

        double numerator   = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths);
        double denominator = Math.pow(1 + monthlyRate, tenureMonths) - 1;

        return (denominator > 0) ? (numerator / denominator) : 0;
    }
}

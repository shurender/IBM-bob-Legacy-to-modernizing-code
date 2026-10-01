package com.nbfc.loan.model;

import java.io.Serializable;

/**
 * Represents the income assessment result for an applicant.
 * Evaluates disposable income, EMI affordability and debt-to-income ratio.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class IncomeAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    // Assessment Status
    public static final String STATUS_PASS   = "PASS";
    public static final String STATUS_FAIL   = "FAIL";
    public static final String STATUS_REVIEW = "REVIEW";

    private double monthlyIncome;
    private double existingEmi;
    private double requestedLoanAmount;
    private int    loanTenureMonths;
    private String employmentType;

    // Calculated Fields
    private double estimatedNewEmi;
    private double totalEmiAfterLoan;
    private double disposableIncome;
    private double debtToIncomeRatio;
    private double loanToIncomeRatio;

    // Assessment
    private String assessmentStatus;
    private String assessmentRemark;

    public IncomeAssessment() {
    }

    // Getters and Setters

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public double getExistingEmi() {
        return existingEmi;
    }

    public void setExistingEmi(double existingEmi) {
        this.existingEmi = existingEmi;
    }

    public double getRequestedLoanAmount() {
        return requestedLoanAmount;
    }

    public void setRequestedLoanAmount(double requestedLoanAmount) {
        this.requestedLoanAmount = requestedLoanAmount;
    }

    public int getLoanTenureMonths() {
        return loanTenureMonths;
    }

    public void setLoanTenureMonths(int loanTenureMonths) {
        this.loanTenureMonths = loanTenureMonths;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public double getEstimatedNewEmi() {
        return estimatedNewEmi;
    }

    public void setEstimatedNewEmi(double estimatedNewEmi) {
        this.estimatedNewEmi = estimatedNewEmi;
    }

    public double getTotalEmiAfterLoan() {
        return totalEmiAfterLoan;
    }

    public void setTotalEmiAfterLoan(double totalEmiAfterLoan) {
        this.totalEmiAfterLoan = totalEmiAfterLoan;
    }

    public double getDisposableIncome() {
        return disposableIncome;
    }

    public void setDisposableIncome(double disposableIncome) {
        this.disposableIncome = disposableIncome;
    }

    public double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public double getLoanToIncomeRatio() {
        return loanToIncomeRatio;
    }

    public void setLoanToIncomeRatio(double loanToIncomeRatio) {
        this.loanToIncomeRatio = loanToIncomeRatio;
    }

    public String getAssessmentStatus() {
        return assessmentStatus;
    }

    public void setAssessmentStatus(String assessmentStatus) {
        this.assessmentStatus = assessmentStatus;
    }

    public String getAssessmentRemark() {
        return assessmentRemark;
    }

    public void setAssessmentRemark(String assessmentRemark) {
        this.assessmentRemark = assessmentRemark;
    }

    @Override
    public String toString() {
        return "IncomeAssessment{" +
                "monthlyIncome=" + monthlyIncome +
                ", existingEmi=" + existingEmi +
                ", debtToIncomeRatio=" + debtToIncomeRatio +
                ", assessmentStatus='" + assessmentStatus + '\'' +
                '}';
    }
}

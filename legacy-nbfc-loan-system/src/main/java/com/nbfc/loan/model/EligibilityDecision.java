package com.nbfc.loan.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the final eligibility decision for a loan application.
 * Contains the decision outcome, reason, and any assessment flags.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class EligibilityDecision implements Serializable {

    private static final long serialVersionUID = 1L;

    // Decision Constants
    public static final String DECISION_ELIGIBLE          = "ELIGIBLE";
    public static final String DECISION_NOT_ELIGIBLE      = "NOT_ELIGIBLE";
    public static final String DECISION_REVIEW_REQUIRED   = "REVIEW_REQUIRED";

    // Decision Code Constants
    public static final String CODE_APPROVED              = "APPROVED";
    public static final String CODE_REJECTED              = "REJECTED";
    public static final String CODE_MANUAL_REVIEW         = "MANUAL_REVIEW";

    private String decision;
    private String decisionCode;
    private String primaryReason;
    private List<String> failureReasons;
    private List<String> reviewReasons;
    private List<String> passedChecks;
    private boolean creditCheckPassed;
    private boolean incomeCheckPassed;
    private boolean ageCheckPassed;
    private boolean emiCheckPassed;
    private boolean loanAmountCheckPassed;
    private int riskScore;

    public EligibilityDecision() {
        this.failureReasons = new ArrayList<String>();
        this.reviewReasons  = new ArrayList<String>();
        this.passedChecks   = new ArrayList<String>();
    }

    public void addFailureReason(String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            this.failureReasons.add(reason);
        }
    }

    public void addReviewReason(String reason) {
        if (reason != null && !reason.trim().isEmpty()) {
            this.reviewReasons.add(reason);
        }
    }

    public void addPassedCheck(String check) {
        if (check != null && !check.trim().isEmpty()) {
            this.passedChecks.add(check);
        }
    }

    public boolean hasFailures() {
        return failureReasons != null && !failureReasons.isEmpty();
    }

    public boolean hasReviewFlags() {
        return reviewReasons != null && !reviewReasons.isEmpty();
    }

    // Getters and Setters

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public String getPrimaryReason() {
        return primaryReason;
    }

    public void setPrimaryReason(String primaryReason) {
        this.primaryReason = primaryReason;
    }

    public List<String> getFailureReasons() {
        return failureReasons;
    }

    public void setFailureReasons(List<String> failureReasons) {
        this.failureReasons = failureReasons;
    }

    public List<String> getReviewReasons() {
        return reviewReasons;
    }

    public void setReviewReasons(List<String> reviewReasons) {
        this.reviewReasons = reviewReasons;
    }

    public List<String> getPassedChecks() {
        return passedChecks;
    }

    public void setPassedChecks(List<String> passedChecks) {
        this.passedChecks = passedChecks;
    }

    public boolean isCreditCheckPassed() {
        return creditCheckPassed;
    }

    public void setCreditCheckPassed(boolean creditCheckPassed) {
        this.creditCheckPassed = creditCheckPassed;
    }

    public boolean isIncomeCheckPassed() {
        return incomeCheckPassed;
    }

    public void setIncomeCheckPassed(boolean incomeCheckPassed) {
        this.incomeCheckPassed = incomeCheckPassed;
    }

    public boolean isAgeCheckPassed() {
        return ageCheckPassed;
    }

    public void setAgeCheckPassed(boolean ageCheckPassed) {
        this.ageCheckPassed = ageCheckPassed;
    }

    public boolean isEmiCheckPassed() {
        return emiCheckPassed;
    }

    public void setEmiCheckPassed(boolean emiCheckPassed) {
        this.emiCheckPassed = emiCheckPassed;
    }

    public boolean isLoanAmountCheckPassed() {
        return loanAmountCheckPassed;
    }

    public void setLoanAmountCheckPassed(boolean loanAmountCheckPassed) {
        this.loanAmountCheckPassed = loanAmountCheckPassed;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    @Override
    public String toString() {
        return "EligibilityDecision{" +
                "decision='" + decision + '\'' +
                ", decisionCode='" + decisionCode + '\'' +
                ", primaryReason='" + primaryReason + '\'' +
                ", riskScore=" + riskScore +
                '}';
    }
}

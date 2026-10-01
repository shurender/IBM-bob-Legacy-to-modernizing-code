package com.nbfc.loan.model;

import java.io.Serializable;

/**
 * Represents the credit assessment result for an applicant.
 * Contains credit score evaluation details and categorization.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class CreditAssessment implements Serializable {

    private static final long serialVersionUID = 1L;

    // Credit Score Categories
    public static final String CATEGORY_EXCELLENT  = "EXCELLENT";
    public static final String CATEGORY_GOOD       = "GOOD";
    public static final String CATEGORY_FAIR       = "FAIR";
    public static final String CATEGORY_POOR       = "POOR";
    public static final String CATEGORY_VERY_POOR  = "VERY_POOR";

    // Assessment Status
    public static final String STATUS_PASS         = "PASS";
    public static final String STATUS_FAIL         = "FAIL";
    public static final String STATUS_REVIEW       = "REVIEW";

    private int creditScore;
    private String creditCategory;
    private String assessmentStatus;
    private String assessmentRemark;
    private int minRequiredScore;
    private int borderlineThreshold;

    public CreditAssessment() {
    }

    public CreditAssessment(int creditScore) {
        this.creditScore = creditScore;
        this.evaluate();
    }

    public void evaluate() {
        if (creditScore >= 750) {
            this.creditCategory   = CATEGORY_EXCELLENT;
        } else if (creditScore >= 700) {
            this.creditCategory   = CATEGORY_GOOD;
        } else if (creditScore >= 650) {
            this.creditCategory   = CATEGORY_FAIR;
        } else if (creditScore >= 600) {
            this.creditCategory   = CATEGORY_POOR;
        } else {
            this.creditCategory   = CATEGORY_VERY_POOR;
        }
    }

    // Getters and Setters

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getCreditCategory() {
        return creditCategory;
    }

    public void setCreditCategory(String creditCategory) {
        this.creditCategory = creditCategory;
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

    public int getMinRequiredScore() {
        return minRequiredScore;
    }

    public void setMinRequiredScore(int minRequiredScore) {
        this.minRequiredScore = minRequiredScore;
    }

    public int getBorderlineThreshold() {
        return borderlineThreshold;
    }

    public void setBorderlineThreshold(int borderlineThreshold) {
        this.borderlineThreshold = borderlineThreshold;
    }

    @Override
    public String toString() {
        return "CreditAssessment{" +
                "creditScore=" + creditScore +
                ", creditCategory='" + creditCategory + '\'' +
                ", assessmentStatus='" + assessmentStatus + '\'' +
                '}';
    }
}

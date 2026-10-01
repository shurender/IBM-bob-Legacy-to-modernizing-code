package com.nbfc.loan.model;

import java.io.Serializable;
import java.util.Date;

/**
 * JavaBean representing a Loan Application.
 * This is the primary domain object in the NBFC Loan Eligibility System.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class LoanApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    // Primary Key
    private Long id;

    // Customer Information
    private String customerName;
    private int age;
    private double monthlyIncome;
    private String employmentType;
    private int creditScore;
    private double existingEmi;

    // Loan Details
    private double loanAmount;
    private int loanTenure;

    // Decision Fields
    private String decision;
    private String decisionReason;
    private String decisionCode;

    // Audit Fields
    private Date createdAt;
    private String applicationReference;

    // Default Constructor
    public LoanApplication() {
        this.createdAt = new Date();
    }

    // Parameterized Constructor
    public LoanApplication(String customerName, int age, double monthlyIncome,
                           String employmentType, int creditScore,
                           double existingEmi, double loanAmount, int loanTenure) {
        this.customerName = customerName;
        this.age = age;
        this.monthlyIncome = monthlyIncome;
        this.employmentType = employmentType;
        this.creditScore = creditScore;
        this.existingEmi = existingEmi;
        this.loanAmount = loanAmount;
        this.loanTenure = loanTenure;
        this.createdAt = new Date();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public double getExistingEmi() {
        return existingEmi;
    }

    public void setExistingEmi(double existingEmi) {
        this.existingEmi = existingEmi;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public int getLoanTenure() {
        return loanTenure;
    }

    public void setLoanTenure(int loanTenure) {
        this.loanTenure = loanTenure;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public void setApplicationReference(String applicationReference) {
        this.applicationReference = applicationReference;
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", age=" + age +
                ", monthlyIncome=" + monthlyIncome +
                ", creditScore=" + creditScore +
                ", loanAmount=" + loanAmount +
                ", decision='" + decision + '\'' +
                '}';
    }
}

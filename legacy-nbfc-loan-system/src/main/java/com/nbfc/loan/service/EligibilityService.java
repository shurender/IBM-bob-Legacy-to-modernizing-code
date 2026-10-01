package com.nbfc.loan.service;

import com.nbfc.loan.model.CreditAssessment;
import com.nbfc.loan.model.EligibilityDecision;
import com.nbfc.loan.model.IncomeAssessment;
import com.nbfc.loan.model.LoanApplication;
import com.nbfc.loan.util.ApplicationConstants;

/**
 * Central eligibility evaluation service.
 * Orchestrates all sub-assessments (credit, income, age) and
 * produces the final ELIGIBLE / NOT_ELIGIBLE / REVIEW_REQUIRED decision.
 *
 * Decision hierarchy:
 *   1. If ANY hard-fail condition exists → NOT_ELIGIBLE
 *   2. If NO hard-fails but review flags exist → REVIEW_REQUIRED
 *   3. If all checks pass cleanly → ELIGIBLE
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class EligibilityService {

    private final CreditAssessmentService creditService;
    private final IncomeAssessmentService incomeService;
    private final AgeAssessmentService    ageService;

    public EligibilityService() {
        this.creditService = new CreditAssessmentService();
        this.incomeService = new IncomeAssessmentService();
        this.ageService    = new AgeAssessmentService();
    }

    /**
     * Evaluates a loan application and returns the eligibility decision.
     *
     * @param application the loan application to evaluate
     * @return EligibilityDecision with full assessment details
     */
    public EligibilityDecision evaluate(LoanApplication application) {

        EligibilityDecision decision = new EligibilityDecision();

        // --- Step 1: Age Assessment ---
        String ageStatus = ageService.assess(application.getAge());
        String ageRemark = ageService.getRemark(application.getAge());

        if (AgeAssessmentService.STATUS_FAIL.equals(ageStatus)) {
            decision.addFailureReason(ageRemark);
            decision.setAgeCheckPassed(false);
        } else {
            decision.setAgeCheckPassed(true);
            decision.addPassedCheck("Age eligibility: " + ageRemark);
        }

        // --- Step 2: Credit Score Assessment ---
        CreditAssessment creditAssessment = creditService.assess(application.getCreditScore());

        if (CreditAssessment.STATUS_FAIL.equals(creditAssessment.getAssessmentStatus())) {
            decision.addFailureReason(creditAssessment.getAssessmentRemark());
            decision.setCreditCheckPassed(false);
        } else if (CreditAssessment.STATUS_REVIEW.equals(creditAssessment.getAssessmentStatus())) {
            decision.addReviewReason(creditAssessment.getAssessmentRemark());
            decision.setCreditCheckPassed(false);
        } else {
            decision.setCreditCheckPassed(true);
            decision.addPassedCheck("Credit score: " + creditAssessment.getAssessmentRemark());
        }

        // --- Step 3: Income Assessment ---
        IncomeAssessment incomeAssessment = incomeService.assess(
                application.getMonthlyIncome(),
                application.getExistingEmi(),
                application.getLoanAmount(),
                application.getLoanTenure(),
                application.getEmploymentType()
        );

        if (IncomeAssessment.STATUS_FAIL.equals(incomeAssessment.getAssessmentStatus())) {
            decision.addFailureReason(incomeAssessment.getAssessmentRemark());
            decision.setIncomeCheckPassed(false);
            decision.setEmiCheckPassed(false);
            decision.setLoanAmountCheckPassed(false);
        } else if (IncomeAssessment.STATUS_REVIEW.equals(incomeAssessment.getAssessmentStatus())) {
            decision.addReviewReason(incomeAssessment.getAssessmentRemark());
            decision.setIncomeCheckPassed(false);
            decision.setEmiCheckPassed(true);
            decision.setLoanAmountCheckPassed(true);
        } else {
            decision.setIncomeCheckPassed(true);
            decision.setEmiCheckPassed(true);
            decision.setLoanAmountCheckPassed(true);
            decision.addPassedCheck("Income assessment: " + incomeAssessment.getAssessmentRemark());
        }

        // --- Step 4: Employment Type Check ---
        if (ApplicationConstants.EMPLOYMENT_UNEMPLOYED.equals(application.getEmploymentType())) {
            decision.addFailureReason("Applicant is currently unemployed. Employment is required for loan eligibility.");
        }

        // --- Step 5: Determine Final Decision ---
        determineFinalDecision(decision);

        return decision;
    }

    /**
     * Applies the decision hierarchy to set the final decision on the result object.
     *
     * @param decision the decision object populated with check results
     */
    private void determineFinalDecision(EligibilityDecision decision) {

        if (decision.hasFailures()) {
            // Hard failures present - reject
            decision.setDecision(EligibilityDecision.DECISION_NOT_ELIGIBLE);
            decision.setDecisionCode(EligibilityDecision.CODE_REJECTED);

            // Use first failure as primary reason
            if (decision.getFailureReasons() != null && !decision.getFailureReasons().isEmpty()) {
                decision.setPrimaryReason(decision.getFailureReasons().get(0));
            } else {
                decision.setPrimaryReason("Application does not meet eligibility criteria.");
            }

        } else if (decision.hasReviewFlags()) {
            // No hard failures but review flags present
            decision.setDecision(EligibilityDecision.DECISION_REVIEW_REQUIRED);
            decision.setDecisionCode(EligibilityDecision.CODE_MANUAL_REVIEW);

            // Combine review reasons
            StringBuilder sb = new StringBuilder();
            for (String reason : decision.getReviewReasons()) {
                sb.append(reason).append(" ");
            }
            decision.setPrimaryReason(sb.toString().trim());

        } else {
            // All checks passed
            decision.setDecision(EligibilityDecision.DECISION_ELIGIBLE);
            decision.setDecisionCode(EligibilityDecision.CODE_APPROVED);
            decision.setPrimaryReason(
                "The customer satisfies the current automated eligibility criteria."
            );
        }
    }
}

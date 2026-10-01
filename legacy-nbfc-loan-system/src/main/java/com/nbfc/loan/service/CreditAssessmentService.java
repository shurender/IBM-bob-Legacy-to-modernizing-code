package com.nbfc.loan.service;

import com.nbfc.loan.model.CreditAssessment;
import com.nbfc.loan.util.ApplicationConstants;

/**
 * Service responsible for evaluating the credit score of a loan applicant.
 * Applies credit score rules and returns a structured assessment result.
 *
 * Rules:
 *   Score >= 700  → PASS  (auto-approve eligible)
 *   Score 650-699 → REVIEW (borderline - send for manual review)
 *   Score < 650   → FAIL  (auto-reject)
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class CreditAssessmentService {

    /**
     * Evaluates the applicant's credit score and returns an assessment.
     *
     * @param creditScore the applicant's credit score (300-900)
     * @return CreditAssessment containing the pass/fail/review status
     */
    public CreditAssessment assess(int creditScore) {
        CreditAssessment assessment = new CreditAssessment(creditScore);

        assessment.setMinRequiredScore(ApplicationConstants.CREDIT_SCORE_MIN_ELIGIBLE);
        assessment.setBorderlineThreshold(ApplicationConstants.CREDIT_SCORE_BORDERLINE_LOW);

        if (creditScore >= ApplicationConstants.CREDIT_SCORE_MIN_ELIGIBLE) {
            // Good credit score - pass
            assessment.setAssessmentStatus(CreditAssessment.STATUS_PASS);
            assessment.setAssessmentRemark(
                "Credit score of " + creditScore + " meets the minimum requirement of " +
                ApplicationConstants.CREDIT_SCORE_MIN_ELIGIBLE + "."
            );

        } else if (creditScore >= ApplicationConstants.CREDIT_SCORE_BORDERLINE_LOW &&
                   creditScore <= ApplicationConstants.CREDIT_SCORE_BORDERLINE_HIGH) {
            // Borderline credit score - send for review
            assessment.setAssessmentStatus(CreditAssessment.STATUS_REVIEW);
            assessment.setAssessmentRemark(
                "Credit score of " + creditScore + " is in the borderline range (" +
                ApplicationConstants.CREDIT_SCORE_BORDERLINE_LOW + "-" +
                ApplicationConstants.CREDIT_SCORE_BORDERLINE_HIGH +
                "). Manual review required."
            );

        } else {
            // Poor credit score - reject
            assessment.setAssessmentStatus(CreditAssessment.STATUS_FAIL);
            assessment.setAssessmentRemark(
                "Credit score of " + creditScore + " does not meet the minimum eligibility " +
                "requirement of " + ApplicationConstants.CREDIT_SCORE_MIN_ELIGIBLE + "."
            );
        }

        return assessment;
    }
}

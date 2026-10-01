package com.nbfc.loan.service;

import com.nbfc.loan.dao.LoanApplicationDAO;
import com.nbfc.loan.model.EligibilityDecision;
import com.nbfc.loan.model.LoanApplication;
import com.nbfc.loan.util.ReferenceGenerator;

import java.sql.SQLException;

/**
 * Top-level service facade for loan eligibility processing.
 * Coordinates the eligibility evaluation and persistence of loan applications.
 *
 * This is the primary entry point called by the LoanServlet.
 *
 * Flow:
 *   1. Generate application reference
 *   2. Evaluate eligibility via EligibilityService
 *   3. Populate decision fields on the application
 *   4. Persist application to database via LoanApplicationDAO
 *   5. Return populated application (with ID) to caller
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class LoanService {

    private final EligibilityService eligibilityService;
    private final LoanApplicationDAO loanApplicationDAO;

    public LoanService() {
        this.eligibilityService = new EligibilityService();
        this.loanApplicationDAO = new LoanApplicationDAO();
    }

    /**
     * Processes a loan application end-to-end:
     * evaluates eligibility, saves to database, and returns the final decision.
     *
     * @param application the loan application with customer and loan details
     * @return the application enriched with decision and database ID
     * @throws SQLException if database persistence fails
     */
    public LoanApplication processApplication(LoanApplication application) throws SQLException {

        // Generate application reference number
        String ref = ReferenceGenerator.generateReference();
        application.setApplicationReference(ref);

        // Run eligibility evaluation
        EligibilityDecision decision = eligibilityService.evaluate(application);

        // Transfer decision to application bean
        application.setDecision(decision.getDecision());
        application.setDecisionCode(decision.getDecisionCode());
        application.setDecisionReason(decision.getPrimaryReason());

        // Persist to database
        Long generatedId = loanApplicationDAO.save(application);

        // Read back from database so the UI displays the persisted SQL record.
        LoanApplication savedApplication = loanApplicationDAO.findById(generatedId);
        if (savedApplication == null) {
            throw new SQLException("Saved loan application could not be read back from database.");
        }

        return savedApplication;
    }

    /**
     * Returns just the eligibility decision without persisting.
     * Useful for preview/dry-run scenarios.
     *
     * @param application the loan application details
     * @return the eligibility decision
     */
    public EligibilityDecision checkEligibilityOnly(LoanApplication application) {
        return eligibilityService.evaluate(application);
    }
}

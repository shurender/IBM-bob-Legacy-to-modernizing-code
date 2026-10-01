package com.nbfc.loan.servlet;

import com.nbfc.loan.model.LoanApplication;
import com.nbfc.loan.model.ValidationResult;
import com.nbfc.loan.service.LoanService;
import com.nbfc.loan.util.ApplicationConstants;
import com.nbfc.loan.util.FormatUtil;
import com.nbfc.loan.validator.InputValidator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Main loan eligibility servlet.
 * Handles form submission, validation, and dispatches to the result view.
 *
 * GET  /loan → redirects to application home page
 * POST /loan → processes the loan eligibility form
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class LoanServlet extends HttpServlet {

    private LoanService     loanService;
    private InputValidator  inputValidator;

    @Override
    public void init() throws ServletException {
        super.init();
        this.loanService    = new LoanService();
        this.inputValidator = new InputValidator();
        getServletContext().log("[LoanServlet] Initialized.");
    }

    /**
     * GET handler - redirect to the home page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/");
    }

    /**
     * POST handler - process the loan eligibility form.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Read form parameters
        String customerName   = request.getParameter("customerName");
        String ageStr         = request.getParameter("age");
        String incomeStr      = request.getParameter("monthlyIncome");
        String employmentType = request.getParameter("employmentType");
        String creditScoreStr = request.getParameter("creditScore");
        String existingEmiStr = request.getParameter("existingEmi");
        String loanAmountStr  = request.getParameter("loanAmount");
        String loanTenureStr  = request.getParameter("loanTenure");

        // Validate input
        ValidationResult validation = inputValidator.validate(
                customerName, ageStr, incomeStr, employmentType,
                creditScoreStr, existingEmiStr, loanAmountStr, loanTenureStr
        );

        if (!validation.isValid()) {
            // Return to form with validation errors
            request.setAttribute(ApplicationConstants.ATTR_VALIDATION_RESULT, validation);
            // Re-populate form values
            repopulateFormValues(request, customerName, ageStr, incomeStr,
                    employmentType, creditScoreStr, existingEmiStr, loanAmountStr, loanTenureStr);
            RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
            rd.forward(request, response);
            return;
        }

        // Build LoanApplication bean
        LoanApplication application = new LoanApplication();
        application.setCustomerName(customerName.trim());
        application.setAge(FormatUtil.parseIntSafe(ageStr, 0));
        application.setMonthlyIncome(FormatUtil.parseDoubleSafe(incomeStr, 0));
        application.setEmploymentType(employmentType.trim());
        application.setCreditScore(FormatUtil.parseIntSafe(creditScoreStr, 0));
        application.setExistingEmi(FormatUtil.parseDoubleSafe(existingEmiStr, 0));
        application.setLoanAmount(FormatUtil.parseDoubleSafe(loanAmountStr, 0));
        application.setLoanTenure(FormatUtil.parseIntSafe(loanTenureStr, 0));

        try {
            // Process application through service layer
            LoanApplication processed = loanService.processApplication(application);

            // Set attributes for result JSP
            request.setAttribute(ApplicationConstants.ATTR_LOAN_APPLICATION, processed);

            // Forward to result view
            RequestDispatcher rd = request.getRequestDispatcher(ApplicationConstants.VIEW_RESULT);
            rd.forward(request, response);

        } catch (SQLException e) {
            getServletContext().log("[LoanServlet] Database error processing application: " + e.getMessage(), e);
            request.setAttribute(ApplicationConstants.ATTR_ERROR_MESSAGE,
                    "A system error occurred while processing your application. Please try again.");
            RequestDispatcher rd = request.getRequestDispatcher(ApplicationConstants.VIEW_ERROR);
            rd.forward(request, response);
        } catch (Exception e) {
            getServletContext().log("[LoanServlet] Unexpected error: " + e.getMessage(), e);
            request.setAttribute(ApplicationConstants.ATTR_ERROR_MESSAGE,
                    "An unexpected error occurred. Please contact the system administrator.");
            RequestDispatcher rd = request.getRequestDispatcher(ApplicationConstants.VIEW_ERROR);
            rd.forward(request, response);
        }
    }

    /**
     * Re-populates request attributes with form values so the form
     * can be redisplayed with the user's previously entered data.
     */
    private void repopulateFormValues(HttpServletRequest request,
                                       String customerName, String age, String income,
                                       String employmentType, String creditScore,
                                       String existingEmi, String loanAmount, String loanTenure) {
        request.setAttribute("f_customerName",   customerName);
        request.setAttribute("f_age",            age);
        request.setAttribute("f_monthlyIncome",  income);
        request.setAttribute("f_employmentType", employmentType);
        request.setAttribute("f_creditScore",    creditScore);
        request.setAttribute("f_existingEmi",    existingEmi);
        request.setAttribute("f_loanAmount",     loanAmount);
        request.setAttribute("f_loanTenure",     loanTenure);
    }
}

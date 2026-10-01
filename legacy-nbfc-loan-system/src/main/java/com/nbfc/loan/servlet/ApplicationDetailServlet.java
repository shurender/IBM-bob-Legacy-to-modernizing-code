package com.nbfc.loan.servlet;

import com.nbfc.loan.dao.LoanApplicationDAO;
import com.nbfc.loan.model.LoanApplication;
import com.nbfc.loan.util.ApplicationConstants;
import com.nbfc.loan.util.FormatUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet for displaying the detail view of a single loan application.
 *
 * GET /application/detail?id=N → shows detail for application with ID=N
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class ApplicationDetailServlet extends HttpServlet {

    private LoanApplicationDAO loanApplicationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.loanApplicationDAO = new LoanApplicationDAO();
        getServletContext().log("[ApplicationDetailServlet] Initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/applications");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/applications");
            return;
        }

        try {
            LoanApplication application = loanApplicationDAO.findById(id);

            if (application == null) {
                request.setAttribute(ApplicationConstants.ATTR_ERROR_MESSAGE,
                        "Application with ID " + id + " was not found.");
                RequestDispatcher rd = request.getRequestDispatcher(ApplicationConstants.VIEW_ERROR);
                rd.forward(request, response);
                return;
            }

            request.setAttribute(ApplicationConstants.ATTR_LOAN_APPLICATION, application);
            RequestDispatcher rd = request.getRequestDispatcher(
                    ApplicationConstants.VIEW_APPLICATION_DETAIL);
            rd.forward(request, response);

        } catch (SQLException e) {
            getServletContext().log("[ApplicationDetailServlet] DB error: " + e.getMessage(), e);
            request.setAttribute(ApplicationConstants.ATTR_ERROR_MESSAGE,
                    "Unable to retrieve application details.");
            RequestDispatcher rd = request.getRequestDispatcher(ApplicationConstants.VIEW_ERROR);
            rd.forward(request, response);
        }
    }
}

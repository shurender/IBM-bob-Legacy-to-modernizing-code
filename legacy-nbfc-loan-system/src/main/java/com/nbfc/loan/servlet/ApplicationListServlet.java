package com.nbfc.loan.servlet;

import com.nbfc.loan.dao.LoanApplicationDAO;
import com.nbfc.loan.model.LoanApplication;
import com.nbfc.loan.util.ApplicationConstants;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet for displaying the list of all submitted loan applications.
 * Provides a simple admin view of all processed applications.
 *
 * GET /applications → shows all applications in tabular format
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class ApplicationListServlet extends HttpServlet {

    private LoanApplicationDAO loanApplicationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.loanApplicationDAO = new LoanApplicationDAO();
        getServletContext().log("[ApplicationListServlet] Initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<LoanApplication> applications = loanApplicationDAO.findAll();
            int totalCount = loanApplicationDAO.countAll();

            request.setAttribute(ApplicationConstants.ATTR_APPLICATION_LIST, applications);
            request.setAttribute("totalCount", totalCount);

            RequestDispatcher rd = request.getRequestDispatcher(
                    ApplicationConstants.VIEW_APPLICATION_LIST);
            rd.forward(request, response);

        } catch (SQLException e) {
            getServletContext().log("[ApplicationListServlet] DB error: " + e.getMessage(), e);
            request.setAttribute(ApplicationConstants.ATTR_ERROR_MESSAGE,
                    "Unable to retrieve applications from database.");
            RequestDispatcher rd = request.getRequestDispatcher(ApplicationConstants.VIEW_ERROR);
            rd.forward(request, response);
        }
    }
}

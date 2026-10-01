<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Loan Eligibility Result - NBFC</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" />
</head>
<body>

<!-- HEADER -->
<div id="header">
    <div id="header-inner">
        <h1>NBFC LOAN ELIGIBILITY SYSTEM - DEMO</h1>
        <div class="subtitle">Demo Page &nbsp;|&nbsp; National Banking &amp; Finance Corporation &nbsp;|&nbsp; Internal Operations Portal</div>
    </div>
</div>

<!-- NAVIGATION -->
<div id="nav-bar">
    <a href="<c:url value='/'/>">Home</a>
</div>

<div id="page-wrapper">
<div id="main-content">

    <div class="section-title">DEMO LOAN ELIGIBILITY RESULT</div>

    <div class="section-content">

        <!-- Result Box -->
        <div class="result-box">

            <!-- Title -->
            <div class="result-title-bar">
                ========================================
                &nbsp;&nbsp;&nbsp;LOAN ELIGIBILITY DECISION&nbsp;&nbsp;&nbsp;
                ========================================
            </div>

            <!-- Customer Info -->
            <div class="result-customer-info">
                <table>
                    <tr>
                        <td class="info-label">Customer Name:</td>
                        <td><strong><c:out value="${loanApplication.customerName}"/></strong></td>
                        <td class="info-label">Application Ref:</td>
                        <td class="small-text"><c:out value="${loanApplication.applicationReference}"/></td>
                    </tr>
                    <tr>
                        <td class="info-label">Age:</td>
                        <td><c:out value="${loanApplication.age}"/> years</td>
                        <td class="info-label">Employment:</td>
                        <td>
                            <c:choose>
                                <c:when test="${loanApplication.employmentType == 'FULL_TIME'}">Full Time</c:when>
                                <c:when test="${loanApplication.employmentType == 'PART_TIME'}">Part Time</c:when>
                                <c:when test="${loanApplication.employmentType == 'SELF_EMPLOYED'}">Self Employed</c:when>
                                <c:when test="${loanApplication.employmentType == 'CONTRACT'}">Contract</c:when>
                                <c:when test="${loanApplication.employmentType == 'UNEMPLOYED'}">Unemployed</c:when>
                                <c:otherwise><c:out value="${loanApplication.employmentType}"/></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td class="info-label">Monthly Income:</td>
                        <td>&#8377;<fmt:formatNumber value="${loanApplication.monthlyIncome}" pattern="#,##,###"/></td>
                        <td class="info-label">Credit Score:</td>
                        <td><strong><c:out value="${loanApplication.creditScore}"/></strong></td>
                    </tr>
                    <tr>
                        <td class="info-label">Loan Amount Requested:</td>
                        <td><strong>&#8377;<fmt:formatNumber value="${loanApplication.loanAmount}" pattern="#,##,###"/></strong></td>
                        <td class="info-label">Existing EMI:</td>
                        <td>&#8377;<fmt:formatNumber value="${loanApplication.existingEmi}" pattern="#,##,###"/></td>
                    </tr>
                    <tr>
                        <td class="info-label">Loan Tenure:</td>
                        <td><c:out value="${loanApplication.loanTenure}"/> months</td>
                        <td class="info-label">&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </div>

            <hr class="result-separator" />

            <!-- DECISION SECTION -->
            <div class="decision-section">
                <div class="decision-label">DECISION:</div>

                <!-- ===== ELIGIBLE ===== -->
                <c:if test="${loanApplication.decision == 'ELIGIBLE'}">
                <div class="decision-eligible">
                    <div class="decision-icon">&#10003;</div>
                    <div class="decision-text">ELIGIBLE FOR LOAN</div>
                    <div class="decision-detail">
                        <c:out value="${loanApplication.decisionReason}"/>
                    </div>
                </div>
                </c:if>

                <!-- ===== NOT ELIGIBLE ===== -->
                <c:if test="${loanApplication.decision == 'NOT_ELIGIBLE'}">
                <div class="decision-not-eligible">
                    <div class="decision-icon">&#10007;</div>
                    <div class="decision-text">NOT ELIGIBLE</div>
                    <div class="reason-label">REASON:</div>
                    <div class="reason-text">
                        <c:out value="${loanApplication.decisionReason}"/>
                    </div>
                </div>
                </c:if>

                <!-- ===== REVIEW REQUIRED ===== -->
                <c:if test="${loanApplication.decision == 'REVIEW_REQUIRED'}">
                <div class="decision-review">
                    <div class="decision-icon">&#9888;</div>
                    <div class="decision-text">MORE INFORMATION REQUIRED</div>
                    <div class="review-detail">
                        This application cannot be automatically approved or rejected.<br />
                        Additional verification is required.<br /><br />
                        The application has been sent for human review.<br /><br />
                        <em>Reason: <c:out value="${loanApplication.decisionReason}"/></em>
                    </div>
                </div>
                </c:if>

            </div>

            <!-- Application Reference Footer -->
            <div class="app-reference">
                Saved SQL Record ID: <strong><c:out value="${loanApplication.id}"/></strong>
                &nbsp;|&nbsp;
                Reference: <strong><c:out value="${loanApplication.applicationReference}"/></strong>
                &nbsp;|&nbsp;
                <c:out value="${pageContext.request.serverName}"/>
                &nbsp;|&nbsp;
                Decision recorded in system.
            </div>

        </div><!-- /result-box -->

        <!-- Action Buttons -->
        <div class="button-row">
            <a href="<c:url value='/'/>" class="btn-secondary">&#171; New Application</a>
        </div>

    </div><!-- /section-content -->

</div><!-- /main-content -->
</div><!-- /page-wrapper -->

<!-- FOOTER -->
<div id="footer">
    NBFC Loan Eligibility System &nbsp;|&nbsp; Version 1.0.0 &nbsp;|&nbsp;
    &copy; 2010 National Banking &amp; Finance Corporation &nbsp;|&nbsp;
    All Rights Reserved
</div>

</body>
</html>

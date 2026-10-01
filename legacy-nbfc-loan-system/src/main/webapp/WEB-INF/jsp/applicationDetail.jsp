<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Application Detail - NBFC</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" />
</head>
<body>

<div id="header">
    <div id="header-inner">
        <h1>NBFC LOAN ELIGIBILITY SYSTEM - DEMO</h1>
        <div class="subtitle">Demo Page &nbsp;|&nbsp; National Banking &amp; Finance Corporation &nbsp;|&nbsp; Application Detail View</div>
    </div>
</div>

<div id="nav-bar">
    <a href="<c:url value='/'/>">Home</a>
</div>

<div id="page-wrapper">
<div id="main-content">

    <div class="section-title">DEMO SQL RECORD DETAIL - ID: ${loanApplication.id}</div>

    <div class="section-content">

        <table class="form-table">
            <tr>
                <td colspan="2" style="padding-bottom:8px;">
                    <strong>Reference:</strong> ${loanApplication.applicationReference}
                    &nbsp;&nbsp;|&nbsp;&nbsp;
                    <strong>Submitted:</strong> ${loanApplication.createdAt}
                </td>
            </tr>
            <tr><td colspan="2"><hr style="border:none;border-top:1px solid #ddd;"/></td></tr>

            <tr>
                <td class="label-col">Customer Name:</td>
                <td>${loanApplication.customerName}</td>
            </tr>
            <tr>
                <td class="label-col">Age:</td>
                <td>${loanApplication.age} years</td>
            </tr>
            <tr>
                <td class="label-col">Employment Type:</td>
                <td>
                    <c:choose>
                        <c:when test="${loanApplication.employmentType == 'FULL_TIME'}">Full Time</c:when>
                        <c:when test="${loanApplication.employmentType == 'PART_TIME'}">Part Time</c:when>
                        <c:when test="${loanApplication.employmentType == 'SELF_EMPLOYED'}">Self Employed</c:when>
                        <c:when test="${loanApplication.employmentType == 'CONTRACT'}">Contract</c:when>
                        <c:when test="${loanApplication.employmentType == 'UNEMPLOYED'}">Unemployed</c:when>
                        <c:otherwise>${loanApplication.employmentType}</c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td class="label-col">Monthly Income:</td>
                <td>&#8377;<fmt:formatNumber value="${loanApplication.monthlyIncome}" pattern="#,##,###"/></td>
            </tr>
            <tr>
                <td class="label-col">Credit Score:</td>
                <td><strong>${loanApplication.creditScore}</strong></td>
            </tr>
            <tr>
                <td class="label-col">Existing EMI:</td>
                <td>&#8377;<fmt:formatNumber value="${loanApplication.existingEmi}" pattern="#,##,###"/> / month</td>
            </tr>
            <tr>
                <td class="label-col">Loan Amount Requested:</td>
                <td><strong>&#8377;<fmt:formatNumber value="${loanApplication.loanAmount}" pattern="#,##,###"/></strong></td>
            </tr>
            <tr>
                <td class="label-col">Loan Tenure:</td>
                <td>${loanApplication.loanTenure} months</td>
            </tr>

            <tr><td colspan="2"><hr style="border:none;border-top:1px solid #ddd; margin:10px 0;"/></td></tr>

            <tr>
                <td class="label-col"><strong>Decision:</strong></td>
                <td>
                    <c:choose>
                        <c:when test="${loanApplication.decision == 'ELIGIBLE'}">
                            <span class="status-eligible" style="font-size:14px;">&#10003; ELIGIBLE FOR LOAN</span>
                        </c:when>
                        <c:when test="${loanApplication.decision == 'NOT_ELIGIBLE'}">
                            <span class="status-rejected" style="font-size:14px;">&#10007; NOT ELIGIBLE</span>
                        </c:when>
                        <c:when test="${loanApplication.decision == 'REVIEW_REQUIRED'}">
                            <span class="status-review" style="font-size:14px;">&#9888; MORE INFORMATION REQUIRED</span>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td class="label-col">Decision Reason:</td>
                <td><em>${loanApplication.decisionReason}</em></td>
            </tr>
        </table>

        <div class="button-row">
            <a href="<c:url value='/applications'/>" class="btn-secondary">&#171; Back to List</a>
            &nbsp;&nbsp;
            <a href="<c:url value='/'/>" class="btn-secondary">New Application</a>
        </div>

    </div>

</div>
</div>

<div id="footer">
    NBFC Loan Eligibility System &nbsp;|&nbsp; Version 1.0.0 &nbsp;|&nbsp;
    &copy; 2010 National Banking &amp; Finance Corporation
</div>

</body>
</html>

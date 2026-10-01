<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>All Applications - NBFC Loan System</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" />
</head>
<body>

<div id="header">
    <div id="header-inner">
        <h1>NBFC LOAN ELIGIBILITY SYSTEM - DEMO</h1>
        <div class="subtitle">Demo Page &nbsp;|&nbsp; National Banking &amp; Finance Corporation &nbsp;|&nbsp; Internal Operations Portal</div>
    </div>
</div>

<div id="nav-bar">
    <a href="<c:url value='/'/>">Home</a>
    <a href="<c:url value='/applications'/>">View All Applications</a>
</div>

<div id="page-wrapper">
<div id="main-content">

    <div class="section-title">DEMO SQL DATABASE APPLICATIONS</div>

    <div class="section-content">

        <div class="info-box">
            <strong>Total Applications Saved in SQL Database:</strong> ${totalCount}
        </div>

        <c:choose>
            <c:when test="${empty applicationList}">
                <div style="padding:20px; text-align:center; color:#666666;">
                    No loan applications have been submitted yet.
                    <br /><br />
                    <a href="<c:url value='/'/>">Submit the first application</a>
                </div>
            </c:when>
            <c:otherwise>

                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Reference</th>
                            <th>Customer Name</th>
                            <th>Age</th>
                            <th>Income (&#8377;)</th>
                            <th>Credit Score</th>
                            <th>Loan Amt (&#8377;)</th>
                            <th>Decision</th>
                            <th>Date</th>
                            <th>Detail</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="app" items="${applicationList}">
                        <tr>
                            <td>${app.id}</td>
                            <td class="small-text">${app.applicationReference}</td>
                            <td>${app.customerName}</td>
                            <td>${app.age}</td>
                            <td><fmt:formatNumber value="${app.monthlyIncome}" pattern="#,##,###"/></td>
                            <td>${app.creditScore}</td>
                            <td><fmt:formatNumber value="${app.loanAmount}" pattern="#,##,###"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${app.decision == 'ELIGIBLE'}">
                                        <span class="status-eligible">&#10003; ELIGIBLE</span>
                                    </c:when>
                                    <c:when test="${app.decision == 'NOT_ELIGIBLE'}">
                                        <span class="status-rejected">&#10007; NOT ELIGIBLE</span>
                                    </c:when>
                                    <c:when test="${app.decision == 'REVIEW_REQUIRED'}">
                                        <span class="status-review">&#9888; REVIEW</span>
                                    </c:when>
                                    <c:otherwise>
                                        ${app.decision}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="small-text">${app.createdAt}</td>
                            <td>
                                <a href="<c:url value='/application/detail'/>?id=${app.id}">View</a>
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>

            </c:otherwise>
        </c:choose>

        <div class="button-row">
            <a href="<c:url value='/'/>" class="btn-secondary">&#171; New Application</a>
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

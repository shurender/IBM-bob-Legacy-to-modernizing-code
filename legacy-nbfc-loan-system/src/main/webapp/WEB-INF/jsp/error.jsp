<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>System Error - NBFC</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" />
</head>
<body>

<div id="header">
    <div id="header-inner">
        <h1>NBFC LOAN ELIGIBILITY SYSTEM - DEMO</h1>
        <div class="subtitle">National Banking &amp; Finance Corporation</div>
    </div>
</div>
<div id="nav-bar">
    <a href="<c:url value='/'/>">Home</a>
</div>

<div id="page-wrapper">
<div id="main-content">
    <div class="section-title">SYSTEM ERROR</div>
    <div class="section-content">
        <div class="error-box">
            <div class="error-title">An error has occurred</div>
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <p style="margin-top:8px;">${errorMessage}</p>
                </c:when>
                <c:otherwise>
                    <p style="margin-top:8px;">An unexpected error occurred. Please try again or contact the system administrator.</p>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="button-row">
            <a href="<c:url value='/'/>" class="btn-secondary">&#171; Return to Home</a>
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

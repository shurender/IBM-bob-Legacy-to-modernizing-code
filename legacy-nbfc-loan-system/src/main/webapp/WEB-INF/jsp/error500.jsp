<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Server Error - NBFC</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>" />
</head>
<body>
<div id="header">
    <div id="header-inner">
        <h1>NBFC LOAN ELIGIBILITY SYSTEM - DEMO</h1>
    </div>
</div>
<div id="page-wrapper">
<div id="main-content">
    <div class="section-title">500 - INTERNAL SERVER ERROR</div>
    <div class="section-content">
        <div class="error-box">
            <div class="error-title">An internal server error has occurred.</div>
            <p style="margin-top:8px;">The server encountered an unexpected condition. Please contact the system administrator.</p>
        </div>
        <div class="button-row">
            <a href="<c:url value='/'/>" class="btn-secondary">&#171; Return to Home</a>
        </div>
    </div>
</div>
</div>
<div id="footer">NBFC Loan Eligibility System &nbsp;|&nbsp; Version 1.0.0</div>
</body>
</html>

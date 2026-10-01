<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <title>NBFC Loan Eligibility System</title>
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

<!-- PAGE WRAPPER -->
<div id="page-wrapper">
<div id="main-content">

    <!-- Section Title -->
    <div class="section-title">DEMO LOAN ELIGIBILITY APPLICATION FORM</div>

    <div class="section-content">

        <!-- Validation Errors -->
        <c:if test="${not empty validationResult and validationResult.hasErrors()}">
        <div class="error-box">
            <div class="error-title">Please correct the following errors:</div>
            <ul>
                <c:forEach var="err" items="${validationResult.errors}">
                    <li>${err}</li>
                </c:forEach>
            </ul>
        </div>
        </c:if>

        <!-- Info Box -->
        <div class="info-box">
            <strong>Instructions:</strong> Please fill in all required fields marked with
            <span style="color:#cc0000;">*</span> and click <strong>CHECK ELIGIBILITY</strong>
            to receive an instant eligibility assessment.
        </div>

        <!-- Application Form -->
        <form method="POST" action="<c:url value='/loan'/>" onsubmit="return validateForm();">

            <table class="form-table">

                <!-- SECTION: Customer Information -->
                <tr>
                    <td colspan="3" style="padding-top:15px; padding-bottom:5px;">
                        <span style="font-weight:bold; color:#003399; font-size:13px;">
                            &#9658; Customer Information
                        </span>
                        <hr style="border:none; border-top:1px solid #ccddff; margin-top:4px;" />
                    </td>
                </tr>

                <tr>
                    <td class="label-col">
                        Customer Name <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <input type="text" name="customerName" maxlength="100"
                               value="<c:out value='${f_customerName}'/>" />
                    </td>
                    <td class="small-text">Full name as per ID proof</td>
                </tr>

                <tr>
                    <td class="label-col">
                        Age (Years) <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <input type="number" name="age" min="18" max="120"
                               value="<c:out value='${f_age}'/>" />
                    </td>
                    <td class="small-text">Must be between 21 and 60</td>
                </tr>

                <tr>
                    <td class="label-col">
                        Employment Type <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <select name="employmentType">
                            <option value="">-- Select --</option>
                            <option value="FULL_TIME"
                                <c:if test="${f_employmentType == 'FULL_TIME'}">selected="selected"</c:if>
                            >Full Time</option>
                            <option value="PART_TIME"
                                <c:if test="${f_employmentType == 'PART_TIME'}">selected="selected"</c:if>
                            >Part Time</option>
                            <option value="SELF_EMPLOYED"
                                <c:if test="${f_employmentType == 'SELF_EMPLOYED'}">selected="selected"</c:if>
                            >Self Employed</option>
                            <option value="CONTRACT"
                                <c:if test="${f_employmentType == 'CONTRACT'}">selected="selected"</c:if>
                            >Contract</option>
                            <option value="UNEMPLOYED"
                                <c:if test="${f_employmentType == 'UNEMPLOYED'}">selected="selected"</c:if>
                            >Unemployed</option>
                        </select>
                    </td>
                    <td class="small-text">&nbsp;</td>
                </tr>

                <!-- SECTION: Financial Information -->
                <tr>
                    <td colspan="3" style="padding-top:15px; padding-bottom:5px;">
                        <span style="font-weight:bold; color:#003399; font-size:13px;">
                            &#9658; Financial Information
                        </span>
                        <hr style="border:none; border-top:1px solid #ccddff; margin-top:4px;" />
                    </td>
                </tr>

                <tr>
                    <td class="label-col">
                        Monthly Income (&#8377;) <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <input type="number" name="monthlyIncome" min="0" step="1000"
                               value="<c:out value='${f_monthlyIncome}'/>" />
                    </td>
                    <td class="small-text">Gross monthly income in INR</td>
                </tr>

                <tr>
                    <td class="label-col">
                        Credit Score <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <input type="number" name="creditScore" min="300" max="900"
                               value="<c:out value='${f_creditScore}'/>" />
                    </td>
                    <td class="small-text">CIBIL score (300 - 900)</td>
                </tr>

                <tr>
                    <td class="label-col">
                        Existing EMI / Month (&#8377;) <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <input type="number" name="existingEmi" min="0" step="500"
                               value="<c:out value='${f_existingEmi}'/>" />
                    </td>
                    <td class="small-text">Enter 0 if no existing loans</td>
                </tr>

                <!-- SECTION: Loan Details -->
                <tr>
                    <td colspan="3" style="padding-top:15px; padding-bottom:5px;">
                        <span style="font-weight:bold; color:#003399; font-size:13px;">
                            &#9658; Loan Details
                        </span>
                        <hr style="border:none; border-top:1px solid #ccddff; margin-top:4px;" />
                    </td>
                </tr>

                <tr>
                    <td class="label-col">
                        Requested Loan Amount (&#8377;) <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <input type="number" name="loanAmount" min="1" step="1"
                               value="<c:out value='${f_loanAmount}'/>" />
                    </td>
                    <td class="small-text">Amount requested in INR</td>
                </tr>

                <tr>
                    <td class="label-col">
                        Loan Tenure (Months) <span class="required">*</span>
                    </td>
                    <td class="field-col">
                        <select name="loanTenure">
                            <option value="">-- Select Tenure --</option>
                            <option value="12"
                                <c:if test="${f_loanTenure == '12'}">selected="selected"</c:if>
                            >12 months (1 year)</option>
                            <option value="24"
                                <c:if test="${f_loanTenure == '24'}">selected="selected"</c:if>
                            >24 months (2 years)</option>
                            <option value="36"
                                <c:if test="${f_loanTenure == '36'}">selected="selected"</c:if>
                            >36 months (3 years)</option>
                            <option value="48"
                                <c:if test="${f_loanTenure == '48'}">selected="selected"</c:if>
                            >48 months (4 years)</option>
                            <option value="60"
                                <c:if test="${f_loanTenure == '60'}">selected="selected"</c:if>
                            >60 months (5 years)</option>
                            <option value="84"
                                <c:if test="${f_loanTenure == '84'}">selected="selected"</c:if>
                            >84 months (7 years)</option>
                            <option value="120"
                                <c:if test="${f_loanTenure == '120'}">selected="selected"</c:if>
                            >120 months (10 years)</option>
                            <option value="180"
                                <c:if test="${f_loanTenure == '180'}">selected="selected"</c:if>
                            >180 months (15 years)</option>
                            <option value="240"
                                <c:if test="${f_loanTenure == '240'}">selected="selected"</c:if>
                            >240 months (20 years)</option>
                        </select>
                    </td>
                    <td class="small-text">&nbsp;</td>
                </tr>

            </table>

            <!-- Submit Button -->
            <div class="button-row">
                <input type="submit" value="CHECK ELIGIBILITY" class="btn-submit" />
            </div>

        </form>


    </div><!-- /section-content -->

</div><!-- /main-content -->
</div><!-- /page-wrapper -->

<!-- FOOTER -->
<div id="footer">
    NBFC Loan Eligibility System &nbsp;|&nbsp; Version 1.0.0 &nbsp;|&nbsp;
    &copy; 2010 National Banking &amp; Finance Corporation &nbsp;|&nbsp;
    All Rights Reserved
</div>

<script type="text/javascript">
// Basic client-side validation
function validateForm() {
    var name = document.getElementsByName("customerName")[0].value;
    if (!name || name.trim().length === 0) {
        alert("Please enter the Customer Name.");
        return false;
    }
    var age = document.getElementsByName("age")[0].value;
    if (!age || isNaN(age) || parseInt(age) < 1) {
        alert("Please enter a valid Age.");
        return false;
    }
    var income = document.getElementsByName("monthlyIncome")[0].value;
    if (!income || isNaN(income) || parseFloat(income) < 0) {
        alert("Please enter a valid Monthly Income.");
        return false;
    }
    var emp = document.getElementsByName("employmentType")[0].value;
    if (!emp || emp.trim().length === 0) {
        alert("Please select an Employment Type.");
        return false;
    }
    var cs = document.getElementsByName("creditScore")[0].value;
    if (!cs || isNaN(cs) || parseInt(cs) < 300 || parseInt(cs) > 900) {
        alert("Please enter a valid Credit Score (300-900).");
        return false;
    }
    var emi = document.getElementsByName("existingEmi")[0].value;
    if (emi === "" || isNaN(emi) || parseFloat(emi) < 0) {
        alert("Please enter the Existing EMI amount (enter 0 if none).");
        return false;
    }
    var la = document.getElementsByName("loanAmount")[0].value;
    if (!la || isNaN(la) || parseFloat(la) <= 0) {
        alert("Please enter a valid Loan Amount.");
        return false;
    }
    var lt = document.getElementsByName("loanTenure")[0].value;
    if (!lt || lt.trim().length === 0) {
        alert("Please select a Loan Tenure.");
        return false;
    }
    return true;
}
</script>

</body>
</html>

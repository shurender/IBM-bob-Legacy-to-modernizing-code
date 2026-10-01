package loan

import "strconv"

type InputValidator struct{}

func (InputValidator) Validate(input ApplicationInput) ValidationResult {
	result := NewValidationResult()

	validateCustomerName(input.CustomerName, &result)
	validateAge(input.Age, &result)
	validateMonthlyIncome(input.MonthlyIncome, &result)
	validateEmploymentType(input.EmploymentType, &result)
	validateCreditScore(input.CreditScore, &result)
	validateExistingEmi(input.ExistingEmi, &result)
	validateLoanAmount(input.LoanAmount, &result)
	validateLoanTenure(input.LoanTenure, &result)

	return result
}

func validateCustomerName(value string, result *ValidationResult) {
	name := trimSpace(value)
	if name == "" {
		result.AddError("Customer Name is required.")
		return
	}
	if len(name) < 2 {
		result.AddError("Customer Name must be at least 2 characters.")
	}
	if len(name) > 100 {
		result.AddError("Customer Name must not exceed 100 characters.")
	}
}

func validateAge(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Age is required.")
		return
	}
	age, err := strconv.Atoi(text)
	if err != nil {
		result.AddError("Age must be a valid whole number.")
		return
	}
	if age < 1 || age > 120 {
		result.AddError("Age must be a valid number between 1 and 120.")
	}
}

func validateMonthlyIncome(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Monthly Income is required.")
		return
	}
	income, err := strconv.ParseFloat(text, 64)
	if err != nil {
		result.AddError("Monthly Income must be a valid number.")
		return
	}
	if income < 0 {
		result.AddError("Monthly Income cannot be negative.")
	}
	if income > 10000000 {
		result.AddError("Monthly Income value seems unreasonably high. Please verify.")
	}
}

func validateEmploymentType(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Employment Type is required.")
		return
	}
	for _, valid := range []string{EmploymentFullTime, EmploymentPartTime, EmploymentSelfEmployed, EmploymentContract, EmploymentUnemployed} {
		if text == valid {
			return
		}
	}
	result.AddError("Please select a valid Employment Type.")
}

func validateCreditScore(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Credit Score is required.")
		return
	}
	score, err := strconv.Atoi(text)
	if err != nil {
		result.AddError("Credit Score must be a valid whole number.")
		return
	}
	if score < 300 || score > 900 {
		result.AddError("Credit Score must be between 300 and 900.")
	}
}

func validateExistingEmi(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Existing EMI is required. Enter 0 if none.")
		return
	}
	emi, err := strconv.ParseFloat(text, 64)
	if err != nil {
		result.AddError("Existing EMI must be a valid number.")
		return
	}
	if emi < 0 {
		result.AddError("Existing EMI cannot be negative.")
	}
}

func validateLoanAmount(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Requested Loan Amount is required.")
		return
	}
	amount, err := strconv.ParseFloat(text, 64)
	if err != nil {
		result.AddError("Requested Loan Amount must be a valid number.")
		return
	}
	if amount <= 0 {
		result.AddError("Requested Loan Amount must be greater than zero.")
	}
	if amount > 100000000 {
		result.AddError("Requested Loan Amount exceeds the maximum allowed limit.")
	}
}

func validateLoanTenure(value string, result *ValidationResult) {
	text := trimSpace(value)
	if text == "" {
		result.AddError("Loan Tenure is required.")
		return
	}
	tenure, err := strconv.Atoi(text)
	if err != nil {
		result.AddError("Loan Tenure must be a valid whole number of months.")
		return
	}
	if tenure < 6 {
		result.AddError("Loan Tenure must be at least 6 months.")
	}
	if tenure > 360 {
		result.AddError("Loan Tenure cannot exceed 360 months (30 years).")
	}
}

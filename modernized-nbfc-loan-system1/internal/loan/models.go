package loan

import "time"

type ApplicationInput struct {
	CustomerName   string `json:"customerName"`
	Age            string `json:"age"`
	MonthlyIncome  string `json:"monthlyIncome"`
	EmploymentType string `json:"employmentType"`
	CreditScore    string `json:"creditScore"`
	ExistingEmi    string `json:"existingEmi"`
	LoanAmount     string `json:"loanAmount"`
	LoanTenure     string `json:"loanTenure"`
}

type LoanApplication struct {
	ID                   int64     `json:"id"`
	ApplicationReference string    `json:"applicationReference"`
	CustomerName         string    `json:"customerName"`
	Age                  int       `json:"age"`
	MonthlyIncome        float64   `json:"monthlyIncome"`
	EmploymentType       string    `json:"employmentType"`
	CreditScore          int       `json:"creditScore"`
	ExistingEmi          float64   `json:"existingEmi"`
	LoanAmount           float64   `json:"loanAmount"`
	LoanTenure           int       `json:"loanTenure"`
	Decision             string    `json:"decision"`
	DecisionCode         string    `json:"decisionCode"`
	DecisionReason       string    `json:"decisionReason"`
	CreatedAt            time.Time `json:"createdAt"`
}

type ValidationResult struct {
	Valid    bool     `json:"valid"`
	Errors   []string `json:"errors"`
	Warnings []string `json:"warnings"`
}

func NewValidationResult() ValidationResult {
	return ValidationResult{Valid: true, Errors: []string{}, Warnings: []string{}}
}

func (r *ValidationResult) AddError(message string) {
	if trimSpace(message) != "" {
		r.Errors = append(r.Errors, message)
		r.Valid = false
	}
}

func (r ValidationResult) IsValid() bool {
	return r.Valid && len(r.Errors) == 0
}

type CreditAssessment struct {
	CreditScore         int    `json:"creditScore"`
	CreditCategory      string `json:"creditCategory"`
	AssessmentStatus    string `json:"assessmentStatus"`
	AssessmentRemark    string `json:"assessmentRemark"`
	MinRequiredScore    int    `json:"minRequiredScore"`
	BorderlineThreshold int    `json:"borderlineThreshold"`
}

type IncomeAssessment struct {
	MonthlyIncome       float64 `json:"monthlyIncome"`
	ExistingEmi         float64 `json:"existingEmi"`
	RequestedLoanAmount float64 `json:"requestedLoanAmount"`
	LoanTenureMonths    int     `json:"loanTenureMonths"`
	EmploymentType      string  `json:"employmentType"`
	EstimatedNewEmi     float64 `json:"estimatedNewEmi"`
	TotalEmiAfterLoan   float64 `json:"totalEmiAfterLoan"`
	DisposableIncome    float64 `json:"disposableIncome"`
	DebtToIncomeRatio   float64 `json:"debtToIncomeRatio"`
	LoanToIncomeRatio   float64 `json:"loanToIncomeRatio"`
	AssessmentStatus    string  `json:"assessmentStatus"`
	AssessmentRemark    string  `json:"assessmentRemark"`
}

type EligibilityDecision struct {
	Decision              string   `json:"decision"`
	DecisionCode          string   `json:"decisionCode"`
	PrimaryReason         string   `json:"primaryReason"`
	FailureReasons        []string `json:"failureReasons"`
	ReviewReasons         []string `json:"reviewReasons"`
	PassedChecks          []string `json:"passedChecks"`
	CreditCheckPassed     bool     `json:"creditCheckPassed"`
	IncomeCheckPassed     bool     `json:"incomeCheckPassed"`
	AgeCheckPassed        bool     `json:"ageCheckPassed"`
	EmiCheckPassed        bool     `json:"emiCheckPassed"`
	LoanAmountCheckPassed bool     `json:"loanAmountCheckPassed"`
	RiskScore             int      `json:"riskScore"`
}

func NewEligibilityDecision() EligibilityDecision {
	return EligibilityDecision{
		FailureReasons: []string{},
		ReviewReasons:  []string{},
		PassedChecks:   []string{},
	}
}

func (d *EligibilityDecision) AddFailureReason(reason string) {
	if trimSpace(reason) != "" {
		d.FailureReasons = append(d.FailureReasons, reason)
	}
}

func (d *EligibilityDecision) AddReviewReason(reason string) {
	if trimSpace(reason) != "" {
		d.ReviewReasons = append(d.ReviewReasons, reason)
	}
}

func (d *EligibilityDecision) AddPassedCheck(check string) {
	if trimSpace(check) != "" {
		d.PassedChecks = append(d.PassedChecks, check)
	}
}

package loan

const (
	CreditScoreMinEligible    = 700
	CreditScoreMaxRejected    = 649
	CreditScoreBorderlineLow  = 650
	CreditScoreBorderlineHigh = 699

	AgeMinimum = 21
	AgeMaximum = 60

	MinMonthlyIncome        = 25000.0
	BorderlineMonthlyIncome = 30000.0

	MaxDebtToIncomeRatio   = 0.50
	BorderlineDebtToIncome = 0.40

	MaxLoanToAnnualIncome  = 10.0
	BorderlineLoanToIncome = 8.0

	DefaultInterestRate = 12.0

	DecisionEligible       = "ELIGIBLE"
	DecisionNotEligible    = "NOT_ELIGIBLE"
	DecisionReviewRequired = "REVIEW_REQUIRED"

	CodeApproved     = "APPROVED"
	CodeRejected     = "REJECTED"
	CodeManualReview = "MANUAL_REVIEW"

	EmploymentFullTime     = "FULL_TIME"
	EmploymentPartTime     = "PART_TIME"
	EmploymentSelfEmployed = "SELF_EMPLOYED"
	EmploymentContract     = "CONTRACT"
	EmploymentUnemployed   = "UNEMPLOYED"

	StatusPass   = "PASS"
	StatusFail   = "FAIL"
	StatusReview = "REVIEW"
)

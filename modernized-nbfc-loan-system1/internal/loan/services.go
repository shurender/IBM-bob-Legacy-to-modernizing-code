package loan

import (
	"fmt"
	"math"
	"strings"
	"sync/atomic"
	"time"
)

type AgeAssessmentService struct{}

func (AgeAssessmentService) Assess(age int) string {
	if age < AgeMinimum {
		return StatusFail
	}
	if age > AgeMaximum {
		return StatusFail
	}
	return StatusPass
}

func (AgeAssessmentService) GetRemark(age int) string {
	if age < AgeMinimum {
		return fmt.Sprintf("Applicant age %d is below the minimum required age of %d.", age, AgeMinimum)
	}
	if age > AgeMaximum {
		return fmt.Sprintf("Applicant age %d exceeds the maximum allowed age of %d.", age, AgeMaximum)
	}
	return fmt.Sprintf("Age %d is within the eligible range (%d-%d).", age, AgeMinimum, AgeMaximum)
}

type CreditAssessmentService struct{}

func (CreditAssessmentService) Assess(creditScore int) CreditAssessment {
	assessment := CreditAssessment{
		CreditScore:         creditScore,
		MinRequiredScore:    CreditScoreMinEligible,
		BorderlineThreshold: CreditScoreBorderlineLow,
		CreditCategory:      creditCategory(creditScore),
	}

	if creditScore >= CreditScoreMinEligible {
		assessment.AssessmentStatus = StatusPass
		assessment.AssessmentRemark = fmt.Sprintf("Credit score of %d meets the minimum requirement of %d.", creditScore, CreditScoreMinEligible)
	} else if creditScore >= CreditScoreBorderlineLow && creditScore <= CreditScoreBorderlineHigh {
		assessment.AssessmentStatus = StatusReview
		assessment.AssessmentRemark = fmt.Sprintf("Credit score of %d is in the borderline range (%d-%d). Manual review required.", creditScore, CreditScoreBorderlineLow, CreditScoreBorderlineHigh)
	} else {
		assessment.AssessmentStatus = StatusFail
		assessment.AssessmentRemark = fmt.Sprintf("Credit score of %d does not meet the minimum eligibility requirement of %d.", creditScore, CreditScoreMinEligible)
	}

	return assessment
}

func creditCategory(score int) string {
	if score >= 750 {
		return "EXCELLENT"
	}
	if score >= 700 {
		return "GOOD"
	}
	if score >= 650 {
		return "FAIR"
	}
	if score >= 600 {
		return "POOR"
	}
	return "VERY_POOR"
}

type IncomeAssessmentService struct{}

func (s IncomeAssessmentService) Assess(monthlyIncome, existingEmi, loanAmount float64, loanTenure int, employmentType string) IncomeAssessment {
	assessment := IncomeAssessment{
		MonthlyIncome:       monthlyIncome,
		ExistingEmi:         existingEmi,
		RequestedLoanAmount: loanAmount,
		LoanTenureMonths:    loanTenure,
		EmploymentType:      employmentType,
	}

	estimatedNewEmi := s.CalculateEstimatedEmi(loanAmount, loanTenure, DefaultInterestRate)
	assessment.EstimatedNewEmi = estimatedNewEmi

	totalEmi := existingEmi + estimatedNewEmi
	assessment.TotalEmiAfterLoan = totalEmi
	assessment.DisposableIncome = monthlyIncome - totalEmi

	dtiRatio := 1.0
	if monthlyIncome > 0 {
		dtiRatio = totalEmi / monthlyIncome
	}
	assessment.DebtToIncomeRatio = dtiRatio

	annualIncome := monthlyIncome * 12
	ltiRatio := 999.0
	if annualIncome > 0 {
		ltiRatio = loanAmount / annualIncome
	}
	assessment.LoanToIncomeRatio = ltiRatio

	determineIncomeStatus(&assessment, monthlyIncome, dtiRatio, ltiRatio)
	return assessment
}

func determineIncomeStatus(assessment *IncomeAssessment, monthlyIncome, dtiRatio, ltiRatio float64) {
	var failReasons strings.Builder
	var reviewReasons strings.Builder

	if monthlyIncome < MinMonthlyIncome {
		failReasons.WriteString(fmt.Sprintf("Monthly income of %s is below the minimum requirement of %s. ", javaDouble(monthlyIncome), javaDouble(MinMonthlyIncome)))
	} else if monthlyIncome < BorderlineMonthlyIncome {
		reviewReasons.WriteString("Monthly income is close to the minimum threshold. ")
	}

	if dtiRatio >= MaxDebtToIncomeRatio {
		failReasons.WriteString(fmt.Sprintf("Total debt-to-income ratio of %.1f%% exceeds the maximum allowed 50%%. ", dtiRatio*100))
	} else if dtiRatio >= BorderlineDebtToIncome {
		reviewReasons.WriteString(fmt.Sprintf("Debt-to-income ratio of %.1f%% is high and requires review. ", dtiRatio*100))
	}

	if ltiRatio > MaxLoanToAnnualIncome {
		failReasons.WriteString("Requested loan amount exceeds 10 times annual income. ")
	} else if ltiRatio > BorderlineLoanToIncome {
		reviewReasons.WriteString("Loan amount is high relative to annual income. ")
	}

	if failReasons.Len() > 0 {
		assessment.AssessmentStatus = StatusFail
		assessment.AssessmentRemark = strings.TrimSpace(failReasons.String())
	} else if reviewReasons.Len() > 0 {
		assessment.AssessmentStatus = StatusReview
		assessment.AssessmentRemark = strings.TrimSpace(reviewReasons.String())
	} else {
		assessment.AssessmentStatus = StatusPass
		assessment.AssessmentRemark = "Income profile meets eligibility criteria."
	}
}

func (IncomeAssessmentService) CalculateEstimatedEmi(principal float64, tenureMonths int, annualRate float64) float64 {
	if principal <= 0 || tenureMonths <= 0 {
		return 0
	}
	monthlyRate := annualRate / (12 * 100)
	if monthlyRate == 0 {
		return principal / float64(tenureMonths)
	}
	numerator := principal * monthlyRate * math.Pow(1+monthlyRate, float64(tenureMonths))
	denominator := math.Pow(1+monthlyRate, float64(tenureMonths)) - 1
	if denominator > 0 {
		return numerator / denominator
	}
	return 0
}

type EligibilityService struct {
	credit CreditAssessmentService
	income IncomeAssessmentService
	age    AgeAssessmentService
}

func NewEligibilityService() EligibilityService {
	return EligibilityService{
		credit: CreditAssessmentService{},
		income: IncomeAssessmentService{},
		age:    AgeAssessmentService{},
	}
}

func (s EligibilityService) Evaluate(application LoanApplication) EligibilityDecision {
	decision := NewEligibilityDecision()

	ageStatus := s.age.Assess(application.Age)
	ageRemark := s.age.GetRemark(application.Age)
	if ageStatus == StatusFail {
		decision.AddFailureReason(ageRemark)
		decision.AgeCheckPassed = false
	} else {
		decision.AgeCheckPassed = true
		decision.AddPassedCheck("Age eligibility: " + ageRemark)
	}

	creditAssessment := s.credit.Assess(application.CreditScore)
	if creditAssessment.AssessmentStatus == StatusFail {
		decision.AddFailureReason(creditAssessment.AssessmentRemark)
		decision.CreditCheckPassed = false
	} else if creditAssessment.AssessmentStatus == StatusReview {
		decision.AddReviewReason(creditAssessment.AssessmentRemark)
		decision.CreditCheckPassed = false
	} else {
		decision.CreditCheckPassed = true
		decision.AddPassedCheck("Credit score: " + creditAssessment.AssessmentRemark)
	}

	incomeAssessment := s.income.Assess(
		application.MonthlyIncome,
		application.ExistingEmi,
		application.LoanAmount,
		application.LoanTenure,
		application.EmploymentType,
	)
	if incomeAssessment.AssessmentStatus == StatusFail {
		decision.AddFailureReason(incomeAssessment.AssessmentRemark)
		decision.IncomeCheckPassed = false
		decision.EmiCheckPassed = false
		decision.LoanAmountCheckPassed = false
	} else if incomeAssessment.AssessmentStatus == StatusReview {
		decision.AddReviewReason(incomeAssessment.AssessmentRemark)
		decision.IncomeCheckPassed = false
		decision.EmiCheckPassed = true
		decision.LoanAmountCheckPassed = true
	} else {
		decision.IncomeCheckPassed = true
		decision.EmiCheckPassed = true
		decision.LoanAmountCheckPassed = true
		decision.AddPassedCheck("Income assessment: " + incomeAssessment.AssessmentRemark)
	}

	if application.EmploymentType == EmploymentUnemployed {
		decision.AddFailureReason("Applicant is currently unemployed. Employment is required for loan eligibility.")
	}

	determineFinalDecision(&decision)
	return decision
}

func determineFinalDecision(decision *EligibilityDecision) {
	if len(decision.FailureReasons) > 0 {
		decision.Decision = DecisionNotEligible
		decision.DecisionCode = CodeRejected
		decision.PrimaryReason = decision.FailureReasons[0]
		return
	}

	if len(decision.ReviewReasons) > 0 {
		decision.Decision = DecisionReviewRequired
		decision.DecisionCode = CodeManualReview
		decision.PrimaryReason = strings.TrimSpace(strings.Join(decision.ReviewReasons, " "))
		return
	}

	decision.Decision = DecisionEligible
	decision.DecisionCode = CodeApproved
	decision.PrimaryReason = "The customer satisfies the current automated eligibility criteria."
}

type LoanService struct {
	eligibility EligibilityService
	store       ApplicationStore
}

type ApplicationStore interface {
	Save(LoanApplication) (int64, error)
	FindByID(int64) (*LoanApplication, error)
}

func NewLoanService(store ApplicationStore) LoanService {
	return LoanService{eligibility: NewEligibilityService(), store: store}
}

func (s LoanService) ProcessApplication(application LoanApplication) (*LoanApplication, error) {
	application.ApplicationReference = GenerateReference()
	decision := s.eligibility.Evaluate(application)
	application.Decision = decision.Decision
	application.DecisionCode = decision.DecisionCode
	application.DecisionReason = decision.PrimaryReason

	generatedID, err := s.store.Save(application)
	if err != nil {
		return nil, err
	}
	return s.store.FindByID(generatedID)
}

func (s LoanService) CheckEligibilityOnly(application LoanApplication) EligibilityDecision {
	return s.eligibility.Evaluate(application)
}

var refCounter atomic.Int64

func init() {
	refCounter.Store(1000)
}

func GenerateReference() string {
	seq := refCounter.Add(1) - 1
	return fmt.Sprintf("NBFC-%s-%d", time.Now().Format("20060102"), seq)
}

func BuildApplication(input ApplicationInput) LoanApplication {
	return LoanApplication{
		CustomerName:   trimSpace(input.CustomerName),
		Age:            ParseIntSafe(input.Age, 0),
		MonthlyIncome:  ParseDoubleSafe(input.MonthlyIncome, 0),
		EmploymentType: trimSpace(input.EmploymentType),
		CreditScore:    ParseIntSafe(input.CreditScore, 0),
		ExistingEmi:    ParseDoubleSafe(input.ExistingEmi, 0),
		LoanAmount:     ParseDoubleSafe(input.LoanAmount, 0),
		LoanTenure:     ParseIntSafe(input.LoanTenure, 0),
		CreatedAt:      time.Now(),
	}
}

func javaDouble(value float64) string {
	if math.IsNaN(value) {
		return "NaN"
	}
	if math.IsInf(value, 1) {
		return "Infinity"
	}
	if math.IsInf(value, -1) {
		return "-Infinity"
	}
	return strconvFormatFloat(value)
}

func strconvFormatFloat(value float64) string {
	text := fmt.Sprintf("%.15g", value)
	if !strings.ContainsAny(text, ".eE") {
		return text + ".0"
	}
	return text
}

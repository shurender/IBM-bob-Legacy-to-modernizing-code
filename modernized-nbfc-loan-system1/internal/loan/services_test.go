package loan

import (
	"reflect"
	"strings"
	"testing"
)

func TestEligibilityMatchesLegacyJavaScenarios(t *testing.T) {
	service := NewEligibilityService()
	tests := []struct {
		name          string
		application   LoanApplication
		decision      string
		decisionCode  string
		primaryReason string
	}{
		{
			name: "eligible",
			application: LoanApplication{
				CustomerName: "Rahul Sharma", Age: 35, EmploymentType: EmploymentFullTime,
				MonthlyIncome: 70000, CreditScore: 750, ExistingEmi: 5000,
				LoanAmount: 500000, LoanTenure: 60,
			},
			decision:      DecisionEligible,
			decisionCode:  CodeApproved,
			primaryReason: "The customer satisfies the current automated eligibility criteria.",
		},
		{
			name: "not eligible",
			application: LoanApplication{
				CustomerName: "Amit Kumar", Age: 28, EmploymentType: EmploymentFullTime,
				MonthlyIncome: 18000, CreditScore: 520, ExistingEmi: 0,
				LoanAmount: 500000, LoanTenure: 36,
			},
			decision:      DecisionNotEligible,
			decisionCode:  CodeRejected,
			primaryReason: "Credit score of 520 does not meet the minimum eligibility requirement of 700.",
		},
		{
			name: "readme review sample is rejected by actual Java logic",
			application: LoanApplication{
				CustomerName: "Priya Singh", Age: 40, EmploymentType: EmploymentFullTime,
				MonthlyIncome: 28000, CreditScore: 675, ExistingEmi: 8000,
				LoanAmount: 900000, LoanTenure: 120,
			},
			decision:      DecisionNotEligible,
			decisionCode:  CodeRejected,
			primaryReason: "Total debt-to-income ratio of 74.7% exceeds the maximum allowed 50%.",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			decision := service.Evaluate(tt.application)
			if decision.Decision != tt.decision {
				t.Fatalf("decision = %q, want %q", decision.Decision, tt.decision)
			}
			if decision.DecisionCode != tt.decisionCode {
				t.Fatalf("decision code = %q, want %q", decision.DecisionCode, tt.decisionCode)
			}
			if decision.PrimaryReason != tt.primaryReason {
				t.Fatalf("primary reason = %q, want %q", decision.PrimaryReason, tt.primaryReason)
			}
		})
	}
}

func TestValidationMessagesPreserveLegacyOrder(t *testing.T) {
	validator := InputValidator{}
	result := validator.Validate(ApplicationInput{
		CustomerName:   "A",
		Age:            "abc",
		MonthlyIncome:  "-1",
		EmploymentType: "BAD",
		CreditScore:    "901",
		ExistingEmi:    "-5",
		LoanAmount:     "0",
		LoanTenure:     "5",
	})

	want := []string{
		"Customer Name must be at least 2 characters.",
		"Age must be a valid whole number.",
		"Monthly Income cannot be negative.",
		"Please select a valid Employment Type.",
		"Credit Score must be between 300 and 900.",
		"Existing EMI cannot be negative.",
		"Requested Loan Amount must be greater than zero.",
		"Loan Tenure must be at least 6 months.",
	}
	if result.IsValid() {
		t.Fatal("result unexpectedly valid")
	}
	if !reflect.DeepEqual(result.Errors, want) {
		t.Fatalf("errors = %#v, want %#v", result.Errors, want)
	}
}

func TestIncomeAssessmentCombinesFailureReasons(t *testing.T) {
	service := IncomeAssessmentService{}
	assessment := service.Assess(0, 0, 500000, 36, EmploymentFullTime)

	if assessment.AssessmentStatus != StatusFail {
		t.Fatalf("status = %q, want FAIL", assessment.AssessmentStatus)
	}
	for _, part := range []string{
		"Monthly income of 0.0 is below the minimum requirement of 25000.0.",
		"Total debt-to-income ratio of 100.0% exceeds the maximum allowed 50%.",
		"Requested loan amount exceeds 10 times annual income.",
	} {
		if !strings.Contains(assessment.AssessmentRemark, part) {
			t.Fatalf("remark %q missing %q", assessment.AssessmentRemark, part)
		}
	}
}

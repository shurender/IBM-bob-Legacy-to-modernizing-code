package store

import (
	"path/filepath"
	"testing"

	"modernized-nbfc-loan-system/internal/loan"
)

func TestSQLiteStoreSavesAndReadsBackPersistedRecord(t *testing.T) {
	store, err := Open(filepath.Join(t.TempDir(), "nbfc_loan.db"))
	if err != nil {
		t.Fatal(err)
	}
	defer store.Close()

	service := loan.NewLoanService(store)
	saved, err := service.ProcessApplication(loan.LoanApplication{
		CustomerName: "Rahul Sharma", Age: 35, EmploymentType: loan.EmploymentFullTime,
		MonthlyIncome: 70000, CreditScore: 750, ExistingEmi: 5000,
		LoanAmount: 500000, LoanTenure: 60,
	})
	if err != nil {
		t.Fatal(err)
	}
	if saved.ID == 0 {
		t.Fatal("expected generated ID")
	}
	if saved.CreatedAt.IsZero() {
		t.Fatal("expected created_at read from database")
	}
	if saved.Decision != loan.DecisionEligible || saved.DecisionCode != loan.CodeApproved {
		t.Fatalf("decision = %s/%s", saved.Decision, saved.DecisionCode)
	}

	total, err := store.CountAll()
	if err != nil {
		t.Fatal(err)
	}
	if total != 1 {
		t.Fatalf("count = %d, want 1", total)
	}
}

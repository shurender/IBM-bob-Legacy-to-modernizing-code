package store

import (
	"database/sql"
	"errors"
	"fmt"
	"os"
	"path/filepath"
	"time"

	"modernized-nbfc-loan-system/internal/loan"

	_ "modernc.org/sqlite"
)

type SQLiteStore struct {
	db *sql.DB
}

func Open(path string) (*SQLiteStore, error) {
	if path == "" {
		path = filepath.Join("data", "nbfc_loan.db")
	}
	if err := os.MkdirAll(filepath.Dir(path), 0755); err != nil {
		return nil, err
	}
	db, err := sql.Open("sqlite", path)
	if err != nil {
		return nil, err
	}
	db.SetMaxOpenConns(1)

	store := &SQLiteStore{db: db}
	if err := store.InitializeSchema(); err != nil {
		db.Close()
		return nil, err
	}
	return store, nil
}

func (s *SQLiteStore) Close() error {
	return s.db.Close()
}

func (s *SQLiteStore) InitializeSchema() error {
	statements := []string{
		`PRAGMA busy_timeout = 5000`,
		`PRAGMA journal_mode = WAL`,
		`CREATE TABLE IF NOT EXISTS loan_applications (
			id                INTEGER PRIMARY KEY AUTOINCREMENT,
			application_ref   TEXT NOT NULL,
			customer_name     TEXT NOT NULL,
			age               INTEGER NOT NULL,
			monthly_income    REAL NOT NULL,
			employment_type   TEXT NOT NULL,
			credit_score      INTEGER NOT NULL,
			existing_emi      REAL NOT NULL DEFAULT 0,
			loan_amount       REAL NOT NULL,
			loan_tenure       INTEGER NOT NULL,
			decision          TEXT NOT NULL,
			decision_code     TEXT NOT NULL,
			decision_reason   TEXT,
			created_at        DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS eligibility_rules (
			id                INTEGER PRIMARY KEY AUTOINCREMENT,
			rule_name         TEXT NOT NULL,
			rule_description  TEXT,
			min_value         REAL,
			max_value         REAL,
			is_active         INTEGER DEFAULT 1,
			created_at        DATETIME DEFAULT CURRENT_TIMESTAMP
		)`,
	}
	for _, statement := range statements {
		if _, err := s.db.Exec(statement); err != nil {
			return err
		}
	}
	return s.seedEligibilityRules()
}

func (s *SQLiteStore) seedEligibilityRules() error {
	var count int
	if err := s.db.QueryRow(`SELECT COUNT(*) FROM eligibility_rules`).Scan(&count); err != nil {
		return err
	}
	if count != 0 {
		return nil
	}

	rules := []struct {
		name, description string
		min, max          any
	}{
		{"MIN_AGE", "Minimum applicant age in years", 21, nil},
		{"MAX_AGE", "Maximum applicant age in years", nil, 60},
		{"MIN_CREDIT_SCORE", "Minimum credit score required for auto-approval", 700, nil},
		{"BORDERLINE_CREDIT_SCORE", "Credit score range requiring manual review", 650, 699},
		{"REJECT_CREDIT_SCORE", "Credit score below which application is auto-rejected", nil, 649},
		{"MIN_MONTHLY_INCOME", "Minimum monthly income in INR", 25000, nil},
		{"MAX_DEBT_TO_INCOME_RATIO", "Maximum allowed debt-to-income ratio (as decimal)", nil, 0.50},
		{"BORDERLINE_DEBT_TO_INCOME", "Debt-to-income ratio flagging for manual review", 0.40, 0.50},
		{"MAX_LOAN_TO_INCOME_RATIO", "Maximum loan amount as multiple of annual income", nil, 10},
	}

	for _, rule := range rules {
		if _, err := s.db.Exec(
			`INSERT INTO eligibility_rules (rule_name, rule_description, min_value, max_value) VALUES (?, ?, ?, ?)`,
			rule.name, rule.description, rule.min, rule.max,
		); err != nil {
			return err
		}
	}
	return nil
}

func (s *SQLiteStore) Save(application loan.LoanApplication) (int64, error) {
	result, err := s.db.Exec(
		`INSERT INTO loan_applications
		(application_ref, customer_name, age, monthly_income, employment_type,
		 credit_score, existing_emi, loan_amount, loan_tenure,
		 decision, decision_code, decision_reason)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
		application.ApplicationReference,
		application.CustomerName,
		application.Age,
		application.MonthlyIncome,
		application.EmploymentType,
		application.CreditScore,
		application.ExistingEmi,
		application.LoanAmount,
		application.LoanTenure,
		application.Decision,
		application.DecisionCode,
		application.DecisionReason,
	)
	if err != nil {
		return 0, err
	}
	affected, err := result.RowsAffected()
	if err == nil && affected == 0 {
		return 0, errors.New("inserting loan application failed, no rows affected")
	}
	id, err := result.LastInsertId()
	if err != nil {
		return 0, errors.New("inserting loan application failed, no ID obtained")
	}
	return id, nil
}

func (s *SQLiteStore) FindByID(id int64) (*loan.LoanApplication, error) {
	row := s.db.QueryRow(
		`SELECT id, application_ref, customer_name, age, monthly_income, employment_type,
		        credit_score, existing_emi, loan_amount, loan_tenure,
		        decision, decision_code, decision_reason, created_at
		   FROM loan_applications WHERE id = ?`,
		id,
	)
	app, err := scanApplication(row)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, nil
	}
	return app, err
}

func (s *SQLiteStore) FindAll() ([]loan.LoanApplication, error) {
	rows, err := s.db.Query(
		`SELECT id, application_ref, customer_name, age, monthly_income, employment_type,
		        credit_score, existing_emi, loan_amount, loan_tenure,
		        decision, decision_code, decision_reason, created_at
		   FROM loan_applications ORDER BY created_at DESC`,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return scanApplications(rows)
}

func (s *SQLiteStore) FindRecent(limit int) ([]loan.LoanApplication, error) {
	rows, err := s.db.Query(
		`SELECT id, application_ref, customer_name, age, monthly_income, employment_type,
		        credit_score, existing_emi, loan_amount, loan_tenure,
		        decision, decision_code, decision_reason, created_at
		   FROM loan_applications ORDER BY created_at DESC LIMIT ?`,
		limit,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	return scanApplications(rows)
}

func (s *SQLiteStore) CountAll() (int, error) {
	var count int
	err := s.db.QueryRow(`SELECT COUNT(*) FROM loan_applications`).Scan(&count)
	return count, err
}

func (s *SQLiteStore) Rules() ([]map[string]any, error) {
	rows, err := s.db.Query(
		`SELECT id, rule_name, rule_description, min_value, max_value, is_active, created_at
		   FROM eligibility_rules ORDER BY id ASC`,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var rules []map[string]any
	for rows.Next() {
		var id, active int
		var name, description, createdAt string
		var minValue, maxValue sql.NullFloat64
		if err := rows.Scan(&id, &name, &description, &minValue, &maxValue, &active, &createdAt); err != nil {
			return nil, err
		}
		rule := map[string]any{
			"id":              id,
			"ruleName":        name,
			"ruleDescription": description,
			"minValue":        nil,
			"maxValue":        nil,
			"isActive":        active,
			"createdAt":       createdAt,
		}
		if minValue.Valid {
			rule["minValue"] = minValue.Float64
		}
		if maxValue.Valid {
			rule["maxValue"] = maxValue.Float64
		}
		rules = append(rules, rule)
	}
	return rules, rows.Err()
}

type scanner interface {
	Scan(dest ...any) error
}

func scanApplication(row scanner) (*loan.LoanApplication, error) {
	var app loan.LoanApplication
	var createdAt string
	if err := row.Scan(
		&app.ID,
		&app.ApplicationReference,
		&app.CustomerName,
		&app.Age,
		&app.MonthlyIncome,
		&app.EmploymentType,
		&app.CreditScore,
		&app.ExistingEmi,
		&app.LoanAmount,
		&app.LoanTenure,
		&app.Decision,
		&app.DecisionCode,
		&app.DecisionReason,
		&createdAt,
	); err != nil {
		return nil, err
	}
	app.CreatedAt = parseSQLiteTime(createdAt)
	return &app, nil
}

func scanApplications(rows *sql.Rows) ([]loan.LoanApplication, error) {
	applications := []loan.LoanApplication{}
	for rows.Next() {
		app, err := scanApplication(rows)
		if err != nil {
			return nil, err
		}
		applications = append(applications, *app)
	}
	return applications, rows.Err()
}

func parseSQLiteTime(value string) time.Time {
	layouts := []string{
		"2006-01-02 15:04:05",
		time.RFC3339Nano,
		time.RFC3339,
	}
	for _, layout := range layouts {
		if parsed, err := time.ParseInLocation(layout, value, time.Local); err == nil {
			return parsed
		}
	}
	return time.Now()
}

func DBPathFromEnv() string {
	if value := os.Getenv("NBFC_DB_PATH"); value != "" {
		return value
	}
	return filepath.Join("data", "nbfc_loan.db")
}

func MustAbsolute(path string) string {
	absolute, err := filepath.Abs(path)
	if err != nil {
		return fmt.Sprintf("%s", path)
	}
	return absolute
}

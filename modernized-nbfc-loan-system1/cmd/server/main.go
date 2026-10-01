package main

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"

	"modernized-nbfc-loan-system/internal/loan"
	"modernized-nbfc-loan-system/internal/store"
)

type appServer struct {
	store       *store.SQLiteStore
	validator   loan.InputValidator
	loanService loan.LoanService
}

func main() {
	dbPath := store.DBPathFromEnv()
	sqliteStore, err := store.Open(dbPath)
	if err != nil {
		log.Fatal(err)
	}
	defer sqliteStore.Close()

	server := appServer{
		store:       sqliteStore,
		validator:   loan.InputValidator{},
		loanService: loan.NewLoanService(sqliteStore),
	}

	mux := http.NewServeMux()
	mux.HandleFunc("GET /api/health", server.health)
	mux.HandleFunc("POST /api/applications", server.createApplication)
	mux.HandleFunc("GET /api/applications", server.listApplications)
	mux.HandleFunc("GET /api/applications/", server.getApplication)
	mux.HandleFunc("POST /api/eligibility/check", server.checkEligibility)
	mux.HandleFunc("GET /api/rules", server.rules)
	mux.Handle("/", staticHandler())

	port := os.Getenv("PORT")
	if port == "" {
		port = "8081"
	}
	log.Printf("[NBFC] Modernized server listening on http://localhost:%s", port)
	log.Printf("[NBFC] SQLite database path: %s", store.MustAbsolute(dbPath))
	log.Fatal(http.ListenAndServe(":"+port, withCORS(mux)))
}

func (s appServer) health(w http.ResponseWriter, r *http.Request) {
	writeJSON(w, http.StatusOK, map[string]any{"status": "ok"})
}

func (s appServer) createApplication(w http.ResponseWriter, r *http.Request) {
	input, err := decodeInput(r)
	if err != nil {
		writeJSON(w, http.StatusBadRequest, map[string]any{"error": "Invalid request body."})
		return
	}

	validation := s.validator.Validate(input)
	if !validation.IsValid() {
		writeJSON(w, http.StatusBadRequest, map[string]any{"validationResult": validation})
		return
	}

	application := loan.BuildApplication(input)
	processed, err := s.loanService.ProcessApplication(application)
	if err != nil {
		log.Printf("[LoanServlet] Database error processing application: %v", err)
		writeJSON(w, http.StatusInternalServerError, map[string]any{
			"error": "A system error occurred while processing your application. Please try again.",
		})
		return
	}

	writeJSON(w, http.StatusCreated, map[string]any{"loanApplication": processed})
}

func (s appServer) checkEligibility(w http.ResponseWriter, r *http.Request) {
	input, err := decodeInput(r)
	if err != nil {
		writeJSON(w, http.StatusBadRequest, map[string]any{"error": "Invalid request body."})
		return
	}

	validation := s.validator.Validate(input)
	if !validation.IsValid() {
		writeJSON(w, http.StatusBadRequest, map[string]any{"validationResult": validation})
		return
	}

	application := loan.BuildApplication(input)
	decision := s.loanService.CheckEligibilityOnly(application)
	writeJSON(w, http.StatusOK, map[string]any{"eligibilityDecision": decision})
}

func (s appServer) listApplications(w http.ResponseWriter, r *http.Request) {
	applications, err := s.store.FindAll()
	if err != nil {
		log.Printf("[ApplicationListServlet] DB error: %v", err)
		writeJSON(w, http.StatusInternalServerError, map[string]any{"error": "Unable to retrieve applications from database."})
		return
	}
	total, err := s.store.CountAll()
	if err != nil {
		log.Printf("[ApplicationListServlet] DB error: %v", err)
		writeJSON(w, http.StatusInternalServerError, map[string]any{"error": "Unable to retrieve applications from database."})
		return
	}
	writeJSON(w, http.StatusOK, map[string]any{"applicationList": applications, "totalCount": total})
}

func (s appServer) getApplication(w http.ResponseWriter, r *http.Request) {
	idText := strings.TrimPrefix(r.URL.Path, "/api/applications/")
	id, err := strconv.ParseInt(strings.TrimSpace(idText), 10, 64)
	if err != nil || idText == "" {
		writeJSON(w, http.StatusBadRequest, map[string]any{"error": "Invalid application ID."})
		return
	}

	application, err := s.store.FindByID(id)
	if err != nil {
		log.Printf("[ApplicationDetailServlet] DB error: %v", err)
		writeJSON(w, http.StatusInternalServerError, map[string]any{"error": "Unable to retrieve application details."})
		return
	}
	if application == nil {
		writeJSON(w, http.StatusNotFound, map[string]any{"error": "Application with ID " + strconv.FormatInt(id, 10) + " was not found."})
		return
	}

	writeJSON(w, http.StatusOK, map[string]any{"loanApplication": application})
}

func (s appServer) rules(w http.ResponseWriter, r *http.Request) {
	rules, err := s.store.Rules()
	if err != nil {
		writeJSON(w, http.StatusInternalServerError, map[string]any{"error": "Unable to retrieve eligibility rules."})
		return
	}
	writeJSON(w, http.StatusOK, map[string]any{"rules": rules})
}

func decodeInput(r *http.Request) (loan.ApplicationInput, error) {
	var input loan.ApplicationInput
	if !strings.Contains(r.Header.Get("Content-Type"), "application/json") {
		if err := r.ParseForm(); err != nil {
			return input, err
		}
		input = loan.ApplicationInput{
			CustomerName:   r.FormValue("customerName"),
			Age:            r.FormValue("age"),
			MonthlyIncome:  r.FormValue("monthlyIncome"),
			EmploymentType: r.FormValue("employmentType"),
			CreditScore:    r.FormValue("creditScore"),
			ExistingEmi:    r.FormValue("existingEmi"),
			LoanAmount:     r.FormValue("loanAmount"),
			LoanTenure:     r.FormValue("loanTenure"),
		}
		return input, nil
	}
	err := json.NewDecoder(r.Body).Decode(&input)
	if err != nil {
		return input, err
	}
	return input, nil
}

func writeJSON(w http.ResponseWriter, status int, payload any) {
	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.WriteHeader(status)
	if err := json.NewEncoder(w).Encode(payload); err != nil && !errors.Is(err, http.ErrHandlerTimeout) {
		log.Printf("failed to write JSON response: %v", err)
	}
}

func withCORS(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type")
		w.Header().Set("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusNoContent)
			return
		}
		next.ServeHTTP(w, r)
	})
}

func staticHandler() http.Handler {
	webDir := filepath.Join(".", "frontend", "dist")
	if _, err := os.Stat(filepath.Join(webDir, "index.html")); err != nil {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			if strings.HasPrefix(r.URL.Path, "/api/") {
				http.NotFound(w, r)
				return
			}
			http.Error(w, "React frontend has not been built. Run: cd frontend && npm run build", http.StatusServiceUnavailable)
		})
	}
	fileServer := http.FileServer(http.Dir(webDir))
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if strings.HasPrefix(r.URL.Path, "/api/") {
			http.NotFound(w, r)
			return
		}
		cleanPath := strings.TrimPrefix(filepath.Clean(r.URL.Path), string(filepath.Separator))
		if cleanPath == "." {
			cleanPath = "index.html"
		}
		path := filepath.Join(webDir, cleanPath)
		if info, err := os.Stat(path); err == nil && !info.IsDir() {
			fileServer.ServeHTTP(w, r)
			return
		}
		http.ServeFile(w, r, filepath.Join(webDir, "index.html"))
	})
}

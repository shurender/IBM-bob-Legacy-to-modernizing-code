package loan

import (
	"strconv"
	"strings"
	"time"
)

func trimSpace(value string) string {
	return strings.TrimSpace(value)
}

func ParseDoubleSafe(value string, defaultValue float64) float64 {
	if trimSpace(value) == "" {
		return defaultValue
	}
	parsed, err := strconv.ParseFloat(trimSpace(value), 64)
	if err != nil {
		return defaultValue
	}
	return parsed
}

func ParseIntSafe(value string, defaultValue int) int {
	if trimSpace(value) == "" {
		return defaultValue
	}
	parsed, err := strconv.Atoi(trimSpace(value))
	if err != nil {
		return defaultValue
	}
	return parsed
}

func FormatEmploymentType(value string) string {
	switch value {
	case EmploymentFullTime:
		return "Full Time"
	case EmploymentPartTime:
		return "Part Time"
	case EmploymentSelfEmployed:
		return "Self Employed"
	case EmploymentContract:
		return "Contract"
	case EmploymentUnemployed:
		return "Unemployed"
	case "":
		return "Unknown"
	default:
		return value
	}
}

func FormatDecision(value string) string {
	switch value {
	case DecisionEligible:
		return "ELIGIBLE FOR LOAN"
	case DecisionNotEligible:
		return "NOT ELIGIBLE"
	case DecisionReviewRequired:
		return "MORE INFORMATION REQUIRED"
	case "":
		return "Unknown"
	default:
		return value
	}
}

func FormatDateTime(t time.Time) string {
	if t.IsZero() {
		return "-"
	}
	return t.Format("02-Jan-2006 15:04")
}

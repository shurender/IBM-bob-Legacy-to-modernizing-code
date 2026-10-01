package com.nbfc.loan.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the result of form input validation.
 * Collects all validation errors before returning to the user.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class ValidationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean valid;
    private List<String> errors;
    private List<String> warnings;

    public ValidationResult() {
        this.valid    = true;
        this.errors   = new ArrayList<String>();
        this.warnings = new ArrayList<String>();
    }

    public void addError(String errorMessage) {
        if (errorMessage != null && !errorMessage.trim().isEmpty()) {
            this.errors.add(errorMessage);
            this.valid = false;
        }
    }

    public void addWarning(String warningMessage) {
        if (warningMessage != null && !warningMessage.trim().isEmpty()) {
            this.warnings.add(warningMessage);
        }
    }

    public boolean isValid() {
        return valid && (errors == null || errors.isEmpty());
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public String getFirstError() {
        if (hasErrors()) {
            return errors.get(0);
        }
        return null;
    }

    @Override
    public String toString() {
        return "ValidationResult{valid=" + valid +
                ", errors=" + errors +
                ", warnings=" + warnings + '}';
    }
}

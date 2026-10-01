package com.nbfc.loan.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for formatting values for display in JSP pages.
 * Handles currency, number, and date formatting.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public final class FormatUtil {

    private static final String CURRENCY_FORMAT  = "##,##,##,###.##";
    private static final String DATE_FORMAT      = "dd-MMM-yyyy HH:mm";
    private static final String DATE_ONLY_FORMAT = "dd-MMM-yyyy";

    private FormatUtil() {
    }

    /**
     * Formats a number as Indian Rupee currency string.
     * Example: 500000.0 → "₹5,00,000"
     *
     * @param amount the amount to format
     * @return formatted currency string
     */
    public static String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat(CURRENCY_FORMAT);
        return "\u20B9" + df.format(amount);
    }

    /**
     * Formats a number with two decimal places.
     *
     * @param value the value to format
     * @return formatted string
     */
    public static String formatDecimal(double value) {
        DecimalFormat df = new DecimalFormat("##,##,##,###.##");
        return df.format(value);
    }

    /**
     * Formats a date as dd-MMM-yyyy HH:mm.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Formats a date as dd-MMM-yyyy.
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) return "-";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_ONLY_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts employment type code to human-readable label.
     *
     * @param employmentType the employment type code
     * @return human-readable label
     */
    public static String formatEmploymentType(String employmentType) {
        if (employmentType == null) return "Unknown";
        if ("FULL_TIME".equals(employmentType))    return "Full Time";
        if ("PART_TIME".equals(employmentType))    return "Part Time";
        if ("SELF_EMPLOYED".equals(employmentType)) return "Self Employed";
        if ("CONTRACT".equals(employmentType))     return "Contract";
        if ("UNEMPLOYED".equals(employmentType))   return "Unemployed";
        return employmentType;
    }

    /**
     * Converts a decision code to a display label.
     *
     * @param decision the decision code
     * @return human-readable decision label
     */
    public static String formatDecision(String decision) {
        if (decision == null) return "Unknown";
        if ("ELIGIBLE".equals(decision))         return "ELIGIBLE FOR LOAN";
        if ("NOT_ELIGIBLE".equals(decision))     return "NOT ELIGIBLE";
        if ("REVIEW_REQUIRED".equals(decision))  return "MORE INFORMATION REQUIRED";
        return decision;
    }

    /**
     * Safely parses a string to double, returning a default value on failure.
     *
     * @param value        the string to parse
     * @param defaultValue the default to return on parse failure
     * @return the parsed double or defaultValue
     */
    public static double parseDoubleSafe(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parses a string to int, returning a default value on failure.
     *
     * @param value        the string to parse
     * @param defaultValue the default to return on parse failure
     * @return the parsed int or defaultValue
     */
    public static int parseIntSafe(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

package com.nbfc.loan.service;

import com.nbfc.loan.util.ApplicationConstants;

/**
 * Service for assessing whether an applicant's age meets eligibility criteria.
 *
 * Rules:
 *   Age < 21  → FAIL (too young)
 *   Age > 60  → FAIL (exceeds maximum age)
 *   Age 21-60 → PASS
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public class AgeAssessmentService {

    public static final String STATUS_PASS   = "PASS";
    public static final String STATUS_FAIL   = "FAIL";

    /**
     * Assesses whether the applicant's age meets eligibility criteria.
     *
     * @param age the applicant's age in years
     * @return STATUS_PASS or STATUS_FAIL
     */
    public String assess(int age) {
        if (age < ApplicationConstants.AGE_MINIMUM) {
            return STATUS_FAIL;
        }
        if (age > ApplicationConstants.AGE_MAXIMUM) {
            return STATUS_FAIL;
        }
        return STATUS_PASS;
    }

    /**
     * Returns a human-readable remark about the age assessment.
     *
     * @param age the applicant's age
     * @return assessment remark string
     */
    public String getRemark(int age) {
        if (age < ApplicationConstants.AGE_MINIMUM) {
            return "Applicant age " + age + " is below the minimum required age of " +
                   ApplicationConstants.AGE_MINIMUM + ".";
        }
        if (age > ApplicationConstants.AGE_MAXIMUM) {
            return "Applicant age " + age + " exceeds the maximum allowed age of " +
                   ApplicationConstants.AGE_MAXIMUM + ".";
        }
        return "Age " + age + " is within the eligible range (" +
               ApplicationConstants.AGE_MINIMUM + "-" +
               ApplicationConstants.AGE_MAXIMUM + ").";
    }
}

package com.nbfc.loan.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique application reference numbers for loan applications.
 * Uses a combination of date prefix and sequence counter.
 *
 * @author NBFC Systems Team
 * @version 1.0
 * @since 2010
 */
public final class ReferenceGenerator {

    private static final String PREFIX         = "NBFC";
    private static final String DATE_FORMAT    = "yyyyMMdd";
    private static final AtomicInteger counter = new AtomicInteger(1000);

    private ReferenceGenerator() {
    }

    /**
     * Generates a unique loan application reference number.
     * Format: NBFC-YYYYMMDD-NNNN
     * Example: NBFC-20101215-1001
     *
     * @return unique reference string
     */
    public static String generateReference() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String datePart = sdf.format(new Date());
        int seq = counter.getAndIncrement();
        return PREFIX + "-" + datePart + "-" + seq;
    }
}

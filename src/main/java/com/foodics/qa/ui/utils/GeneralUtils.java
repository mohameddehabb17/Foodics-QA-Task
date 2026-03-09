package com.foodics.qa.ui.utils;

/**
 * Generic helpers shared across UI flows.
 */
public final class GeneralUtils {
    private GeneralUtils() {
    }

    /**
     * Compares two integers.
     *
     * @param a first value
     * @param b second value
     * @return {@code true} when {@code a < b}
     */
    public static boolean isLessThan(int a, int b) {
        return a < b;
    }

    /**
     * Compares two integers.
     *
     * @param a first value
     * @param b second value
     * @return {@code true} when {@code a > b}
     */
    public static boolean isMoreThan(int a, int b) {
        return a > b;
    }

    /**
     * Extracts integer price value from formatted currency text.
     *
     * @param text raw text that may include currency symbols and separators
     * @return parsed integer value, or {@code -1} when parsing fails
     */
    public static int extractPriceFromText(String text) {
        if (text == null || text.isBlank()) {
            return -1;
        }
        boolean isNegative = text.trim().startsWith("-");
        String cleaned = text.replaceAll("[^0-9.,]", "");
        if (cleaned.isBlank()) {
            return -1;
        }
        cleaned = cleaned.replace(",", "");
        int dotIndex = cleaned.indexOf('.');
        if (dotIndex > 0) {
            cleaned = cleaned.substring(0, dotIndex);
        }
        try {
            int value = Integer.parseInt(cleaned);
            return isNegative ? -value : value;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Extracts numeric digits from any text and parses them as integer.
     *
     * @param text source text
     * @param defaultValue fallback when parsing fails
     * @return parsed digits value or fallback
     */
    public static int extractDigitsAsInt(String text, int defaultValue) {
        if (text == null || text.isBlank()) {
            return defaultValue;
        }

        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException exception) {
            return defaultValue;
        }
    }
}

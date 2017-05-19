package org.pustule.fanfarepustule.utils;

/**
 * Created by Paul Mougin on 19/05/2017.
 */

public final class StringHelper {

    /**
     * Check if a {@link String} is empty or null
     *
     * @param string tested String
     * @return true if empty or null
     */
    public static boolean isEmptyOrNull(String string) {
        if (string == null || string.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Capitalize the first letter of a String
     *
     * @param s given String
     * @return the capitalized string
     */

    public static String capitalizeFirstLetter(String s) {
        if (s == null) {
            return null;
        } else if (s.length() == 1) {
            return s.toUpperCase();
        } else if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        } else {
            return "";
        }
    }
}

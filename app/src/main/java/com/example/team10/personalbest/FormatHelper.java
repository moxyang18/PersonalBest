package com.example.team10.personalbest;

public class FormatHelper {
    /**
     * reformatEmailForCloud
     *
     * Periods (.) are prohibited in JSON strings. We'll reformat
     * the email string to replace periods with commas.
     *
     * @param email The email to reformat
     * @return A reformatted email with periods replaced with commas.
     */
    public static String reformatEmailForCloud (String email) {

        char[] emailChar = email.toCharArray();

        for (int i = 0; i < emailChar.length; i++) {
            if (emailChar[i] == '.') {
                emailChar[i] = ',';
            }
        }
        return new String(emailChar);
    }

    /**
     * reformatEmailForUser
     *
     * For main memory, we'll need strings to be in their original
     * format. We'll return email strings formatted with commas to
     * email strings with periods.
     *
     * @param email The email to reformat.
     * @return A reformatted email with commas replaced with periods.
     */
    public static String reformatEmailForUser (String email) {
        char[] emailChar = email.toCharArray();

        for (int i = 0; i < emailChar.length; i++) {
            if (emailChar[i] == ',') {
                emailChar[i] = '.';
            }
        }
        return new String(emailChar);
    }
}

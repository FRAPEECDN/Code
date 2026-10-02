package com.frapee;

/**
 * Foundation guide to reversible character-to-number string encodings.
 * Covers lower-case, upper-case, and printable ASCII-style alphanumeric text;
 * spaces are represented by '+' in the alphabetic formats. These examples are
 * educational encodings, not security or general-purpose text serialization.
 */
public class FoundationEncodingStrings {

    /**
     * Running a conversion for the AlphaString using a-z characters and returning a number string.
     * @param alphaString - input of alpha string to encode
     * @return encoded number string
     */
    public static String encodeToNumberLowerAlpha(String alphaString) {
        StringBuilder numberString = new StringBuilder();

        for (char letter : alphaString.toCharArray()) {
            if (letter == ' ') {
                if (numberString.length() > 0) {
                    numberString.deleteCharAt(numberString.length() - 1);
                }
                numberString.append('+');
            } else {
                int number = letter - 'a' + 1;
                numberString.append(String.valueOf(number));
                numberString.append(' ');
            }
        }

        if (numberString.length() > 0) {
            numberString.deleteCharAt(numberString.length() - 1);
        }

        return numberString.toString();
    }

    /**
     * Running a conversion for the AlphaString using A-Z characters and returning a number string.
     * @param alphaString - input of alpha string to encode
     * @return encoded number string
     */
    public static String encodeToNumberUpperAlpha(String alphaString) {
        StringBuilder numberString = new StringBuilder();

        for (char letter : alphaString.toCharArray()) {
            if (letter == ' ') {
                if (numberString.length() > 0) {
                    numberString.deleteCharAt(numberString.length() - 1);
                }
                numberString.append('+');
            } else {
                int number = letter - 'A' + 1;
                numberString.append(String.valueOf(number));
                numberString.append(' ');
            }
        }

        if (numberString.length() > 0) {
            numberString.deleteCharAt(numberString.length() - 1);
        }

        return numberString.toString();
    }

    /**
     * Running a conversion for the number string to AlphaString using a-z characters.
     * @param numberString number string presenting encoded alpha string.
     * @return decoded alpha string.
     */
    public static String decodeToLowerAlphaNumber(String numberString) {
        StringBuilder letterString = new StringBuilder();
        
        String digits = "";
        for (char digit : numberString.toCharArray()) {
            if (digit == ' ' || digit == '+') {
                int number = Integer.valueOf(digits);
                char letter = (char)((int)'a' + number - 1);
                digits = "";
                letterString.append(letter);
                if (digit == '+') {
                    letterString.append(' ');
                }
            } else {
                digits += digit;                
            }
        }
        if (digits.length() > 0) {
            int number = Integer.valueOf(digits);
            char letter = (char)((int)'a' + number - 1);
            letterString.append(letter);
        }
        return letterString.toString();
    }

    /**
     * Running a conversion for the number string to AlphaString using A-Z characters.
     * @param numberString number string presenting encoded alpha string.
     * @return decoded alpha string.
     */
    public static String decodeToUpperAlphaNumber(String numberString) {
        StringBuilder letterString = new StringBuilder();
        
        String digits = "";
        for (char digit : numberString.toCharArray()) {
            if (digit == ' ' || digit == '+') {
                int number = Integer.valueOf(digits);
                char letter = (char)((int)'A' + number - 1);
                digits = "";
                letterString.append(letter);
                if (digit == '+') {
                    letterString.append(' ');
                }
            } else {
                digits += digit;                
            }
        }
        if (digits.length() > 0) {
            int number = Integer.valueOf(digits);
            char letter = (char)((int)'A' + number - 1);
            letterString.append(letter);
        }
        return letterString.toString();
    }

    /**
     * Running a conversion for the string of AlphaNumeric + symbols to number string.
     * @param alphaNumeric - input of AlphaNumeric + symbols string to encode
     * @return encoded number string
     */
    public static String encodeAlphaNumeric(String alphaNumeric) {
        StringBuilder numberString = new StringBuilder();

        for (char letter : alphaNumeric.toCharArray()) {
            int number = letter - ' ' + 1;
            if (number > 0 && number <= 94) {
                numberString.append(String.valueOf(number));
                numberString.append(' ');                
            }
        }
        
        if (numberString.length() > 0) {
            numberString.deleteCharAt(numberString.length() - 1);
        }

        return numberString.toString();
    }

    /**
     * Running a conversion for the number string to AlphaNumeric + symbols.
     * @param numberString number string presenting encoded string.
     * @return decoded string
     */
    public static String decodeAlphaNumeric(String numberString) {
        StringBuilder letterString = new StringBuilder();
        
        String digits = "";
        for (char digit : numberString.toCharArray()) {
            if (digit == ' ') {
                int number = Integer.valueOf(digits);
                char letter = (char)((int)' ' + number - 1);
                digits = "";
                letterString.append(letter);
            } else {
                digits += digit;                
            }
        }
        if (digits.length() > 0) {
            int number = Integer.valueOf(digits);
            char letter = (char)((int)' ' + number - 1);
            letterString.append(letter);
        }        
        return letterString.toString();
    }    
}

package com.fp.coding;

import java.text.Normalizer;

/** Shared name normalization and validation for the information examples. */
final class NameValidation {
    private NameValidation() {
    }

    static String normalize(String name) {
        if (name == null) {
            throw invalidName();
        }

        String normalized = Normalizer.normalize(name.strip(), Normalizer.Form.NFC);
        if (normalized.isEmpty()
                || normalized.codePoints().noneMatch(Character::isLetter)
                || !normalized.codePoints().allMatch(NameValidation::isAllowed)) {
            throw invalidName();
        }
        return normalized;
    }

    private static boolean isAllowed(int codePoint) {
        int type = Character.getType(codePoint);
        return Character.isLetter(codePoint)
                || Character.isWhitespace(codePoint)
                || type == Character.NON_SPACING_MARK
                || type == Character.COMBINING_SPACING_MARK
                || type == Character.ENCLOSING_MARK
                || codePoint == '\''
                || codePoint == 0x2018
                || codePoint == 0x2019
                || codePoint == '-'
                || codePoint == 0x2010
                || codePoint == 0x2011
                || codePoint == '.';
    }

    private static IllegalArgumentException invalidName() {
        return new IllegalArgumentException(
                "name must contain a letter and may include whitespace, apostrophes, hyphens, or periods");
    }
}
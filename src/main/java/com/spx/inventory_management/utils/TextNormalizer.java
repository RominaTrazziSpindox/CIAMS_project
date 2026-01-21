package com.spx.inventory_management.utils;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public final class TextNormalizer {

    /* Clean and normalize an incoming key value */
    public static String normalizeKey(String incomingText) {

        if (incomingText == null) {

            log.info("There is no text to normalize");
            return null;

        }

        // Convert key value text
        return incomingText.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);

    }

    /* Clean and normalize a description (= no lowercase) */
    public static String normalizeText(String incomingDescription) {

        if (incomingDescription == null) {

           log.info("There is no description to normalize");
            return null;

        }

        // Convert description
        return incomingDescription.trim().replaceAll("\\s+", " ");

    }
}




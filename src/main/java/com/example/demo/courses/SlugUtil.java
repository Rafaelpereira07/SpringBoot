package com.example.demo.courses;

import java.text.Normalizer;
import java.util.regex.Pattern;

/** Turns a title into a URL-friendly slug (used for both courses and lessons). */
public final class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern EDGE_DASHES = Pattern.compile("^-+|-+$");

    private SlugUtil() {
    }

    public static String slugify(String input) {
        String noWhitespace = WHITESPACE.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("").toLowerCase();
        return EDGE_DASHES.matcher(slug).replaceAll("");
    }
}

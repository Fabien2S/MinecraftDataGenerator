package dev.fabien2s.mdg.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegexUtils {

    private static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile(",");

    public static String[] split(String input) {
        input = input.trim();
        if (input.isEmpty())
            return new String[0];
        return COMMA_SPLIT_PATTERN.split(input);
    }

}

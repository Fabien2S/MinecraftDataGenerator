package dev.fabien2s.mdg.mappings;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class MappingParser implements Closeable {

    private static final Logger LOGGER = LogManager.getLogger(MappingParser.class);

    private static final String COMMENT_PREFIX = "#";
    private static final String FIELD_PREFIX = "    ";

    private static final Pattern CLASS_PATTERN = Pattern.compile("^(.+) -> (.+):$");
    private static final Pattern FIELD_PATTERN = Pattern.compile("^ {4}(.+) (.+) -> (.+)$");

    private final BufferedReader reader;

    private String line;

    public MappingContext parse() throws IOException, MappingSyntaxException {
        Map<String, MappingClass> classMap = new HashMap<>();

        while ((this.line = reader.readLine()) != null) {
            if (this.line.startsWith(COMMENT_PREFIX))
                continue;

            MappingClass mappingClass = parseClass();
            classMap.put(mappingClass.getOriginal(), mappingClass);
        }

        return new MappingContext(classMap);
    }

    private MappingClass parseClass() throws MappingSyntaxException, IOException {
        Matcher classMatcher = CLASS_PATTERN.matcher(this.line);
        if (!classMatcher.matches())
            throw new MappingSyntaxException("Invalid class definition (" + this.line + ')');

        String classOriginal = classMatcher.group(1);
        String classObfuscated = classMatcher.group(2);
        LOGGER.debug("Parsed class: {} ({})", classOriginal, classObfuscated);

        Map<String, String> fields = new HashMap<>();

        this.reader.mark(4096);
        while ((this.line = reader.readLine()) != null && this.line.startsWith(FIELD_PREFIX)) {

            Matcher fieldMatcher = FIELD_PATTERN.matcher(this.line);
            if (!fieldMatcher.matches())
                throw new MappingSyntaxException("Invalid field definition (" + this.line + ')');

            String fieldOriginal = fieldMatcher.group(2);
            String fieldObfuscated = fieldMatcher.group(3);
            LOGGER.debug("Parsed field in class {}: {} ({})", classOriginal, fieldOriginal, fieldObfuscated);

            fields.put(fieldOriginal, fieldObfuscated);
            this.reader.mark(4096);

        }

        this.reader.reset();
        return new MappingClass(classObfuscated, classOriginal, fields);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}

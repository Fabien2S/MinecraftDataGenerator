package dev.fabien2s.mdg.mapping;

import dev.fabien2s.mdg.mapping.exceptions.MappingSyntaxException;
import dev.fabien2s.mdg.utils.RegexUtils;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class MappingParser implements Closeable {

    private static final Logger LOGGER = LogManager.getLogger(MappingParser.class);

    private static final String COMMENT_PREFIX = "#";
    private static final String FIELD_PREFIX = "    ";

    private static final Pattern CLASS_PATTERN = Pattern.compile("^(.+) -> (.+):$");
    private static final Pattern FIELD_PATTERN = Pattern.compile("^ {4}(.+) (.+) -> (.+)$");
    private static final Pattern METHOD_PATTERN = Pattern.compile("^ {4}(.+) (.+)\\((.*)\\) -> (.+)$");

    private final BufferedReader reader;

    private String line;

    public MappingContext parse() throws IOException, MappingSyntaxException {
        Map<String, MappingClass> classMap = new HashMap<>();

        while ((this.line = reader.readLine()) != null) {
            if (this.line.startsWith(COMMENT_PREFIX))
                continue;

            MappingClass mappingClass = parseClass();
            classMap.put(mappingClass.getName(), mappingClass);
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
        Set<MappingMethod> methods = new HashSet<>();

        int readAheadLimit = 4096;
        this.reader.mark(readAheadLimit);
        while ((this.line = reader.readLine()) != null && this.line.startsWith(FIELD_PREFIX)) {

            final Matcher methodMatcher = METHOD_PATTERN.matcher(this.line);
            if(methodMatcher.matches()) {

                String methodOriginal = methodMatcher.group(2);
                String[] methodParameters = RegexUtils.split(methodMatcher.group(3));
                String methodObfuscated = methodMatcher.group(4);
                LOGGER.debug("Parsed method in class {}: {}({}) -> {}", classOriginal, methodOriginal, methodParameters, methodObfuscated);

                methods.add(new MappingMethod(methodOriginal, methodObfuscated, methodParameters));

                this.reader.mark(readAheadLimit);
                continue;
            }

            Matcher fieldMatcher = FIELD_PATTERN.matcher(this.line);
            if(fieldMatcher.matches()) {
                String fieldOriginal = fieldMatcher.group(2);
                String fieldObfuscated = fieldMatcher.group(3);
                LOGGER.debug("Parsed field in class {}: {} -> {}", classOriginal, fieldOriginal, fieldObfuscated);

                fields.put(fieldOriginal, fieldObfuscated);

                this.reader.mark(readAheadLimit);
                continue;
            }

            throw new MappingSyntaxException("Invalid definition (" + this.line + ')');
        }

        this.reader.reset();
        return new MappingClass(classOriginal, classObfuscated, fields, methods);
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}

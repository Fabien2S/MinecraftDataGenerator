package dev.fabien2s.mdg.mapping;

import dev.fabien2s.mdg.mapping.exceptions.MappingException;
import dev.fabien2s.mdg.mapping.exceptions.MappingNotFoundException;
import dev.fabien2s.mdg.utils.RegexUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class MappingClass {

    private static final Pattern METHOD_PATTERN = Pattern.compile("^(.+)\\((.*)\\)$");

    @Getter private final String name;
    @Getter private final String obfuscated;

    private final Map<String, String> fields;
    private final Set<MappingMethod> methods;

    public String remapField(String original) throws MappingNotFoundException {
        if(fields.containsKey(original))
            return fields.get(original);
        throw new MappingNotFoundException("No mapping for field " + original + " in " + original);
    }

    public MappingMethod remapMethod(String original) throws MappingException {
        final Matcher matcher = METHOD_PATTERN.matcher(original);
        if(!matcher.matches())
            throw new MappingException("Invalid syntax for method " + original);

        String methodName = matcher.group(1);
        String[] methodParameters = RegexUtils.split(matcher.group(2));
        for (MappingMethod method : methods) {
            final String name = method.getName();
            final String[] parameters = method.getParameters();
            if(name.equals(methodName) && Arrays.equals(parameters, methodParameters))
                return method;
        }

        throw new MappingNotFoundException("No mapping for method " + original);
    }


}

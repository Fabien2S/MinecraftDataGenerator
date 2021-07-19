package dev.fabien2s.mdg.mapping;

import dev.fabien2s.mdg.mapping.exceptions.MappingNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MappingContext {

    private final Map<String, MappingClass> classes;

    public boolean hasMapping(String className) {
        return classes.containsKey(className);
    }

    public MappingClass remapClass(String original) throws MappingNotFoundException {
        if (classes.containsKey(original))
            return classes.get(original);
        throw new MappingNotFoundException("No mapping for class " + original);
    }

    public MappingClass remapClass(Class<?> clazz) throws MappingNotFoundException {
        final String className = clazz.getName();
        for (MappingClass mappingClass : classes.values()) {
            final String obfuscated = mappingClass.getObfuscated();
            if (obfuscated.equals(className))
                return mappingClass;
        }
        throw new MappingNotFoundException("No mapping for obfuscated class " + className);
    }

}
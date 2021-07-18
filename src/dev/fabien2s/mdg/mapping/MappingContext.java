package dev.fabien2s.mdg.mapping;

import dev.fabien2s.mdg.mapping.exceptions.MappingNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MappingContext {

    private final Map<String, MappingClass> classes;

    public MappingClass remapClass(String original) throws MappingNotFoundException {
        if (classes.containsKey(original))
            return classes.get(original);
        throw new MappingNotFoundException("No mapping for class " + original);
    }

    public MappingClass deobfuscate(String search) throws MappingNotFoundException {
        for (MappingClass mappingClass : classes.values()) {
            final String obfuscated = mappingClass.getObfuscated();
            if(obfuscated.equals(search))
                return mappingClass;
        }
        throw new MappingNotFoundException("No mapping for obfuscated class " + search);
    }

}
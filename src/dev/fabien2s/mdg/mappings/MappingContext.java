package dev.fabien2s.mdg.mappings;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MappingContext {

    private final Map<String, MappingClass> classes;

    public MappingClass remap(String original) {
        return classes.get(original);
    }

}
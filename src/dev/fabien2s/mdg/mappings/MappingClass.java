package dev.fabien2s.mdg.mappings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MappingClass {

    @Getter private final String obfuscated;
    @Getter private final String original;

    private final Map<String, String> fields;

    public String remap(String original) {
        if (this.original.equals(original))
            return this.obfuscated;
        return fields.get(original);
    }

}

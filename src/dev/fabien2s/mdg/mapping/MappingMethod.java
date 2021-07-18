package dev.fabien2s.mdg.mapping;

import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.mapping.exceptions.MappingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MappingMethod {

    @Getter private final String name;
    @Getter private final String obfuscated;
    @Getter private final String[] parameters;

    public Class<?>[] getParametersClasses(ServerRuntime runtime) throws ClassNotFoundException {
        Class<?>[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            try {
                classes[i] = runtime.getClass(parameters[i]);
            } catch (MappingException e) {
                classes[i] = Class.forName(parameters[i]);
            }
        }
        return classes;
    }

}
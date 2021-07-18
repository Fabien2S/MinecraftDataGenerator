package dev.fabien2s.mdg.extractor.scripted;

import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.mapping.exceptions.MappingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ScriptedExtractor {

    private final ScriptedExtractorInvoker[] init;
    @Getter private final ScriptedDataExtractor[] extractors;
    @Getter private final ScriptedRegistryExtractor[] registries;

    public void initialize(ServerRuntime runtime) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, MappingException {
        for (ScriptedExtractorInvoker invoker : init)
            runtime.invokeMethod(invoker.getClazz(), invoker.getInvokable() + "()");
    }

}

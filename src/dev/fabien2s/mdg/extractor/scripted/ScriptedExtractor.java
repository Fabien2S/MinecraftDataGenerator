package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ScriptedExtractor {

    private final Invoker[] init;
    @Getter private final ScriptedDataExtractor[] extractors;

    public void initialize(ServerRuntime runtime) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (Invoker invoker : init)
            runtime.invokeMethod(invoker.clazz, invoker.invokable);
    }

    @RequiredArgsConstructor
    private static class ScriptedDataExtractor implements DataExtractor {

        private final String name;
        private final Invoker[] fields;
        private final Invoker[] methods;

        @Override
        public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception {
            writer.beginObject();

            for (Invoker invoker : fields) {
                final Object output = runtime.getField(invoker.clazz, invoker.invokable);
                final Class<?> outputClass = output.getClass();

                writer.name(invoker.name);
                gson.toJson(output, outputClass, writer);
            }

            for (Invoker invoker : methods) {
                final Object output = runtime.invokeMethod(invoker.clazz, invoker.invokable);
                final Class<?> outputClass = output.getClass();

                writer.name(invoker.name);
                gson.toJson(output, outputClass, writer);
            }

            writer.endObject();
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    @RequiredArgsConstructor
    private static class Invoker {
        @Getter private final String name;
        @SerializedName("class")
        @Getter private final String clazz;
        @Getter private final String invokable;
    }

}

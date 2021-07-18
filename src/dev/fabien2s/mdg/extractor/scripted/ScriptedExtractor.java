package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import dev.fabien2s.mdg.mapping.exceptions.MappingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

@RequiredArgsConstructor
public class ScriptedExtractor {

    private final Invoker[] init;
    @Getter private final ScriptedDataExtractor[] extractors;

    public void initialize(ServerRuntime runtime) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, MappingException {
        for (Invoker invoker : init)
            runtime.invokeMethod(invoker.clazz, invoker.invokable + "()");
    }

    @RequiredArgsConstructor
    private static class ScriptedDataExtractor implements DataExtractor {

        private final String name;
        private final RegistryInvoker registry;
        private final Invoker[] fields;
        private final Invoker[] methods;

        @Override
        public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception {
            writer.beginObject();

            if(this.registry != null) {

                writer.name(registry.name.toLowerCase(Locale.ROOT));
                writer.beginObject();

                final Class<?> registryClass = runtime.getClass("net.minecraft.core.Registry");
                final Iterable<?> registry = (Iterable<?>) runtime.getField("net.minecraft.data.BuiltinRegistries", this.registry.name);
                for (Object entry : registry) {
                    final Class<?> entryClass = entry.getClass();

                    final Object entryName = runtime.invokeMethod(registry, registryClass, "getKey(java.lang.Object)", entry);
                    writer.name(entryName.toString());
                    writer.beginObject();

                    for (Invoker field : this.registry.fields) {
                        final Object output = runtime.getField(entry, entryClass, field.invokable);
                        final Class<?> outputClass = output.getClass();

                        writer.name(field.name);
                        gson.toJson(output, outputClass, writer);
                    }

                    for (Invoker method : this.registry.methods) {
                        final Object output = runtime.invokeMethod(entry, entryClass, method.invokable + "()");
                        final Class<?> outputClass = output.getClass();

                        writer.name(method.name);
                        gson.toJson(output, outputClass, writer);
                    }

                    writer.endObject();
                }

                writer.endObject();

            }

            for (Invoker field : fields) {
                final Object output = runtime.getField(field.clazz, field.invokable);
                final Class<?> outputClass = output.getClass();

                writer.name(field.name);
                gson.toJson(output, outputClass, writer);
            }

            for (Invoker method : methods) {
                final Object output = runtime.invokeMethod(method.clazz, method.invokable + "()");
                final Class<?> outputClass = output.getClass();

                writer.name(method.name);
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
    private static class RegistryInvoker {
        @Getter private final String name;
        @Getter private final Invoker[] fields;
        @Getter private final Invoker[] methods;
    }

    @RequiredArgsConstructor
    private static class Invoker {
        @Getter private final String name;
        @SerializedName("class")
        @Getter private final String clazz;
        @Getter private final String invokable;
    }

}

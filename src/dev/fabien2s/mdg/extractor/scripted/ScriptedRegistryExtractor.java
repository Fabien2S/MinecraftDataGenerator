package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import dev.fabien2s.mdg.mapping.exceptions.MappingException;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ScriptedRegistryExtractor implements DataExtractor {

    private final String name;
    @SerializedName("class") private final String clazz;
    private final String entryClass;
    private final String invokable;
    private final ScriptedExtractorInvoker[] fields;
    private final ScriptedExtractorInvoker[] methods;

    @Override
    public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception {
        writer.beginObject();

        final Class<?> baseRegistryClass = runtime.getClass("net.minecraft.core.Registry");

        final Iterable<?> registry = (Iterable<?>) runtime.getField(this.clazz, this.invokable);
        final Class<?> entryClass = runtime.getClass(this.entryClass);

        for (Object entry : registry) {
            final Object entryName = runtime.invokeMethod(registry, baseRegistryClass, "getKey(java.lang.Object)", entry);
            writer.name(entryName.toString());
            writer.beginObject();

            for (ScriptedExtractorInvoker field : this.fields) {
                final Object output = runtime.getField(entry, entryClass, field.getInvokable());

                writer.name(field.getName());
                write(runtime, writer, gson, output);
            }

            for (ScriptedExtractorInvoker method : this.methods) {
                final Object output = runtime.invokeMethod(entry, entryClass, method.getInvokable() + "()");

                writer.name(method.getName());
                write(runtime, writer, gson, output);
            }

            writer.endObject();
        }

        writer.endObject();
    }

    private void write(ServerRuntime runtime, JsonWriter writer, Gson gson, Object value) throws MappingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        final Class<?> stringRepresentableClass = runtime.getClass("net.minecraft.util.StringRepresentable");
        if (stringRepresentableClass.isInstance(value)) {
            String serializedName = (String) runtime.invokeMethod(value, stringRepresentableClass, "getSerializedName()");
            writer.value(serializedName);
        } else if (value == null)
            writer.nullValue();
        else
            gson.toJson(value, value.getClass(), writer);
    }

    @Override
    public String getName() {
        return this.name;
    }

}

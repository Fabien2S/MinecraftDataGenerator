package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import dev.fabien2s.mdg.mapping.MappingConstants;
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

        final Class<?> baseRegistryClass = runtime.getClass(MappingConstants.REGISTRY_CLASS);

        final Iterable<?> registry = (Iterable<?>) runtime.getField(this.clazz, this.invokable);
        final Class<?> entryClass = runtime.getClass(this.entryClass);

        for (Object entry : registry) {
            final Object entryName = runtime.invokeMethod(registry, baseRegistryClass, MappingConstants.REGISTRY_KEY_METHOD, entry);
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
        final Class<?> stringRepresentableClass = runtime.getClass(MappingConstants.SERIALIZABLE_INTERFACE);
        if (stringRepresentableClass.isInstance(value)) {
            String serializedName = (String) runtime.invokeMethod(value, stringRepresentableClass, MappingConstants.SERIALIZABLE_NAME_METHOD);
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

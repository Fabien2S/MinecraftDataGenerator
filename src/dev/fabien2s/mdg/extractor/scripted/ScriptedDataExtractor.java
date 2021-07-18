package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScriptedDataExtractor implements DataExtractor {

    private final String name;
    private final ScriptedExtractorInvoker[] fields;
    private final ScriptedExtractorInvoker[] methods;

    @Override
    public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception {
        writer.beginObject();

        for (ScriptedExtractorInvoker field : fields) {
            final Object output = runtime.getField(field.getClazz(), field.getInvokable());
            final Class<?> outputClass = output.getClass();

            writer.name(field.getName());
            gson.toJson(output, outputClass, writer);
        }

        for (ScriptedExtractorInvoker method : methods) {
            final Object output = runtime.invokeMethod(method.getClazz(), method.getInvokable() + "()");
            final Class<?> outputClass = output.getClass();

            writer.name(method.getName());
            gson.toJson(output, outputClass, writer);
        }

        writer.endObject();
    }

    @Override
    public String getName() {
        return this.name;
    }

}
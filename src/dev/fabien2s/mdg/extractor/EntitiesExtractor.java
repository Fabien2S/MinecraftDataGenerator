package dev.fabien2s.mdg.extractor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;

import java.io.IOException;

public class EntitiesExtractor implements DataExtractor {

    @Override
    public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws IOException {
        writer.beginArray();

        for (int i = 0; i < 10; i++) {
            writer.beginObject();

            writer.name("temp");
            writer.value(i);

            writer.endObject();
        }

        writer.endArray();
    }

    @Override
    public String getName() {
        return "entities";
    }

}

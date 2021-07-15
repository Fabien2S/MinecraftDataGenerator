package dev.fabien2s.mdg.extractor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;

public interface DataExtractor {

    void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception;

    String getName();

}

package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@RequiredArgsConstructor
public class ScriptedExtractorManager {

    private static final Logger LOGGER = LogManager.getLogger(ScriptedExtractorManager.class);

    private static final Gson GSON = new Gson();

    public ScriptedExtractor loadExtractor(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, ScriptedExtractor.class);
        }
    }

    public void executeExtractors(ServerRuntime runtime, DataExtractor[] extractors) {
        for (DataExtractor extractor : extractors) {
            LOGGER.info("Executing {} extractor", extractor.getName());

            final String name = extractor.getName();
            final File file = new File("generated", name + ".json");

            try (final JsonWriter writer = new JsonWriter(new FileWriter(file))) {
                writer.setIndent("    ");
                extractor.extract(runtime, writer, GSON);
            } catch (Exception e) {
                LOGGER.error("Unable to execute " + name + " extractor", e);
            }
        }
    }

}

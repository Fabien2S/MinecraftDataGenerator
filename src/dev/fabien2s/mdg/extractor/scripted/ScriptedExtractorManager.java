package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@RequiredArgsConstructor
public class ScriptedExtractorManager {

    private static final Gson GSON = new Gson();

    public ScriptedExtractor loadExtractor(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return GSON.fromJson(reader, ScriptedExtractor.class);
        }
    }

}

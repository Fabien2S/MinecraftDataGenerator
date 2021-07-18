package dev.fabien2s.mdg.extractor.scripted;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScriptedExtractorInvoker {
    @Getter private final String name;
    @SerializedName("class")
    @Getter private final String clazz;
    @Getter private final String invokable;
}
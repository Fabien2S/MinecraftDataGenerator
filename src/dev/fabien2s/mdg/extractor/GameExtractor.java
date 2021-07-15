package dev.fabien2s.mdg.extractor;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class GameExtractor implements DataExtractor {

    @Override
    public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception {
        writer.beginObject();

        this.writeVersion(runtime, writer);
        this.writeTicks(runtime, writer);

        writer.endObject();
    }

    private void writeVersion(ServerRuntime runtime, JsonWriter writer) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        writer.name("version");
        writer.beginObject();

        final int protocolVersion = (int) runtime.invokeMethod("net.minecraft.SharedConstants", "getProtocolVersion");
        final boolean snapshot = (boolean) runtime.getField("net.minecraft.SharedConstants", "SNAPSHOT");

        writer.name("protocol_id");
        writer.value(protocolVersion);

        writer.name("snapshot");
        writer.value(snapshot);

        writer.endObject();
    }

    private void writeTicks(ServerRuntime runtime, JsonWriter writer) throws IOException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        writer.name("ticks");
        writer.beginObject();

        final int ticksPerSecond = (int) runtime.getField("net.minecraft.SharedConstants", "TICKS_PER_SECOND");
        final int ticksPerMinute = (int) runtime.getField("net.minecraft.SharedConstants", "TICKS_PER_MINUTE");
        final int ticksPerGameDay = (int) runtime.getField("net.minecraft.SharedConstants", "TICKS_PER_GAME_DAY");

        writer.name("perSecond");
        writer.value(ticksPerSecond);
        writer.name("perMinute");
        writer.value(ticksPerMinute);
        writer.name("perGameDay");
        writer.value(ticksPerGameDay);
        writer.endObject();
    }

    @Override
    public String getName() {
        return "game";
    }

}
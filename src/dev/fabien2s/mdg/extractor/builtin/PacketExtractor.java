package dev.fabien2s.mdg.extractor.builtin;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.ServerRuntime;
import dev.fabien2s.mdg.extractor.DataExtractor;
import dev.fabien2s.mdg.mapping.MappingClass;
import dev.fabien2s.mdg.mapping.MappingConstants;
import dev.fabien2s.mdg.mapping.MappingContext;
import dev.fabien2s.mdg.mapping.exceptions.MappingException;

import java.util.Locale;
import java.util.Map;

public class PacketExtractor implements DataExtractor {

    @Override
    public void extract(ServerRuntime runtime, JsonWriter writer, Gson gson) throws Exception {
        final Class<?> protocolEnum = runtime.getClass(MappingConstants.CONNECTION_PROTOCOL_ENUM);
        if (!protocolEnum.isEnum())
            throw new MappingException(MappingConstants.CONNECTION_PROTOCOL_ENUM + " is not an enum");

        final MappingContext mappingContext = runtime.getMappingContext();

        writer.beginObject();

        final Enum<?>[] connectionProtocols = (Enum<?>[]) protocolEnum.getEnumConstants();
        for (Enum<?> protocol : connectionProtocols) {

            final String protocolName = protocol.name().toLowerCase(Locale.ROOT);
            writer.name(protocolName);

            writer.beginObject();

            final Map<?, ?> flows = (Map<?, ?>) runtime.getField(protocol, protocolEnum, MappingConstants.CONNECTION_PROTOCOL_PACKETS_FIELD);
            for (Map.Entry<?, ?> entry : flows.entrySet()) {

                final Enum<?> packetFlow = (Enum<?>) entry.getKey();
                final String packetFlowName = packetFlow.name().toLowerCase(Locale.ROOT);
                writer.name(packetFlowName);
                writer.beginObject();

                final Object packetSet = entry.getValue();
                final Class<?> packetSetClass = packetSet.getClass();

                @SuppressWarnings("unchecked")
                final Iterable<Class<?>> packets = (Iterable<Class<?>>) runtime.invokeMethod(packetSet, packetSetClass, MappingConstants.CONNECTION_PROTOCOL_PACKETS_METHOD);
                for (Class<?> packet : packets) {
                    final MappingClass packetMappingClass = mappingContext.remapClass(packet);
                    final String packetMappingClassName = packetMappingClass.getName();
                    writer.name(packetMappingClassName);
                    writer.nullValue();
                }

                writer.endObject();
            }

            writer.endObject();

        }

        writer.endObject();
    }

    @Override
    public String getName() {
        return "packets";
    }

}

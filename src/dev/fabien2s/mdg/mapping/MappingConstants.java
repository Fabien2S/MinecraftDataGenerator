package dev.fabien2s.mdg.mapping;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MappingConstants {

    public static final String REGISTRY_CLASS = "net.minecraft.core.Registry";
    public static final String REGISTRY_KEY_METHOD = "getKey(java.lang.Object)";

    public static final String CONNECTION_PROTOCOL_ENUM = "net.minecraft.network.ConnectionProtocol";
    public static final String CONNECTION_PROTOCOL_PACKETS_FIELD = "flows";
    public static final String CONNECTION_PROTOCOL_PACKETS_METHOD = "getAllPackets()";

    public static final String SERIALIZABLE_INTERFACE = "net.minecraft.util.StringRepresentable";
    public static final String SERIALIZABLE_NAME_METHOD = "getSerializedName()";

}

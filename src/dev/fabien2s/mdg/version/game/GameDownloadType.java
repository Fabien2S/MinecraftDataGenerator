package dev.fabien2s.mdg.version.game;

import com.google.gson.annotations.SerializedName;

public enum GameDownloadType {

    @SerializedName("client") CLIENT,
    @SerializedName("client_mappings") CLIENT_MAPPINGS,
    @SerializedName("server") SERVER,
    @SerializedName("server_mappings") SERVER_MAPPINGS

}

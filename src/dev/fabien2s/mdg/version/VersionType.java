package dev.fabien2s.mdg.version;

import com.google.gson.annotations.SerializedName;

public enum VersionType {

    @SerializedName("release") RELEASE,
    @SerializedName("snapshot") SNAPSHOT

}

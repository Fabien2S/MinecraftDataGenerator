package dev.fabien2s.mdg.version;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class VersionManifest {

    private final Map<VersionType, String> latest;
    private final List<Version> versions;

    public VersionManifest() {
        latest = new EnumMap<>(VersionType.class);
        versions = new ArrayList<>();
    }

    public String getLatestVersionId(VersionType type) {
        return latest.get(type);
    }

    public Version getLatestVersion(VersionType type) {
        String versionId = latest.get(type);
        return getVersion(versionId);
    }

    @Nullable
    public Version getVersion(String id) {
        for (Version version : versions) {
            String versionId = version.getId();
            if (versionId.equals(id))
                return version;
        }

        return null;
    }

    public Iterable<Version> getVersions() {
        return versions;
    }

}

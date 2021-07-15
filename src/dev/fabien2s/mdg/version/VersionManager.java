package dev.fabien2s.mdg.version;

import com.google.gson.Gson;
import dev.fabien2s.mdg.version.game.GameVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class VersionManager {

    private static final Logger LOGGER = LogManager.getLogger(VersionManager.class);

    private static final URL MANIFEST_URL = retrieveManifestUrl();
    private static final Gson GSON = new Gson();

    public VersionManifest downloadVersionManifest() throws IOException {
        LOGGER.info("Downloading version manifest ({})", MANIFEST_URL);
        try (InputStreamReader reader = new InputStreamReader(MANIFEST_URL.openStream())) {
            return GSON.fromJson(reader, VersionManifest.class);
        }
    }

    public GameVersion downloadGameVersion(URL url) throws IOException {
        LOGGER.info("Downloading game version ({})", url);
        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            return GSON.fromJson(reader, GameVersion.class);
        }
    }

    public GameVersion retrieveGameVersion(String id) throws IOException {
        VersionManifest versionManifest = this.downloadVersionManifest();

        Version version = id != null && !id.isEmpty() ? versionManifest.getVersion(id) : null;
        if (version == null) {
            LOGGER.info("No version specified. Retrieving latest stable version");
            version = versionManifest.getLatestVersion(VersionType.RELEASE);
        }

        LOGGER.info("Using Minecraft version {} ({})", version.getId(), version.getType());
        URL versionUrl = version.getUrl();
        return this.downloadGameVersion(versionUrl);
    }

    private static URL retrieveManifestUrl() {
        try {
            return new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json");
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

}

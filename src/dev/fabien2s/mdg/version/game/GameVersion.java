package dev.fabien2s.mdg.version.game;

import java.util.EnumMap;
import java.util.Map;

public class GameVersion {

    private final Map<GameDownloadType, GameDownloadInfo> downloads;

    public GameVersion() {
        this.downloads = new EnumMap<GameDownloadType, GameDownloadInfo>(GameDownloadType.class);
    }

    public GameDownloadInfo getDownloadInfo(GameDownloadType type) {
        return downloads.get(type);
    }

}

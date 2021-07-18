package dev.fabien2s.mdg.version.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GameVersion {

    @Getter private final String id;

    private final Map<GameDownloadType, GameDownloadInfo> downloads;

    public GameDownloadInfo getDownloadInfo(GameDownloadType type) {
        return downloads.get(type);
    }

}

package dev.fabien2s.mdg.version.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@RequiredArgsConstructor
public class GameDownloadInfo {

    @Getter private final URL url;
    @Getter private final int size;
    @Getter private final String sha1;

}

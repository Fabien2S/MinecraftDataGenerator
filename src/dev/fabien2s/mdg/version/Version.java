package dev.fabien2s.mdg.version;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URL;

@RequiredArgsConstructor
public final class Version {

    @Getter private final String id;
    @Getter private final VersionType type;
    @Getter private final URL url;
    @Getter private final String time;
    @Getter private final String releaseTime;

}

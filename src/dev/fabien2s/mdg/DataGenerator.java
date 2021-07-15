package dev.fabien2s.mdg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.extractor.DataExtractor;
import dev.fabien2s.mdg.extractor.GameExtractor;
import dev.fabien2s.mdg.mappings.MappingContext;
import dev.fabien2s.mdg.mappings.MappingParser;
import dev.fabien2s.mdg.mappings.MappingSyntaxException;
import dev.fabien2s.mdg.version.VersionManager;
import dev.fabien2s.mdg.version.game.GameDownloadInfo;
import dev.fabien2s.mdg.version.game.GameDownloadType;
import dev.fabien2s.mdg.version.game.GameVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Set;

public class DataGenerator {

    private static final Logger LOGGER = LogManager.getLogger(DataGenerator.class);

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Set<DataExtractor> EXTRACTORS = Set.of(
            new GameExtractor()
    );

    private final VersionManager versionManager = new VersionManager();

    public void generate(String versionId) throws IOException, MappingSyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        LOGGER.info("Retrieving Game Version");
        GameVersion gameVersion = versionManager.retrieveGameVersion(versionId);

        final MappingContext context = this.downloadMappings(gameVersion);
        final ServerRuntime serverRuntime = this.createRuntime(context, gameVersion);

        serverRuntime.initialize();
        serverRuntime.exportServerData();

        LOGGER.info("Initializing bootstrap");
        serverRuntime.invokeMethod("net.minecraft.server.Bootstrap", "bootStrap");

        LOGGER.info("Extracting data");
        for (DataExtractor extractor : EXTRACTORS) {
            LOGGER.info("Extracting {}", extractor.getName());

            final String name = extractor.getName();
            final File file = new File("generated", name + ".json");

            try (final JsonWriter writer = new JsonWriter(new FileWriter(file))) {
                writer.setIndent("    ");
                extractor.extract(serverRuntime, writer, GSON);
            } catch (Exception e) {
                LOGGER.error("Unable to execute " + name + " extractor", e);
            }
        }
    }

    private MappingContext downloadMappings(GameVersion version) throws MappingSyntaxException, IOException {
        GameDownloadInfo serverMappingsDownloadInfo = version.getDownloadInfo(GameDownloadType.SERVER_MAPPINGS);
        URL serverMappingsUrl = serverMappingsDownloadInfo.getUrl();
        LOGGER.info("Downloading Server Mappings ({})", serverMappingsUrl);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serverMappingsUrl.openStream()))) {
            MappingParser parser = new MappingParser(reader);

            LOGGER.info("Parsing Server Mappings ({})", serverMappingsUrl);
            return parser.parse();
        }
    }

    private ServerRuntime createRuntime(MappingContext context, GameVersion gameVersion) {
        GameDownloadInfo downloadInfo = gameVersion.getDownloadInfo(GameDownloadType.SERVER);
        URL serverJarUrl = downloadInfo.getUrl();
        return new ServerRuntime(context, serverJarUrl);
    }

    public static void main(String[] args) {
        DataGenerator dataGenerator = new DataGenerator();
        try {
            dataGenerator.generate(args.length > 0 ? args[0] : null);
        } catch (Exception e) {
            LOGGER.fatal("An error occurred while generating data", e);
        }
    }

}

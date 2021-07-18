package dev.fabien2s.mdg;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dev.fabien2s.mdg.extractor.DataExtractor;
import dev.fabien2s.mdg.extractor.scripted.ScriptedExtractor;
import dev.fabien2s.mdg.extractor.scripted.ScriptedExtractorManager;
import dev.fabien2s.mdg.mappings.MappingContext;
import dev.fabien2s.mdg.mappings.MappingParser;
import dev.fabien2s.mdg.mappings.MappingSyntaxException;
import dev.fabien2s.mdg.utils.FileUtils;
import dev.fabien2s.mdg.version.VersionManager;
import dev.fabien2s.mdg.version.game.GameDownloadInfo;
import dev.fabien2s.mdg.version.game.GameDownloadType;
import dev.fabien2s.mdg.version.game.GameVersion;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

@RequiredArgsConstructor
public class DataGenerator {

    private static final Logger LOGGER = LogManager.getLogger(DataGenerator.class);

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final File outputDirectory;
    private final File extractorsDirectory;

    private final VersionManager versionManager = new VersionManager();
    private final ScriptedExtractorManager extractorManager = new ScriptedExtractorManager();

    public void generate(String versionId) throws IOException, MappingSyntaxException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        LOGGER.debug("Validating output directory");
        FileUtils.ensureEmptyDirectory(this.outputDirectory);
        LOGGER.debug("Validating extractors directory");
        FileUtils.ensureDirectory(this.extractorsDirectory.getAbsoluteFile());

        LOGGER.info("Retrieving Game Version");
        GameVersion gameVersion = versionManager.retrieveGameVersion(versionId);

        final MappingContext context = this.downloadMappings(gameVersion);
        final ServerRuntime serverRuntime = this.createRuntime(context, gameVersion);


//        serverRuntime.exportServerData();

        File extractorFile = new File(extractorsDirectory, gameVersion.getId() + ".json");
        if(!extractorFile.exists()) {
            LOGGER.error("Missing extractor file for version {}. If this is a new release, you could try copying the extractor of the previous version", gameVersion.getId());
            throw new FileNotFoundException();
        }
        if(!extractorFile.isFile()) {
            LOGGER.error("The extractor for the {} version is not a file", gameVersion.getId());
            throw new IOException();
        }

        LOGGER.info("Loading extractors from {}", extractorFile);
        final ScriptedExtractor scriptedExtractor = this.extractorManager.loadExtractor(extractorFile);

        LOGGER.info("Initializing extractors");
        scriptedExtractor.initialize(serverRuntime);

        LOGGER.info("Executing extractors");
        for (DataExtractor extractor : scriptedExtractor.getExtractors()) {
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
        LOGGER.info("Downloading Server JAR ({})", serverJarUrl);
        return new ServerRuntime(context, serverJarUrl);
    }

    public static void main(String[] args) {
        try {
            File outputDirectory = new File("generated");
            File extractorDirectory = new File("extractors");
            DataGenerator dataGenerator = new DataGenerator(outputDirectory, extractorDirectory);
            dataGenerator.generate(args.length > 0 ? args[0] : null);
        } catch (Exception e) {
            LOGGER.fatal("An error occurred while generating data", e);
        }
    }

}

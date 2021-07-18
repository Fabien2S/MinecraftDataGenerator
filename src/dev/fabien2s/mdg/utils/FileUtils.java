package dev.fabien2s.mdg.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static void ensureEmptyDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (directory.isDirectory()) {

                final File[] files = directory.listFiles();
                if(files != null) {
                    for (File file : files) {
                        if (!file.delete())
                            throw new IOException("Unable to delete " + file);
                    }
                }

                if (!directory.delete())
                    throw new IOException("Unable to delete " + directory);
            } else
                throw new IOException(directory + " is not a directory");
        }

        if (!directory.mkdirs())
            throw new IOException("Unable to create " + directory);
    }

    public static void ensureDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory())
                throw new IOException(directory + " is not a directory");
        } else if (!directory.mkdirs())
            throw new IOException("Unable to create " + directory);
    }

}

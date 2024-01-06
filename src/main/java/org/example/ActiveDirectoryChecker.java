package org.example;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

public class ActiveDirectoryChecker {

    public Path findMostRecentDirectory(String directoryPath) {
        Path mostRecentFolder = null;
        LocalDateTime mostRecentDateTime = LocalDateTime.MIN;

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Path.of(directoryPath))) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    String folderName = path.getFileName().toString();
                    LocalDateTime folderDateTime = UtilParser.parseDateTime(folderName);
                    if (folderDateTime != null && folderDateTime.isAfter(mostRecentDateTime)) {
                        mostRecentFolder = path;
                        mostRecentDateTime = folderDateTime;
                    }
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
        return mostRecentFolder;
    }

    public boolean isDirectoryInUse(Path directory) {
        // Assumes directory contains this files.
        Path knownFile = directory.resolve("Login.log");
        Path tempCopy = directory.resolve("tempLogin.log");

        try {
            // Copy the file
            Files.copy(knownFile, tempCopy, StandardCopyOption.REPLACE_EXISTING);

            //Attempt to delete the original file
            Files.delete(knownFile);

            //Replace file with the copy
            Files.move(tempCopy, knownFile, StandardCopyOption.REPLACE_EXISTING);
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            try {
                Files.deleteIfExists(tempCopy);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
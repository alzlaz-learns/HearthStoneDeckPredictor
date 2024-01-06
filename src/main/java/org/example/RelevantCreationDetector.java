package org.example;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class RelevantCreationDetector {
//    https://www.baeldung.com/java-nio2-watchservice
    private static final String POWERLOG = "Power.log";
    public String folderdetector() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            // Replace with the target path
            Path path = Paths.get("E:\\Hearthstone\\Logs");
            path.register(watchService, ENTRY_CREATE);

            System.out.println("Monitoring directory for changes...");

            while (true) {
                WatchKey key;
                key = watchService.take(); // This line blocks until something happens

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == ENTRY_CREATE) {
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path fileName = ev.context();
                        if (Files.isDirectory(path.resolve(fileName))) {
                            System.out.println("New folder created: " + fileName);
                            return fileName.toString();
                        }

                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    return null;
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public String fileDetector(String targetFolder) {

        Path p =  Paths.get(targetFolder, POWERLOG);
        if(Files.exists(p)){
            return p.toString();
        }
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Path path = Paths.get(targetFolder);
            path.register(watchService, ENTRY_CREATE);

            System.out.println("Monitoring directory for changes...");

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == ENTRY_CREATE) {
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path fileName = ev.context();
                        if (fileName.toString().equals(POWERLOG)) {
                            System.out.println("Target file created: " + fileName);
                            return fileName.toString();
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    return null;
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }
}

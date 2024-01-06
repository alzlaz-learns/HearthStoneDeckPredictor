package org.example;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogParser implements Runnable{
    /*
        Not exactly sure how I want to do it and what structure I want to do it
        but I guess the goal is to
        -parse the document an initial time to get important key data such as:
            -recognizing which is the current game.
            -determine who is friendly player and enemy (which i think I got thanks Hearthsims).
            -determine start of the match and start reading from there.

         -then to access it Power.log to read and update important events.
         -determine and store the relevant events.


    */



    private final String filePath;
    private long lastPosition = 0;

    public LogParser(String filePath) {
        this.filePath = filePath;
    }

    public void processCurrentGame(){
        long startPosition = findStartOfCurrentGame();
        if (startPosition == -1) {
            System.out.println("Could not find the start of the current game.");
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            file.seek(startPosition);
            String line;
            while ((line = file.readLine()) != null) {
                processLine(line);
            }


            while (true) {
                if (file.getFilePointer() < file.length()) {
                    line = file.readLine();
                    if (line != null) {
                        processLine(line);
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }  catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processLine(String line) {
        // test display
        System.out.println(line);
    }

    private long findStartOfCurrentGame(){
        final String CREATEGAME = "CREATE_GAME";

        try(RandomAccessFile file = new RandomAccessFile(filePath, "r")){
            long fileLength = file.length();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            long pos = fileLength - bufferSize;



            while(pos >= 0){
                int readLength = file.read(buffer);
                String chunk = new String(buffer, 0, readLength);
                String[] lines = chunk.split("\n");
                // read lines in reverse
                for (int i = lines.length - 1; i >= 0; i--) {
                    if (lines[i].contains(CREATEGAME)) {
                        // Return the position of the start of this line
                        long startOfLine = pos + chunk.lastIndexOf("\n", chunk.lastIndexOf(lines[i]));
                        return startOfLine + 1;
                    }
                }
                //moves pos backward
                pos -= (bufferSize - CREATEGAME.length());
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return -1;  // Return -1 if the start marker is not found
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Thread.sleep(1000);
                readFile();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            file.seek(lastPosition);
            String line;
            while ((line = file.readLine()) != null) {
                System.out.println(line); // test output of reading lines.
                lastPosition = file.getFilePointer();
            }
        }
    }

    public boolean friendlyMatcher(String line){
        Pattern pattern = Pattern.compile("SHOW_ENTITY - Updating Entity=\\[.*? id=(\\d+) .* CardID=\\w+");

        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }


    public void searchForFriendlyPlayer(){

        int friendlyId = -1;
        int count = 0;

        try (Stream<String> stream = Files.lines(Paths.get(this.filePath))) {
            Iterator<String> iterator = stream.iterator();

            while (iterator.hasNext() && friendlyId == -1) {
                String line = iterator.next();
                count++;
                // System.out.println(line + " lines processed " + count.get());
                if (friendlyMatcher(line)) {
                    Pattern pattern = Pattern.compile("player=(\\d+)");
                    Matcher matcher = pattern.matcher(line);

                    if (matcher.find()) {
                        // Update the array value
                        friendlyId = Integer.parseInt(matcher.group(1));
                        System.out.println("this here is the friendlyId: " + friendlyId);
                        break;
                    }
                }
            }
            System.out.println("last line processed " + count);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

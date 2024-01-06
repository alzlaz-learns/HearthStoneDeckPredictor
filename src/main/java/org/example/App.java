package org.example;

import java.nio.file.Path;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//        LogParser lp = new LogParser("E:\\Hearthstone\\Logs\\Hearthstone_2024_01_01_20_26_37\\Power.log");
//        lp.searchForFriendlyPlayer();
        ActiveDirectoryChecker ADC = new ActiveDirectoryChecker();
////
        Path latest = ADC.findMostRecentDirectory("E:\\Hearthstone\\Logs");
        System.out.println(latest.toString());

        RelevantCreationDetector rcd = new RelevantCreationDetector();
        String currentDirectory = null;
        String currentPowerLog = null;
        if(ADC.isDirectoryInUse(latest) == true){
            System.out.println(latest + ": is currently in use.");
            currentDirectory = latest.toString();
        }
        else{
            System.out.println("No current folder is in use");
            currentDirectory = rcd.folderdetector();
        }


        currentPowerLog = rcd.fileDetector(currentDirectory);
        System.out.println(currentPowerLog);


        LogParser lp = new LogParser(currentPowerLog);
        lp.processCurrentGame();



    }
}

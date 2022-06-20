package org.example.TimeManager;

import org.example.TimeManager.util.Pair;
import org.example.TimeManager.util.Utils;

import java.util.Date;

public class TimeManager {
    public static void add(int inc) {
        int time = inc;
        Logger logger = Logger.getSingleton();
        if(!logger.isEmpty()) {
            Pair<Date, Integer> pair = Utils.parseLine(logger.getLastLine());
            if(Utils.areDatesEqual(pair.getFirst(), new Date())) {
                time += pair.getSecond();
                logger.popLine();
            }
        }
        logger.addLine(Utils.getLine(new Date(), time));
    }

    private static Date timerStartDate = null;

    public static boolean isTimerRunning() {
        return timerStartDate != null;
    }

    public static void startTimer() {
        timerStartDate = new Date();
    }

    public static int stopTimer() {
        int time = (int) (new Date().getTime() - timerStartDate.getTime()) / 1000;
        timerStartDate = null;
        return time;
    }

    public static int getTodayTime() {
        int time = 0;
        Logger logger = Logger.getSingleton();
        if(!logger.isEmpty()) {
            Pair<Date, Integer> last = Utils.parseLine(logger.getLastLine());
            if (last.getFirst().after(new Date(new Date().getTime() - 86400000))) {
                time += last.getSecond();
            }
        }
        if(isTimerRunning()) {
            time += (int) (new Date().getTime() - timerStartDate.getTime()) / 1000;
        }
        return time;
    }

    public static int getTimeBetweenDates(Date date1, Date date2) {
        Logger.getSingleton();
        int time = 0;
        for(String line : Logger.getLines()) {
            Pair<Date, Integer> pair = Utils.parseLine(line);
            if((pair.getFirst().after(date1) && pair.getFirst().before(date2)) || pair.getFirst().equals(date1) || pair.getFirst().equals(date2)) {
                time += pair.getSecond();
            }
        }
        return time;
    }
}

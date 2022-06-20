package org.example.TimeManager.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

    public static String[] removeArrayElement(String[] arr, int index) {
        String[] arrDestination = new String[arr.length - 1];
        int remainingElements = arr.length - (index + 1);
        System.arraycopy(arr, 0, arrDestination, 0, index);
        System.arraycopy(arr, index + 1, arrDestination, index, remainingElements);
        return arrDestination;
    }

    public static Pair<Date, Integer> parseLine(String line) {
        String[] s = line.split(";");
        Date date = null;
        int time = 0;
        try {
            date = dateFormat.parse(s[0]);
            time = Integer.parseInt(s[1]);
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
        return new Pair<>(date, time);
    }

    public static String getLine(Date date, int time) {
        return dateFormat.format(date) + ";" + time;
    }

    public static boolean areDatesEqual(Date date1, Date date2) {
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDay() == date2.getDay();
    }

    public static ArrayList<String> getLines(Path path) {
        ArrayList<String> lines = null;
        try {
            lines = (ArrayList<String>) Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeLines(Path path, ArrayList<String> lines) {
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

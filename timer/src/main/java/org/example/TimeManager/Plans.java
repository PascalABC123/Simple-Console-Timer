package org.example.TimeManager;

import org.example.TimeManager.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Plans {
    private static final Path plans = Path.of(AppPlatform.getPlansPath());

    static {
        if (!Files.exists(plans)) {
            try {
                Files.createFile(plans);
                Files.write(plans, new ArrayList<>() {{
                    add("#plans");
                    add("#end");
                }});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static final String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    public static void requestCreate() {
        try {
            System.out.println("Enter plan name:");
            String name = reader.readLine();
            Date start = getStartDate(), end = getEndDate();
            int[] days = new int[7];
            for (int i = 0; i < 7; i++) {
                days[i] = getDay(dayNames[i]);
            }
            create(name, start, end, days);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy.MM.dd");

    private static void create(String name, Date start, Date end, int[] days) {
        ArrayList<String> lines = Utils.getLines(plans);
        lines.add(1, name);
        lines.add("#" + name);
        lines.add(inputDateFormat.format(start));
        lines.add(inputDateFormat.format(end));
        for (int time : days) {
            lines.add(String.valueOf(time));
        }
        Utils.writeLines(plans, lines);
    }

    private static Date getStartDate() throws IOException {
        try {
            System.out.println("Enter plan start date (yyyy.MM.dd):");
            return inputDateFormat.parse(reader.readLine());
        } catch (ParseException e) {
            System.out.println("Wrong date format! Enter yyyy.MM.dd.");
            return getStartDate();
        }
    }

    private static Date getEndDate() throws IOException {
        try {
            System.out.println("Enter plan end date (yyyy.MM.dd):");
            return inputDateFormat.parse(reader.readLine());
        } catch (ParseException e) {
            System.out.println("Wrong date format! Enter yyyy.MM.dd.");
            return getStartDate();
        }
    }

    private static int getDay(String name) throws IOException {
        try {
            System.out.printf("Enter time for %s (hr:min:sec):\n", name);
            String[] input = reader.readLine().split(":");
            return Integer.parseInt(input[0]) * 3600 + Integer.parseInt(input[1]) * 60 + Integer.parseInt(input[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Wrong data format! Enter hr:min:sec.");
            return getDay(name);
        }
    }

    public static ArrayList<String> getPlanNamesList() {
        ArrayList<String> lines = Utils.getLines(plans);
        ArrayList<String> planNames = new ArrayList<>();
        for (int i = 1; !lines.get(i).equals("#end"); i++) {
            planNames.add(lines.get(i));
        }
        return planNames;
    }

    public static ArrayList<Plan> getPlanList() {
        ArrayList<String> planNames = getPlanNamesList();
        ArrayList<Plan> list = new ArrayList<>();
        for(String name : planNames) {
            list.add(getPlanByName(name));
        }
        return list;
    }

    public static ArrayList<Plan> getCurrentPlansList() {
        ArrayList<Plan> list = new ArrayList<>();
        Date now = new Date();
        for(Plan plan : Plans.getPlanList()) {
            if(plan.start().before(now) && plan.end().after(now)) {
                list.add(plan);
            }
        }
        return list;
    }

    public static Plan getPlanByName(String name) {
        try {
            ArrayList<String> lines = Utils.getLines(plans);
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).equals("#" + name)) {
                    int[] days = new int[7];
                    for(int d = 0; d < 7; d++) {
                        days[d] = Integer.parseInt(lines.get(i + 3 + d));
                    }
                    return new Plan(name, inputDateFormat.parse(lines.get(i + 1)), inputDateFormat.parse(lines.get(i + 2)), days);
                }
            }
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getPlanTime(Plan plan) {
        return getPlanTimeBetweenDates(plan, plan.start(), plan.end());
    }

    public static int getPlanTimeBetweenDates(Plan plan, Date date1, Date date2) {
        date1 = new Date(date1.getTime());
        int time = 0;
        while(date1.getTime() <= date2.getTime()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            time += plan.days()[(calendar.get((Calendar.DAY_OF_WEEK)) + 5) % 7];
            date1.setTime(date1.getTime() + 86400000);
        }
        return time;
    }

    public static int getDebt(Plan plan) {
        int time = getPlanTimeBetweenDates(plan, plan.start(), new Date());
        time -= TimeManager.getTimeBetweenDates(plan.start(), new Date());
        return time;
    }

    public static double getFulfill(Plan plan) {
        return TimeManager.getTimeBetweenDates(plan.start(), plan.end()) * 100f / getPlanTime(plan);
    }
}

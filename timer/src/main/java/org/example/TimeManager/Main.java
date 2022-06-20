package org.example.TimeManager;

import org.example.TimeManager.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static org.example.TimeManager.Main.Cmd.executeRawCommand;

public class Main {
    public static long Iteration = 0;

    private static final String path = AppPlatform.getApplicationDataFolder();
    private static final Logger logger = Logger.getSingleton();

    public static void main(String[] args) {
        while (true) {
            try {
                readCmd();
                Iteration++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final BufferedReader reader;

    static {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    private static void readCmd() throws IOException {
        executeRawCommand(reader.readLine());
    }

    static class Cmd {
        protected static void executeRawCommand(String s) {
            s = s.trim();
            String[] cmd = s.split("\\.");
            for (int i = 0; i < cmd.length; i++) {
                cmd[i] = cmd[i].trim().toLowerCase(Locale.ROOT);
            }
            if (cmd.length > 0) {
                execute(cmd[0], Utils.removeArrayElement(cmd, 0));
            }
        }

        protected static void execute(String cmd, String[] args) {
            switch (cmd) {

                case "add":
                case "a":
                    if (!checkArgs(args, 3)) {
                        return;
                    }

                    int time = 0;
                    try {
                        time += Integer.parseInt(args[0]) * 3600; // hours
                        time += Integer.parseInt(args[1]) * 60; // minutes
                        time += Integer.parseInt(args[2]); // seconds
                    } catch (NumberFormatException e) {
                        error(ErrorCode.WRONG_ARGUMENT_TYPE);
                        return;
                    }
                    TimeManager.add(time);
                    break;

                case "timer":
                case "t":
                    if (!checkArgs(args, 1)) {
                        return;
                    }

                    if (Objects.equals(args[0], "start")) {
                        if (TimeManager.isTimerRunning()) {
                            error(ErrorCode.TIMER_ALREADY_STARTED);
                            return;
                        }
                        TimeManager.startTimer();
                    } else if (Objects.equals(args[0], "stop")) {
                        int timer = TimeManager.stopTimer();
                        TimeManager.add(timer);
                        System.out.printf("Timer stopped! %d hr %d min %d sec were added.\n", timer / 3600, (timer % 3600) / 60, timer % 60);
                        return;
                    } else {
                        error(ErrorCode.WRONG_ARGUMENT_TYPE);
                        return;
                    }

                    break;

                case "plan":
                case "plans":
                case "p":
                    if (!checkMoreArgs(args, 0)) {
                        return;
                    }

                    if (Objects.equals(args[0], "create")) {
                        Plans.requestCreate();
                    } else if (Objects.equals(args[0], "list")) {
                        System.out.println("Plans:");
                        Plans.getPlanNamesList();
                        for (String s : Plans.getPlanNamesList()) {
                            System.out.println("- " + s);
                        }
                        return;
                    } else if (Objects.equals(args[0], "fulfill")) {
                        System.out.println("Accomplishment:");
                        if (args.length == 2 && args[1].equals("hours")) {
                            for (Plan plan : Plans.getCurrentPlansList()) {
                                System.out.printf("%s: %d/%d hr\n", plan.name(), TimeManager.getTimeBetweenDates(plan.start(), plan.end()) / 3600, Plans.getPlanTime(plan) / 3600);
                            }
                        } else {
                            for (Plan plan : Plans.getCurrentPlansList()) {
                                System.out.printf("%s: %.3f%%\n", plan.name(), Plans.getFulfill(plan));
                            }
                        }
                        return;
                    } else if (Objects.equals(args[0], "fulfill_g")) {
                        System.out.println("Accomplishment:");
                        int hours1 = 0, hours2 = 0;
                        for (Plan plan : Plans.getPlanList()) {
                            hours1 += TimeManager.getTimeBetweenDates(plan.start(), plan.end()) / 3600;
                            hours2 += Plans.getPlanTime(plan) / 3600;
                        }
                        if (args.length == 2 && args[1].equals("hours")) {
                            System.out.printf("Global: %d/%d hr\n", hours1, hours2);
                        } else {
                            System.out.printf("Global: %.3f%%\n", hours1 / hours2 * 100f);
                        }
                        return;
                    } else if (Objects.equals(args[0], "current")) {
                        System.out.println("Current plans:");
                        for (Plan plan : Plans.getCurrentPlansList()) {
                            System.out.println("- " + plan.name());
                        }
                        return;
                    } else if (Objects.equals(args[0], "today")) {
                        for (Plan plan : Plans.getCurrentPlansList()) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            int last = plan.days()[(calendar.get((Calendar.DAY_OF_WEEK)) + 5) % 7] - TimeManager.getTodayTime();
                            if (last >= 0) {
                                System.out.printf("%s - %d hr %d min %d sec\n", plan.name(), last / 3600, last % 3600 / 60, last % 60);
                            } else {
                                System.out.println(plan.name() + " - accomplished!");
                            }
                        }
                        return;
                    } else if (Objects.equals(args[0], "debt")) {
                        System.out.println("Debt:");
                        for(Plan plan : Plans.getCurrentPlansList()) {
                            int hrs = Plans.getDebt(plan) / 3600;
                            if(hrs > 0) {
                                System.out.printf("%s: -%d hr\n", plan.name(), hrs);
                            } else {
                                System.out.printf("%s: +%d hr\n", plan.name(), -hrs);
                            }
                        }
                        return;
                    } else {
                        error(ErrorCode.WRONG_ARGUMENT_TYPE);
                        return;
                    }
                    break;

                default:
                    error(ErrorCode.COMMAND_NOT_FOUND, args.length, 1);
                    return;
            }
            System.out.println("Successful!");
        }

        protected static void error(ErrorCode code, int... args) {
            System.out.println("ERROR!");
            switch (code) {
                case COMMAND_NOT_FOUND -> {
                    System.out.println("Command not found!");
                }
                case WRONG_AMOUNT_OF_ARGUMENTS -> {
                    int prov = args[0], need = args[1];
                    if (prov == 0) {
                        System.out.printf("No arguments provided! %d needed.", need);
                    }
                    if (prov > need) {
                        System.out.printf("Too few arguments provided! %d needed.", need);
                    }
                    if (prov < need && prov != 0) {
                        System.out.printf("Too much arguments provided! %d needed.", need);
                    }
                }
                case WRONG_ARGUMENT_TYPE -> {
                    System.out.println("Wrong argument type!");
                }
                case TIMER_ALREADY_STARTED -> {
                    System.out.println("Timer already started! Use \"timer.stop\" to stop it.");
                }
            }
            System.out.println('\n');
        }

        private static boolean checkArgs(String[] args, int need) {
            if (args.length != need) {
                error(ErrorCode.WRONG_AMOUNT_OF_ARGUMENTS, args.length, need);
            }
            return args.length == need;
        }

        private static boolean checkMoreArgs(String[] args, int need) {
            if (args.length <= need) {
                error(ErrorCode.WRONG_AMOUNT_OF_ARGUMENTS, args.length, need + 1);
            }
            return args.length > need;
        }
    }
}

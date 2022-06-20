package org.example.TimeManager;

import java.io.File;

public class AppPlatform {
    public static final String APP_NAME = "Time-Manager";

    private static final String userDataFolder = System.getenv("APPDATA") + "\\";
    private static final String applicationDataFolder = userDataFolder + APP_NAME + "\\";
    private static final String configPath = applicationDataFolder + "config";
    private static final String loggingPath = applicationDataFolder + "log";
    private static final String plansPath = applicationDataFolder + "plans";

    public static String getApplicationDataFolder() {
        new File(applicationDataFolder).mkdirs();
        return applicationDataFolder;
    }

    public static String getConfigPath() {
        return configPath;
    }

    public static String getLoggingPath() {
        return loggingPath;
    }

    public static String getPlansPath() {
        return plansPath;
    }

}
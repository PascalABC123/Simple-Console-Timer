package org.example.TimeManager;

import org.example.TimeManager.util.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Logger {
    private static volatile Logger singleton;

    private static final Path log = Path.of(AppPlatform.getLoggingPath());

    public static synchronized Logger getSingleton() {
        if (singleton == null) singleton = new Logger();
        return singleton;
    }

    private static boolean unset = false;

    public static boolean isUnset() {
        return unset;
    }

    static {
        if (!Files.exists(log)) {
            unset = true;
            try {
                Files.createFile(log);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void addLine(String line) {
        try (var writer = new FileWriter(log.toString(), true)) {
            writer.write(line + '\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void popLine() {
        ArrayList<String> lines = Utils.getLines(log);
        if (lines.size() == 0) {
            return;
        }
        lines.remove(lines.size() - 1);
        try {
            Files.write(log, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getLastLine() {
        ArrayList<String> lines = Utils.getLines(log);
        if (lines.size() == 0) {
            return null;
        }
        return lines.get(lines.size() - 1);
    }

    public static ArrayList<String> getLines() {
        return Utils.getLines(log);
    }

    public synchronized boolean isEmpty() {
        return Utils.getLines(log) == null || Utils.getLines(log).size() == 0;
    }
}

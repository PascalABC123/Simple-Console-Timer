package org.example.TimeManager;

import java.util.Date;

public record Plan(String name, Date start, Date end, int[] days) {

}

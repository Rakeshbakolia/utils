package com.techi.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Constants {

    private Constants() {
    }

    public static final long SECONDS_IN_A_DAY = 86400;
    public static final long DAYS_SEC = 86399;
    public static final long GMT_IST_DIFF = 19800;
    public static final long SECONDS_IN_A_HOUR = 3600;
    public static final long SECONDS_IN_A_MIN = 60;
    public static final String ASIA_ZONE = "Asia/Kolkata";
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
}


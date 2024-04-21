package com.techi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.techi.utils.Constants.*;


public class DateUtil {

    private DateUtil() {
    }

    @Data
    @AllArgsConstructor
    public static class TimeData {
        private Long fromTime;
        private Long toTime;
    }

    public static Long currentTimeSec() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getTimeString(Long epocInSec) {
        Date date = new Date(epocInSec * 1000);
        return TIME_FORMAT.format(date);
    }

    public static Long getDayEpochEndTimeFromEpochTime(@NonNull Long epoc) {
        return (epoc / SECONDS_IN_A_DAY) * SECONDS_IN_A_DAY + SECONDS_IN_A_DAY - GMT_IST_DIFF;
    }

    public static String getDateAndTimeString(@NonNull Long epoc) {
        Date date = new Date(epoc * 1000);
        return DATE_FORMAT.format(date);
    }

    public static long getDiffInDays(@NonNull Long fromTime, @NonNull Long toTime) {
        return (toTime - fromTime) / (24 * 60 * 60);
    }

    public static String convertToTime(@NonNull Long timeDiff) {
        long days = timeDiff / SECONDS_IN_A_DAY;

        long secInDays = timeDiff - (days * SECONDS_IN_A_DAY);
        long hours = secInDays / SECONDS_IN_A_HOUR;

        long secInHours = secInDays - (hours * SECONDS_IN_A_HOUR);
        long mins = secInHours / SECONDS_IN_A_MIN;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" days ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hours ");
        }
        sb.append(mins).append(" mins");
        return sb.toString();
    }

    public static List<Long> getDayStartEpoch(@NonNull Long firstDayStart, @NonNull Long lastDayEnd) {
        List<Long> timeDataList = new ArrayList<>();
        long diffInDays = getDiffInDays(firstDayStart, lastDayEnd);
        if (diffInDays == 0) {
            timeDataList.add(firstDayStart);
            return timeDataList;
        }
        timeDataList.add(firstDayStart);
        long prevStart = firstDayStart;
        for (long timer = 1; timer < diffInDays; timer++) {
            long start = prevStart + SECONDS_IN_A_DAY;
            prevStart = start;
            timeDataList.add(start);
        }
        return timeDataList;
    }

    public static LocalDateTime dateToLocalDateTime(@NonNull Date date, String zone) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of(zone));
    }

    public static Date localDateTimeToDate(@NonNull LocalDateTime startOfDay, String zone) {
        return Date.from(startOfDay.atZone(ZoneId.of(zone)).toInstant());
    }

    public static Long getEndOfDayEpocInSec(Long startTimeInSec, String zone) {
        Date date = new Date(startTimeInSec * 1000);
        LocalDateTime localDateTime = dateToLocalDateTime(date, zone);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        date = localDateTimeToDate(endOfDay, zone);
        return date.getTime() / 1000;
    }

    public static Long getStartOfDayEpocInSec(Long endTimeInSec, String zone) {
        Date date = new Date(endTimeInSec * 1000);
        LocalDateTime localDateTime = dateToLocalDateTime(date, zone);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        date = localDateTimeToDate(startOfDay, zone);
        return date.getTime() / 1000;
    }

    public static List<TimeData> getDayRangeEpoch(Long firstDayStart, Long lastDayEnd, String zone) {
        List<TimeData> timeDataList = new ArrayList<>();
        long diffInDays = getDiffInDays(firstDayStart, lastDayEnd);
        if (diffInDays == 0) {
            TimeData timeData = new TimeData(firstDayStart, lastDayEnd);
            timeDataList.add(timeData);
            return timeDataList;
        }
        Long firstDayEnd = getEndOfDayEpocInSec(firstDayStart, zone);
        TimeData firstDay = new TimeData(firstDayStart, firstDayEnd);
        timeDataList.add(firstDay);
        long prevStart = firstDayEnd + 1;
        for (long timer = 1; timer < diffInDays; timer++) {
            long currEnd = prevStart + DAYS_SEC;
            TimeData data = new TimeData(prevStart, currEnd);
            timeDataList.add(data);
            prevStart = currEnd + 1;
        }
        Long lastDayStart = getStartOfDayEpocInSec(lastDayEnd, zone);
        TimeData lastDay = new TimeData(lastDayStart, lastDayEnd);
        timeDataList.add(lastDay);
        return timeDataList;
    }

    public static Long secondsAtToday12AM(String zone) {
        return getStartOfDayEpocInSec(currentTimeSec(), zone);
    }

    public static Long secondsAtTomorrow12AM(String zone) {
        return secondsAtToday12AM(zone) + SECONDS_IN_A_DAY;
    }

    public static Long secondsAtYesterday12AM(String zone) {
        return secondsAtToday12AM(zone) - SECONDS_IN_A_DAY;
    }

    public static List<TimeData> getAnyEpochTimeRangeInEpochDayWiseRange(@NonNull Long startTime, @NonNull Long endTime, @NonNull String zone) {
        List<TimeData> timeDataList = new ArrayList<>();

        ZoneId zoneId = ZoneId.of(zone);
        // Get Local Date Time
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(startTime), zoneId);
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(endTime), zoneId);

        // Get Local Date
        LocalDate startLocalDate = Instant.ofEpochSecond(startTime).atZone(zoneId).toLocalDate();
        LocalDate endLocalDate = Instant.ofEpochSecond(endTime).atZone(zoneId).toLocalDate();

        // the comparator value, negative if less, positive if greater
        int diffDays = 0;
        for (LocalDate startDate = startLocalDate; startDate.isBefore(endLocalDate); startDate = startDate.plusDays(1)) {
            ++diffDays;
        }
        LocalDate currentStart = startLocalDate;
        LocalDateTime newStartTime = null;
        LocalDateTime newEndTime = null;
        // Two cases, where same day or different day
        if (diffDays == 0) {
            newStartTime = startLocalDateTime;
            newEndTime = endLocalDateTime;
            timeDataList.add(new TimeData(newStartTime.atZone(zoneId).toEpochSecond(), newEndTime.atZone(zoneId).toEpochSecond()));
        } else {
            LocalDate prevStart = null;
            while (diffDays > 0) {
                if (prevStart == null) {
                    newStartTime = startLocalDateTime;
                    newEndTime = currentStart.atTime(LocalTime.MAX);
                    prevStart = currentStart;
                    timeDataList.add(new TimeData(newStartTime.atZone(zoneId).toEpochSecond(), newEndTime.atZone(zoneId).toEpochSecond()));
                } else {
                    currentStart = prevStart.plusDays(1);
                    newStartTime = currentStart.atTime(LocalTime.MIDNIGHT);
                    newEndTime = currentStart.atTime(LocalTime.MAX);
                    timeDataList.add(new TimeData(newStartTime.atZone(zoneId).toEpochSecond(), newEndTime.atZone(zoneId).toEpochSecond()));
                    prevStart = currentStart;
                }
                --diffDays;
            }
            assert prevStart != null;
            currentStart = prevStart.plusDays(1);
            newStartTime = currentStart.atTime(LocalTime.MIDNIGHT);
            newEndTime = endLocalDateTime;
            timeDataList.add(new TimeData(newStartTime.atZone(zoneId).toEpochSecond(), newEndTime.atZone(zoneId).toEpochSecond()));
        }
        return timeDataList;
    }

    public static Long getStartTimeOfMonth(String zone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime() / 1000;
    }

    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }

    public static LocalDateTime getLocalDateTimeFromEpoch(Long epochTime, String zone) {
        if(null!=epochTime) {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.of(zone));
        }else{
            return null;
        }
    }

    public static Date convertStringToDate(String date, String pattern) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.parse(date);

    }
    public static Date getDateFromEpochInSec(Long epochTime){
        return new Date(epochTime*1000);
    }

    public static String ymdDateFormat(Date time, String zone){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dtf.format(dateToLocalDateTime(time, zone));
    }
}

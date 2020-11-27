package org.star.jaising.jarmony.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author zhengjiaxing
 * @description 日期工具类
 * @date 2020/11/5
 */
@Slf4j
public class TimeUtils {

    public static final String DATE_TIME_FORMATTER_CONTINUES = "yyyyMMddHHmmss";
    public static final String DATE_FORMATTER_STANDARD = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMATTER_STANDARD = "yyyy-MM-dd HH:mm:ss";

    /**
     * 当前时间格式化，yyyyMMddHHmmss
     */
    public static String now() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_CONTINUES);
        return now.format(dateTimeFormatter);
    }

    /**
     * 时间格式化
     * ISO Zoned DateTime -> yyyy-MM-dd HH:mm:ss
     */
    public static String convertISOZonedDateTime2Standard(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_STANDARD);
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 时间格式转化
     * yyyyMMddHHmmss -> yyyy-MM-dd
     */
    public static String convertTime(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_CONTINUES));
        LocalDate localDate = localDateTime.toLocalDate();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER_STANDARD);
        return localDate.format(dateTimeFormatter);
    }

    /**
     * 时间格式转化
     * yyyyMMddHHmmss -> yyyy-MM-dd HH:mm:ss
     */
    public static String convertDateTime(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_CONTINUES));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_STANDARD);
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 时间格式转化
     * yyyy-MM-dd HH:mm:ss -> LocalDateTime
     */
    public static LocalDateTime convert2LocalDateTime(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_STANDARD));
    }

    /**
     * 时间格式转化
     * yyyy-MM-dd -> yyyyMMddHHmmss，如2020-11-11 -> 20201111000000
     */
    public static String revertTime(String time) {
        LocalDate localDate = LocalDate.parse(time, DateTimeFormatter.ofPattern(DATE_FORMATTER_STANDARD));
        LocalTime localTime = LocalTime.MIDNIGHT;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER_CONTINUES);
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * Date转LocalDate
     * @param date          Date
     * @return LocalDate    LocalDate
     */
    public static LocalDate date2LocalDate(Date date) {
        if(null == date) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * LocalDate转Date
     * @param localDate         LocalDate
     * @return                  Date
     */
    public static Date localDate2Date(LocalDate localDate) {
        if(null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * localDateTime转Date
     * @param localDateTime         LocalDateTime
     * @return                      Date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        if(null == localDateTime) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * localDateTime转Date
     * @param date         Date
     * @return             LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        if(null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 获取毫秒时间戳
     * @param localDateTime     LocalDateTime
     * @return                  毫秒时间戳
     */
    public static long convertMilliSeconds(LocalDateTime localDateTime) {
        if(null == localDateTime) {
            return 0;
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        return instant.toEpochMilli();
    }

}

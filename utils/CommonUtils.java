package com.sgtc.countdown.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {
    // 生成唯一hash值id
    public static String generateId() {
        return String.valueOf((int) System.currentTimeMillis());
    }

    // 获取当前时间并格式化 00:00
    public static String getCurrentTimeText() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return currentTime.format(formatter);
    }

    public static String getTimeText(int time) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        if (hours == 0) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

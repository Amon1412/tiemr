package com.sgtc.countdown.data;

import android.content.Context;

import com.sgtc.countdown.R;
import com.sgtc.countdown.utils.CommonUtils;

public class TimerEntity {
    private int totalSeconds;
    private int curSeconds;
    private boolean isRunning;
    private String id;

    public TimerEntity(int totalSeconds) {
        this.totalSeconds = totalSeconds;
        this.curSeconds = totalSeconds;
        this.id = CommonUtils.generateId();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setCurSeconds(int curSeconds) {
        if (curSeconds <= 0) {
            curSeconds = 0;
        }
        this.curSeconds = curSeconds;
    }

    public int getCurSeconds() {
        return curSeconds;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public String getCurTimeString() {
        return CommonUtils.getTimeText(curSeconds);
    }
    
    public String getTotalTimeString(Context context) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours != 0 && minutes == 0 && seconds == 0) {
            return hours + context.getString(R.string.hour);
        } else if (minutes != 0 && hours == 0 && seconds == 0) {
            return minutes + context.getString(R.string.minute);
        } else if (seconds != 0 && hours == 0 && minutes == 0) {
            return seconds + context.getString(R.string.second);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

    }

    public String getId() {
        return id;
    }

    public void reset() {
        curSeconds = totalSeconds;
        isRunning = true;
    }
}

package com.sgtc.countdown.data;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sgtc.countdown.activity.TimerCompleteActivity;

import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.apache.commons.collections4.set.ListOrderedSet;

public class Data {
    // 单例模式
    private static Data instance;

    DatabaseHelper databaseHelper;
    private Context context;

    public static Data init(Context context) {
        if (instance == null) {
            instance = new Data(context);
        }
        return instance;
    }

    public static Data getInstance() {
        return instance;
    }

    private Data(Context context) {
        this.context = context;
        this.databaseHelper = DatabaseHelper.getInstance();
    }


    private ListOrderedMap<String, TimerEntity> timers = new ListOrderedMap<>();
    private ListOrderedSet<Integer> recentTimer = new ListOrderedSet<>();
    private ListOrderedSet<Integer> defaultTimer = new ListOrderedSet<>();


    public ListOrderedSet<Integer> getRecentTimer() {
        Log.e("TAG", "getRecentTimer: recentTimer.size "+recentTimer.size());
        return recentTimer;
    }

    public ListOrderedSet<Integer> getDefaultTimer() {
        Log.e("TAG", "getDefaultTimer: defaultTimer.size "+defaultTimer.size());

        return defaultTimer;
    }

    private void setDefaultTimer(ListOrderedSet<Integer> defaultTimer) {
        this.defaultTimer.clear();
        this.defaultTimer.addAll(defaultTimer);
        Log.e("TAG", "setDefaultTimer: defaultTimer.size "+this.defaultTimer.size());

    }

    private void setRecentTimer(ListOrderedSet<Integer> recentTimer) {
        this.recentTimer.clear();
        this.recentTimer.addAll(recentTimer);
        Log.e("TAG", "setRecentTimer: recentTimer.size "+this.recentTimer.size());

    }

    public ListOrderedMap<String, TimerEntity> getTimers() {
        return timers;
    }

    public int addTimer(TimerEntity timer) {
        timers.put(timer.getId(), timer);
        return timers.size() - 1;
    }

    public TimerEntity getTimerByIndex(int index) {
        return timers.getValue(index);
    }

    public TimerEntity getTimerById(String id) {
        return timers.get(id);
    }

    public int removeTimer(int index) {
        int lastIndex;
        if (timers.size() == 1) {
            lastIndex = -1;
        } else {
            if (timers.size() == index + 1) {
                lastIndex = index - 1;
            } else {
                lastIndex = index;
            }
        }

        String id = timers.get(index);
        timers.remove(id);
        return lastIndex;
    }

    public void removeTimer(String id) {
        timers.remove(id);
    }

    public void timeGone() {
        if (timers == null || timers.isEmpty()) {
            return;
        }

        // 遍历所有在运行计时器，减少时间
        OrderedMapIterator<String, TimerEntity> iterator = timers.mapIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            TimerEntity timerEntity = timers.get(next);
            if (timerEntity.isRunning()) {
                timerEntity.setCurSeconds(timerEntity.getCurSeconds() - 1);
//                Log.e("TAG", "timeGone: timerEntity "+timerEntity.getCurSeconds()+"  id="+timerEntity.getId());
            }
            // 如果时间为0，停止计时器
            if (timerEntity.getCurSeconds() == 0 && timerEntity.isRunning()) {
                timerEntity.setRunning(false);
                // 暂时不删除
                Intent intent = new Intent(context, TimerCompleteActivity.class);
                intent.putExtra("id", timerEntity.getId());
                context.startActivity(intent);
            }
        }
    }

    public void updateTime(int type) {
        databaseHelper = DatabaseHelper.getInstance();
        if (type == DatabaseHelper.DEFAULT_TIMER) {
            setDefaultTimer(databaseHelper.getTimeList(DatabaseHelper.DEFAULT_TIMER));
        } else if (type == DatabaseHelper.RECENT_TIMER) {
            setRecentTimer(databaseHelper.getTimeList(DatabaseHelper.RECENT_TIMER));
        }
    }

    public void addTimer(int type, int time) {
        if (type == DatabaseHelper.DEFAULT_TIMER) {
            databaseHelper.insertTime(DatabaseHelper.DEFAULT_TIMER, time);
            updateTime(DatabaseHelper.DEFAULT_TIMER);
        } else if (type == DatabaseHelper.RECENT_TIMER) {
            if (!recentTimer.contains(time) && recentTimer.size() == 4) {
                Integer remove = recentTimer.remove(3);
                databaseHelper.deleteTime(DatabaseHelper.RECENT_TIMER, remove);
            }
            databaseHelper.insertTime(DatabaseHelper.RECENT_TIMER, time);
            updateTime(DatabaseHelper.RECENT_TIMER);
        }
    }

    public void removeTimerDefault(int time) {
        databaseHelper.deleteTime(DatabaseHelper.DEFAULT_TIMER, time);
        updateTime(DatabaseHelper.DEFAULT_TIMER);
    }

    public boolean haveRunningTimer() {
        OrderedMapIterator<String, TimerEntity> iterator = timers.mapIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            TimerEntity timerEntity = timers.get(next);
            if (timerEntity.isRunning()) {
                return true;
            }
        }
        return false;
    }
}

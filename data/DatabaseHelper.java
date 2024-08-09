package com.sgtc.countdown.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.commons.collections4.set.ListOrderedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DEFAULT_TIMER = 0;
    public static final int RECENT_TIMER = 1;
    private static final String DATABASE_NAME = "timer.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_DEFAULT_TIMER = "default_timer";
    public static final String TABLE_NAME_RECENT_TIMER = "recent_timer";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_MODIFY_TIME = "modify_time";

    private int[] defaultTimer = new int[]{
            60, 3*60, 5*60, 10*60, 15*60, 30*60, 60*60, 90*60, 120*60
    };

//    private boolean isCreated = false;
//    SharedPreferences sharedPreferences;
    private static DatabaseHelper instance;
    public static void init(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        sharedPreferences = context.getSharedPreferences("database_status", Context.MODE_PRIVATE);
//        isCreated = sharedPreferences.getBoolean("isCreated", false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.e("TAG", "onCreate: db isCreated"+isCreated);
//
//        if (!isCreated) {
            db.execSQL(getCreateSQL(TABLE_NAME_DEFAULT_TIMER));
            db.execSQL(getCreateSQL(TABLE_NAME_RECENT_TIMER));
            initDefaultTimer(db);
//            sharedPreferences.edit().putBoolean("isCreated", true).apply();
//        }
    }

    public ListOrderedSet<Integer> getTimeList(int timerType) {
        String tableName = null;
        if (timerType == DEFAULT_TIMER) {
            tableName = TABLE_NAME_DEFAULT_TIMER;
        } else if (timerType == RECENT_TIMER) {
            tableName = TABLE_NAME_RECENT_TIMER;
        }
        List<Integer> timeList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor query = db.query(tableName, new String[]{COLUMN_TIME}, null, null, null, null, COLUMN_MODIFY_TIME + " DESC");
        while (query.moveToNext()) {
            timeList.add(query.getInt(0));
        }
        query.close();
        db.close();
        ListOrderedSet<Integer> times = new ListOrderedSet<>();
        if (timerType == DEFAULT_TIMER) {
            Collections.sort(timeList);
        }
        times.addAll(timeList);
        return times;
    }

    public void insertTime(int timerType, int time) {
        String tableName = null;
        SQLiteDatabase db = getWritableDatabase();
        if (timerType == DEFAULT_TIMER) {
            tableName = TABLE_NAME_DEFAULT_TIMER;
        } else if (timerType == RECENT_TIMER) {
            tableName = TABLE_NAME_RECENT_TIMER;
        }
        int currentTime = (int)System.currentTimeMillis();
        db.execSQL(String.format("INSERT OR REPLACE INTO %s ( %s, %s ) VALUES ( %s, %s )", tableName, COLUMN_TIME, COLUMN_MODIFY_TIME, time, currentTime));
        db.close();
    }

    private void initDefaultTimer(SQLiteDatabase db) {
        for (int i = 0; i < defaultTimer.length; i++) {
            db.execSQL("INSERT INTO " + TABLE_NAME_DEFAULT_TIMER + " (" + COLUMN_TIME + ") VALUES (" + defaultTimer[i] + ")");
        }
    }

    public void deleteTime(int timerType, int time) {
        String tableName = null;
        if (timerType == DEFAULT_TIMER) {
            tableName = TABLE_NAME_DEFAULT_TIMER;
        } else if (timerType == RECENT_TIMER) {
            tableName = TABLE_NAME_RECENT_TIMER;
        }
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE " + COLUMN_TIME + " = " + time);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("TAG", "onUpgrade: ");
//        isCreated = false;
//        sharedPreferences.edit().putBoolean("isCreated", false).apply();
        db.execSQL(getDropSQL(TABLE_NAME_DEFAULT_TIMER));
        db.execSQL(getDropSQL(TABLE_NAME_RECENT_TIMER));
        onCreate(db);

    }

    private String getCreateSQL(String tableName) {
        return "CREATE TABLE " + tableName + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TIME + " INTEGER UNIQUE, " + // 添加 UNIQUE 约束
                COLUMN_MODIFY_TIME + " INTEGER" +
                ");";
    }

    private String getDropSQL(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }

}

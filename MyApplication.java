package com.sgtc.countdown;

import android.app.Application;
import android.os.AsyncTask;

import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.DatabaseHelper;

public class MyApplication extends Application {
    Data data;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper.init(this);
        data = Data.init(this);
        data.updateTime(DatabaseHelper.DEFAULT_TIMER);
        data.updateTime(DatabaseHelper.RECENT_TIMER);

    }
}

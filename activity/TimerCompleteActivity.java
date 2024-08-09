package com.sgtc.countdown.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

import com.sgtc.countdown.R;
import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.TimerEntity;
import com.sgtc.countdown.view.Timer;

public class TimerCompleteActivity extends AppCompatActivity {
    private static final Integer STOP = 0;
    private static final Integer START_VIBRATE = 1;

    TimerEntity timerEntity;
    Vibrator vibrator;
    Ringtone ringtone;
    private PowerManager.WakeLock wakeLock;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == START_VIBRATE) {
                vibrate();
                playRingtone();
                sendEmptyMessageDelayed(START_VIBRATE, 1500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_complete);
        String id = getIntent().getStringExtra("id");
        timerEntity = Data.getInstance().getTimerById(id);
        initView();

        init();
    }

    private void initView() {
        findViewById(R.id.ic_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                Data.getInstance().removeTimer(timerEntity.getId());
                finish();
                overridePendingTransition(0, 0);

            }
        });

        findViewById(R.id.ic_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if (timerEntity != null) {
                    timerEntity.reset();
                }
                finish();
                overridePendingTransition(0, 0);

            }
        });

        Timer timer = findViewById(R.id.timer);
        timer.setCenterTime(getString(R.string.timer_complete));
        timer.setSubTimeShow(false);
    }

    private void init() {
        handler.sendEmptyMessage(START_VIBRATE);
        wakeUpScreen();
    }

    private void wakeUpScreen() {
        // 获取PowerManager实例
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // 创建一个WakeLock
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "MyApp::WakeLockTag");

            // 获取WakeLock
            wakeLock.acquire(10*60*1000L /*10 minutes*/);

            // 设置窗口标志，确保屏幕开启
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }
    }

    private void vibrate() {
        if (vibrator == null) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect = VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE);  // 重复从第0个索引开始
            vibrator.vibrate(effect);
        }
    }

    private void playRingtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(START_VIBRATE);
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (ringtone != null) {
            ringtone.stop();
        }
        // 释放WakeLock
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}

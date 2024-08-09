package com.sgtc.countdown.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.countdown.R;
import com.sgtc.countdown.TimerService;
import com.sgtc.countdown.adapter.TimerInfoAdapter;
import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.EventMessage;
import com.sgtc.countdown.utils.AnimationUtil;

public class MainActivity extends BaseActivity {

    private Data data;
    TimerInfoAdapter timerInfoAdapter;
    RecyclerView rv_timers;
    View tv_no_timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView(savedInstanceState);
        // 注册服务
        Intent intent = new Intent(this, TimerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        tv_no_timer.setVisibility(data.getTimers().size() == 0 ? View.VISIBLE : View.GONE);
        timerInfoAdapter.notifyDataSetChanged();
    }

    @Override
    void initData() {
        super.initData();
        data = Data.getInstance();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        findViewById(R.id.ic_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                Intent intent = new Intent(MainActivity.this, AddTimerActivity.class);
                AnimationUtil.animateAndLaunchNewActivity(MainActivity.this, intent);
            }
        });

        findViewById(R.id.rl_header).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                return true;
            }
        });

        // 初始化 recycleview
        rv_timers = findViewById(R.id.rv_timers);
        timerInfoAdapter = new TimerInfoAdapter(this);
        tv_no_timer = findViewById(R.id.tv_no_timer);
        timerInfoAdapter.setOnTimeGoneListener(() -> {
            tv_no_timer.setVisibility(data.getTimers().size() == 0 ? View.VISIBLE : View.GONE);
        });
        rv_timers.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean isAutoMeasureEnabled() {
                return true;
            }
        });
        rv_timers.setNestedScrollingEnabled(false);

        rv_timers.setAdapter(timerInfoAdapter);

        View sv_timers = findViewById(R.id.sv_timers);
        onStartAnimateViews.add(sv_timers);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_CENTER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销服务
        if (!data.haveRunningTimer()) {
            Intent intent = new Intent(this, TimerService.class);
            stopService(intent);
        }
    }


    @Override
    public void onTimeGone(EventMessage eventMessage) {
        super.onTimeGone(eventMessage);
        if (!isShow) {
            return;
        }

        if (timerInfoAdapter != null) {
            timerInfoAdapter.onTimeGone();
        }
    }


}

package com.sgtc.countdown.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.sgtc.countdown.callback.OnSwipeTouchListener;
import com.sgtc.countdown.R;
import com.sgtc.countdown.utils.AnimationUtil;
import com.sgtc.countdown.view.MyTabLayout;
import com.sgtc.countdown.view.Timer;
import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.DatabaseHelper;
import com.sgtc.countdown.data.EventMessage;
import com.sgtc.countdown.data.TimerEntity;

public class TimerActivity extends BaseActivity {
    Timer timer;
    CheckBox icSwitch;


    TimerEntity curTimerEntity;
    int curTimerIndex = 0;
    boolean isCreate;
    Data data;
    MyTabLayout scroll_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        initData();
        initView(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        switchTimer();
    }

    @Override
    void initData() {
        super.initData();
        curTimerIndex = getIntent().getIntExtra("index", 0);
        isCreate = getIntent().getBooleanExtra("isCreate", false);
        data = Data.getInstance();
        curTimerEntity = data.getTimerByIndex(curTimerIndex);
        if (isCreate) {
            Data.getInstance().addTimer(DatabaseHelper.RECENT_TIMER, curTimerEntity.getTotalSeconds());
        }

    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        timer = findViewById(R.id.timer);
        timer.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeUp() {
                if (data.getTimers().size() > curTimerIndex + 1) {
                    curTimerIndex++;
                    switchTimer();
                    timer.fadeOut(500, false);
                    timer.fadeIn(500, false);
                }
                Log.e("TimerActivity", "onSwipeUp index: " + curTimerIndex);

            }

            @Override
            public void onSwipeDown() {
                if (curTimerIndex > 0) {
                    curTimerIndex--;
                    switchTimer();
                    timer.fadeOut(500, true);
                    timer.fadeIn(500, true);
                }
                Log.e("TimerActivity", "onSwipeDown index: " + curTimerIndex);

            }
        });
        timer.setTimerEntity(curTimerEntity);


        icSwitch = findViewById(R.id.ic_switch);
        icSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                curTimerEntity.setRunning(true);
            } else {
                curTimerEntity.setRunning(false);
            }
        });

        View icAdd = findViewById(R.id.ic_add);
        icAdd.setOnClickListener(v -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            Intent intent = new Intent(this, AddTimerActivity.class);
            AnimationUtil.animateAndLaunchNewActivity(this, intent, true);
        });

        View icClose = findViewById(R.id.ic_close);
        icClose.setOnClickListener(v -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            curTimerIndex = data.removeTimer(curTimerIndex);
            switchTimer();
        });

        View icBack = findViewById(R.id.ic_back);
        icBack.setOnClickListener(v -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            Intent intent = new Intent(this, MainActivity.class);
            AnimationUtil.animateAndLaunchNewActivity(this, intent, true);
        });

        if (curTimerEntity.isRunning()) {
            icSwitch.setChecked(true);
        }
        scroll_bar = findViewById(R.id.scroll_bar);
        scroll_bar.setTabCount(data.getTimers().size());

        onStartAnimateViews.add(timer);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_RIGHT);

        onStartAnimateViews.add(icBack);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_CENTER);
        onStartAnimateViews.add(icAdd);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_CENTER);
        onStartAnimateViews.add(icClose);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_CENTER);
        onStartAnimateViews.add(icSwitch);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_CENTER);
        onStartAnimateViews.add(scroll_bar);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_CENTER);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        updateScrollBar();
    }

    private void switchTimer() {
        if (data.getTimers().size() > 0) {
            if (data.getTimers().size() <= curTimerIndex) {
                curTimerIndex = data.getTimers().size() - 1;
            }
            curTimerEntity = data.getTimerByIndex(curTimerIndex);
            timer.setTimerEntity(curTimerEntity);
            if (curTimerEntity.isRunning()) {
                icSwitch.setChecked(true);
            } else {
                icSwitch.setChecked(false);
            }
            updateScrollBar();
        } else {
            AnimationUtil.animateAndLaunchNewActivity(this, null, true);
        }

    }

    private void updateScrollBar() {
        scroll_bar.setTabCount(data.getTimers().size());
        scroll_bar.setTabSelected(curTimerIndex);
    }

    @Override
    public void onTimeGone(EventMessage message) {
        super.onTimeGone(message);
        if (!isShow) {
            return;
        }
//         更新计时器相关的ui显示

        if (!curTimerEntity.isRunning()) {
            icSwitch.setChecked(false);
        }
        if (curTimerEntity.getCurSeconds() == 0) {
            switchTimer();
        }

        timer.onTimeGone();
    }

}

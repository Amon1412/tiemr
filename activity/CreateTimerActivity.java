package com.sgtc.countdown.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.contrarywind.view.WheelView;
import com.sgtc.countdown.R;
import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.DatabaseHelper;
import com.sgtc.countdown.data.TimerEntity;
import com.sgtc.countdown.utils.AnimationUtil;
import com.sgtc.countdown.adapter.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.List;


public class CreateTimerActivity extends BaseActivity {
    WheelView[] wheelViews = new WheelView[3];
    TextView[] textViews = new TextView[3];
    View[] views = new View[3];

    List<String>[] items = new List[3];
    int lastFocus = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_timer);
        initData();
        initView(savedInstanceState);

    }

    @Override
    void initData() {
        super.initData();
        items[0] = new ArrayList<>();
        items[1] = new ArrayList<>();
        items[2] = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                items[0].add("0" + i);
            } else {
                items[0].add(i + "");
            }
        }

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                items[1].add("0" + i);
            } else {
                items[1].add(i + "");
            }
        }

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                items[2].add("0" + i);
            } else {
                items[2].add(i + "");
            }
        }
    }


    @Override
    void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        findViewById(R.id.ic_back).setOnClickListener(v -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            finish();
        });


        views[0] = findViewById(R.id.ll_hour);
        views[1] = findViewById(R.id.ll_minute);
        views[2] = findViewById(R.id.ll_second);

        for (int i = 0; i < views.length; i++) {
            int finalI = i;
            views[i].setOnTouchListener((v, event) -> {
                onItemFocus(finalI);
                return false;
            });
        }


        textViews[0] = findViewById(R.id.tv_selector_hour);
        textViews[1] = findViewById(R.id.tv_selector_minute);
        textViews[2] = findViewById(R.id.tv_selector_second);

        wheelViews[0] = findViewById(R.id.wv_hour);
        wheelViews[1] = findViewById(R.id.wv_minute);
        wheelViews[2] = findViewById(R.id.wv_second);

        for (int i = 0; i < wheelViews.length; i++) {
            wheelViews[i].setAdapter(new ArrayWheelAdapter(items[i]));
            int finalI = i;
            wheelViews[i].setOnTouchListener((v, event) -> {
                onItemFocus(finalI);
                return false;
            });

            wheelViews[i].setCyclic(false);
            wheelViews[i].setItemsVisibleCount(3);
            wheelViews[i].setTextColorCenter(Color.WHITE);
            wheelViews[i].setTextSize(getResources().getDimensionPixelSize(R.dimen.dp_26));
        }

        wheelViews[0].setCurrentItem(0);
        wheelViews[1].setCurrentItem(1);
        wheelViews[2].setCurrentItem(0);

        Button btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            int hour = wheelViews[0].getCurrentItem();
            int minute = wheelViews[1].getCurrentItem();
            int second = wheelViews[2].getCurrentItem();

            int time = hour * 60 * 60 + minute * 60 + second;

            // 插入数据库
            Data.getInstance().addTimer(DatabaseHelper.DEFAULT_TIMER, time);


            TimerEntity timerEntity = new TimerEntity(time);
            int index = Data.getInstance().addTimer(timerEntity);
            timerEntity.setRunning(true);
            Intent intent = new Intent(CreateTimerActivity.this, TimerActivity.class);
            intent.putExtra("index", index);
            intent.putExtra("isCreate", true);
            AnimationUtil.animateAndLaunchNewActivity(CreateTimerActivity.this, intent, true);
        });

        onItemFocus(1);
        View rl_time_picker = findViewById(R.id.rl_time_picker);
        onStartAnimateViews.add(rl_time_picker);
        onStartAnimationTypes.add(AnimationUtil.ANIMATION_FADE_IN_RIGHT);
    }

    private void onItemFocus(int index) {
        if (lastFocus == index) {
            return;
        }
        for (int i = 0; i < textViews.length; i++) {
            if (i == index) {
                textViews[i].setVisibility(View.VISIBLE);
                wheelViews[i].setSelected(true);
            } else {
                textViews[i].setVisibility(View.INVISIBLE);
                wheelViews[i].setSelected(false);
            }
        }
        lastFocus = index;
    }
}

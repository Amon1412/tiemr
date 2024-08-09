package com.sgtc.countdown.activity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.sgtc.countdown.R;
import com.sgtc.countdown.data.EventMessage;
import com.sgtc.countdown.utils.AnimationUtil;
import com.sgtc.countdown.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    public TextView tvTime;
    boolean isShow;
    public ExitActivityTransition start;

    public boolean isStartOtherActivity = false;
    public List<View> onStartAnimateViews = new ArrayList<>();
    public List<Integer> onStartAnimationTypes = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (!registered) {
            EventBus.getDefault().register(this);
        }
        isStartOtherActivity = true;
    }

    void initView(Bundle savedInstanceState) {
        tvTime = findViewById(R.id.tv_time);
        tvTime.setText(CommonUtils.getCurrentTimeText());
        if (getIntent().getExtras() != null) {
            start = ActivityTransition.with(getIntent()).duration(300).to(tvTime).start(savedInstanceState);
        }
    }

    void initData() {}

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
        if (tvTime != null) {
            tvTime.setText(CommonUtils.getCurrentTimeText());
        }
        if (isStartOtherActivity) {
            AnimationUtil.animateOnActivityBack(this);
            isStartOtherActivity = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        Bundle transitionBundle = ActivityTransitionLauncher.with(this).from(tvTime).createBundle();
        intent.putExtras(transitionBundle);
        setResult(0, new Intent());
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeGone(EventMessage eventMessage) {
        if (!isShow) {
            return;
        }
        if (tvTime != null) {
            tvTime.setText(CommonUtils.getCurrentTimeText());
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void finish() {
        exitAnimation();
        super.finish();
    }

    public void exitAnimation() {
        if (start != null) {
            start.exit(this);
        }
    }
}

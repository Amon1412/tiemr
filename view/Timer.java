package com.sgtc.countdown.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.sgtc.countdown.R;
import com.sgtc.countdown.data.TimerEntity;
import com.sgtc.countdown.utils.DrawUtils;

public class Timer extends View {
    private static final String TAG = "CircularProgressBar";
    public int maxProgress = 60;
    private int progress = 0;
    private int defaultColor = Color.GRAY;
    private int progressColor = Color.GREEN;
    private int progressWidth = (int) getResources().getDimension(R.dimen.progress_width);
    private int progressCount = 60;

    private int width;
    private int height;
    private int baseAngle = 0;

    private boolean isSubTimeShow = true;

    private String centerTime = "00:00:00";
    private String subTime = "00:00:00";

    private Paint progressPaint;
    private Paint textPaint;
    private TimerEntity timerEntity;
    private Context context;

    private int defaultWith = getResources().getDimensionPixelSize(R.dimen.dp_204);
    float scale;
    public boolean needDraw = false;

    public Timer(Context context) {
        super(context);
        this.context = context;
        defaultColor = getResources().getColor(R.color.default_progress);
        progressColor = getResources().getColor(R.color.progress);
        progressWidth = (int) getResources().getDimension(R.dimen.progress_width) ;
        progressCount = 120;
        baseAngle = 90;
        isSubTimeShow = true;
        boolean isFullProgress = true;
        if (isFullProgress) {
            progress = maxProgress;
        } else {
            progress = 0;
        }
        init();
    }

    public Timer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircularProgressBar,
                0, 0);
        try {
            defaultColor = a.getColor(R.styleable.CircularProgressBar_defaultColor, getResources().getColor(R.color.default_progress));
            progressColor = a.getColor(R.styleable.CircularProgressBar_progressColor, getResources().getColor(R.color.progress));
            progressWidth = a.getDimensionPixelSize(R.styleable.CircularProgressBar_progressWidth, (int) getResources().getDimension(R.dimen.progress_width)) ;
            progressCount = a.getInt(R.styleable.CircularProgressBar_progressCount, 120);
            baseAngle = a.getInt(R.styleable.CircularProgressBar_baseAngle, 90);
            isSubTimeShow = a.getBoolean(R.styleable.CircularProgressBar_isSubTimeShow, true);
            boolean isFullProgress = a.getBoolean(R.styleable.CircularProgressBar_isFullProgress, true);
            if (isFullProgress) {
                progress = maxProgress;
            } else {
                progress = 0;
            }

        } finally {
            a.recycle();
        }
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        scale = (float) width / defaultWith;
        setMeasuredDimension(width, height);
    }

    private void init() {

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        setProgress(maxProgress);

    }


    public void setTimerEntity(TimerEntity timerEntity) {
        this.timerEntity = timerEntity;
        setProgress(maxProgress * timerEntity.getCurSeconds()/timerEntity.getTotalSeconds());
        setCenterTime(timerEntity.getCurTimeString());
        setSubTime(timerEntity.getTotalTimeString(context));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needDraw) {
            needDraw = false;
        }

        int width = getWidth();
        int height = getHeight();

        // 绘制进度条背景
        progressPaint.setColor(defaultColor);
        for (int i = 0; i < progressCount; i++) {
            float angle = 360f / progressCount * i;
            canvas.drawArc(progressWidth, progressWidth, width-progressWidth, height-progressWidth, angle -baseAngle, 360f / (progressCount*2), false, progressPaint);
        }

        // 绘制进度条
        progressPaint.setColor(progressColor);
        int count = progressCount * progress / maxProgress;
        for (int i = 0; i < count; i++) {
            float angle = 360f / progressCount * i;
            canvas.drawArc(progressWidth, progressWidth, width-progressWidth, height-progressWidth, angle -baseAngle, 360f / (progressCount*2), false, progressPaint);
        }

        // 绘制中心文字
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(getResources().getDimension(R.dimen.dp_41) * scale);
        int marginBottomCenterTime = (int) (getResources().getDimension(R.dimen.margin_bottom_center_time) * scale);
        DrawUtils.drawTextCenter(canvas, textPaint, centerTime, width / 2, height / 2 - marginBottomCenterTime);

        // 绘制底部文字
        if (isSubTimeShow) {
            textPaint.setColor(Color.GRAY);
            textPaint.setTextSize(getResources().getDimension(R.dimen.dp_12) * scale);
            int marginTopSubtime = (int) (getResources().getDimension(R.dimen.margin_top_subtime) * scale);
            DrawUtils.drawTextCenter(canvas, textPaint, subTime, width / 2, height / 2 + marginTopSubtime);
        }
    }

    // 淡入动画
    public void fadeIn(int duration, boolean orientation) {
        // 淡入动画
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        fadeIn.setDuration(duration);

        ObjectAnimator move;
        if (orientation) {
            // from up
            move = ObjectAnimator.ofFloat(this, "translationY", -10, 0f);
        } else {
            // from down
            move = ObjectAnimator.ofFloat(this, "translationY", 10, 0f);
        }
        move.setDuration(duration);


        // 同时播放动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, move);
        animatorSet.start();
    }

    // 淡出动画
    public void fadeOut(int duration, boolean orientation) {
        // 淡出动画
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);

        ObjectAnimator move;
        if (orientation) {
            // to down
            move = ObjectAnimator.ofFloat(this, "translationY", 0, 10f);
        } else {
            // to up
            move = ObjectAnimator.ofFloat(this, "translationY", 0, -10f);
        }
        move.setDuration(duration);

        // 同时播放动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeOut, move);
        animatorSet.start();
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress, maxProgress);
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setSubTimeShow(boolean show) {
        isSubTimeShow = show;
        invalidate();
    }

    public void setCenterTime(String centerTime) {
        this.centerTime = centerTime;
        invalidate();
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
        invalidate();
    }

    public void onTimeGone() {
        setCenterTime(timerEntity.getCurTimeString());
        setProgress(maxProgress * timerEntity.getCurSeconds() / timerEntity.getTotalSeconds());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false; // 不拦截触摸事件
    }

    public void setProgressCount(int progressCount) {
        this.progressCount = progressCount;
    }

    public void setEnable(boolean enable) {
        if (enable) {
            setProgress(maxProgress);
        } else {
            setProgress(0);
        }
        needDraw = true;
        Log.e("TAG", "setEnable: enable "+enable );
        invalidate();
    }
}

package com.sgtc.countdown.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ScrollView;

import com.kogitune.activity_transition.ActivityTransitionLauncher;
import com.sgtc.countdown.R;
import com.sgtc.countdown.activity.AddTimerActivity;
import com.sgtc.countdown.activity.BaseActivity;
import com.sgtc.countdown.activity.MainActivity;

public class AnimationUtil {
    public static int ANIMATION_FADE_IN = 1;
    public static int ANIMATION_FADE_OUT = 2;
    public static int ANIMATION_FADE_IN_CENTER = 3;
    public static int ANIMATION_FADE_OUT_CENTER = 4;
    public static int ANIMATION_FADE_IN_UP = 5;
    public static int ANIMATION_FADE_OUT_UP = 6;
    public static int ANIMATION_FADE_IN_RIGHT = 7;
    public static int ANIMATION_FADE_OUT_RIGHT = 8;

    public static void animateAndLaunchNewActivity(BaseActivity activity, Intent intent) {
        animateAndLaunchNewActivity(activity, intent, false);
    }

    public static void animateAndLaunchNewActivity(BaseActivity activity, Intent intent, boolean finishSelf) {

        for (int i = 0; i < activity.onStartAnimateViews.size(); i++) {
            View view = activity.onStartAnimateViews.get(i);
            int animationType = activity.onStartAnimationTypes.get(i) + 1;
            ViewPropertyAnimator animator = view.animate();
            if (animationType == ANIMATION_FADE_OUT) {
            } else if (animationType == ANIMATION_FADE_OUT_CENTER) {
                // 缩小并渐隐动画
                animator.scaleX(0.8f).scaleY(0.8f);
            } else if (animationType == ANIMATION_FADE_OUT_UP) {
                // 缩小并渐隐动画
                animator.translationY(-activity.getResources().getDimension(R.dimen.dp_40)).alpha(0f);
            } else if (animationType == ANIMATION_FADE_OUT_RIGHT) {
                // 缩小并渐隐动画
                animator.translationX(activity.getResources().getDimension(R.dimen.dp_100));
            }
            animator.alpha(0);
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(200); // 动画持续时间
            animator.start();
        }
        activity.isStartOtherActivity = true;




        if (intent != null) {
            startActivity(activity, intent);
        }

        if (finishSelf) {
            activity.finish();
        } else {
            activity.exitAnimation();
        }

    }

    public static void animateOnActivityBack(BaseActivity activity) {
        if (activity.onStartAnimateViews.isEmpty()) {
            return;
        }
        for (int i = 0; i < activity.onStartAnimateViews.size(); i++) {
            View view = activity.onStartAnimateViews.get(i);
            ViewPropertyAnimator animator = view.animate();
            int animationType = activity.onStartAnimationTypes.get(i);

            view.setAlpha(0);
            if (animationType == ANIMATION_FADE_IN) {
                // 放大并渐显动画
                animator.alpha(1f);
            } else if (animationType == ANIMATION_FADE_IN_CENTER) {
                // 放大并渐显动画
                view.setScaleX(0.6f);
                view.setScaleY(0.6f);
                animator.scaleX(1f).scaleY(1f).alpha(1f);
            } else if (animationType == ANIMATION_FADE_IN_UP) {
                view.setTranslationY(-activity.getResources().getDimension(R.dimen.dp_40));
                animator.translationY(0).alpha(1f);
            } else if (animationType == ANIMATION_FADE_IN_RIGHT) {
                view.setTranslationX(activity.getResources().getDimension(R.dimen.dp_100));
                animator.translationX(0).alpha(1f);
            }
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(200); // 动画持续时间
            // 动画结束时启动新的 Activity
            animator.withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (view instanceof ScrollView) {
                        ((ScrollView) view).smoothScrollTo(0, 0);
                    }
                }
            });
            animator.start();
        }

    }

    public static void startActivity(BaseActivity activity, Intent intent) {
        ActivityTransitionLauncher.with(activity).from(activity.tvTime).launch(intent);
        activity.overridePendingTransition(0, 0); // 禁用系统默认的过渡动画
    }
}

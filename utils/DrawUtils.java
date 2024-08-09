package com.sgtc.countdown.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

public class DrawUtils {
    public static void drawTextCenter(Canvas canvas, Paint paint, String str, int centerX, int centerY) {
        if (canvas != null && paint != null && !TextUtils.isEmpty(str)) {
            int textWidth = (int) (paint.measureText(str) + 0.5);
            int textHeight = (int) (paint.descent() - paint.ascent() - 4);
            centerX = centerX - (textWidth >> 1);
            centerY = centerY + (textHeight >> 1);
            canvas.drawText(String.valueOf(str), centerX, centerY, paint);
        }
    }
}

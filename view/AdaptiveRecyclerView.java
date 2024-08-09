package com.sgtc.countdown.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class AdaptiveRecyclerView extends RecyclerView {

    public AdaptiveRecyclerView(Context context) {
        super(context);
    }

    public AdaptiveRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int heightSpecCustom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpecCustom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}

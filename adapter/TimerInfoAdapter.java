package com.sgtc.countdown.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.countdown.R;
import com.sgtc.countdown.activity.MainActivity;
import com.sgtc.countdown.activity.TimerActivity;
import com.sgtc.countdown.callback.OnTimeGoneListener;
import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.TimerEntity;
import com.sgtc.countdown.utils.AnimationUtil;

import org.apache.commons.collections4.map.ListOrderedMap;

import java.util.ArrayList;
import java.util.List;

public class TimerInfoAdapter extends RecyclerView.Adapter<TimerInfoAdapter.ViewHolder> {

    ListOrderedMap<String, TimerEntity> timers;

    private Context context;

    public TimerInfoAdapter(Context context) {
        this.context = context;
        timers = Data.getInstance().getTimers();
    }

    @NonNull
    @Override
    public TimerInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timer_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerInfoAdapter.ViewHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeListener();
    }

    @Override
    public int getItemCount() {
        return timers.size();
    }

    public void onTimeGone() {
        for (OnTimeGoneListener listener : listeners) {
            listener.onTimeGone();
        }
    }

    public void setOnTimeGoneListener(OnTimeGoneListener listener) {
        this.listener = listener;
    }
    OnTimeGoneListener listener;

    private List<OnTimeGoneListener> listeners = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder implements OnTimeGoneListener{
        public View rl_timer_info;
        public View rl_delete;
        public TextView tvCurTime;
        public TextView tvTotalTime;
        public CheckBox icSwitch;
        public ImageView icDelete;
        TimerEntity timerEntity;
        private int index;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rl_timer_info = itemView.findViewById(R.id.rl_timer_info);
            rl_delete = itemView.findViewById(R.id.rl_delete);
            tvCurTime = itemView.findViewById(R.id.tv_cur_time);
            tvTotalTime = itemView.findViewById(R.id.tv_toal_time);
            icSwitch = itemView.findViewById(R.id.ic_switch);
            icDelete = itemView.findViewById(R.id.ic_delete);

            rl_timer_info.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                // 处理点击事件
                MainActivity mainActivity = (MainActivity) context;
                Intent intent = new Intent(context, TimerActivity.class);
                intent.putExtra("index",index);
                View parent = (View) itemView.getParent();
                while (parent != null && !(parent instanceof RecyclerView)) {
                    parent = (View) parent.getParent();
                }
                AnimationUtil.animateAndLaunchNewActivity(mainActivity, intent);
            });


            icDelete.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                // 处理删除事件
                Log.e("amon", "removeTimer:index: "+index );
                Data.getInstance().removeTimer(index);

                notifyItemRemoved(index);
                notifyItemRangeChanged(index, timers.size());
                listener.onTimeGone();
            });

        }
        public void onBind() {
            this.index = getAdapterPosition();
            timerEntity = timers.get(timers.get(index));
            tvCurTime.setText(timerEntity.getCurTimeString());
            tvTotalTime.setText(timerEntity.getTotalTimeString(context));
            icSwitch.setChecked(timerEntity.isRunning());
            listeners.add(this);

            icSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // 处理开关事件
                TimerEntity timerEntity = timers.get(timers.get(index));
                timerEntity.setRunning(isChecked);

            });
        }
        @Override
        public void onTimeGone() {
            if (timerEntity != null && tvCurTime != null && timerEntity.isRunning()) {
                tvCurTime.setText(timerEntity.getCurTimeString());
            } else if (timerEntity != null && timerEntity.getCurSeconds() == 0) {
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, timers.size());
            }
        }

        public void removeListener() {
            listeners.remove(this);
        }
    }

}

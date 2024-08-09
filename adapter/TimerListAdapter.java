package com.sgtc.countdown.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgtc.countdown.R;
import com.sgtc.countdown.activity.AddTimerActivity;
import com.sgtc.countdown.utils.AnimationUtil;
import com.sgtc.countdown.view.Timer;
import com.sgtc.countdown.activity.CreateTimerActivity;
import com.sgtc.countdown.activity.TimerActivity;
import com.sgtc.countdown.data.Data;
import com.sgtc.countdown.data.TimerEntity;
import com.sgtc.countdown.utils.CommonUtils;

import org.apache.commons.collections4.set.ListOrderedSet;

public class TimerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private ListOrderedSet<Integer> dataList;
    private boolean isShowHeader;

    Context context;
    private static boolean isEdit = false;

    public TimerListAdapter(Context context, ListOrderedSet<Integer> dataList, boolean isShowHeader) {
        // 初始化数据
        this.context = context;
        this.dataList = dataList;
        this.isShowHeader = isShowHeader;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_add_timer, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_timer, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            // 绑定 header 数据
            ((HeaderViewHolder)holder).onBind();
        } else {
            if (isShowHeader) {
                position -= 1;
            }
            int time = dataList.get(position);// 减去 header
            ((ItemViewHolder)holder).onBind(time);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isShowHeader) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        if (isShowHeader) {
            return dataList.size() + 1;
        }
        return dataList.size();
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        View view;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }
        public void onBind() {
            itemView.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                Intent intent = new Intent(v.getContext(), CreateTimerActivity.class);
                AddTimerActivity addTimerActivity = (AddTimerActivity) v.getContext();
                AnimationUtil.animateAndLaunchNewActivity(addTimerActivity, intent);
            });
            if (isEdit) {
                itemView.setEnabled(false);
            } else {
                itemView.setEnabled(true);
            }
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        public void onBind(int time) {
            Timer timer = view.findViewById(R.id.timer);
            timer.setCenterTime(CommonUtils.getTimeText(time));
            timer.setSubTimeShow(false);
            timer.setProgress(timer.maxProgress);
            timer.setProgressCount(60);
            view.setOnClickListener(v -> {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                // 处理点击事件
                Data data = Data.getInstance();
                TimerEntity timerEntity = new TimerEntity(time);
                int index = data.addTimer(timerEntity);
                AddTimerActivity addTimerActivity = (AddTimerActivity) this.view.getContext();
                Intent intent = new Intent(addTimerActivity, TimerActivity.class);
                intent.putExtra("index", index);
                intent.putExtra("isCreate", true);
                timerEntity.setRunning(true);
                AnimationUtil.animateAndLaunchNewActivity(addTimerActivity, intent);
            });
            timer.setOnTouchListener((v, event) -> {
                if (isEdit) {
                    return true;
                }
                return false;
            });
            if (isShowHeader) {
                View ic_remove = view.findViewById(R.id.ic_remove);
                if (isEdit) {
                    ic_remove.setVisibility(View.VISIBLE);
                    ic_remove.setOnClickListener(v -> {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        Data.getInstance().removeTimerDefault(time);
                        notifyDataSetChanged();
                    });
                } else {
                    ic_remove.setVisibility(View.GONE);
                }
            } else {
                if (isEdit) {
                    timer.setEnable(false);
                } else {
                    timer.setEnable(true);
                }
            }

        }
    }

}

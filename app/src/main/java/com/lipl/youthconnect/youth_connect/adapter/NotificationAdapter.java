package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Notification;
import com.lipl.youthconnect.youth_connect.pojo.ShowcaseEvent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Suhasini on 02.03.15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 5;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;
    private List<Notification> notificationList = null;

    private OnActivityItemClickListener onActivityItemClickListener;

    public NotificationAdapter(Context context, List<Notification> showcaseEventList) {
        this.context = context;
        this.notificationList = showcaseEventList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.notification_list_item, parent, false);
        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);

        return cellFeedViewHolder;
    }

    private void runEnterAnimation(View view, int position) {
        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Util.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        runEnterAnimation(viewHolder.itemView, position);
        CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;

        holder.mTvQuestion.setTag(position);
        holder.mTvQuestion.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_BOLD_CONDENSED));
        holder.imgNewRelease.setTag(position);

        Notification notification = notificationList.get(position);
        if(notification != null) {
            holder.mTvQuestion.setText(notification.getUser().getFull_name() + " " + notification.getNotification());
            int isNew = notification.getIsNew();
            if(isNew == 1) {
                holder.imgNewRelease.setVisibility(View.VISIBLE);
                //if(position == 0 || position % 2 == 0){
                    //holder.card_view.setBackgroundResource(R.color.safronn_transparent);
                //} else{
                  //  holder.card_view.setBackgroundResource(R.color.safronn2);
                //}
                holder.mTvQuestion.setTextColor(Color.parseColor("#fb8c00"));
            } else{
                holder.imgNewRelease.setVisibility(View.GONE);
                holder.mTvQuestion.setTextColor(Color.WHITE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.card_view) {
            if (onActivityItemClickListener != null) {
                int position = (Integer) view.getTag();
                if(notificationList != null
                        && notificationList.size() > 0
                        && position < notificationList.size()) {
                    Notification notification = notificationList.get(position);
                    onActivityItemClickListener.onActivityItemClick(view, position, notification);
                }
            }
        }
    }

    public void updateItems(boolean animated) {
        itemsCount = notificationList.size();
        animateItems = animated;
        notifyDataSetChanged();
    }

    public void setOnActivityItemClickListener(OnActivityItemClickListener onActivityItemClickListener) {
        this.onActivityItemClickListener = onActivityItemClickListener;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tvQuestion)
        TextView mTvQuestion;
        @InjectView(R.id.card_view)
        RelativeLayout card_view;
        @InjectView(R.id.imgNewRelease)
        ImageView imgNewRelease;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public interface OnActivityItemClickListener {
        public void onActivityItemClick(View v, int position, Notification notification);
    }
}
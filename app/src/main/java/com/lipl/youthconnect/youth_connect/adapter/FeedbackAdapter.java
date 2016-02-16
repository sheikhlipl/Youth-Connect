package com.lipl.youthconnect.youth_connect.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.ReportFromDetailsActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Feedback;
import com.lipl.youthconnect.youth_connect.pojo.ShowcaseEvent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Suhasini on 02.03.15.
 */
public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private static final int ANIMATED_ITEMS_COUNT = 5;

    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;
    private boolean animateItems = false;

    private OnActivityItemClickListener onActivityItemClickListener;

    private List<Feedback> userReports;
    private Activity context;
    int tabNumber;

    static final int MIN_DISTANCE = 100;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;

    public FeedbackAdapter(Activity context, List<Feedback> userReports, int number){
        this.context = context;
        this.userReports = userReports;
        this.tabNumber = number;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.user_report_item_layout, parent, false);
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

        holder.mLayoutParent.setTag(position);
        holder.tvReportTitle.setTag(position);
        holder.tvReportCreatedBy.setTag(position);
        holder.tvReportCreatedOn.setTag(position);
        //holder.submitsts.setTag(position);
        holder.tvReportType.setTag(position);
        holder.imgNext.setTag(position);

        holder.tvReportTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_BOLD_CONDENSED));
        holder.tvReportCreatedBy.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_LIGHT));
        holder.tvReportCreatedOn.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_LIGHT));
        //holder.submitsts.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_BOLD_CONDENSED));
        holder.tvReportType.setTypeface(Typeface.createFromAsset(context.getAssets(), Constants.ROBOTO_LIGHT));

        if(userReports != null && userReports.size() > 0) {

            holder.tvReportTitle.setText(userReports.get(position).getReportTitle());
            holder.tvReportCreatedBy.setText("Assigned By : " + userReports.get(position).getReportCreatedBy());
            if (userReports.get(position).getLastDateofSubmission() != null
                    && userReports.get(position).getLastDateofSubmission().trim().length() > 0) {
                holder.tvReportCreatedOn.setText(userReports.get(position).getLastDateofSubmission());
            }
            holder.tvReportType.setText("Report Type : " + userReports.get(position).getReportType());

            if (tabNumber == 0) {

                //holder.submitsts.setTextColor(context.getResources().getColor(R.color.red));

                //if(userReports.get(position).getReportType().equalsIgnoreCase("instant")) {
                //holder.submitsts.setVisibility(View.VISIBLE);
                String total = userReports.get(position).getNoOfTimesReportSubmission() + "";
                String pending = userReports.get(position).getNoOfTimesReportToBeSubmitted() + "";
                //holder.submitsts.setText(pending+"/"+total+" left");
                //} else{
                //Weekly, Daily, Monthly
                //	submitsts.setVisibility(View.INVISIBLE);
                //	}

                if (userReports.get(position).getShowSubmitButton().equalsIgnoreCase("y")) {
                    holder.imgNext.setVisibility(View.VISIBLE);
                } else {
                    holder.imgNext.setVisibility(View.INVISIBLE);
                }

                holder.mLayoutParent.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int position = (Integer) (v.getTag());
                        if (userReports != null && userReports.size() > 0) {
                            Feedback feedback = userReports.get(position);
                            if (feedback.getShowSubmitButton().equalsIgnoreCase("y")) {
                                String fb_form_id = feedback.getReportID() + "";
                                ReportFromDetailsActivity.mReportForm = null;
                                Intent intent = new Intent(context, ReportFromDetailsActivity.class);
                                intent.putExtra("fb_form_id", fb_form_id + "");
                                intent.putExtra("no_of_times_report_submited", feedback.getNoOfTimesReportSubmitted() + "");
                                intent.putExtra("fb_assign_user_id", feedback.getFbAssignUserID() + "");
                                intent.putExtra("report_title", feedback.getReportTitle() + "");
                                context.startActivityForResult(intent, 1234);
                            }
                        }
                    }
                });

            } else if (tabNumber == 1) {

                //holder.submitsts.setTextColor(context.getResources().getColor(R.color.login_btn_color_pressed));
                holder.imgNext.setVisibility(View.INVISIBLE);

                if (userReports != null && userReports.size() > 0) {
                    String total = userReports.get(position).getNoOfTimesReportSubmission() + "";
                    String submitted = userReports.get(position).getNoOfTimesReportSubmitted() + "";
                    //holder.submitsts.setText(submitted+"/"+total+" submitted");
                }
            } else {
                //holder.submitsts.setVisibility(View.INVISIBLE);
                holder.imgNext.setVisibility(View.INVISIBLE);
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
                onActivityItemClickListener.onActivityItemClick(view, (Integer) view.getTag());
            }
        }
    }

    public void updateItems(boolean animated) {
        itemsCount = userReports.size();
        animateItems = animated;
        notifyDataSetChanged();
    }

    public void setOnActivityItemClickListener(OnActivityItemClickListener onActivityItemClickListener) {
        this.onActivityItemClickListener = onActivityItemClickListener;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.layoutParent)
        LinearLayout mLayoutParent;
        @InjectView(R.id.tvReportTitle)
        TextView tvReportTitle;
        @InjectView(R.id.tvReportCreatedBy)
        TextView tvReportCreatedBy;
        @InjectView(R.id.tvReportCreatedOn)
        TextView tvReportCreatedOn;
       // @InjectView(R.id.submitsts)
        //TextView submitsts;
        @InjectView(R.id.tvReportType)
        TextView tvReportType;
        /*@InjectView(R.id.btnSubmit)
        TextView btnSubmit;*/
        @InjectView(R.id.imgNext)
        ImageView imgNext;

        public CellFeedViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public interface OnActivityItemClickListener {
        public void onActivityItemClick(View v, int position);
    }
}
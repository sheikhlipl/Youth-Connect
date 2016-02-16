package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.FileDetailsActivity;
import com.lipl.youthconnect.youth_connect.activity.FileUploadNodalOfficerActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.pojo.District;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.util.MyAdapter;

import java.util.List;

/**
 * Created by luminousinfoways on 17/12/15.
 */
public class DistrictListAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<District> districtList;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private MyAdapter.OnLoadMoreListener onLoadMoreListener;

    public DistrictListAdapter(List<District> districtList, RecyclerView recyclerView) {
        this.districtList = districtList;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return districtList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_row_dist, parent, false);

            vh = new StudentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {

            District district= (District) districtList.get(position);
            ((StudentViewHolder) holder).tvDist.setText(district.getM_district());
            ((StudentViewHolder) holder).district = district;
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return districtList.size();
    }

    public void setOnLoadMoreListener(MyAdapter.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDist;
        public District district;

        public StudentViewHolder(View v) {
            super(v);
            tvDist = (TextView) v.findViewById(R.id.tvDist);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, FileUploadNodalOfficerActivity.class);
                    intent.putExtra(Constants.INTENT_KEY_DISTRICT, district);
                    context.startActivity(intent);
                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
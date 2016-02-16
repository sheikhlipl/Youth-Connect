package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.FileUploadFinalActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.pojo.User;
import com.lipl.youthconnect.youth_connect.util.MyAdapter;

import java.util.List;

/**
 * Created by luminousinfoways on 17/12/15.
 */
public class NodalOfficerListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<User> userList;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private MyAdapter.OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;

    public NodalOfficerListAdapter(List<User> userList, RecyclerView recyclerView) {
        this.userList = userList;

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
        return userList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        if (viewId == R.id.tvDist) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, (Integer) view.getTag());
            }
        }
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

            User user = (User) userList.get(position);
            ((StudentViewHolder) holder).checkBox.setText(user.getFull_name());
            ((StudentViewHolder) holder).user = user;
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnLoadMoreListener(MyAdapter.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public User user;

        public StudentViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.tvDist);
//            v.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, FileUploadFinalActivity.class);
//                    intent.putExtra(Constants.INTENT_KEY_NODAL_OFFICER, user);
//                    context.startActivity(intent);
//                }
//            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }
}
package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.RoundedImageView;

/**
 * Created by luminousinfoways on 10/09/15.
 */
public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.ViewHolder> {

    private Context mContext;
    private boolean mIsPending;
    private int tabPosition;

    public PagerAdapter(Context context, boolean isPending, int tabPosition) {
        mContext = context;
        mIsPending = isPending;
        this.tabPosition = tabPosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        viewHolder.mTextView.setText("Text" + i);
        viewHolder.mTextView.setTag(i);
        viewHolder.mListItem.setTag(i);

        viewHolder.mTvBloodGrp.setTag(i);
        viewHolder.maudio_object_deleteview.setTag(i);
        viewHolder.maudio_object_deleteview.setVisibility(View.GONE);
        viewHolder.mainLayout.setTag(i);

        viewHolder.mListItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(tabPosition == 2) {
                    viewHolder.maudio_object_deleteview.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });

        viewHolder.maudio_object_deleteview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                showAlertDialog("Are you sure want to remove this donor?", "Remove from List", "Remove", "Cancel", position, v);
                //TODO change screen title and subtitle
            }
        });

        //BgAsync async = new BgAsync(i, viewHolder);
        //async.execute();
    }

    /*private class BgAsync extends AsyncTask<Void, Void, Void> {

        private Bitmap bitmap = null;
        private int index = -1;
        private ViewHolder viewHolder = null;

        public BgAsync(int index, ViewHolder viewHolder) {
            BgAsync.this.index = index;
            BgAsync.this.viewHolder = viewHolder;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (index >= 0 && mItems.get(index).getProfileFileName() != null && mItems.get(index).getProfileFileName().length() > 0) {
                byte[] profile = Util.getByteFromFile(mContext, mItems.get(index).getProfileFileName());
                bitmap = Util.getPhoto(profile, Util.getScreenWidth(mContext));
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            viewHolder.mProgressBar.setVisibility(View.VISIBLE);
            viewHolder.mDonorProfilePic.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            viewHolder.mProgressBar.setVisibility(View.INVISIBLE);
            viewHolder.mDonorProfilePic.setVisibility(View.VISIBLE);

            if (bitmap != null) {
                viewHolder.mDonorProfilePic.setImageBitmap(bitmap);
            } else {
                viewHolder.mDonorProfilePic.setImageResource(R.drawable.ic_action_person);
            }
        }

    }*/

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final TextView mTvBloodGrp;
        private final RoundedImageView mDonorProfilePic;
        private final RelativeLayout mListItem;
        private final ProgressBar mProgressBar;
        //        private final ImageView mDonorProfilePic;
        private final RelativeLayout maudio_object_deleteview;
        private final RelativeLayout mainLayout;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tvDonorName);
            mTvBloodGrp = (TextView) v.findViewById(R.id.tvDonorBloodGroup);
//            mDonorProfilePic = (ImageView) v.findViewById(R.id.imgDonor);
            mDonorProfilePic = (RoundedImageView) v.findViewById(R.id.imgDonor);
            mListItem = (RelativeLayout) v.findViewById(R.id.layoutDonorItem);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            maudio_object_deleteview = (RelativeLayout) v.findViewById(R.id.audio_object_deleteview);
            mainLayout = (RelativeLayout) v.findViewById(R.id.mainLayout);
        }
    }


    private void showAlertDialog(String message, String title, String positiveButtonText, String negativeButtonText, final int position, final View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                v.setVisibility(View.GONE);
            }
        });
        builder.show();
    }
}
package com.lipl.youthconnect.youth_connect.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;
import com.lipl.youthconnect.youth_connect.activity.ShowcaseEventDetailsActivity;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.DocumentUpload;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 04/01/16.
 */
public class DataAdapterExp extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Document> documentList;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;

    public DataAdapterExp(List<Document> documentList, RecyclerView recyclerView, Context context) {
        this.documentList = documentList;
        this.context = context;

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
        return documentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.showcase_event_list_item, parent, false);

            vh = new DocumentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DocumentViewHolder) {

            Document document= (Document) documentList.get(position);

            ((DocumentViewHolder) holder).tvActivityName.setText(document.getDocumentMaster().getDocument_title());

            ((DocumentViewHolder) holder).tvActivityPurpose.setText(document.getDocumentMaster().getDocument_purpose());

            String dateTime = document.getDocumentMaster().getCreated();
            try {
                if (dateTime != null && dateTime.length() > 0) {
                    //Format 2015-12-24 11:16:44
                    if (dateTime.contains(" ")) {
                        String[] dt = dateTime.split(" ");
                        String date = dt[0];
                        String time = dt[1];

                        if (date != null && date.length() > 0 && date.contains("-")) {
                            String[] dd = date.split("-");
                            String year = dd[0];
                            String month = dd[1];
                            String day = dd[2];

                            String _date_time = day + "-" + month + "-" + year + " " + time;
                            ((DocumentViewHolder) holder).tvDate.setText(_date_time);
                        }
                    }
                }
            } catch(Exception e){
                Log.e("DataAdapter", "onBindViewHolder()", e);
            }

            ((DocumentViewHolder) holder).tvPostedBy.setText(document.getUserFullName());

            ((DocumentViewHolder) holder).document = document;

            ((DocumentViewHolder) holder).horizontalAdapter.setData(documentList.get(position).getDocumentUploadList(), context);
            ((DocumentViewHolder) holder).horizontalAdapter.setRowIndex(position);

            Document doc = (Document) documentList.get(position);
            ((DocumentViewHolder) holder).document = doc;

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //
    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        public TextView tvActivityName;

        public TextView tvActivityPurpose;

        public Document document;

        public RecyclerView item_horizontal_list;

        public HorizontalAdapter horizontalAdapter;
        public TextView tvPostedBy;
        public TextView tvDate;

        public DocumentViewHolder(View v) {
            super(v);
            tvActivityName = (TextView) v.findViewById(R.id.tvActivityName);
            tvActivityPurpose = (TextView) v.findViewById(R.id.tvActivityPurpose);
            item_horizontal_list = (RecyclerView) v.findViewById(R.id.item_horizontal_list);
            tvPostedBy = (TextView) v.findViewById(R.id.tvPostedBy);
            tvDate = (TextView) v.findViewById(R.id.tvDate);

            item_horizontal_list.setLayoutManager(new LinearLayoutManager(v.getContext(), LinearLayoutManager.HORIZONTAL, false));
            horizontalAdapter = new HorizontalAdapter();
            item_horizontal_list.setAdapter(horizontalAdapter);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    private static class HorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<DocumentUpload> mDataList;
        private int mRowIndex = -1;
        private Context context;

        public HorizontalAdapter() {
        }

        public void setData(List<DocumentUpload> data, Context context) {
            if (mDataList != data) {
                mDataList = data;
                notifyDataSetChanged();
            } else {
                mDataList = new ArrayList<DocumentUpload>();
            }
            this.context = context;
        }

        public void setRowIndex(int index) {
            mRowIndex = index;
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder {

            private ImageView img;
            private ProgressBar progressBar;

            public ItemViewHolder(View itemView) {
                super(itemView);
                img = (ImageView) itemView.findViewById(R.id.imageView);
                /*img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                        int position = (Integer) (view.getTag());
                        Intent intent = new Intent(context, ShowcaseEventDetailsActivity.class);
                        intent.putExtra(Constants.SHOWCASE_EVENT_INTENT_KEY, mDataList.get(position));
                        context.startActivity(intent);
                    }
                });*/
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                //itemView.setOnClickListener(mItemClickListener);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            ItemViewHolder holder = new ItemViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
            ItemViewHolder holder = (ItemViewHolder) rawHolder;

            String uploadfile = null;
            if (mDataList != null && mDataList.size() > 0) {
                DocumentUpload documentUpload = mDataList.get(position);
                uploadfile = documentUpload.getUpload_file();
            }

            holder.img.setImageResource(R.drawable.ic_insert_drive_file);
            holder.img.setBackgroundResource(R.color.blue);
            holder.img.setTag(position);
            holder.progressBar.setTag(position);

            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youth_connect";
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;
            String filename = mDataList.get(position).getUpload_file();
            File _file = new File(fullPath, filename);
            if (_file.exists()) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.img.setVisibility(View.VISIBLE);
                setImage(_file, holder);
            } else {
                if(Util.getNetworkConnectivityStatus(context)) {
                    BgAsync async = new BgAsync(position, holder, context);
                    async.execute();
                } else{

                }
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        private View.OnClickListener mItemClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (v == null) {
                    return;
                }

                int columnIndex = (int) v.getTag();
                int rowIndex = mRowIndex;

                String text = String.format("rowIndex:%d ,columnIndex:%d", rowIndex, columnIndex);
                Log.d("test", text);
            }
        };

        private class BgAsync extends AsyncTask<Void, Void, File> {

            private Bitmap bitmap = null;
            private int index = -1;
            private ItemViewHolder viewHolder = null;
            private Context context;

            public BgAsync(int index, ItemViewHolder viewHolder, Context context) {
                BgAsync.this.index = index;
                BgAsync.this.viewHolder = viewHolder;
                BgAsync.this.context = context;
            }

            @Override
            protected File doInBackground(Void... params) {

                int a = index;
                List<DocumentUpload> _mDataList = mDataList;
                String df= mDataList.get(index).getUpload_file();
                int sd = mDataList.get(index).getUpload_file().length();

                if (index >= 0 && mDataList != null && mDataList.size() > 0
                        && index <= mDataList.size() - 1 &&
                        mDataList.get(index).getUpload_file() != null &&
                        mDataList.get(index).getUpload_file().length() > 0) {

                    int count;
                    try {

                        String fileName = mDataList.get(index).getUpload_file();

                        if (fileName == null || fileName.trim().length() <= 0) {
                            return null;
                        }

                        String req_url = Constants.BASE_URL + Constants.DOCUMENT_DOWNLOAD_REQUEST_URL + fileName;

                        URL url = new URL(req_url);
                        URLConnection conection = url.openConnection();
                        conection.connect();
                        // getting file length
                        int lenghtOfFile = conection.getContentLength();

                        // input stream to read file - with 8k buffer
                        InputStream input = new BufferedInputStream(url.openStream(), 8192);

                        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youth_connect";
                        File dir = new File(fullPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        OutputStream fOut = null;
                        File file = new File(fullPath, fileName);
                        if (file.exists())
                            file.delete();
                        file.createNewFile();

                        // Output stream to write file
                        OutputStream output = new FileOutputStream(file);

                        byte data[] = new byte[1024];

                        long total = 0;

                        while ((count = input.read(data)) != -1) {
                            total += count;
                            // publishing the progress....
                            // After this onProgressUpdate will be called
                            //publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                            // writing data to file
                            output.write(data, 0, count);
                        }

                        // flushing output
                        output.flush();

                        // closing streams
                        output.close();
                        input.close();

                        return file;

                    } catch (SocketTimeoutException e) {
                        Log.e("Error: ", e.getMessage());
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                }

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.img.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(File file) {
                super.onPostExecute(file);

                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                viewHolder.img.setVisibility(View.VISIBLE);
                setImage(file, viewHolder);
            }
        }

        private void setImage(File file, ItemViewHolder viewHolder) {
            if (file == null || file.getAbsolutePath() == null) {
                return;
            }

            final String filePath = file.getAbsolutePath();
            if (filePath != null && filePath.length() > 0
                    && ((filePath.contains("jpg")) ||
                    (filePath.contains("jpeg")) ||
                    (filePath.contains("bmp")) ||
                    (filePath.contains("png")))) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

                if (bitmap != null) {
                    viewHolder.img.setImageBitmap(bitmap);
                } else {
                    viewHolder.img.setImageResource(R.drawable.ic_file_download);
                    viewHolder.img.setBackgroundResource(R.color.blue);
                }

                viewHolder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Open Image
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File(filePath);
                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        if (extension.equalsIgnoreCase("") || mimetype == null) {
                            // if there is no extension or there is no definite mimetype, still try to open the file
                            intent.setDataAndType(Uri.fromFile(file), "image/*");
                        } else {
                            intent.setDataAndType(Uri.fromFile(file), mimetype);
                        }

                        // custom message for the intent
                        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
                        if (appIntent != null) {
                            context.startActivity(appIntent);
                        } else {
                            if (context != null && context instanceof MainActivity) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity)context, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(context.getResources().getString(R.string.no_app_found_title));
                                builder.setMessage(context.getResources().getString(R.string.no_app_found_message));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                });
            } else if (filePath != null && filePath.length() > 0
                    && ((filePath.contains("mp4")) ||
                    (filePath.contains("flv")) ||
                    (filePath.contains("3gp")) ||
                    (filePath.contains("avi")))) {
                viewHolder.img.setImageResource(R.drawable.ic_action_play_over_video);
                viewHolder.img.setBackgroundResource(R.color.blue);
                viewHolder.img.setPadding(12, 12, 12, 12);

                viewHolder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Open Video
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File(filePath);
                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        if (extension.equalsIgnoreCase("") || mimetype == null) {
                            // if there is no extension or there is no definite mimetype, still try to open the file
                            intent.setDataAndType(Uri.fromFile(file), "video/*");
                        } else {
                            intent.setDataAndType(Uri.fromFile(file), mimetype);
                        }

                        // custom message for the intent
                        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
                        if (appIntent != null) {
                            context.startActivity(appIntent);
                        } else {
                            if(context != null && context instanceof MainActivity) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity)context, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(context.getResources().getString(R.string.no_app_found_title));
                                builder.setMessage(context.getResources().getString(R.string.no_app_found_message));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                });

            } else {

                viewHolder.img.setImageResource(R.drawable.ic_insert_drive_file);
                viewHolder.img.setBackgroundResource(R.color.blue);

                viewHolder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Open Document
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File(filePath);
                        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        if (extension.equalsIgnoreCase("") || mimetype == null) {
                            // if there is no extension or there is no definite mimetype, still try to open the file
                            intent.setDataAndType(Uri.fromFile(file), "text/*");
                        } else {
                            intent.setDataAndType(Uri.fromFile(file), mimetype);
                        }

                        // custom message for the intent
                        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
                        if (appIntent != null) {
                            context.startActivity(appIntent);
                        } else {
                            if(context != null && context instanceof MainActivity) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity)context, R.style.AppCompatAlertDialogStyle);
                                builder.setTitle(context.getResources().getString(R.string.no_app_found_title));
                                builder.setMessage(context.getResources().getString(R.string.no_app_found_message));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }
                });
            }
        }
    }

    public void openDocumentFile(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }

        // custom message for the intent
        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
        if(appIntent != null){
            context.startActivity(appIntent);
        } else{
            if(context != null && context instanceof MainActivity) {
                AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity)context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(context.getResources().getString(R.string.no_app_found_title));
                builder.setMessage(context.getResources().getString(R.string.no_app_found_message));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    }

    public void openVideoFile(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "video/mp4");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }

        // custom message for the intent
        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
        if(appIntent != null){
            context.startActivity(appIntent);
        } else{
            if(context != null && context instanceof MainActivity) {
                AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity)context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(context.getResources().getString(R.string.no_app_found_title));
                builder.setMessage(context.getResources().getString(R.string.no_app_found_message));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        }
    }
}
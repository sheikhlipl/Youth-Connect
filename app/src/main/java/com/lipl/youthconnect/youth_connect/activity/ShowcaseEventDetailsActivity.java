package com.lipl.youthconnect.youth_connect.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.DocumentUpload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ShowcaseEventDetailsActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;
    private DocumentUpload documentUpload = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_event_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getIntent().getExtras() != null){
            documentUpload = getIntent().getExtras().getParcelable(Constants.SHOWCASE_EVENT_INTENT_KEY);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("File Details");

        if(Util.getNetworkConnectivityStatus(ShowcaseEventDetailsActivity.this)) {
            BgAsync async = new BgAsync(documentUpload, ShowcaseEventDetailsActivity.this);
            async.execute();
        } else{
            //TODO set image :(
            //ImageView imgShowCaseEventPic = (ImageView) findViewById(R.id.imgShowCaseEventPic);
            //imgShowCaseEventPic.setImageResource();

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.no_internet_connection_title));
            builder.setMessage(getResources().getString(R.string.no_internet_connection_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    /**
     * When touch on screen outside the keyboard, the input keyboard will hide automatically
     * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                Util.hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * To Show Material Alert Dialog
     *
     * @param code Should be one of the global declared integer constants
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText, String negativeButtonText, final int code){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
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
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private class BgAsync extends AsyncTask<Void, Void, File> {

        private Bitmap bitmap = null;
        private DocumentUpload documentUpload;
        private Context context;

        public BgAsync(DocumentUpload documentUpload, Context context) {
            BgAsync.this.documentUpload = documentUpload;
            BgAsync.this.context = context;
        }

        @Override
        protected File doInBackground(Void... params) {

            if (documentUpload != null && documentUpload.getUpload_file() != null &&
                    documentUpload.getUpload_file().length() > 0) {

                int count;
                try {

                    String fileName = documentUpload.getUpload_file();

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

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar pBar = (ProgressBar) findViewById(R.id.pBar);
            pBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);

            ProgressBar pBar = (ProgressBar) findViewById(R.id.pBar);
            pBar.setVisibility(View.INVISIBLE);

            if (file == null || file.getAbsolutePath() == null) {
                return;
            }

            ImageView imageViewPic = (ImageView) findViewById(R.id.imgShowCaseEventPic);

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
                    imageViewPic.setImageBitmap(bitmap);
                    imageViewPic.setPadding(0, 0, 0, 0);
                    imageViewPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                } else {
                    imageViewPic.setImageResource(R.drawable.ic_file_download);
                    imageViewPic.setPadding(0, 0, 0, 0);
                    imageViewPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }
            } else if(filePath != null && filePath.length() > 0
                    && ((filePath.contains("mp4")) ||
                    (filePath.contains("flv")) ||
                    (filePath.contains("3gp")) ||
                    (filePath.contains("avi")))) {
                imageViewPic.setImageResource(R.drawable.ic_action_play_over_video);
                imageViewPic.setPadding(0, 0, 0, 0);
                imageViewPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openVideoFile(filePath);
                    }
                });
            } else{
                imageViewPic.setImageResource(R.drawable.ic_insert_drive_file);
                imageViewPic.setBackgroundResource(R.color.blue);
                imageViewPic.setPadding(12, 12, 12, 12);

                imageViewPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openDocumentFile(filePath);
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
            startActivity(appIntent);
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.no_app_found_title));
            builder.setMessage(getResources().getString(R.string.no_app_found_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
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
            startActivity(appIntent);
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.no_app_found_title));
            builder.setMessage(getResources().getString(R.string.no_app_found_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    public void openAudioFile(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "audio/mp3");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }

        // custom message for the intent
        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
        if(appIntent != null){
            startActivity(appIntent);
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.no_app_found_title));
            builder.setMessage(getResources().getString(R.string.no_app_found_message));
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
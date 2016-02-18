package com.lipl.youthconnect.youth_connect.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.AssignedToUSer;
import com.lipl.youthconnect.youth_connect.pojo.FileChooseDetaiuls;
import com.lipl.youthconnect.youth_connect.pojo.FileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.FileOption;
import com.lipl.youthconnect.youth_connect.util.Util;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class FileChooserMultipleActivity extends ActionBarActivity
        implements ListView.OnItemClickListener, View.OnClickListener,
        Replication.ChangeListener{

    private static Toolbar mToolbar = null;
    private String title = "";
    private String purpose = "";
    private String doc_id = "";
    private int is_doc_id_auto_generated = 0;

    private LinearLayout mRevealView;
    private boolean hidden = true;

    private static final int PICK_IMAGE_REQUEST = 134;
    private static final int PICK_VIDEO_REQUEST = 127;
    private static final int PICK_AUDIO_REQUEST = 115;
    private static final int PICK_DOC_REQUEST = 105;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 102;
    private static final int RQS_RECORDING = 144;
    private static final String TAG = "FileChooserActivity";

    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static List<PendingFileToUpload> fileUploadList = null;
    public static List<FileToUpload> fileToUploads = null;
    private boolean isFromActivityResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Choose file to send");

        if(fileUploadList == null){
            fileUploadList = new ArrayList<PendingFileToUpload>();
        }

        if(fileToUploads == null){
            fileToUploads = new ArrayList<FileToUpload>();
        }

        if(getIntent().getExtras() != null){
            title = getIntent().getExtras().getString(Constants.DOC_TITLE);
            purpose = getIntent().getExtras().getString(Constants.DOC_PURPOSE);
            doc_id = getIntent().getExtras().getString(Constants.DOC_ID);
            is_doc_id_auto_generated = getIntent().getExtras().getInt(Constants.IS_DOC_ID_AUTO_GENERATED);
        }

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);

        ImageView imgGallery = (ImageView) findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);
        ImageView imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(this);
        ImageView imgVideo = (ImageView) findViewById(R.id.imgVideo);
        imgVideo.setOnClickListener(this);
        ImageView imgAudio = (ImageView) findViewById(R.id.imgAudio);
        imgAudio.setOnClickListener(this);
        ImageView imgDoc = (ImageView) findViewById(R.id.imgDoc);
        imgDoc.setOnClickListener(this);
        ImageView imgMicrophone = (ImageView) findViewById(R.id.imgMicrophone);
        imgMicrophone.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isFromActivityResult == false){
            setPreviousDataToList();
        }
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION_REPLICATION_CHANGE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void setPreviousDataToList(){

        LinearLayout layoutDoc = (LinearLayout) findViewById(R.id.layoutDoc);
        if(fileUploadList != null && fileUploadList.size() > 0){
            for(int i = 0; i < fileUploadList.size(); i++){
                PendingFileToUpload fileUpload1 = fileUploadList.get(i);
                int file_type = fileUpload1.getFileType();
                String file_path = fileUpload1.getFilePath();
                int is_uploaded = fileUpload1.getIs_uploaded();

                if(fileUpload1 != null && file_type == Constants.DOC){
                    if (file_path == null || file_path.length() <= 0) {
                        return;
                    } else{
                        PendingFileToUpload pendingFileToUpload = new PendingFileToUpload(Parcel.obtain());
                        String FilePath = file_path;
                        pendingFileToUpload = getPendingFileToBeUploadFromData(FilePath, "", Constants.DOC, is_uploaded);

                        addViewToList(pendingFileToUpload, 1);
                    }
                }

                if(fileUpload1 != null
                        && fileUpload1.getFileUri() != null) {
                    Uri uri_path = Uri.parse(fileUpload1.getFileUri());
                    if (uri_path == null || file_type < 0) {
                        return;
                    }

                    if (file_type == Constants.AUDIO) {
                        PendingFileToUpload pendingFileToUpload = new PendingFileToUpload(Parcel.obtain());

                            Uri selectedAudioUri = uri_path;
                            String selectedAudioPath = getPathForAudio(selectedAudioUri);
                            pendingFileToUpload = getPendingFileToBeUploadFromData(selectedAudioPath, selectedAudioUri.toString(), Constants.AUDIO, is_uploaded);

                        addViewToList(pendingFileToUpload, 1);
                    } else if (file_type == Constants.VIDEO) {
                        final Uri uri = uri_path;
                        PendingFileToUpload pendingFileToUpload = new PendingFileToUpload(Parcel.obtain());
                        Uri selectedVideoUri = uri;
                        String selectedImagePath = getPathForVideo(selectedVideoUri);
                        pendingFileToUpload = getPendingFileToBeUploadFromData(selectedImagePath, selectedVideoUri.toString(), Constants.VIDEO, is_uploaded);
                        addViewToList(pendingFileToUpload, 1);
                    } else if(file_type == Constants.IMAGE){
                        final Uri uri = uri_path;
                        String filePathForImage = getRealPathFromURI(uri_path);
                        Uri selectedImageUri = uri;
                        PendingFileToUpload pendingFileToUpload = new PendingFileToUpload(Parcel.obtain());
                        pendingFileToUpload = getPendingFileToBeUploadFromData(filePathForImage, selectedImageUri.toString(), Constants.IMAGE, is_uploaded);
                        addViewToList(pendingFileToUpload, 1);
                    }
                }
            }
        }
    }

    private void addViewToList(PendingFileToUpload pendingFileToBeUpload, int is_new) {
        LinearLayout layoutDoc = (LinearLayout) findViewById(R.id.layoutDoc);

        String file_path = pendingFileToBeUpload.getFilePath();
        final String uri_path = pendingFileToBeUpload.getFileUri();
        int status = pendingFileToBeUpload.getIs_uploaded();
        int file_type = pendingFileToBeUpload.getFileType();

        if(is_new == 0){

            for(int i= 0; i < layoutDoc.getChildCount(); i++){
                RelativeLayout relativeLayout = (RelativeLayout) ((ViewGroup)layoutDoc.getChildAt(i));
                // new background because something has changed
                // check if it's not the imageView you just clicked because you don't want to change its background

                String file_path_tag = (String) relativeLayout.getTag();
                if(pendingFileToBeUpload != null){
                    String filePath =  pendingFileToBeUpload.getFilePath();
                    if(filePath != null && file_path.equalsIgnoreCase(file_path_tag)){
                        ((ProgressBar) relativeLayout.findViewById(R.id.pBar)).setVisibility(View.INVISIBLE);
                        if(status == 1){
                            //Sent failure
                            ImageView imgSent = (ImageView) relativeLayout.findViewById(R.id.imgSent);
                            imgSent.setVisibility(View.VISIBLE);
                            ImageView imgPending = (ImageView) relativeLayout.findViewById(R.id.imgPending);
                            imgPending.setVisibility(View.GONE);
                        } else {
                            //Sent successfully
                            ImageView imgSent = (ImageView) relativeLayout.findViewById(R.id.imgSent);
                            imgSent.setVisibility(View.GONE);
                            ImageView imgPending = (ImageView) relativeLayout.findViewById(R.id.imgPending);
                            imgPending.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        } else{
            switch (file_type) {
                case Constants.AUDIO:

                    RelativeLayout layout_doc = (RelativeLayout) LayoutInflater
                            .from(FileChooserMultipleActivity.this)
                            .inflate(R.layout.layout_list_file_child_doc, null);

                    final String _filePath = getPathForAudio(Uri.parse(uri_path));
                    if(_filePath != null) {
                        TextView textDocName = (TextView) layout_doc.findViewById(R.id.textDocName);
                        textDocName.setText(_filePath);
                    }

                    ((ProgressBar) layout_doc.findViewById(R.id.pBar)).setVisibility(View.INVISIBLE);
                    if(status == 1){
                        //Sent failure
                        ImageView imgSent = (ImageView) layout_doc.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.VISIBLE);
                        ImageView imgPending = (ImageView) layout_doc.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.GONE);
                    } else {
                        //Sent successfully
                        ImageView imgSent = (ImageView) layout_doc.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.GONE);
                        ImageView imgPending = (ImageView) layout_doc.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.VISIBLE);
                    }

                    layoutDoc.addView(layout_doc);
                    layout_doc.setTag(_filePath);
                    layout_doc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            File file = new File(_filePath);
                            intent.setDataAndType(Uri.fromFile(file), "audio/*");
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else{
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent),
                                        "Your device does not support this file." ,
                                        Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                View snackbarView = snackbar.getView();
                                TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                tv.setTextColor(Color.WHITE);
                                TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                                tvAction.setTextColor(Color.CYAN);
                                snackbar.show();
                            }
                        }
                    });

                    break;
                case Constants.VIDEO:

                    RelativeLayout layoutVideoItem = (RelativeLayout) LayoutInflater
                            .from(FileChooserMultipleActivity.this)
                            .inflate(R.layout.layout_list_file_child_video, null);

                    ImageView imageThumbnail = (ImageView) layoutVideoItem.findViewById(R.id.imageThumbnail);
                    ImageView imgPlay = (ImageView) layoutVideoItem.findViewById(R.id.imgPlay);

                    imgPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(uri_path), "video/*");
                            startActivity(Intent.createChooser(intent, "Complete action using"));
                        }
                    });

                    String selectedImagePath = getPathForVideo(Uri.parse(uri_path));
                    if(selectedImagePath != null && selectedImagePath.length() > 0) {
                        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(selectedImagePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        imageThumbnail.setImageBitmap(bmThumbnail);
                    }

                    String filePathForVideo = getPathForVideo(Uri.parse(uri_path));
                    layoutVideoItem.setTag(filePathForVideo);

                    ((ProgressBar) layoutVideoItem.findViewById(R.id.pBar)).setVisibility(View.INVISIBLE);
                    if(status == 1){
                        //Sent failure
                        ImageView imgSent = (ImageView) layoutVideoItem.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.VISIBLE);
                        ImageView imgPending = (ImageView) layoutVideoItem.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.GONE);
                    } else {
                        //Sent successfully
                        ImageView imgSent = (ImageView) layoutVideoItem.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.GONE);
                        ImageView imgPending = (ImageView) layoutVideoItem.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.VISIBLE);
                    }

                    layoutDoc.addView(layoutVideoItem);

                    break;
                case Constants.IMAGE:

                    RelativeLayout layoutImageItem = (RelativeLayout) LayoutInflater
                            .from(FileChooserMultipleActivity.this)
                            .inflate(R.layout.layout_list_file_child_image, null);

                    ImageView img = (ImageView) layoutImageItem.findViewById(R.id.img);
                    String imagePath = getRealPathFromURI(Uri.parse(uri_path));
                    System.out.println("Image Path : " + imagePath);
                    img.setImageURI(Uri.parse(uri_path));

                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(uri_path), "image/*");
                            startActivity(Intent.createChooser(intent, "Complete action using"));
                        }
                    });

                    String filePathForImage = getRealPathFromURI(Uri.parse(uri_path));
                    layoutImageItem.setTag(filePathForImage);

                    ((ProgressBar) layoutImageItem.findViewById(R.id.pBar)).setVisibility(View.INVISIBLE);
                    if(status == 1){
                        //Sent failure
                        ImageView imgSent = (ImageView) layoutImageItem.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.VISIBLE);
                        ImageView imgPending = (ImageView) layoutImageItem.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.GONE);
                    } else {
                        //Sent successfully
                        ImageView imgSent = (ImageView) layoutImageItem.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.GONE);
                        ImageView imgPending = (ImageView) layoutImageItem.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.VISIBLE);
                    }

                    layoutDoc.addView(layoutImageItem);

                    break;
                case Constants.DOC:

                    RelativeLayout _layout_doc = (RelativeLayout) LayoutInflater
                            .from(FileChooserMultipleActivity.this)
                            .inflate(R.layout.layout_list_file_child_doc, null);

                    if(file_path != null) {
                        TextView textDocName = (TextView) _layout_doc.findViewById(R.id.textDocName);
                        textDocName.setText(file_path);
                        _layout_doc.setTag(file_path);
                    }

                    ((ProgressBar) _layout_doc.findViewById(R.id.pBar)).setVisibility(View.INVISIBLE);
                    if(status == 1){
                        //Sent failure
                        ImageView imgSent = (ImageView) _layout_doc.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.VISIBLE);
                        ImageView imgPending = (ImageView) _layout_doc.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.GONE);
                    } else {
                        //Sent successfully
                        ImageView imgSent = (ImageView) _layout_doc.findViewById(R.id.imgSent);
                        imgSent.setVisibility(View.GONE);
                        ImageView imgPending = (ImageView) _layout_doc.findViewById(R.id.imgPending);
                        imgPending.setVisibility(View.VISIBLE);
                    }

                    layoutDoc.addView(_layout_doc);

                    final String _file_path_ = file_path;
                    _layout_doc.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (_file_path_ != null && _file_path_.length() > 0) {
                                openDocumentFile(_file_path_);
                            }
                        }
                    });

                    break;
                default:
                    break;
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
        if(appIntent != null) {
            startActivity(appIntent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(FileChooserMultipleActivity.this, R.style.AppCompatAlertDialogStyle);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.imgGallery:

                CharSequence colors[] = new CharSequence[] {"Videos", "Images"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose one");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    switch (which) {
                        case 0:
                            Intent intentVideo = new Intent();
                            intentVideo.setType("video/*");
                            intentVideo.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intentVideo, "Select video"), PICK_VIDEO_REQUEST);
                            break;
                        case 1:
                            Intent intentImage = new Intent();
                            intentImage.setType("image/*");
                            intentImage.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intentImage, "Select image"), PICK_IMAGE_REQUEST);
                            break;
                        default:
                            break;
                    }
                    }
                });
                builder.show();

                break;
            case R.id.imgPhoto:
                clickPic();
                break;
            case R.id.imgVideo:
                recordVideo();
                break;
            case R.id.imgAudio:
                Intent intentImage = new Intent();
                intentImage.setType("audio/*");
                intentImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentImage, "Select audio"), PICK_AUDIO_REQUEST);
                break;
            case R.id.imgDoc:
                Intent intentDoc = new Intent();
                intentDoc.setType("file/*");
                intentDoc.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentDoc, "Select document"), PICK_DOC_REQUEST);
                break;
            case R.id.imgMicrophone:
                Intent intent =
                        new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent, RQS_RECORDING);
                break;
            default:
                break;
        }
    }

    private PendingFileToUpload getPendingFileToBeUploadFromData
            (String file_path, String uri_path, int file_type, int is_uploaded){

        String user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0)+"";
        int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_TYPE, 0);
        String m_desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.SP_USER_DESG_ID, "");

        PendingFileToUpload pendingFileToUpload = new PendingFileToUpload(Parcel.obtain());
        pendingFileToUpload.setUser_id(user_id);
        pendingFileToUpload.setUser_type_id(user_type_id+"");
        pendingFileToUpload.setM_desg_id(m_desg_id);
        pendingFileToUpload.setTitle(title);
        pendingFileToUpload.setPurpose(purpose);
        pendingFileToUpload.setFileUri(uri_path);
        pendingFileToUpload.setFileType(file_type);
        pendingFileToUpload.setIs_uploaded(is_uploaded);
        pendingFileToUpload.setFilePath(file_path);

        if(is_doc_id_auto_generated == 1){
            pendingFileToUpload.setIsDocIdFromServer(0);
        } else {
            pendingFileToUpload.setIsDocIdFromServer(1);
        }

        List<String> fileList =  new ArrayList<String>();
        fileList.add(file_path);

        return pendingFileToUpload;
    }

    private String getMimeTypeFromUri(Uri uri){
        ContentResolver cR = getContentResolver();
        String type = cR.getType(uri);
        return type;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        setPreviousDataToList();
        isFromActivityResult = true;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("File Upload");
        builder.setMessage("Are you sure want to upload this file?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                PendingFileToUpload pendingFileToUpload = new PendingFileToUpload(Parcel.obtain());
                FileToUpload fileToUpload = new FileToUpload();

                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                    Uri selectedImageUri = data.getData();
                    String filePathForImage = getRealPathFromURI(selectedImageUri);
                    pendingFileToUpload = getPendingFileToBeUploadFromData(filePathForImage, selectedImageUri.toString(), Constants.IMAGE, 0);
                    fileToUpload.setInputStream(selectedImageUri);
                    String filename = filePathForImage.substring(filePathForImage.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    fileToUpload.setMime_type(getMimeTypeFromUri(selectedImageUri));

                } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                    Uri selectedVideoUri = data.getData();
                    String selectedImagePath = getPathForVideo(selectedVideoUri);
                    pendingFileToUpload = getPendingFileToBeUploadFromData(selectedImagePath, selectedVideoUri.toString(), Constants.VIDEO, 0);
                    fileToUpload.setInputStream(selectedVideoUri);
                    String filename = selectedImagePath.substring(selectedImagePath.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    fileToUpload.setMime_type(getMimeTypeFromUri(selectedVideoUri));

                } else if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK
                        && data != null && data.getData() != null) {
                    Uri selectedAudioUri = data.getData();
                    String selectedAudioPath = getPathForAudio(selectedAudioUri);
                    pendingFileToUpload = getPendingFileToBeUploadFromData(selectedAudioPath, selectedAudioUri.toString(), Constants.AUDIO, 0);
                    fileToUpload.setInputStream(selectedAudioUri);
                    String filename = selectedAudioPath.substring(selectedAudioPath.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    fileToUpload.setMime_type(getMimeTypeFromUri(selectedAudioUri));

                } else if (requestCode == PICK_DOC_REQUEST && resultCode == RESULT_OK
                        && data != null && data.getData() != null) {

                    String FilePath = data.getData().getPath();
                    Uri geturi = data.getData();
                    pendingFileToUpload = getPendingFileToBeUploadFromData(FilePath, "", Constants.DOC, 0);
                    fileToUpload.setInputStream(geturi);
                    String filename = FilePath.substring(FilePath.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    String type = null;
                    String extension = MimeTypeMap.getFileExtensionFromUrl(filename);
                    if (extension != null) {
                        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    }
                    fileToUpload.setMime_type(type);
                    Log.i("asdfsdf","asdfsadf");

                } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                        && data != null && data.getExtras() != null) {

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    File finalFile = new File(getRealPathFromURI(tempUri));
                    if (finalFile != null) {
                        System.out.println(finalFile.getPath());
                    }
                    String filePathForImage = getRealPathFromURI(tempUri);
                    pendingFileToUpload = getPendingFileToBeUploadFromData(filePathForImage, tempUri.toString(), Constants.IMAGE, 0);
                    fileToUpload.setInputStream(tempUri);
                    String filename = filePathForImage.substring(filePathForImage.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    fileToUpload.setMime_type(getMimeTypeFromUri(tempUri));
                } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK
                        && data != null && data.getData() != null) {
                    Uri selectedVideoUri = data.getData();
                    String selectedImagePath = getPathForVideo(selectedVideoUri);
                    pendingFileToUpload = getPendingFileToBeUploadFromData(selectedImagePath, selectedVideoUri.toString(), Constants.VIDEO, 0);
                    fileToUpload.setInputStream(selectedVideoUri);
                    String filename = selectedImagePath.substring(selectedImagePath.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    fileToUpload.setMime_type(getMimeTypeFromUri(selectedVideoUri));
                } else if (requestCode == RQS_RECORDING && resultCode == RESULT_OK
                        && data != null && data.getData() != null) {
                    Uri selectedAudioUri = data.getData();
                    String selectedAudioPath = getPathForAudio(selectedAudioUri);
                    pendingFileToUpload = getPendingFileToBeUploadFromData(selectedAudioPath, selectedAudioUri.toString(), Constants.AUDIO, 0);
                    fileToUpload.setInputStream(selectedAudioUri);
                    String filename = selectedAudioPath.substring(selectedAudioPath.lastIndexOf("/")+1);
                    fileToUpload.setFile_name(filename);
                    fileToUpload.setMime_type(getMimeTypeFromUri(selectedAudioUri));
                }

                addViewToList(pendingFileToUpload, 1);
                fileUploadList.add(pendingFileToUpload);
                fileToUploads.add(fileToUpload);

                /*
                Log.i(TAG, "File path call service");
                Intent intent = new Intent(FileChooserMultipleActivity.this, FileUploadService.class);
                intent.putExtra("FileUpload", pendingFileToUpload);
                Log.i(TAG, "File Details before start service " + pendingFileToUpload.getFilePath());
                startService(intent);
                registerReceiver(broadcastReceiver, new IntentFilter(FileUploadService.BROADCAST_ACTION));*/


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if(resultCode == RESULT_OK){
            builder.show();
        }
    }

    private String createDocument(Database database, String doc_title,
                String doc_purpose, String doc_created_by_user_name,
                List<String> fileToUploads,List<FileToUpload> fileToUploadList, int doc_created_by_user_id,
                                  List<AssignedToUSer> assigned_to_user_ids) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        String currentTimestamp = System.currentTimeMillis()+"";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DatabaseUtil.DOC_TITLE, doc_title);
        map.put(DatabaseUtil.DOC_PURPOSE, doc_purpose);
        map.put(DatabaseUtil.DOC_CREATED_BY_USER_NAME, doc_created_by_user_name);
        map.put(DatabaseUtil.DOC_CREATED_BY_USER_ID, doc_created_by_user_id);
        map.put(DatabaseUtil.DOC_FILES, fileToUploads);
        map.put(DatabaseUtil.DOC_IS_UPLOADED, 0);
        map.put(DatabaseUtil.DOC_CREATED, currentTimestamp);
        map.put(DatabaseUtil.DOC_ASSIGNED_TO_USER_IDS, assigned_to_user_ids);
        try {
            // Save the properties to the document
            document.putProperties(map);
            Log.i(TAG, "Document created.");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        try {
            DatabaseUtil.getDocumentFromDocumentId(DatabaseUtil.getDatabaseInstance(FileChooserMultipleActivity.this, Constants.YOUTH_CONNECT_DATABASE), documentId);
        } catch(CouchbaseLiteException exception){
            Log.e(TAG, "gdsfg", exception);
        } catch(IOException exception){
            Log.e(TAG, "gdsfg", exception);
        } catch(Exception exception){
            Log.e(TAG, "gdsfg", exception);
        }
        Log.i(TAG, "Doc Id" + documentId);

        try {
            // Add or update an image to a document as a JPEG attachment:
            UnsavedRevision newRev = document.getCurrentRevision().createRevision();
            if(fileToUploadList != null && fileToUploadList.size() > 0){
                for(int i = 0; i < fileToUploadList.size(); i++){
                    FileToUpload fileToUpload = fileToUploadList.get(i);
                    Uri uri = fileToUpload.getInputStream();
                    String file_name = fileToUpload.getFile_name();
                    String mime_type = fileToUpload.getMime_type();
                    InputStream stream = getContentResolver().openInputStream(uri);
                    newRev.setAttachment(file_name, mime_type, stream);
                }
            }
            newRev.save();
        } catch(FileNotFoundException exception){
            Log.e("FileChooserMultiple", "onActivityResult", exception);
        } catch (CouchbaseLiteException exception){
            Log.e("FileChooserMultiple", "onActivityResult", exception);
        } catch(Exception exception){
            Log.e("FileChooserMultiple", "onActivityResult", exception);
        }

        return documentId;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /*public String getRealPathFromURI(Uri uri) {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.LOLLIPOP){

            Uri selectedImage = uri;
            String wholeID = DocumentsContract.getDocumentId(selectedImage);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);

            String filePath = "";

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;

        } else {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }*/

    public String getRealPathFromURI(final Uri uri) {

        Context context = FileChooserMultipleActivity.this;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private String getBase64EcodedString(String picturePath) {
        // Image location URL
        Log.e("path", "----------------" + picturePath);

        if(picturePath == null){
            return null;
        }

        // Image
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        //String ba1 = Base64.encodeBase64String(ba);
        String ba1 = android.util.Base64.encodeToString(ba, android.util.Base64.DEFAULT);
        Log.e("base64", "-----" + ba1);

        return ba1;
    }

    /**
     *  Manage the quality of image by changing it quality eg. 90, 50, etc.
     *  bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
     * */
    private String getBase64EncodedStringFullImage(String picturePath){

        if(picturePath == null){
            return null;
        }

        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBase64String(ba);

        return ba1;
    }

    private void setPic(String picturePath) {

        if(picturePath == null){
            return;
        }

        // Get the dimensions of the View
        ImageView mImageView = (ImageView) findViewById(R.id.img);
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private File createImageFile(String picturePath) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        picturePath = image.getAbsolutePath();
        Log.e("Getpath", "Cool" + picturePath);
        return image;
    }

    public String getPathForVideo(Uri uri) {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.LOLLIPOP){

            Uri selectedImage = uri;
            String wholeID = DocumentsContract.getDocumentId(selectedImage);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Video.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Video.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);

            String filePath = "";

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;

        } else {

            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    public String getPathForAudio(Uri uri) {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion >= Build.VERSION_CODES.LOLLIPOP){

            Uri selectedImage = uri;
            String wholeID = DocumentsContract.getDocumentId(selectedImage);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Audio.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Audio.Media._ID + "=?";

            Cursor cursor = getContentResolver().
                    query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);

            String filePath = "";

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;

        } else {

            String[] projection = {MediaStore.Audio.Media.DATA};
            Cursor cursor = managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    private void clickPic() {
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    /**
     * To Show Material Alert Dialog
     *
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText, final boolean isSuccess){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (isSuccess) {
                        finish();
                    }
                }
            });
            if (!((Activity) FileChooserMultipleActivity.this).isFinishing()) {
                builder.show();
            }
        } catch (WindowManager.BadTokenException exception){
            Log.e("FileChooserMultiple","showAlertDialog()",exception);
        }
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//        FileOption o = adapter.getItem(position);
//        o.toggleChecked();
//        CheckBox checkBox = (CheckBox) view.findViewById(R.id.chkBox);
//        checkBox.setChecked(o.isSelected());
//
//        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
//            currentDir = new File(o.getPathForImage());
//            fill(currentDir);
//        }else
//        {
//            onFileClick(o);
//        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.INTENT_KEY_FILE_PATH, "");
        setResult(RESULT_OK, returnIntent);
        finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_upload_multiple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_attach) {
            int cx = (mRevealView.getLeft() + mRevealView.getRight());
//                int cy = (mRevealView.getTop() + mRevealView.getBottom())/2;
            int cy = mRevealView.getTop();

            int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                SupportAnimator animator =
                        ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(800);

                SupportAnimator animator_reverse = animator.reverse();

                if (hidden) {
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {
                    animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {

                        }

                        @Override
                        public void onAnimationEnd() {
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;

                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });
                    animator_reverse.start();

                }
            } else {
                if (hidden) {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
                    mRevealView.setVisibility(View.VISIBLE);
                    anim.start();
                    hidden = false;

                } else {
                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    anim.start();

                }
            }

            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
        } else if(id == R.id.action_done){
            String doc_created_by_user_name = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.SP_USER_NAME, "");
            int doc_created_by_user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);
            List<AssignedToUSer> assigned_to_user_list = new ArrayList<AssignedToUSer>();

            int userid = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);
            String username = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.SP_USER_NAME, "");

            AssignedToUSer assignedToUSer = new AssignedToUSer();
            assignedToUSer.setUser_id(userid);
            assignedToUSer.setUser_name(username);
            assigned_to_user_list.add(assignedToUSer);

            List<String> fileNames = new ArrayList<String>();
            if(fileToUploads != null && fileToUploads.size() > 0) {
                for (FileToUpload fileToUpload : fileToUploads) {
                    if (fileToUpload.getFile_name() != null) {
                        fileNames.add(fileToUpload.getFile_name());
                    }
                }
            }

            try {
                createDocument(DatabaseUtil.getDatabaseInstance(FileChooserMultipleActivity.this, Constants.YOUTH_CONNECT_DATABASE),
                        title, purpose, doc_created_by_user_name, fileNames, fileToUploads, doc_created_by_user_id, assigned_to_user_list);
            } catch(CouchbaseLiteException exception){
                Log.e(TAG, "onActivityResult()", exception);
            } catch(IOException exception){
                Log.e(TAG, "onActivityResult()", exception);
            } catch(Exception exception){
                Log.e(TAG, "onActivityResult()", exception);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(FileChooserMultipleActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Doc upload");
            builder.setMessage("Done");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        DatabaseUtil.startReplications(FileChooserMultipleActivity.this, FileChooserMultipleActivity.this, TAG);
                    } catch(CouchbaseLiteException exception){
                        Log.e(TAG, "onClick()", exception);
                    } catch(IOException exception){
                        Log.e(TAG, "onClick()", exception);
                    } catch (Exception exception){
                        Log.e(TAG, "onClick()", exception);
                    }
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changed(Replication.ChangeEvent event) {

    }
}
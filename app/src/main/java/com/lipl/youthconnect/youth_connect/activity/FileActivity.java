package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.PullAndLoadListView;
import com.lipl.youthconnect.youth_connect.util.PullToRefreshListView;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.DocDataAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.DocumentAssign;
import com.lipl.youthconnect.youth_connect.pojo.DocumentMaster;
import com.lipl.youthconnect.youth_connect.pojo.DocumentUpload;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;

    private LinkedList<Document> mListItems;
    private PullAndLoadListView listView;
    private DocDataAdapter adapter;
    private static final String TAG = "FileActivity";
    private int doc_last_id = 0;
    private SearchView search;
    private ProgressBar pBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Documentation");

        pBar = (ProgressBar) findViewById(R.id.pBar);

        search = (SearchView) findViewById(R.id.searchView1);
        search.setQueryHint("Search Here");

        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setFileList(newText);
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FileActivity.this, FileUploadFinalActivity.class));
            }
        });

        if(mListItems == null) {
            mListItems = new LinkedList<Document>();
        }

        listView = (PullAndLoadListView) findViewById(R.id.qnaList);
        adapter = new DocDataAdapter(mListItems, this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mListItems == null) {
            mListItems = new LinkedList<Document>();
        }

        mListItems = new LinkedList<Document>();
        listView = (PullAndLoadListView) findViewById(R.id.qnaList);
        adapter = new DocDataAdapter(mListItems, FileActivity.this);

        //TODO if any user took action then refresh otherwise not to refresh,
        // simply show data from local db

        int is_action_taken = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 0);
        if(is_action_taken == 1 && Util.getNetworkConnectivityStatus(FileActivity.this)) {
            getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 0).commit();
            doc_last_id = 0;
        }

        new AsyncTask<Void, Void, Void>(){

            //private ActivityIndicator activityIndicator = ActivityIndicator.ctor(FileActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(isFinishing() == false){
                    if (pBar == null) {
                        pBar = (ProgressBar) findViewById(R.id.pBar);
                    }
                    pBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(isFinishing() == false) {
                    if (pBar == null) {
                        pBar = (ProgressBar) findViewById(R.id.pBar);
                    }
                    pBar.setVisibility(View.GONE);
                }

                if(FileActivity.this.isFinishing() == false ){
                    adapter = new DocDataAdapter(mListItems, FileActivity.this);
                    listView.setAdapter(adapter);

                    if ((mListItems == null || mListItems.size() <= 0)
                            && (Util.getNetworkConnectivityStatus(FileActivity.this))) {
                        doc_last_id = 0;

                    }

                    // Set a listener to be invoked when the list should be refreshed.
                    listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

                        public void onRefresh() {
                            // Do work to refresh the list here.
                            if(Util.getNetworkConnectivityStatus(FileActivity.this)) {
                                doc_last_id = 0;

                            }
                        }
                    });

                    // set a listener to be invoked when the list reaches the end
                    listView.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

                        public void onLoadMore() {
                            // Do the work to load more items at the end of list
                            // here
                        }
                    });
                }
            }
        }.execute();
    }

    private void setFileList(String filterText){
        if(mListItems == null){
            mListItems = new LinkedList<Document>();
        }

        final List<Document> _documentList = new ArrayList<Document>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < mListItems.size(); i++){
                String report_title = mListItems.get(i).getDocumentMaster().getDocument_title();
                if(report_title.contains(filterText)){
                    _documentList.add(mListItems.get(i));
                }
            }
        } else{
            _documentList.addAll(mListItems);
        }

        adapter = new DocDataAdapter(_documentList, FileActivity.this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.menu_file_actionbar, menu);
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
}
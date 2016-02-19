package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.design.widget.Snackbar;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.AssignedToUSer;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.DocUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.NodalOfficerListViewAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.User;

import org.json.JSONArray;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUploadNodalOfficerActivity extends ActionBarActivity implements Replication.ChangeListener {

    private static Toolbar mToolbar = null;
    private TextView tvEmptyView;
    //private RecyclerView listView;
    private ListView listView;
    private NodalOfficerListViewAdapter mAdapter;

    private List<User> nodalOfficers;
    public static List<User> selectedNodalOfficers;

    private SearchView search;
    protected Handler handler;

    private Doc document = null;
    private static final String TAG = "FUNodalOActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_nodal_officer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Select Nodal Officers");

        search = (SearchView) findViewById(R.id.searchView1);
        search.setQueryHint("SearchView");

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
                setNodalOfficersList(newText);
                return false;
            }
        });

        tvEmptyView = (TextView) findViewById(R.id.tvNoRecordFoundText);
        listView = (ListView) findViewById(R.id.distList);
        if(nodalOfficers == null) {
            nodalOfficers = new ArrayList<User>();
        }
        handler = new Handler();

        // create an Object for Adapter
        mAdapter = new NodalOfficerListViewAdapter(nodalOfficers, FileUploadNodalOfficerActivity.this);

        // set the adapter object to the Recyclerview
        listView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();

        if (nodalOfficers.isEmpty()) {
            listView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
        selectedNodalOfficers = new ArrayList<User>();

        if(getIntent().getExtras() != null){
            document = (Doc) getIntent().getExtras().getSerializable(Constants.INTENT_KEY_DOCUMENT);
        }

    }

    private void setNodalOfficersList(String filterText){

        final List<User> nodalOfficerList = new ArrayList<User>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < nodalOfficers.size(); i++){
                String m_district = nodalOfficers.get(i).getFull_name();
                if(m_district.contains(filterText)){
                    nodalOfficerList.add(nodalOfficers.get(i));
                }
            }
        } else{
            nodalOfficerList.addAll(nodalOfficers);
        }

        // create an Object for Adapter
        mAdapter = new NodalOfficerListViewAdapter(nodalOfficerList, FileUploadNodalOfficerActivity.this);

        // set the adapter object to the Recyclerview
        listView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();


        if (nodalOfficerList.isEmpty()) {
            listView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.menu_actionbar_nodal_officer_list, menu);
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
        } else if(id == android.R.id.home) {
            onBackPressed();
        } else if(id == R.id.action_send) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage("Are you sure want to send document to selected nodal officers?");
            builder.setTitle("Document assign");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendDocumnetToNodalOfficers();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendDocumnetToNodalOfficers(){
        if(selectedNodalOfficers != null && selectedNodalOfficers.size() > 0){
            List<AssignedToUSer> assignedToUSers = document.getDoc_assigned_to_user_ids();
            if(assignedToUSers == null){
                assignedToUSers = new ArrayList<AssignedToUSer>();
            }
            for(int i = 0; i < selectedNodalOfficers.size(); i++){
                User user = selectedNodalOfficers.get(i);
                String user_name = user.getFull_name();
                int user_id = user.getUser_id();

                AssignedToUSer assignedToUSer = new AssignedToUSer();
                assignedToUSer.setUser_name(user_name);
                assignedToUSer.setUser_id(user_id);

                assignedToUSers.add(assignedToUSer);
            }

            if(document != null){
                String doc_id = document.getDoc_id();
                try {
                    DocUtil.updateDocForAssignedUsers(DatabaseUtil.getDatabaseInstance(FileUploadNodalOfficerActivity.this,
                            Constants.YOUTH_CONNECT_DATABASE), doc_id, assignedToUSers);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc assignment");
                    builder.setMessage("Done.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                DatabaseUtil.startReplications(FileUploadNodalOfficerActivity.this,
                                        FileUploadNodalOfficerActivity.this, TAG);
                            } catch(CouchbaseLiteException exception){
                                Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                            } catch(IOException exception){
                                Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                            } catch(Exception exception){
                                Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } catch(CouchbaseLiteException exception){
                    Log.e(TAG, "OnClick()", exception);
                } catch(IOException exception){
                    Log.e(TAG, "OnClick()", exception);
                } catch(Exception exception){
                    Log.e(TAG, "OnClick()", exception);
                }
            }
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {

    }
}
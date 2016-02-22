package com.lipl.youthconnect.youth_connect.activity;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.AssignedToUSer;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.DocUtil;
import com.lipl.youthconnect.youth_connect.util.PullAndLoadListView;
import com.lipl.youthconnect.youth_connect.util.PullToRefreshListView;
import com.lipl.youthconnect.youth_connect.util.QAUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.DocDataAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.DocumentAssign;
import com.lipl.youthconnect.youth_connect.pojo.DocumentMaster;
import com.lipl.youthconnect.youth_connect.pojo.DocumentUpload;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.User;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private static Toolbar mToolbar = null;

    private LinkedList<Doc> mListItems;
    private ListView listView;
    private DocDataAdapter adapter;
    private static final String TAG = "FileActivity";
    private int doc_last_id = 0;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FileActivity.this, FileUploadFinalActivity.class));
            }
        });

        if(mListItems == null) {
            mListItems = new LinkedList<Doc>();
        }

        listView = (ListView) findViewById(R.id.qnaList);
        adapter = new DocDataAdapter(mListItems, this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        /*if(newText == null || newText.length() <= 0) return false;

        List<Doc> docList = new ArrayList<Doc>();
        if(mListItems != null && mListItems.size() > 0){
            for(Doc doc : mListItems){
                if(doc != null && doc.getDoc_title() != null
                        && doc.getDoc_title().toLowerCase().contains(newText.toLowerCase())){
                    docList.add(doc);
                }
            }
        }

        adapter = new DocDataAdapter(docList, FileActivity.this);
        listView.setAdapter(adapter);*/
        setFileList(newText);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION_REPLICATION_CHANGE));

        if(mListItems == null) {
            mListItems = new LinkedList<Doc>();
        }

        mListItems = new LinkedList<Doc>();
        try {
            List<Doc> docs = getDocList();
            if (docs != null && docs.size() > 0) {
                mListItems.addAll(docs);
            }
        } catch(CouchbaseLiteException exception){
            Log.e(TAG, "onResume()", exception);
        } catch(IOException exception){
            Log.e(TAG, "onResume()", exception);
        } catch(Exception exception){
            Log.e(TAG, "onResume()", exception);
        }

        listView = (ListView) findViewById(R.id.qnaList);
        adapter = new DocDataAdapter(mListItems, FileActivity.this);
        listView.setAdapter(adapter);
    }

    private void setFileList(String filterText){
        if(mListItems == null){
            mListItems = new LinkedList<Doc>();
        }

        final List<Doc> _documentList = new ArrayList<Doc>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < mListItems.size(); i++){
                String report_title = mListItems.get(i).getDoc_title();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_actionbar, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    private List<Doc> getDocList() throws CouchbaseLiteException, IOException {

        List<Doc> docList = new ArrayList<Doc>();
        List<String> ids = getAllDocumentIds();
        if(ids != null && ids.size() > 0) {
            for (String id : ids) {
                com.couchbase.lite.Document document = DatabaseUtil.getDocumentFromDocumentId(DatabaseUtil.getDatabaseInstance(FileActivity.this,
                        Constants.YOUTH_CONNECT_DATABASE), id);
                Doc doc = DocUtil.getDocFromDocument(document);
                int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_TYPE, 0);
                if(user_type_id == 1) {
                    // for admin show all pending questions which are not answer
                    if(doc != null) {
                        docList.add(doc);
                    }
                } else if(user_type_id == 2){
                    // for nodal officers show all pending questions which are not answered
                    // and asked by logged in user only
                    int curently_logged_in_user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);
                    if(doc != null) {
                        int currently_logged_in_user_id =
                                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0)
                                        .getInt(Constants.SP_USER_ID, 0);
                        if(doc.getCreated_by_user_id() == curently_logged_in_user_id){
                            docList.add(doc);
                        } else{
                            int count = 0;
                            List<AssignedToUSer> assignedToUSers = doc.getDoc_assigned_to_user_ids();
                            if(assignedToUSers != null){
                                for(AssignedToUSer assignedToUSer : assignedToUSers){
                                    if(assignedToUSer.getUser_id() == curently_logged_in_user_id){
                                        count++;
                                    }
                                }
                                if(count > 0){
                                    docList.add(doc);
                                }
                            }
                        }
                    }
                }
            }
        }
        return docList;
    }

    private List<String> getAllDocumentIds(){

        List<String> docIds = new ArrayList<String>();
        try {
            Database database = DatabaseUtil.getDatabaseInstance(FileActivity.this, Constants.YOUTH_CONNECT_DATABASE);
            Query query = database.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.BY_SEQUENCE);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                docIds.add(row.getDocumentId());
            }
        } catch(CouchbaseLiteException exception){
            Log.e(TAG, "Error", exception);
        } catch (IOException exception){
            com.couchbase.lite.util.Log.e(TAG, "onDeleteClick()", exception);
        }

        return docIds;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}
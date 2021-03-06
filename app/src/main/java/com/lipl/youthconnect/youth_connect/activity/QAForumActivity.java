package com.lipl.youthconnect.youth_connect.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.adapter.QADataAdapter;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.pojo.User;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Application;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.PasswordValidator;
import com.lipl.youthconnect.youth_connect.util.QAUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.squareup.leakcanary.RefWatcher;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class QAForumActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;

    /**
     * SearchView
     * */
    private SearchView search;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private LinkedList<QuestionAndAnswer> mListItems;
    private QADataAdapter adapter;
    private ListView listView;
    private static final String TAG = "ForumFragment";
    private int last_id = 0;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Forum");

        int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QAForumActivity.this, AskQuestionActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUp();
    }

    private void setUp(){

        listView = (ListView) findViewById(R.id.qnaList);
        search = (SearchView) findViewById(R.id.searchView1);
        search.setQueryHint("SearchView");

        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setQuestionAnswerList(newText);

                return false;
            }
        });

        final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            List<QuestionAndAnswer> questionAndAnswerList = new ArrayList<QuestionAndAnswer>();
            //ActivityIndicator activityIndicator = new ActivityIndicator(getActivity());
            //ProgressDialog progressDialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /*if (isCancelled() == false && isVisible() == true
                        && getActivity() != null && activityIndicator != null) {
                    activityIndicator.show();
                }*/
                //progressDialog = ProgressDialog.show(QAForumActivity.this, "Title", "Message");
                if(isFinishing() == false) {
                    ProgressBar pBar = (ProgressBar) findViewById(R.id.pBar);
                    pBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    questionAndAnswerList = getQAList();
                } catch (CouchbaseLiteException exception) {
                    Log.e(TAG, "onViewCreated()", exception);
                } catch (IOException exception) {
                    Log.e(TAG, "onViewCreated()", exception);
                } catch (Exception exception) {
                    Log.e(TAG, "onViewCreated()", exception);
                } catch (OutOfMemoryError outOfMemoryError) {
                    Log.e(TAG, "onViewCreated()", outOfMemoryError);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                /*if (isCancelled() == false && isVisible() == true
                        && getActivity() != null && activityIndicator != null) {
                    activityIndicator.dismiss();
                }*/
//                if(progressDialog != null) {
//                    progressDialog.dismiss();
//                }
                if(isFinishing() == false) {
                    ProgressBar pBar = (ProgressBar) findViewById(R.id.pBar);
                    pBar.setVisibility(View.GONE);
                }
                try {
                    if (questionAndAnswerList != null && questionAndAnswerList.size() > 0) {
                        if (mListItems == null) {
                            mListItems = new LinkedList<QuestionAndAnswer>();
                        }
                        mListItems.clear();
                        mListItems.addAll(questionAndAnswerList);
                        setNoRecordsTextView(mListItems);
                        adapter = new QADataAdapter(mListItems, QAForumActivity.this, true, false);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "onViewCreated()", exception);
                }
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                asyncTask.execute();
            }
        }, 200);
    }

    private void setNoRecordsTextView(List<QuestionAndAnswer> listWhichIsSetToList){
        TextView tvNoRecordFoundText = (TextView) findViewById(R.id.tvNoRecordFoundText);
        if(listWhichIsSetToList != null && listWhichIsSetToList.size() > 0) {
            tvNoRecordFoundText.setVisibility(View.GONE);
        } else {
            tvNoRecordFoundText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){

            default:
                break;
        }
    }

    public static Toolbar getToolbar(){
        return mToolbar;
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

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION_REPLICATION_CHANGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setUp();
        }
    };


    private List<QuestionAndAnswer> getQAList() throws CouchbaseLiteException, IOException {

        List<QuestionAndAnswer> questionAndAnswerArrayList = new ArrayList<QuestionAndAnswer>();
        List<String> ids = getAllDocumentIds();
        if(ids != null && ids.size() > 0) {
            for (String id : ids) {
                Document document = DatabaseUtil.getDocumentFromDocumentId(DatabaseUtil.getDatabaseInstance(QAForumActivity.this,
                        Constants.YOUTH_CONNECT_DATABASE), id);
                QuestionAndAnswer questionAndAnswer = QAUtil.getQAFromDocument(document);
                if(questionAndAnswer != null
                        && questionAndAnswer.getIs_published() == 1) {
                    questionAndAnswerArrayList.add(questionAndAnswer);
                }
            }
        }

        return questionAndAnswerArrayList;
    }

    private List<String> getAllDocumentIds(){

        List<String> docIds = new ArrayList<String>();
        try {
            Database database = DatabaseUtil.getDatabaseInstance(QAForumActivity.this, Constants.YOUTH_CONNECT_DATABASE);
            Query query = database.createAllDocumentsQuery();
            query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                docIds.add(row.getDocumentId());
            }
        } catch(CouchbaseLiteException exception){
            android.util.Log.e(TAG, "Error", exception);
        } catch (IOException exception){
            com.couchbase.lite.util.Log.e(TAG, "onDeleteClick()", exception);
        }

        return docIds;
    }


    private void setQuestionAnswerList(String filterText){

        if(QAForumActivity.this.mListItems == null){
            QAForumActivity.this.mListItems = new LinkedList<QuestionAndAnswer>();
        }

        final List<QuestionAndAnswer> _questionAndAnswerList = new ArrayList<QuestionAndAnswer>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < mListItems.size(); i++){
                String question = mListItems.get(i).getQuestion().getQa_title();
                if(question != null &&
                        filterText != null && question.length() > 0
                        && filterText.length() > 0 &&
                        question.toLowerCase().contains(filterText.toLowerCase())){
                    _questionAndAnswerList.add(mListItems.get(i));
                }
            }
        } else{
            _questionAndAnswerList.addAll(mListItems);
        }

        // create an Object for Adapter
        adapter = new QADataAdapter(_questionAndAnswerList, QAForumActivity.this, true, false);

        // set the adapter object to the Recyclerview
        listView.setAdapter(adapter);
        //  mAdapter.notifyDataSetChanged();

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
//        RefWatcher refWatcher = Application.getRefWatcher(QAForumActivity.this);
//        refWatcher.watch(this);
    }
}
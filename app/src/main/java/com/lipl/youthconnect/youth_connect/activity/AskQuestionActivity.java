package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.adapter.QASyncArrayAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luminousinfoways on 18/12/15.
 */
public class AskQuestionActivity extends ActionBarActivity implements View.OnClickListener,
        Replication.ChangeListener {

    public static String TAG = "QASync";

    //constants
    public static final String DATABASE_NAME = Constants.YOUTH_CONNECT_DATABASE;
    public static final String designDocName = "youthconnect-local";
    public static final String byDateViewName = "byQATitle";

    // By default, use the sync gateway running on the Couchbase demo server.
    // Warning: this will have "random data" entered by other users.
    // If you want to limit this to your own data, please install and run your own
    // Sync Gateway and point it to that URL instead.
    public static final String SYNC_URL = DatabaseUtil.syncURL;
    //couch internals
    protected static Manager manager;
    private Database database;
    private LiveQuery liveQuery;

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getIntent().getExtras() != null){
            questionAndAnswer = getIntent().getExtras().getParcelable(Constants.QUESTION_DETAILS);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Ask a question");

        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);
        Button btnAsk = (Button) findViewById(R.id.btnAsk);
        btnAsk.setOnClickListener(this);
        YouthConnectSingleTone.getInstance().IS_FROM_QUESTION_ASK_WITH_SUCCESS = 0;
    }

    protected void onDestroy() {
        if(manager != null) {
            manager.close();
        }
        super.onDestroy();
    }

    protected void startCBLite() throws Exception {

        Manager.enableLogging(TAG, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, com.couchbase.lite.util.Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, com.couchbase.lite.util.Log.VERBOSE);

        manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);

        //install a view definition needed by the application
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);
        database = manager.openDatabase(DATABASE_NAME, options);
        com.couchbase.lite.View viewItemsByDate = database.getView(String.format("%s/%s", designDocName, byDateViewName));
        viewItemsByDate.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object createdAt = document.get("created_at");
                if (createdAt != null) {
                    emitter.emit(createdAt.toString(), null);
                }
            }
        }, "1.0");

        startSync();

    }

    private void startSync() {

        URL syncUrl;
        try {
            syncUrl = new URL(SYNC_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Replication pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);

        Replication pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(this);
        pushReplication.addChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(questionAndAnswer != null){
            MaterialEditText qusTitle = (MaterialEditText) findViewById(R.id.qusTitle);
            qusTitle.setText(questionAndAnswer.getQuestion().getQa_title());

            MaterialEditText qusDesc = (MaterialEditText) findViewById(R.id.qusDesc);
            qusDesc.setText(questionAndAnswer.getQuestion().getQa_description());
            isEdit = true;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        MaterialEditText qusTitle = (MaterialEditText) findViewById(R.id.qusTitle);
        final String title = qusTitle.getText().toString().trim();
        MaterialEditText qusDesc = (MaterialEditText) findViewById(R.id.qusDesc);
        final String description = qusDesc.getText().toString().trim();

        switch (id){
            case R.id.btnAsk:

                if(title == null || title.length() <= 0){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Enter title for question.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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
                    return;
                }

                if(description == null || description.length() <= 0){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Enter description for question.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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
                    return;
                }

                if(isEdit){
                    if(questionAndAnswer == null
                            || questionAndAnswer.getQuestion() == null
                            ||  questionAndAnswer.getQuestion().getQa_title() == null
                            || questionAndAnswer.getQuestion().getQa_description() == null){

                        Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent),
                                "Edit your question title and description before post.",
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
                        return;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Post Question");
                builder.setMessage("Are you sure want to post this question?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int is_answered = 0;
                            int is_published = 0;
                            if(questionAndAnswer != null
                                    && questionAndAnswer.getQuestion() != null
                                    && questionAndAnswer.getQuestion().getIs_publish() == 1){
                                is_published = 1;
                            }
                            if(questionAndAnswer != null
                                    &&questionAndAnswer.getQuestion() != null
                                    && questionAndAnswer.getQuestion().getIs_answer() == 1){
                                is_answered = 1;
                            }
                            String user_name = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_NAME, null);
                            int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
                            createDocument(database,
                                    description, title, user_name, is_answered, is_published, user_id);
                            AlertDialog.Builder builder12 = new AlertDialog.Builder(AskQuestionActivity.this, R.style.AppCompatAlertDialogStyle);
                            builder12.setTitle("Post Question");
                            builder12.setMessage("Done.");
                            builder12.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder12.show();
                        } catch(Exception exception){
                            Log.e(TAG, "on Add Click", exception);
                        }
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();

                break;
            case R.id.btnReset:

                if(isEdit){
                    if(questionAndAnswer != null){
                        qusTitle.setText(questionAndAnswer.getQuestion().getQa_title());
                        qusDesc.setText(questionAndAnswer.getQuestion().getQa_description());
                    }
                } else {
                    qusTitle.setText("");
                    qusDesc.setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {

    }

    private String createDocument(Database database, String description,
                                  String title, String user_name,
                                  int is_answered, int is_published, int asked_by_user_id) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        String currentTimestamp = System.currentTimeMillis()+"";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DatabaseUtil.QA_TITLE, title);
        map.put(DatabaseUtil.QA_DESC, description);
        map.put(DatabaseUtil.QA_UPDATED_TIMESTAMP, currentTimestamp);
        map.put(DatabaseUtil.QA_ASKED_BY_USER_NAME, user_name);
        map.put(DatabaseUtil.QA_ASKED_BY_USER_ID, asked_by_user_id);
        map.put(DatabaseUtil.QA_IS_ANSWERED, is_answered);
        map.put(DatabaseUtil.QA_IS_PUBLISHED, is_published);
        map.put(DatabaseUtil.QA_IS_UPLOADED, 0);
        map.put(DatabaseUtil.QA_ANSWER, new ArrayList<Answer>());
        map.put(DatabaseUtil.QA_COMMENT, new ArrayList<Comment>());
        try {
            // Save the properties to the document
            document.putProperties(map);
            Log.i(TAG, "Document created.");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting", e);
        }
        return documentId;
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
}
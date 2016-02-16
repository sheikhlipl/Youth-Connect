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
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by luminousinfoways on 18/12/15.
 */
public class AskQuestionActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = "AskQuestionActivity";

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
        String title = qusTitle.getText().toString().trim();
        MaterialEditText qusDesc = (MaterialEditText) findViewById(R.id.qusDesc);
        String description = qusDesc.getText().toString().trim();

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

                String qa_id = "";
                if(isEdit && questionAndAnswer != null){
                    qa_id = questionAndAnswer.getQid()+"";
                }

                String qa_title = title;
                String qa_description = description;

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
                    createDocument(DatabaseUtil.getDatabaseInstance(this, Constants.YOUTH_CONNECT_DATABASE),
                            description, title, user_name, is_answered, is_published);
                } catch(CouchbaseLiteException exception){
                    Log.e(TAG, "on Add Click", exception);
                } catch(IOException exception){
                    Log.e(TAG, "on Add Click", exception);
                } catch(Exception exception){
                    Log.e(TAG, "on Add Click", exception);
                }

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

    private String createDocument(Database database, String description,
                                  String title, String user_name,
                                  int is_answered, int is_published) {
        // Create a new document and add data
        Document document = database.createDocument();
        String documentId = document.getId();
        String currentTimestamp = System.currentTimeMillis()+"";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(DatabaseUtil.QA_TITLE, title);
        map.put(DatabaseUtil.QA_DESC, description);
        map.put(DatabaseUtil.QA_UPDATED_TIMESTAMP, currentTimestamp);
        map.put(DatabaseUtil.QA_ASKED_BY_USER_NAME, user_name);
        map.put(DatabaseUtil.QA_IS_ANSWERED, is_answered);
        map.put(DatabaseUtil.QA_IS_PUBLISHED, is_published);
        map.put(DatabaseUtil.QA_ANSWER, null);
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

    /**
     * To Show Material Alert Dialog
     *
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra(Constants.QUESTION_RESULT, 1);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        builder.show();
    }
}
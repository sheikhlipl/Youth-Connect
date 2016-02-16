package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by luminousinfoways on 18/12/15.
 */
public class CommentListActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private boolean isFromForum = false;
    private boolean isFromAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qus_n_ans_comment_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(getIntent().getExtras() != null){
            questionAndAnswer = getIntent().getExtras().getParcelable(Constants.QUESTION_DETAILS);
            isFromAnswer = getIntent().getExtras().getBoolean(Constants.INTENT_KEY_IS_FROM_ANSWERED);
            isFromForum = getIntent().getExtras().getBoolean(Constants.INTENT_KEY_IS_FROM_FORUM);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Comments");
    }

    private List<Comment> getPublishedCommentList(){
        List<Comment> publishedCommentList = new ArrayList<Comment>();
        if(questionAndAnswer != null){
            List<Comment> cList = questionAndAnswer.getCommentList();
            if(cList != null) {
                for (Comment comment : cList) {
                    if(comment != null && comment.getIs_published() != null
                            && comment.getIs_published().trim().length() > 0
                            && comment.getIs_published().equalsIgnoreCase("Y")){
                        publishedCommentList.add(comment);
                    }
                }
            }
        }

        return publishedCommentList;
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void setCommentsToList(){

        LinearLayout layoutCommentList = (LinearLayout) findViewById(R.id.layoutCommentLsit);
        List<Comment> commentList = new ArrayList<Comment>();
        if(isFromForum){
            commentList = getPublishedCommentList();
        } else {
            commentList = questionAndAnswer.getCommentList();
        }

        Collections.reverse(commentList);

        if(commentList != null &&  commentList.size() > 0) {
            for (int i = 0; i < commentList.size(); i++) {
                if (commentList != null && commentList.size() > 0) {
                    RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.comment_list_item, null);
                    TextView tvCommentDesc = (TextView) itemLayout.findViewById(R.id.tvCommentDesc);
                    tvCommentDesc.setText(commentList.get(i).getComment_description().trim());

                    TextView tvAnswerBy = (TextView) itemLayout.findViewById(R.id.tvAnswerBy);
                    tvAnswerBy.setText(commentList.get(i).getUser_name());

                    String dateTime = commentList.get(i).getComment_date();
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

                                    TextView tvAnswerDateTime = (TextView) itemLayout.findViewById(R.id.tvAnswerDateTime);
                                    String _date_time = day + "-" + month + "-" + year + " " + time;
                                    tvAnswerDateTime.setText(_date_time);
                                }
                            } else {
                                TextView tvAnswerDateTime = (TextView) itemLayout.findViewById(R.id.tvAnswerDateTime);
                                tvAnswerDateTime.setText(dateTime);
                            }
                        }
                    } catch(Exception e){
                        Log.e("DataAdapter", "onBindViewHolder()", e);
                    }

                    /*ImageView imgPendingIcon = (ImageView) itemLayout.findViewById(R.id.imgPendingIcon);
                    if(commentList.get(i) != null
                            && commentList.get(i).getIs_uploaded() == 0) {
                        imgPendingIcon.setImageResource(R.drawable.ic_watch_later);
                    } else {
                        imgPendingIcon.setImageResource(R.drawable.ic_watch_later);
                    }*/

                    layoutCommentList.addView(itemLayout);
                }
            }
        }
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
package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.DistrictListAdapter;
import com.lipl.youthconnect.youth_connect.pojo.District;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.util.ArrayList;
import java.util.List;

public class FileUploadDistrictActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;
    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private DistrictListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private List<District> districts;

    private SearchView search;
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_district);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Select District");

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
                setDistictList(newText);
                return false;
            }
        });

        tvEmptyView = (TextView) findViewById(R.id.tvNoRecordFoundText);
        mRecyclerView = (RecyclerView) findViewById(R.id.distList);
        if(districts == null) {
            districts = new ArrayList<District>();
        }
        handler = new Handler();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(FileUploadDistrictActivity.this);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        // create an Object for Adapter
        mAdapter = new DistrictListAdapter(districts, mRecyclerView);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();

        if (districts.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void setDistictList(String filterText){

        final List<District> districtList = new ArrayList<District>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < districts.size(); i++){
                String m_district = districts.get(i).getM_district();
                if(m_district.contains(filterText)){
                    districtList.add(districts.get(i));
                }
            }
        } else{
            districtList.addAll(districts);
        }

        // create an Object for Adapter
        mAdapter = new DistrictListAdapter(districtList, mRecyclerView);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();


        if (districtList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
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
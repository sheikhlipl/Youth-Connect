package com.lipl.youthconnect.youth_connect.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.demo.QNADataAdapter;
import com.lipl.youthconnect.youth_connect.demo.QQAPendingActivity;
import com.lipl.youthconnect.youth_connect.fragment.AnsweredFragment;
import com.lipl.youthconnect.youth_connect.fragment.PendingFragment;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.fragment.DashboardFragment;
import com.lipl.youthconnect.youth_connect.fragment.ForumFragment;
import com.lipl.youthconnect.youth_connect.fragment.HomeFragment;
import com.lipl.youthconnect.youth_connect.fragment.NotificationFragment;
import com.lipl.youthconnect.youth_connect.fragment.QNAFragment;
import com.lipl.youthconnect.youth_connect.fragment.ReportFragment;
import com.lipl.youthconnect.youth_connect.fragment.ShowcaseFragment;

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

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends ActionBarActivity implements
        HomeFragment.OnFragmentInteractionListener,
        ShowcaseFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        ReportFragment.OnFragmentInteractionListener,
        QNAFragment.OnFragmentInteractionListener,
        ForumFragment.OnFragmentInteractionListener,
        PendingFragment.OnFragmentInteractionListener,
        AnsweredFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener,
        Replication.ChangeListener{

    private DrawerLayout mDrawerLayout = null;
    //private FrameLayout frameLayout;
    private NavigationView navigationView;
    private static Toolbar mToolbar = null;
    private static final int LOGOUT = 10;
    public static ProgressDialog progressDialog = null;
    private static final String TAG = "MainActivity";

    static RelativeLayout notifCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationDrawer);

        progressDialog = new ProgressDialog(this);

        if( getIntent().getBooleanExtra("Exit me", false)){

            YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_HOME_DASHBOARD;
            try {
                ShortcutBadger.setBadge(MainActivity.this, 0);
            } catch (Exception e) {
                Log.e("MainActivity", "Error", e);
            }

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
            nMgr.cancelAll();

            getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_NOTIFICATION_COUNT, 0).commit();
            getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putString(Constants.SP_USER_TYPE, null).commit();
            getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_LOGIN_STATUS, 0).commit();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        //String userFullName = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_NAME, "");
        //TextView drawerHeaderText = (TextView) findViewById(R.id.tvDrawerHeaderText);
        //drawerHeaderText.setText(userFullName);

        int is_from_notification_panel = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_FROM_NOTIFICATION_PANEL, 0);
        if(is_from_notification_panel == 1){
            YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_NOTIFICATION;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            NotificationFragment fragment = NotificationFragment.newInstance("Demo", "Demo");
            ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.attach(fragment);
            /*ft.add(fragment, Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.show(fragment);*/
            ft.commitAllowingStateLoss();
        }

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    DatabaseUtil.startReplications(MainActivity.this, MainActivity.this, TAG);
                } catch(CouchbaseLiteException exception){
                    Log.e(TAG, "onCreate()", exception);
                } catch(IOException exception){
                    Log.e(TAG, "onCreate()", exception);
                } catch(Exception exception){
                    Log.e(TAG, "onCreate()", exception);
                }
                return null;
            }
        }.execute();

    }

    public void setVisibilityOfFloatingIcon(boolean visibility){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(visibility){
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, AskQuestionActivity.class));
                }
            });
        } else{
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFragmentInteraction() {

    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            int currentSectionInMain = YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_MAIN_ACTIVITY;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (currentSectionInMain){
                case Constants.SECTION_HOME:
                    HomeFragment fragment = HomeFragment.newInstance("Demo", "Demo");
                    ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_HOME_DASHBOARD_PAGE);
                    ft.attach(fragment);
                    ft.addToBackStack(Constants.FRAGMENT_HOME_DASHBOARD_PAGE);
                    ft.commitAllowingStateLoss();
                    break;
                case Constants.SECTION_QA:
                    QNAFragment fragment2 = QNAFragment.newInstance("Demo", "Demo");
                    ft.replace(R.id.frameLayout, fragment2, Constants.FRAGMENT_QA_PAGE_FORUM);
                    ft.attach(fragment2);
                    ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_FORUM);
                    ft.commitAllowingStateLoss();
                    break;
                case Constants.SECTION_NOTIFICATION:
                    NotificationFragment fragment1 = NotificationFragment.newInstance("Demo", "Demo");
                    ft.replace(R.id.frameLayout, fragment1, Constants.FRAGMENT_NOTIFICATION_PAGE);
                    ft.attach(fragment1);
                    ft.addToBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE);
                    ft.commitAllowingStateLoss();
                    break;
                default:
                    HomeFragment fragment12 = HomeFragment.newInstance("Demo", "Demo");
                    ft.replace(R.id.frameLayout, fragment12, Constants.FRAGMENT_HOME_DASHBOARD_PAGE);
                    ft.attach(fragment12);
                    ft.addToBackStack(Constants.FRAGMENT_HOME_DASHBOARD_PAGE);
                    ft.commitAllowingStateLoss();
                    break;
            }
            refreshView();
        } catch(Exception e){
            Log.e("MainActivity", "onResume()", e);
        }
    }

    @Override
    public void onFragmentInteraction(String str) {

    }

    public void refreshView(){

        navigationView.getMenu().getItem(0).setChecked(true);
        int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
        if(user_type_id == 1){
            navigationView.getMenu().getItem(0).setVisible(false);
        } else{
            navigationView.getMenu().getItem(0).setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                Menu menu = navigationView.getMenu();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (menuItem.getItemId()) {

                    case R.id.action_feedback:
                        navigationView.getMenu().getItem(0).setChecked(true);
                        Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.action_file:
                        navigationView.getMenu().getItem(1).setChecked(true);
                        Intent intent1 = new Intent(MainActivity.this, FileActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.action_settings:
                        navigationView.getMenu().getItem(0).setChecked(true);
                        Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_logout:
                        showAlertDialog("Are you sure want to logout?", "Logout", "Yes", "No", LOGOUT);
                        break;

                    case R.id.action_forum:
                        navigationView.getMenu().getItem(0).setChecked(true);
                        Intent intent3 = new Intent(MainActivity.this, QAForumActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_pending:
                        navigationView.getMenu().getItem(1).setChecked(true);
                        Intent intent4 = new Intent(MainActivity.this, QAPendingActivity.class);
                        startActivity(intent4);
                        break;

                    case R.id.action_answered:
                        navigationView.getMenu().getItem(2).setChecked(true);
                        Intent intent5 = new Intent(MainActivity.this, QAAnsweredActivity.class);
                        startActivity(intent5);
                        break;

                    default:
                        navigationView.getMenu().getItem(0).setChecked(true);
                        break;
                }
                return true;
            }
        });

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
                if (code == LOGOUT) {

                    if (Util.getNetworkConnectivityStatus(MainActivity.this)) {

                        //unregister device
                        unregisterForPushNotificationsAsync unregisterForPushNotificationsAsync = new unregisterForPushNotificationsAsync();
                        unregisterForPushNotificationsAsync.execute();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Logout");
                        builder.setMessage("Sorry, no internet available.\nPlease reset your internet connection.");
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
        /*getMenuInflater().inflate(R.menu.menu_home_actionbar, menu);

        MenuItem item = menu.findItem(R.id.action_notification);
        MenuItemCompat.setActionView(item, R.layout.feed_update_count);
        notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        TextView tvCount = (TextView) notifCount.findViewById(R.id.hotlist_hot);

        tvCount.setVisibility(View.VISIBLE);
        tvCount.setText("");

        notifCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_NOTIFICATION;
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                NotificationFragment fragment = NotificationFragment.newInstance("Demo", "Demo");
                ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_NOTIFICATION_PAGE);
                ft.attach(fragment);
                ft.addToBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE);
                ft.commitAllowingStateLoss();
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    public void setNotifCount(int count){
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_feedback){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_file){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_logout){
            finish();
        } else if(id == R.id.action_home){
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_HOME = Constants.FRAGMENT_HOME_SUB_FRAGMENT_DASHBOARD;
            YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_HOME_DASHBOARD;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            HomeFragment fragment = HomeFragment.newInstance("Demo", "Demo");
            ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_HOME_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_HOME_PAGE);
            ft.attach(fragment);
            /*ft.add(fragment, Constants.FRAGMENT_HOME_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_HOME_PAGE);
            ft.show(fragment);*/
            ft.commitAllowingStateLoss();
           // Toast.makeText(MainActivity.this, "Home Screen", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.action_help){
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_FORUM;
            YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_QA;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            QNAFragment fragment = QNAFragment.newInstance("Demo", "Demo");
            ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_QA_PAGE_FORUM);
            ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_FORUM);
            ft.attach(fragment);
            /*ft.add(fragment, Constants.FRAGMENT_QA_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_QA_PAGE);
            ft.show(fragment);*/
            ft.commitAllowingStateLoss();
            //Toast.makeText(MainActivity.this, "Help Screen", Toast.LENGTH_SHORT).show();
        } else if(id == R.id.action_notification) {
            YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_NOTIFICATION;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            NotificationFragment fragment = NotificationFragment.newInstance("Demo", "Demo");
            ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.attach(fragment);
            /*ft.add(fragment, Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.addToBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE);
            ft.show(fragment);*/
            ft.commitAllowingStateLoss();
            //Toast.makeText(MainActivity.this, "Notification Screen", Toast.LENGTH_SHORT).show();
//        } else if(id == R.id.action_chat){
//            Toast.makeText(MainActivity.this, "Chat Screen", Toast.LENGTH_SHORT).show();
//        }
        }  else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_pending){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_answered){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if(id == R.id.action_forum){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private class unregisterForPushNotificationsAsync extends AsyncTask<Void, Void, Boolean>
    {
        private static final String TAG = "unregisterForPushy";
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(MainActivity.this);
            }
            activityIndicator.show();
        }

        protected Boolean doInBackground(Void... params)
        {
            try
            {
                // Acquire a unique registration ID for this device
                //String registrationId = Pushy.register(context.getApplicationContext());

                //Log.i("RegisterID", registrationId);

                // Send the registration ID to your backend server and store it for later
                boolean status = sendRegistrationIdToBackendServer("");
                return status;
            } catch(NullPointerException exception){

            }  catch( Exception exc ) {
                // Return exc to onPostExecute
                return false;
            }

            // We're good
            return null;
        }

        @Override
        protected void onPostExecute(Boolean exc)
        {

            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(MainActivity.this);
            }
            activityIndicator.dismiss();
            // Failed?
            if (exc) {
                // Show error as toast message

                // Show error as toast message
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) getSystemService(ns);
                nMgr.cancelAll();

                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_NOTIFICATION_COUNT, 0).commit();
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putString(Constants.SP_USER_TYPE, null).commit();
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_LOGIN_STATUS, 0).commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();


                return;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Logout");
                builder.setMessage("Please try again to get logout.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            // Succeeded, do something to alert the user
        }

        // Example implementation
        boolean sendRegistrationIdToBackendServer(String registrationId) throws Exception
        {
//        // The URL to the function in your backend API that stores registration IDs
//        URL sendRegIdRequest = new URL("https://{YOUR_API_HOSTNAME}/register/device?registration_id=" + registrationId);
//
//        // Send the registration ID by executing the GET request
//        sendRegIdRequest.openConnection();
//        Log.i("PushyNotification", "Registration ID "+registrationId);

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
            int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);

            if(api_key == null){
                return false;
            }

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_URL_GET_DEVICE_ID;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", api_key);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data[User][device_id]", registrationId)
                        .appendQueryParameter("data[Qa][user_id]", user_id+"")
                        .appendQueryParameter("response", "mobile");

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }
                if(in == null){
                    return false;
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

            if(response != null && response.length() > 0 && response.charAt(0) == '{'){
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                    String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                    if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
                        return false;
                    }
                }
            }

            /**
             * {
             {
             "message": "successfully inserted"
             }
             * */

            if(response != null && response.length() > 0){

                JSONObject res = new JSONObject(response);
                int status = res.optInt("status");
                String message = res.optString("message");
                if(status == 1){
                    return true;
                }
            }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }
            return false;
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {
        Replication replication = event.getSource();
        com.couchbase.lite.util.Log.i(TAG, "Replication : " + replication + "changed.");
        if(!replication.isRunning()){
            String msg = String.format("Replicator %s not running", replication);
            com.couchbase.lite.util.Log.i(TAG, msg);
        } else{
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            com.couchbase.lite.util.Log.i(TAG, msg);
        }

        if(event.getError() != null){
            showError("Sync error", event.getError());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent_answered = new Intent(Constants.BROADCAST_ACTION_REPLICATION_CHANGE);
                    sendBroadcast(intent_answered);
                } catch (Exception exception) {
                    com.couchbase.lite.util.Log.e(TAG, "changed()", exception);
                }
            }
        });
    }

    public void showError(final String errorMessage, final Throwable throwable){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = String.format("%s: %s", errorMessage, throwable);
                com.couchbase.lite.util.Log.e(TAG, msg, throwable);
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
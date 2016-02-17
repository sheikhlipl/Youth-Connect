package com.lipl.youthconnect.youth_connect.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.adapter.AdapterNotification;
import com.lipl.youthconnect.youth_connect.pojo.Notification;
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
import java.util.Collections;
import java.util.List;

import butterknife.OnLongClick;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interfaces
 * to handle interaction events.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView list;
    public List<Notification> notificationList;
    //public ProgressBar progressBarHome;
    public AdapterNotification adapter;

    public boolean doRefresh = false;
    public SwipeRefreshLayout mSwipeRefreshLayout= null;

    public boolean isRunning = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.frag_notification, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setColorSchemeResources(R.color.blue,
                    R.color.blue,
                    R.color.blue,
                    R.color.blue);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //On Refresh
                    refreshContent();
                }
            });
        }

        //mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        list = (ListView) view.findViewById(R.id.listViewPost);

        //progressBarHome = (ProgressBar) view.findViewById(R.id.progressBarHome);
        notificationList = new ArrayList<Notification>();

        if(Util.getNetworkConnectivityStatus(getActivity())) {
            NotificationListAsync asyncTask = new NotificationListAsync(getActivity());
            asyncTask.execute();
        } else {
            int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);


        }

        if(getActivity() != null &&
                getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setVisibilityOfFloatingIcon(false);
        }

        FloatingActionButton fabClear = (FloatingActionButton) view.findViewById(R.id.fabClear);
        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notificationList == null){
                    notificationList = new ArrayList<Notification>();
                }
                notificationList.clear();
                // Create custom adapter for listview
                adapter = new AdapterNotification(getActivity(), notificationList);
                //Set adapter to listview
                list.setAdapter(adapter);

                YouthConnectSingleTone.getInstance().notificationCount = 0;
                try {
                    ShortcutBadger.setBadge(getActivity(), 0);
                } catch (Exception e) {
                    Log.e("MainActivity", "Error", e);
                }

                YouthConnectSingleTone.getInstance().notificationCount = 0;
                if(getActivity() != null
                        && getActivity() instanceof MainActivity) {
                    ((MainActivity) (getActivity())).setNotifCount(0);
                }
            }
        });

        return view;
    }

    public void onAsyncTaskStart() {
        showProgress();
    }

    public void onAsyncTaskComplete() {

        if (getActivity() == null) return;
        int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);

        if(userid > 0) {

        }
        hideProgress();
    }

    private void showProgress(){
        isRunning = true;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void hideProgress(){
        isRunning= false;
        if (mSwipeRefreshLayout != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }
    }

    private void refreshContent(){
        //progressBarHome.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!Util.getNetworkConnectivityStatus(getActivity()))
                    return;

                // Do work to refresh the list here.
                if (adapter != null && !isRunning) {
                    if(Util.getNetworkConnectivityStatus(getActivity())) {
                        NotificationListAsync asyncTask = new NotificationListAsync(getActivity());
                        asyncTask.execute();
                    } else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                        builder.setTitle(getResources().getString(R.string.no_internet_connection_title));
                        builder.setMessage(getResources().getString(R.string.no_internet_connection_message));
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
    }

    @Override
    public void onResume() {
        super.onResume();

        YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_MAIN_ACTIVITY = Constants.SECTION_NOTIFICATION;
        if(doRefresh){
            if(Util.getNetworkConnectivityStatus(getActivity())){
                NotificationListAsync notificationListAsync = new NotificationListAsync(getActivity());
                notificationListAsync.execute();
            } else {
                int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);
                if (userid > 0) {

                }
                doRefresh = false;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interfaces must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }

    /**
     * Async task to get sync camp table from server
     * */
    public class NotificationListAsync extends AsyncTask<String, Void, List<Notification>> {

        private static final String TAG = "NotificationListAsync";
        private Context context = null;
        private boolean isChangePassword = false;
        //private ProgressDialog progressDialog = null;

        public NotificationListAsync(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        /*if(progressDialog == null) {
            progressDialog = ProgressDialog.show(context, "Loading", "Please wait...");
        }*/

            if(getActivity() != null && getView() != null){
                ProgressBar pBar = (ProgressBar) getView().findViewById(R.id.pBar);
                pBar.setVisibility(View.VISIBLE);
            }

            onAsyncTaskStart();
        }

        @Override
        protected List<Notification> doInBackground(String... params) {

            try {
                String api_key = context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
                int _user_id = context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);

                if(api_key == null){
                    return null;
                }

                if(_user_id <= 0){
                    return null;
                }

                InputStream in = null;
                int resCode = -1;

                int count = YouthConnectSingleTone.getInstance().notificationCount;

                String link = Constants.BASE_URL+Constants.REQUEST_NOTIFICATION_LIST;
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
                        .appendQueryParameter("user_id", _user_id + "")
                        .appendQueryParameter("notification_count", count + "")
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("is_read", "Y");
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
                    return null;
                }

                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 * "Notification": {
                 "notification_id": "82",
                 "module_name": "",
                 "module_id": null,
                 "from_user_id": "20",
                 "from_desgs_id": "2",
                 "from_user_type_id": "2",
                 "to_user_id": "2",
                 "to_desgs_id": "2",
                 "to_user_type_id": "2",
                 "to_read_user_id": null,
                 "notification_type": "Has commented on your Q&A post",
                 "notification": "insert",
                 "created": "2015-12-22 18:43:21",
                 "modified": "2015-12-22 18:43:21"
                 },
                 "User": {
                 "user_id": "20",
                 "full_name": "Test User",
                 "username": "lumi",
                 "password": "5f4dcc3b5aa765d61d8327deb882cf99",
                 "m_user_type_id": "2",
                 "m_desg_id": "2",
                 "m_state_id": "1",
                 "m_district_id": "1",
                 "m_block_id": "3",
                 "m_organization_id": "2",
                 "mobile_no": "88888888",
                 "email_id": "lumi@gmail.com",
                 "contact_no": null,
                 "is_active": "Y",
                 "api_key": "2a7314bfd62664878c88838d4b54c441",
                 "created": "2015-12-16 15:18:01",
                 "modified": "2015-12-21 08:40:19"
                 }
                 }
                 ]
                 * */

                if(response != null && response.length() > 0 && response.charAt(0) == '{'){
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                        String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                        if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
                            isChangePassword = true;
                            return null;
                        }
                    }
                }

                if(response != null && response.length() > 0){

                    List<Notification> notificationArrayList = new ArrayList<Notification>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject notificationObj = null;
                        if (jsonArray != null && jsonArray.optJSONObject(i).isNull("Notification") == false) {
                            notificationObj = jsonArray.optJSONObject(i).optJSONObject("Notification");
                        }

                        if (notificationObj == null) {
                            return null;
                        }

                        String notification_id = null;
                        if (notificationObj != null && notificationObj.isNull("notification_id") == false) {
                            notification_id = notificationObj.getString("notification_id");
                        }
                        String module_name = null;
                        if (notificationObj != null && notificationObj.isNull("module_name") == false) {
                            module_name = notificationObj.getString("module_name");
                        }
                        String module_id = null;
                        if (notificationObj != null && notificationObj.isNull("module_id") == false) {
                            module_id = notificationObj.getString("module_id");
                        }
                        String from_user_id = null;
                        if (notificationObj != null && notificationObj.isNull("from_user_id") == false) {
                            from_user_id = notificationObj.getString("from_user_id");
                        }
                        String from_desgs_id = null;
                        if (notificationObj != null && notificationObj.isNull("from_desgs_id") == false) {
                            from_desgs_id = notificationObj.getString("from_desgs_id");
                        }
                        String from_user_type_id = null;
                        if (notificationObj != null && notificationObj.isNull("from_user_type_id") == false) {
                            from_user_type_id = notificationObj.getString("from_user_type_id");
                        }
                        String to_user_id = null;
                        if (notificationObj != null && notificationObj.isNull("to_user_id") == false) {
                            to_user_id = notificationObj.getString("to_user_id");
                        }
                        String to_desgs_id = null;
                        if (notificationObj != null && notificationObj.isNull("to_desgs_id") == false) {
                            to_desgs_id = notificationObj.getString("to_desgs_id");
                        }
                        String to_user_type_id = null;
                        if (notificationObj != null && notificationObj.isNull("to_user_type_id") == false) {
                            to_user_type_id = notificationObj.getString("to_user_type_id");
                        }
                        String to_read_user_id = null;
                        if (notificationObj != null && notificationObj.isNull("to_read_user_id") == false) {
                            to_read_user_id = notificationObj.getString("to_read_user_id");
                        }
                        String notification_type = null;
                        if (notificationObj != null && notificationObj.isNull("notification_type") == false) {
                            notification_type = notificationObj.getString("notification_type");
                        }
                        String _notification = null;
                        if (notificationObj != null && notificationObj.isNull("notification") == false) {
                            _notification = notificationObj.getString("notification");
                        }
                        String created = null;
                        if (notificationObj != null && notificationObj.isNull("created") == false) {
                            created = notificationObj.getString("created");
                        }
                        String modified = null;
                        if (notificationObj != null && notificationObj.isNull("modified") == false) {
                            modified = notificationObj.getString("modified");
                        }

                        if (notification_id != null && notification_id.trim().length() > 0 && TextUtils.isDigitsOnly(notification_id)) {
                            Notification notification = new Notification(Parcel.obtain());
                            notification.setNotification_id(Integer.parseInt(notification_id));
                            notification.setModule_name(module_name);
                            notification.setModule_id(module_id);
                            notification.setFrom_user_id(from_user_id);
                            notification.setFrom_desgs_id(from_desgs_id);
                            notification.setFrom_user_type_id(from_user_type_id);
                            notification.setTo_user_id(to_user_id);
                            notification.setTo_desgs_id(to_desgs_id);
                            notification.setTo_user_type_id(to_user_type_id);
                            notification.setTo_read_user_id(to_read_user_id);
                            notification.setNotification_type(notification_type);
                            notification.setNotification(_notification);
                            notification.setCreated(created);
                            notification.setModified(modified);

                            JSONObject userObj = null;
                            if (jsonArray != null && jsonArray.optJSONObject(i).isNull("User") == false) {
                                userObj = jsonArray.optJSONObject(i).optJSONObject("User");
                            }

                            String ___user_id = null;
                            if (userObj != null && userObj.isNull("user_id") == false) {
                                ___user_id = userObj.optString("user_id");
                            }
                            String full_name = null;
                            if (userObj != null && userObj.isNull("full_name") == false) {
                                full_name = userObj.optString("full_name");
                            }
                            String username = null;
                            if (userObj != null && userObj.isNull("username") == false) {
                                username = userObj.optString("username");
                            }
                            String password = null;
                            if (userObj != null && userObj.isNull("password") == false) {
                                password = userObj.optString("password");
                            }
                            String _api_key = null;
                            if (userObj != null && userObj.isNull("api_key") == false) {
                                _api_key = userObj.optString("api_key");
                            }
                            String m_user_type_id = null;
                            if (userObj != null && userObj.isNull("m_user_type_id") == false) {
                                m_user_type_id = userObj.optString("m_user_type_id");
                            }
                            String m_desg_id = null;
                            if (userObj != null && userObj.isNull("m_desg_id") == false) {
                                m_desg_id = userObj.optString("m_desg_id");
                            }
                            String m_state_id = null;
                            if (userObj != null && userObj.isNull("m_state_id") == false) {
                                m_state_id = userObj.optString("m_state_id");
                            }
                            String m_district_id = null;
                            if (userObj != null && userObj.isNull("m_district_id") == false) {
                                m_district_id = userObj.optString("m_district_id");
                            }
                            String m_block_id = null;
                            if (userObj != null && userObj.isNull("m_block_id") == false) {
                                m_block_id = userObj.optString("m_block_id");
                            }
                            String m_organization_id = userObj.optString("m_organization_id");
                            if (userObj != null && userObj.isNull("m_organization_id") == false) {
                                m_organization_id = userObj.optString("m_organization_id");
                            }
                            String mobile_no = userObj.optString("mobile_no");
                            if (userObj != null && userObj.isNull("mobile_no") == false) {
                                mobile_no = userObj.optString("mobile_no");
                            }
                            String email_id = null;
                            if (userObj != null && userObj.isNull("email_id") == false) {
                                email_id = userObj.optString("email_id");
                            }
                            String is_active = null;
                            if (userObj != null && userObj.isNull("is_active") == false) {
                                is_active = userObj.optString("is_active");
                            }
                            String _created = null;
                            if (userObj != null && userObj.isNull("created") == false) {
                                _created = userObj.optString("created");
                            }
                            String _modified = null;
                            if (userObj != null && userObj.isNull("modified") == false) {
                                _modified = userObj.optString("modified");
                            }

                            User user = null;
                            if (___user_id != null && TextUtils.isDigitsOnly(___user_id)) {
                                user = new User(Parcel.obtain());
                                user.setUser_id(Integer.parseInt(___user_id));
                                user.setFull_name(full_name);
                                user.setApi_key(_api_key);
                                user.setUsername(username);
                                user.setPassword(password);
                                user.setM_user_type_id(m_user_type_id);
                                user.setM_desg_id(m_desg_id);
                                user.setM_state_id(m_state_id);
                                user.setM_district_id(m_district_id);
                                user.setM_block_id(m_block_id);
                                user.setM_organization_id(m_organization_id);
                                user.setMobile_no(mobile_no);
                                user.setEmail_id(email_id);
                                user.setIs_active(is_active);
                                user.setCreated(_created);
                                user.setModified(_modified);
                                notification.setUser(user);
                            }
                            notificationArrayList.add(notification);
                        }
                    }
                    return  notificationArrayList;
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<Notification> notificationList) {
            super.onPostExecute(notificationList);

            if(getActivity() == null){
                return;
            }

            if(getActivity() != null && getView() != null){
                ProgressBar pBar = (ProgressBar) getView().findViewById(R.id.pBar);
                pBar.setVisibility(View.INVISIBLE);
            }

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                builder.show();

                return;
            }

            if(notificationList != null && notificationList.size() > 0) {

                int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);

            }
            onAsyncTaskComplete();
        }
    }

    @Override
    public void onDestroyView() {

        if(getActivity() != null) {
            int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);

        }
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("gif--", "fragment back key is clicked");
                    getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_NOTIFICATION_PAGE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });
        setHasOptionsMenu(true);
    }
}
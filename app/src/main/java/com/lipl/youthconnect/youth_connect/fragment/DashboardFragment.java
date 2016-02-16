package com.lipl.youthconnect.youth_connect.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.FeedbackActivity;
import com.lipl.youthconnect.youth_connect.activity.FileActivity;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DummyContent;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.adapter.PagerAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Dashboard;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements
        OnChartValueSelectedListener, AbsListView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CAMP_CODE = "campcode";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamCampCode;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private PagerAdapter mAdapter;

    protected String[] mXValuesQA = new String[] {
            "Pending", "Answered", "Published"
    };

    protected String[] mXValuesFeedback = new String[] {
            "Pending", "Submitted"
    };

    private SwipeRefreshLayout swipeRefreshLayout;

    protected HorizontalBarChart mChartQA;
    protected HorizontalBarChart mChartFeedback;
    private Typeface tf;
    private Dashboard dashboard;
    private int mUserTypeId = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAMP_CODE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamCampCode = getArguments().getString(ARG_CAMP_CODE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(R.array.movie_serial_bg);
        swipeRefreshLayout.setOnRefreshListener(this);

        RelativeLayout layoutQusAnswered = (RelativeLayout) view.findViewById(R.id.layoutQusAnswered);
        layoutQusAnswered.setOnClickListener(this);
        RelativeLayout layoutPendingQus = (RelativeLayout) view.findViewById(R.id.layoutPendingQus);
        layoutPendingQus.setOnClickListener(this);
        RelativeLayout layoutPendingFeedback = (RelativeLayout) view.findViewById(R.id.layoutPendingFeedback);
        layoutPendingFeedback.setOnClickListener(this);
        RelativeLayout layoutComment = (RelativeLayout) view.findViewById(R.id.layoutComment);
        layoutComment.setOnClickListener(this);

        if(getActivity() != null &&
                getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setVisibilityOfFloatingIcon(false);
        }

        RelativeLayout docUploadLayout = (RelativeLayout) view.findViewById(R.id.layoutDoc);
        docUploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (getActivity() != null) {
                getActivity().startActivity(new Intent(getActivity(), FileActivity.class));
            }
            }
        });

        int user_type_id = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_TYPE, 0);
        this.mUserTypeId = user_type_id;

        if(mUserTypeId == 1){
            ImageView imgNodalOfficers = (ImageView) view.findViewById(R.id.imgNodalOfficers);
            imgNodalOfficers.setImageResource(R.drawable.ic_nodal_officers);
        } else if(mUserTypeId == 2) {
            ImageView imgNodalOfficers = (ImageView) view.findViewById(R.id.imgNodalOfficers);
            imgNodalOfficers.setImageResource(R.drawable.ic_nodal_officers);
        } else{
            ImageView imgNodalOfficers = (ImageView) view.findViewById(R.id.imgNodalOfficers);
            imgNodalOfficers.setImageResource(R.drawable.ic_nodal_officers);
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        if(getActivity() == null){
            return;
        }
        int user_type_id = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
        int id = view.getId();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        switch (id){
            case R.id.layoutQusAnswered:
                if(getActivity() != null && user_type_id == 2) {
                    YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_QA;
                    QNAFragment fragment = QNAFragment.newInstance("Demo", "2");
                    ft.replace(R.id.frameLayout, fragment, Constants.FRAGMENT_QA_PAGE_FORUM);
                    ft.show(fragment);
                    ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_FORUM);
                    ft.commitAllowingStateLoss();
                }
                break;
            case R.id.layoutPendingQus:
                YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_QA;
                QNAFragment fragment2 = QNAFragment.newInstance("Demo", "1");
                ft.replace(R.id.frameLayout, fragment2, Constants.FRAGMENT_QA_PAGE_PENDING);
                ft.show(fragment2);
                ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_PENDING);
                ft.commitAllowingStateLoss();
                break;
            case R.id.layoutPendingFeedback:
                if(getActivity() != null && user_type_id == 2) {
                    Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                    getActivity().startActivity(intent);
                }
                break;
            case R.id.layoutComment:
                YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_HOME = Constants.FRAGMENT_HOME_SUB_FRAGMENT_SHOWCASE;
                YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_HOME_SHOWCASE;
                HomeFragment _fragment = HomeFragment.newInstance("Demo", "Demo");
                ft.replace(R.id.frameLayout, _fragment, Constants.FRAGMENT_HOME_SHOWCASE_PAGE);
                ft.show(_fragment);
                ft.addToBackStack(Constants.FRAGMENT_HOME_SHOWCASE_PAGE);
                ft.commitAllowingStateLoss();
                break;
            default:
                //TODO
                break;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        final View _view = view;
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchData(_view, false);
                                    }
                                }
        );
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        if (getView() != null) {
            fetchData(getView(), true);
        }
    }

    private void fetchData(View view, boolean isSwipeToRefresh) {

        Dashboard _dash = null;
        if(getActivity() != null){
            int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            if(userid > 0) {

            }
        }

        if(_dash != null && isSwipeToRefresh == false){
            this.dashboard = _dash;
            setDataToView(view, this.dashboard);
        } else {
            if (Util.getNetworkConnectivityStatus(getActivity())) {
                GetDashboardAsync getDashboardAsync = new GetDashboardAsync();
                getDashboardAsync.execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                Snackbar snackbar = Snackbar.make(view.findViewById(R.id.layoutParent), "No internet connection.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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

                setDataToView(getView(), null);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String str) {
        if (mListener != null) {
            mListener.onFragmentInteraction(str);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
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
        public void onFragmentInteraction(String str);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        /*menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_upload).setVisible(false);

        SearchView sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setVisibility(View.VISIBLE);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query, donorList);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText, donorList);
                return true;
            }
        });*/
    }

    private void showLineChartQA(View view, Dashboard dashboard) {

        if (getActivity() == null || dashboard == null) {
            return;
        }

        mChartQA = (HorizontalBarChart) view.findViewById(R.id.chart1);
        mChartQA.setOnChartValueSelectedListener(this);
        mChartQA.setTouchEnabled(false);
        // mChartQA.setHighlightEnabled(false);

        mChartQA.setDrawBarShadow(false);
        mChartQA.setDrawValueAboveBar(true);
        mChartQA.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChartQA.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChartQA.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChartQA.setDrawBarShadow(true);

        // mChartQA.setDrawXLabels(false);

        mChartQA.setDrawGridBackground(false);

        // mChartQA.setDrawYLabels(false);

        tf = Typeface.createFromAsset(getActivity().getAssets(), Constants.ROBOTO_LIGHT);

        XAxis xl = mChartQA.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChartQA.getAxisLeft();
        yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
//        yl.setInverted(true);

        YAxis yr = mChartQA.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
//        yr.setInverted(true);

        setDataQA(dashboard);
        mChartQA.animateY(2500);

        // setting data
        setDataQA(dashboard);

        Legend l = mChartQA.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        // mChartQA.setDrawLegend(false);
    }

    private void setDataQA(Dashboard dashboard) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        //Pending
        xVals.add(mXValuesQA[0]);
        yVals1.add(new BarEntry((float) (dashboard.getPennding_qsn()), 0));

        //Answered
        xVals.add(mXValuesQA[1]);
        yVals1.add(new BarEntry((float) (dashboard.getQsn_answered()), 1));

        //Published
        xVals.add(mXValuesQA[2]);
        yVals1.add(new BarEntry((float) (dashboard.getQsn_publish()), 2));

        BarDataSet set1 = new BarDataSet(yVals1, "Data Set QA");

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChartQA.setData(data);
    }

    private void showLineChartFeedback(View view, Dashboard dashboard) {

        if (getActivity() == null || dashboard == null) {
            return;
        }

        mChartFeedback = (HorizontalBarChart) view.findViewById(R.id.chart2);
        mChartFeedback.setOnChartValueSelectedListener(this);
        mChartFeedback.setTouchEnabled(false);
        // mChartQA.setHighlightEnabled(false);

        mChartFeedback.setDrawBarShadow(false);

        mChartFeedback.setDrawValueAboveBar(true);

        mChartFeedback.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChartFeedback.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChartFeedback.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChartQA.setDrawBarShadow(true);

        // mChartQA.setDrawXLabels(false);

        mChartFeedback.setDrawGridBackground(false);

        // mChartQA.setDrawYLabels(false);

        tf = Typeface.createFromAsset(getActivity().getAssets(), Constants.ROBOTO_LIGHT);

        XAxis xl = mChartFeedback.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChartFeedback.getAxisLeft();
        yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
//        yl.setInverted(true);

        YAxis yr = mChartFeedback.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
//        yr.setInverted(true);

        setDataFeedback(dashboard);
        mChartFeedback.animateY(2500);

        // setting data
        setDataFeedback(dashboard);

        Legend l = mChartFeedback.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        // mChartQA.setDrawLegend(false);
    }

    private void setDataFeedback(Dashboard dashboard) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        //Pending
        xVals.add(mXValuesFeedback[0]);
        yVals1.add(new BarEntry((float) (dashboard.getPending_feedback()), 0));

        //Answered
        xVals.add(mXValuesFeedback[1]);
        yVals1.add(new BarEntry((float) (dashboard.getSubmitted_feedback()), 1));

        BarDataSet set1 = new BarDataSet(yVals1, "Data set Feedback");

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChartFeedback.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
    /**
     * Async task to get sync camp table from server
     */
    private class GetDashboardAsync extends AsyncTask<Void, Void, Dashboard> {

        private static final String TAG = "GetFileListAsyncTask";
        private boolean isChangePassword = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Dashboard doInBackground(Void... params) {

            if (getActivity() == null) {
                return null;
            }

            int user_id = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            int user_type_id = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
            if (user_id <= 0 || user_type_id <= 0) {
                return null;
            }

            try {
                if (getActivity() == null) {
                    return null;
                }
                String api_key = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

                if (api_key == null) {
                    return null;
                }

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL + Constants.REQUEST_DASHBOARD;
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
                        .appendQueryParameter("user_type_id", user_type_id + "")
                        .appendQueryParameter("user_id", user_id + "");
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
                if (in == null) {
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "", data = "";

                while ((data = reader.readLine()) != null) {
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 *
                 * {
                 "pennding_qsn": 11,
                 "qsn_answered": 7,
                 "pending_feedback": 9,
                 "showcased_event": 7
                 }

                 {

                 "pennding_qsn": 10,
                 "qsn_answered": 9,
                 "qsn_publish": 8,
                 "pending_feedback": 8,
                 "received_feedback": 10,
                 "showcased_event": 9

                 }

                 *
                 * */

            /*
            * {
                     "Apikey": "Api key does not exit"
              }
            * **/

                if(response != null && response.length() > 0){
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                        String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                        if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
                            isChangePassword = true;
                            return null;
                        }
                    }
                }

                if (response != null && response.length() > 0) {
                    JSONObject jsonObject = new JSONObject(response);

                    int pennding_qsn = 0;
                    if (jsonObject != null && jsonObject.isNull("pennding_qsn") == false) {
                        pennding_qsn = jsonObject.optInt("pennding_qsn");
                    }

                    int qsn_answered = 0;
                    if (jsonObject != null && jsonObject.isNull("qsn_answered") == false) {
                        qsn_answered = jsonObject.optInt("qsn_answered");
                    }

                    int qsn_publish = 0;
                    if (jsonObject != null && jsonObject.isNull("qsn_publish") == false) {
                        qsn_publish = jsonObject.optInt("qsn_publish");
                    }

                    int pending_feedback = 0;
                    if (jsonObject != null && jsonObject.isNull("pending_feedback") == false) {
                        pending_feedback = jsonObject.optInt("pending_feedback");
                    }

                    int received_feedback = 0;
                    if (jsonObject != null && jsonObject.isNull("received_feedback") == false) {
                        received_feedback = jsonObject.optInt("received_feedback");
                    }

                    int showcased_event = 0;
                    if (jsonObject != null && jsonObject.isNull("showcased_event") == false) {
                        showcased_event = jsonObject.optInt("showcased_event");
                    }

                    int count_user = 0;
                    if (jsonObject != null && jsonObject.isNull("count_user") == false) {
                        count_user = jsonObject.optInt("count_user");
                    }

                    Dashboard dashboard = new Dashboard(Parcel.obtain());
                    dashboard.setPennding_qsn(pennding_qsn);
                    dashboard.setQsn_answered(qsn_answered);
                    dashboard.setPending_feedback(pending_feedback);
                    dashboard.setShowcased_event(showcased_event);
                    dashboard.setSubmitted_feedback(received_feedback);
                    dashboard.setQsn_publish(qsn_publish);
                    dashboard.setCount_user(count_user);

                    return dashboard;
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch (MalformedURLException exception) {
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch (IOException exception) {
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch (Exception exception) {
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Dashboard dashboard) {
            super.onPostExecute(dashboard);

            if (getActivity() == null) {
                return;
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

            /*if(progressDialog != null){
                progressDialog.dismiss();
            }*/
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);

            if (dashboard != null) {

            } else {
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.layoutParent),
                        "Some problem occurred.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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
            }
        }
    }

    private void setDataToView(View view, Dashboard _dashboard){
        if (view == null ) {
            return;
        }

        if(_dashboard != null) {
            this.dashboard = _dashboard;
            showLineChartQA(view, _dashboard);
            showLineChartFeedback(view, _dashboard);
        } else {
            if(getActivity() != null){
                int userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            }
        }

        if(this.dashboard != null) {
            TextView tvNodalOfficers = (TextView) view.findViewById(R.id.tvNodalOfficers);
            if(mUserTypeId == 1){
                ImageView imgNodalOfficers = (ImageView) view.findViewById(R.id.imgNodalOfficers);
                imgNodalOfficers.setImageResource(R.drawable.ic_nodal_officers);

                TextView tvFirstTitle = (TextView) view.findViewById(R.id.tvFirstTitle);
                tvFirstTitle.setText("Nodal Officer(s)");
                tvNodalOfficers.setText(this.dashboard.getCount_user() + "");
            } else if(mUserTypeId == 2) {
                ImageView imgNodalOfficers = (ImageView) view.findViewById(R.id.imgNodalOfficers);
                imgNodalOfficers.setImageResource(R.drawable.ic_list_white);

                TextView tvFirstTitle = (TextView) view.findViewById(R.id.tvFirstTitle);
                tvFirstTitle.setText("Question(s) Answered");
                tvNodalOfficers.setText(this.dashboard.getQsn_answered() + "");
            } else {
                ImageView imgNodalOfficers = (ImageView) view.findViewById(R.id.imgNodalOfficers);
                imgNodalOfficers.setImageResource(R.drawable.ic_nodal_officers);

                TextView tvFirstTitle = (TextView) view.findViewById(R.id.tvFirstTitle);
                tvFirstTitle.setText("Nodal Officer(s)");
                tvNodalOfficers.setText(this.dashboard.getCount_user() + "");
            }

            TextView tvPendingQus = (TextView) view.findViewById(R.id.tvPendingQus);
            TextView tvFeedbackReceived = (TextView) view.findViewById(R.id.tvFeedbackReceived);
            TextView tvComments = (TextView) view.findViewById(R.id.tvComments);

            tvPendingQus.setText(this.dashboard.getPennding_qsn() + "");
            tvFeedbackReceived.setText(this.dashboard.getPending_feedback() + "");
            tvComments.setText(this.dashboard.getShowcased_event() + "");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() == null){
            return;
        }

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("gif--", "fragment back key is clicked");
                    //int lastFragmentCount = getActivity().getSupportFragmentManager().getBackStackEntryCount() - 1;
                    //if (lastFragmentCount != 0) {
                      //  getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_HOME_PAGE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    //} else {
                        getActivity().finish();
                    //}
                    return true;
                }
                return false;
            }
        });
        setHasOptionsMenu(true);
    }

    public void setUserVisibleHint(boolean visible)
    {
        super.setUserVisibleHint(visible);
        if (visible && isResumed())
        {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!getUserVisibleHint() || getView() == null)
        {
            return;
        }
        YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_HOME = Constants.FRAGMENT_HOME_SUB_FRAGMENT_DASHBOARD;
    }
}
package com.lipl.youthconnect.youth_connect.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.PullAndLoadListView;
import com.lipl.youthconnect.youth_connect.util.PullToRefreshListView;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.adapter.QADataAdapter;
import com.lipl.youthconnect.youth_connect.adapter.ShowcaseDataAdapterExp;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.DocumentAssign;
import com.lipl.youthconnect.youth_connect.pojo.DocumentMaster;
import com.lipl.youthconnect.youth_connect.pojo.DocumentUpload;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

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
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowcaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowcaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CAMP_CODE = "campcode";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamCampCode;
    private String mParam2;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private LinkedList<Document> mListItems;
    private PullAndLoadListView listView;
    private ShowcaseDataAdapterExp adapter;
    private static final String TAG = "ShowcaseFragment";
    private int doc_last_id = 0;
    private ProgressBar pBar;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowcaseFragment newInstance(String param1, String param2) {
        ShowcaseFragment fragment = new ShowcaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAMP_CODE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ShowcaseFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_showcase, container, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPage(view);
    }

    private void setPage(final View view){

        if(view == null || getActivity() == null){
            return;
        }

        mListItems = new LinkedList<Document>();
        listView = (PullAndLoadListView) view.findViewById(R.id.showcaseEventRecycleList);
        adapter = new ShowcaseDataAdapterExp(mListItems, getActivity());

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if(isVisible() && getActivity() != null) {
                    if (pBar == null) {
                        pBar = (ProgressBar) view.findViewById(R.id.pBar);
                    }
                    pBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                fetchDataFromDatabaseForPendingQAList();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(isVisible() && getActivity() != null) {
                    if (pBar == null) {
                        pBar = (ProgressBar) view.findViewById(R.id.pBar);
                    }
                    pBar.setVisibility(View.GONE);
                }

                if(isVisible() && getActivity() != null){
                    adapter = new ShowcaseDataAdapterExp(mListItems, getActivity());
                    listView.setAdapter(adapter);

                    if ((mListItems == null || mListItems.size() <= 0)
                            && (Util.getNetworkConnectivityStatus(getActivity()))) {
                        doc_last_id = 0;
                        new PullToRefreshDataTask().execute();
                    }

                    // Set a listener to be invoked when the list should be refreshed.
                    listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

                        public void onRefresh() {
                            // Do work to refresh the list here.
                            if(Util.getNetworkConnectivityStatus(getActivity())) {
                                doc_last_id = 0;
                                new PullToRefreshDataTask().execute();
                            }
                        }
                    });

                    // set a listener to be invoked when the list reaches the end
                    listView.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

                        public void onLoadMore() {
                            // Do the work to load more items at the end of list
                            // here
                            if (Util.getNetworkConnectivityStatus(getActivity())) {
                                new LoadMoreDataTask().execute();
                            }
                        }
                    });
                }
            }
        }.execute();
    }

    private void fetchDataFromDatabaseForPendingQAList(){

        if(getActivity() == null){
            return;
        }

        if(ShowcaseFragment.this.mListItems == null) {
            ShowcaseFragment.this.mListItems = new LinkedList<Document>();
        }
        int user_id = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
        if (user_id <= 0) {
            return;
        }

        int userid = 0;
        if(getActivity() != null) {
            userid = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
        }
    }

    private class LoadMoreDataTask extends AsyncTask<Void, Void, List<Document>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isVisible() && getActivity() != null && getView() != null) {
                if (pBar == null) {
                    pBar = (ProgressBar) getView().findViewById(R.id.pBar);
                }
                pBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Document> doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }

            // Simulates a background task
            try {
                List<Document> qusList = doNetworkCall(doc_last_id +"");
                return qusList;
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Document> result) {

            if(isVisible() && getActivity() != null && getView() != null) {
                if (pBar == null) {
                    pBar = (ProgressBar) getView().findViewById(R.id.pBar);
                }
                pBar.setVisibility(View.GONE);
            }

            if(result != null && result.size() > 0) {
                List<Document> documents = new ArrayList<Document>();
                for (int i = 0; i < result.size(); i++) {
                    if(result.get(i) != null
                            && result.get(i).getDocumentMaster() != null
                            && result.get(i).getDocumentMaster().getIs_published() != null
                            && result.get(i).getDocumentMaster().getIs_published().equalsIgnoreCase("Y")){
                        //PendingFragment.this.questionAndAnswerList.add(questionAndAnswerList.get(i));
                        documents.add(result.get(i));
                    }
                }
                mListItems.addAll(documents);
                adapter.notifyDataSetChanged();
            }
            listView.onLoadMoreComplete();

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            listView.onLoadMoreComplete();
        }
    }

    private class PullToRefreshDataTask extends AsyncTask<Void, Void, List<Document>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isVisible() && getActivity() != null && getView() != null) {
                if (pBar == null) {
                    pBar = (ProgressBar) getView().findViewById(R.id.pBar);
                }
                pBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Document> doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }

            // Simulates a background task
            try {
                List<Document> qusList = doNetworkCall(doc_last_id +"");
                return qusList;
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Document> result) {

            if(isVisible() && getActivity() != null && getView() != null) {
                if (pBar == null) {
                    pBar = (ProgressBar) getView().findViewById(R.id.pBar);
                }
                pBar.setVisibility(View.GONE);
            }

            if(result != null && result.size() > 0) {
                List<Document> documents = new ArrayList<Document>();
                for (int i = 0; i < result.size(); i++) {
                    if(result.get(i) != null
                            && result.get(i).getDocumentMaster() != null
                            && result.get(i).getDocumentMaster().getIs_published() != null
                            && result.get(i).getDocumentMaster().getIs_published().equalsIgnoreCase("Y")){
                        //PendingFragment.this.questionAndAnswerList.add(questionAndAnswerList.get(i));
                        documents.add(result.get(i));
                    }
                }

                if(documents.size() > 0){
                    mListItems.addAll(documents);
                    adapter.notifyDataSetChanged();
                    listView.onRefreshComplete();
                }

                if(documents.size() < 7){
                    if(Util.getNetworkConnectivityStatus(getActivity())) {
                        new PullToRefreshDataTask().execute();
                    }
                }
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            // Notify the loading more operation has finished
            listView.onLoadMoreComplete();
        }
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
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
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
                    getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_HOME_SHOWCASE, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        if (visible && isResumed()){
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

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(Constants.FRAGMENT_HOME_SHOWCASE_PAGE);
        YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_HOME_SHOWCASE;
        YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_HOME = Constants.FRAGMENT_HOME_SUB_FRAGMENT_SHOWCASE;
        //setPage(getView());
    }


    private List<Document> doNetworkCall(String _last_id) {
        if(getActivity() == null){
            return null;
        }

        List<Document> documentArrayList = new ArrayList<Document>();
        String last_id = _last_id;
        if(last_id == null || last_id.trim().length() <= 0){
            last_id = "0";
        }
        String m_user_type_id = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0)+"";
        int userId = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
        if(userId == 0){
            return null;
        }

        try {
            String api_key = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

            if(api_key == null){
                return null;
            }

            InputStream in = null;
            int resCode = -1;

            String link = Constants.BASE_URL+Constants.REQUEST_DOC_LIST;
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
                    .appendQueryParameter("document_master_id", last_id);
            //.appendQueryParameter("m_user_type_id", m_user_type_id)
            //.appendQueryParameter("user_id", userId+"");
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

                /*
                *
                * [
   {
      "DocumentMaster":{
         "document_master_id":"10",
         "document_title":"Sports Event",
         "document_purpose":"Sports Creation Image",
         "user_id":"1",
         "m_user_type_id":"1",
         "m_desg_id":"1",
         "is_published":"Y",
         "is_archive":"N",
         "created":"2015-12-19 10:32:08",
         "modified":"2015-12-19 11:29:31"
      },
      "User":{
         "full_name":"Administrator"
      },
      "DocumentAssign":[
         {
            "document_assign_id":"30",
            "document_master_id":"10",
            "m_district_id":"1",
            "user_id":"2",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         }

      ],
      "DocumentUpload":[
         {
            "document_upload_id":"35",
            "document_master_id":"10",
            "upload_file":"document_4101_2138_1450517470.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"36",
            "document_master_id":"10",
            "upload_file":"document_27846_21639_1450517471.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"37",
            "document_master_id":"10",
            "upload_file":"document_10891_10088_1450517471.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"38",
            "document_master_id":"10",
            "upload_file":"document_20404_30685_1450517471.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"39",
            "document_master_id":"10",
            "upload_file":"document_10113_20518_1450517472.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"40",
            "document_master_id":"10",
            "upload_file":"document_3922_12835_1450517472.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"41",
            "document_master_id":"10",
            "upload_file":"document_25703_21012_1450517473.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         },
         {
            "document_upload_id":"42",
            "document_master_id":"10",
            "upload_file":"document_13088_13529_1450517473.jpg",
            "created":"2015-12-19 10:32:08",
            "modified":"2015-12-19 10:32:08"
         }
      ]
   }]
                * */

            if(response != null && response.length() > 0 && response.charAt(0) == '{'){
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                    String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                    if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
                        return null;
                    }
                }
            }

            if(response != null && response.length() > 0){

                if(response.trim().equalsIgnoreCase("{\"message\":\"No details Found.\"}")){
                    return null;
                }



                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++){

                    Document document = new Document(Parcel.obtain());

                    JSONObject documentMAsterObj = null;
                    if(jsonArray != null && jsonArray.optJSONObject(i).isNull("DocumentMaster") == false){
                        documentMAsterObj = jsonArray.optJSONObject(i).optJSONObject("DocumentMaster");
                    }

                    if(documentMAsterObj == null){
                        return null;
                    }

                    String document_master_id = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("document_master_id") == false){
                        document_master_id = documentMAsterObj.getString("document_master_id");
                    }
                    String document_title = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("document_title") == false){
                        document_title = documentMAsterObj.getString("document_title");
                    }
                    String document_purpose = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("document_purpose") == false){
                        document_purpose = documentMAsterObj.getString("document_purpose");
                    }
                    String user_id = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("user_id") == false){
                        user_id = documentMAsterObj.getString("user_id");
                    }
                    String _m_user_type_id = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("m_user_type_id") == false){
                        _m_user_type_id = documentMAsterObj.getString("m_user_type_id");
                    }
                    String m_desg_id = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("m_desg_id") == false){
                        m_desg_id = documentMAsterObj.getString("m_desg_id");
                    }
                    String is_published = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("is_published") == false){
                        is_published = documentMAsterObj.getString("is_published");
                    }
                    String is_archive = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("is_archive") == false){
                        is_archive = documentMAsterObj.getString("is_archive");
                    }
                    String created = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("created") == false){
                        created = documentMAsterObj.getString("created");
                    }
                    String modified = null;
                    if(documentMAsterObj != null && documentMAsterObj.isNull("modified") == false){
                        modified = documentMAsterObj.getString("modified");
                    }

                    if(document_master_id != null && document_master_id.trim().length() > 0 && TextUtils.isDigitsOnly(document_master_id)){
                        DocumentMaster documentMaster = new DocumentMaster(Parcel.obtain());
                        documentMaster.setDocument_master_id(Integer.parseInt(document_master_id));
                        documentMaster.setDocument_title(document_title);
                        documentMaster.setDocument_purpose(document_purpose);
                        documentMaster.setUser_id(user_id);
                        documentMaster.setM_user_type_id(m_user_type_id);
                        documentMaster.setM_desg_id(m_desg_id);
                        documentMaster.setIs_published(is_published);
                        documentMaster.setIs_archive(is_archive);
                        documentMaster.setCreated(created);
                        documentMaster.setModified(modified);
                        document.setDocumentMaster(documentMaster);
                    }

                    JSONObject userObj = null;
                    if(jsonArray != null && jsonArray.optJSONObject(i).isNull("User") == false) {
                        userObj = jsonArray.optJSONObject(i).optJSONObject("User");
                    }

                    String userFullName = null;
                    if(userObj != null && userObj.isNull("full_name") == false){
                        userFullName = userObj.optString("full_name");
                    }
                    document.setUserFullName(userFullName);

                    JSONArray documentAssign = null;
                    if(jsonArray != null && jsonArray.optJSONObject(i).isNull("DocumentAssign") == false){
                        documentAssign = jsonArray.optJSONObject(i).optJSONArray("DocumentAssign");
                    }

                    List<DocumentAssign> documentAssignList = new ArrayList<DocumentAssign>();
                    for(int j = 0 ; j < documentAssign.length(); j++){
                        String document_assign_id = null;
                        if(documentAssign != null && documentAssign.getJSONObject(j).isNull("document_assign_id") == false){
                            document_assign_id = documentAssign.getJSONObject(j).optString("document_assign_id");
                        }
                        String _document_master_id = null;
                        if(documentAssign != null && documentAssign.getJSONObject(j).isNull("document_master_id") == false){
                            _document_master_id = documentAssign.getJSONObject(j).optString("document_master_id");
                        }
                        String m_district_id = null;
                        if(documentAssign != null && documentAssign.getJSONObject(j).isNull("m_district_id") == false){
                            m_district_id = documentAssign.getJSONObject(j).optString("m_district_id");
                        }
                        String __user_id = null;
                        if(documentAssign != null && documentAssign.getJSONObject(j).isNull("user_id") == false){
                            __user_id = documentAssign.getJSONObject(j).optString("user_id");
                        }
                        String ans_created = null;
                        if(documentAssign != null && documentAssign.getJSONObject(j).isNull("created") == false){
                            ans_created = documentAssign.getJSONObject(j).optString("created");
                        }
                        String ans_modified = null;
                        if(documentAssign != null && documentAssign.getJSONObject(j).isNull("modified") == false){
                            ans_modified = documentAssign.getJSONObject(j).optString("modified");
                        }

                        if(document_assign_id != null && document_assign_id.trim().length() > 0 && TextUtils.isDigitsOnly(document_assign_id)) {
                            DocumentAssign documentAssign1 = new DocumentAssign(Parcel.obtain());
                            documentAssign1.setDocument_assign_id(Integer.parseInt(document_assign_id));
                            documentAssign1.setDocument_master_id(_document_master_id);
                            documentAssign1.setM_district_id(m_district_id);
                            documentAssign1.setUser_id(__user_id);
                            documentAssign1.setCreated(ans_created);
                            documentAssign1.setModified(ans_modified);
                            documentAssignList.add(documentAssign1);
                        }
                    }
                    document.setDocumentAssignList(documentAssignList);

                    JSONArray documentUpload = null;
                    if(jsonArray != null && jsonArray.optJSONObject(i).isNull("DocumentUpload") == false){
                        documentUpload = jsonArray.optJSONObject(i).optJSONArray("DocumentUpload");
                    }

                    List<DocumentUpload> documentUploadList = new ArrayList<DocumentUpload>();
                    for(int j = 0 ; j < documentUpload.length(); j++){
                        String document_upload_id = null;
                        if(documentUpload != null && documentUpload.getJSONObject(j).isNull("document_upload_id") == false){
                            document_upload_id = documentUpload.getJSONObject(j).optString("document_upload_id");
                        }
                        String _document_master_id = null;
                        if(documentUpload != null && documentUpload.getJSONObject(j).isNull("document_master_id") == false){
                            _document_master_id = documentUpload.getJSONObject(j).optString("document_master_id");
                        }
                        String upload_file = null;
                        if(documentUpload != null && documentUpload.getJSONObject(j).isNull("upload_file") == false){
                            upload_file = documentUpload.getJSONObject(j).optString("upload_file");
                        }
                        String _created = null;
                        if(documentUpload != null && documentUpload.getJSONObject(j).isNull("created") == false){
                            _created = documentUpload.getJSONObject(j).optString("created");
                        }
                        String _modified = null;
                        if(documentUpload != null && documentUpload.getJSONObject(j).isNull("modified") == false){
                            _modified = documentUpload.getJSONObject(j).optString("modified");
                        }

                        if(document_upload_id != null && document_upload_id.trim().length() > 0 && TextUtils.isDigitsOnly(document_upload_id)) {
                            DocumentUpload documentUpload1 = new DocumentUpload(Parcel.obtain());
                            documentUpload1.setDocument_upload_id(Integer.parseInt(document_upload_id));
                            documentUpload1.setDocument_master_id(_document_master_id);
                            documentUpload1.setUpload_file(upload_file);
                            documentUpload1.setCreated(_created);
                            documentUpload1.setModified(_modified);
                            documentUploadList.add(documentUpload1);
                        }
                    }
                    document.setDocumentUploadList(documentUploadList);
                    documentArrayList.add(document);

                    if(i == jsonArray.length() -1 && document_master_id != null && TextUtils.isDigitsOnly(document_master_id.trim())){
                        if(doc_last_id == 0){
                            mListItems.clear();
                        }
                        doc_last_id = Integer.parseInt(document_master_id.trim());
                    }
                }
                return documentArrayList;
            }
        } catch(SocketTimeoutException exception){
            Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
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

}
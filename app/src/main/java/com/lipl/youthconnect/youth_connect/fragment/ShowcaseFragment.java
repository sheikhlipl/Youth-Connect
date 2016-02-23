package com.lipl.youthconnect.youth_connect.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.AssignedToUSer;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Application;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.DocUtil;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.adapter.QADataAdapter;
import com.lipl.youthconnect.youth_connect.adapter.ShowcaseDataAdapterExp;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.squareup.leakcanary.RefWatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interfaces
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
    private LinkedList<Doc> mListItems;
    private ListView listView;
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

        mListItems = new LinkedList<Doc>();
        listView = (ListView) view.findViewById(R.id.showcaseEventRecycleList);
        setNoRecordsTextView(view, mListItems);
        adapter = new ShowcaseDataAdapterExp(mListItems, getActivity());
        listView.setAdapter(adapter);

        final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            List<Doc> docList = new ArrayList<Doc>();
//            ActivityIndicator activityIndicator = new ActivityIndicator(getActivity());
//ProgressDialog progressDialog = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(view != null) {
                    ProgressBar pBar = (ProgressBar) view.findViewById(R.id.pBar);
                    pBar.setVisibility(View.VISIBLE);
                }
//                if (isCancelled() == false && isVisible() == true
//                        && getActivity() != null && activityIndicator != null) {
//                    activityIndicator.show();
//                }
                //progressDialog = ProgressDialog.show(getActivity(), "Title", "Message");
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    docList = getDocList();
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
                super.onPostExecute(aVoid);//progressDialog.dismiss();
                /*if (isCancelled() == false && isVisible() == true
                        && getActivity() != null && activityIndicator != null) {
                    activityIndicator.dismiss();
                }*/
                if(view != null) {
                    ProgressBar pBar = (ProgressBar) view.findViewById(R.id.pBar);
                    pBar.setVisibility(View.GONE);
                }
                try {
                    if (docList != null && docList.size() > 0) {
                        if (mListItems == null) {
                            mListItems = new LinkedList<Doc>();
                        }
                        mListItems.clear();
                        mListItems.addAll(docList);
                        setNoRecordsTextView(view, mListItems);
                        adapter = new ShowcaseDataAdapterExp(mListItems, getActivity());
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
        }, 2000);
    }

    private void setNoRecordsTextView(View view, List<Doc> listWhichIsSetToList){
        TextView tvNoRecordFoundText = (TextView) view.findViewById(R.id.tvNoRecordFoundText);
        if(listWhichIsSetToList != null && listWhichIsSetToList.size() > 0) {
            tvNoRecordFoundText.setVisibility(View.GONE);
        } else {
            tvNoRecordFoundText.setVisibility(View.VISIBLE);
        }
    }

    private List<Doc> getDocList() throws CouchbaseLiteException, IOException {

        if(getActivity() == null){
            return null;
        }

        List<Doc> docList = new ArrayList<Doc>();
        List<String> ids = getAllDocumentIds();
        if(ids != null && ids.size() > 0) {
            for (String id : ids) {
                com.couchbase.lite.Document document = DatabaseUtil.getDocumentFromDocumentId(DatabaseUtil.getDatabaseInstance(getActivity(),
                        Constants.YOUTH_CONNECT_DATABASE), id);
                Doc doc = DocUtil.getDocFromDocument(document);
                if (doc != null && doc.getIs_published() == 1) {
                    docList.add(doc);
                }
            }
        }
        return docList;
    }

    private List<String> getAllDocumentIds(){

        if(getActivity() == null){
            return null;
        }

        List<String> docIds = new ArrayList<String>();
        try {
            Database database = DatabaseUtil.getDatabaseInstance(getActivity(), Constants.YOUTH_CONNECT_DATABASE);
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

    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
//        RefWatcher refWatcher = Application.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

    @Override
    public void onDestroyView() {
        System.gc();
        super.onDestroyView();
    }
}
package com.lipl.youthconnect.youth_connect.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;
import com.lipl.youthconnect.youth_connect.adapter.HelpQusAndAnsAdapter;
import com.lipl.youthconnect.youth_connect.adapter.QADataAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Question;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.QAUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnsweredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnsweredFragment extends Fragment implements
        HelpQusAndAnsAdapter.OnActivityItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CAMP_CODE = "campcode";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamCampCode;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    private static final String TAG = "AnsweredFragment";
    private int last_id = 0;
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
    public static AnsweredFragment newInstance(String param1, String param2) {
        AnsweredFragment fragment = new AnsweredFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAMP_CODE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AnsweredFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityItemClick(View v, int position) {

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
        final View view = inflater.inflate(R.layout.fragment_help, container, false);

        if(getActivity() != null &&
                getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setVisibilityOfFloatingIcon(true);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.qnaList);
        search = (SearchView) view.findViewById(R.id.searchView1);
        search.setQueryHint("SearchView");

        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new OnQueryTextListener() {

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

        try {
            if(getQAList() != null && getQAList().size() > 0) {
                if(mListItems == null){
                    mListItems = new LinkedList<QuestionAndAnswer>();
                }
                mListItems.addAll(getQAList());
                adapter = new QADataAdapter(mListItems, getActivity(),false, true);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        } catch(CouchbaseLiteException exception){
            Log.e(TAG, "onViewCreated()", exception);
        } catch(IOException exception){
            Log.e(TAG, "onViewCreated()", exception);
        } catch(Exception exception){
            Log.e(TAG, "onViewCreated()", exception);
        }

    }

    private List<QuestionAndAnswer> getQAList() throws CouchbaseLiteException, IOException {

        List<QuestionAndAnswer> questionAndAnswerArrayList = new ArrayList<QuestionAndAnswer>();
        List<String> ids = getAllDocumentIds();
        if(ids != null && ids.size() > 0) {
            for (String id : ids) {
                Document document = DatabaseUtil.getDocumentFromDocumentId(DatabaseUtil.getDatabaseInstance(getActivity(),
                        Constants.YOUTH_CONNECT_DATABASE), id);
                QuestionAndAnswer questionAndAnswer = QAUtil.getQAFromDocument(document);
                if(questionAndAnswer != null
                        && questionAndAnswer.getQuestion() != null
                        && questionAndAnswer.getQuestion().getIs_answer() == 1) {
                    questionAndAnswerArrayList.add(questionAndAnswer);
                }
            }
        }

        return questionAndAnswerArrayList;
    }



    private List<String> getAllDocumentIds(){

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


    private void setQuestionAnswerList(String filterText){

        if(AnsweredFragment.this.mListItems == null){
            AnsweredFragment.this.mListItems = new LinkedList<QuestionAndAnswer>();
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
        adapter = new QADataAdapter(_questionAndAnswerList, getActivity(), false, true);

        // set the adapter object to the Recyclerview
        listView.setAdapter(adapter);
        //  mAdapter.notifyDataSetChanged();

        adapter.notifyDataSetChanged();
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
                    getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_QA_PAGE_FORUM, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
            getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.CURRENT_FRAGMENT_OF_QA, 0).commit();
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
        //setPage(getView());
        YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED;
        YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity = Constants.FRAGMENT_QA_ANSWERED;
    }
}
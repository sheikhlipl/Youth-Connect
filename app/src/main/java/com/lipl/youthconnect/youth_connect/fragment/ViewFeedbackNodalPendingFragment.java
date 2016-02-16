package com.lipl.youthconnect.youth_connect.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.FeedbackActivity;
import com.lipl.youthconnect.youth_connect.util.DummyContent;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.adapter.FeedbackAdapter;
import com.lipl.youthconnect.youth_connect.adapter.NodalFeedbackAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Feedback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewFeedbackNodalPendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewFeedbackNodalPendingFragment extends Fragment implements AbsListView.OnItemClickListener,
    NodalFeedbackAdapter.OnActivityItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CAMP_CODE = "campcode";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamCampCode;
    private String mParam2;

    private RecyclerView mRecyclerView = null;
    private OnFragmentInteractionListener mListener;
    private LinearLayoutManager linearLayoutManager;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private FeedbackAdapter mAdapter;
    private SearchView search;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewFeedbackNodalPendingFragment newInstance(String param1, ArrayList<Feedback> feedbackList) {
        ViewFeedbackNodalPendingFragment fragment = new ViewFeedbackNodalPendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAMP_CODE, param1);
        args.putParcelableArrayList(ARG_PARAM2, feedbackList);
        fragment.setArguments(args);

        return fragment;
    }

    public ViewFeedbackNodalPendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamCampCode = getArguments().getString(ARG_CAMP_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewfeedback, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.showcaseEventRecycleList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //Tocheck if  recycler is on top
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    //Its at top ..
                    if (getActivity() != null && getActivity() instanceof FeedbackActivity
                            && ((FeedbackActivity) getActivity()).swipeRefreshLayout != null) {
                        ((FeedbackActivity) getActivity()).swipeRefreshLayout.setEnabled(true);
                    }
                } else {
                    if (getActivity() != null && getActivity() instanceof FeedbackActivity
                            && ((FeedbackActivity) getActivity()).swipeRefreshLayout != null) {
                        ((FeedbackActivity) getActivity()).swipeRefreshLayout.setEnabled(false);
                    }
                }
            }
        });
        TextView tvNoRecordFoundText = (TextView) view.findViewById(R.id.tvNoRecordFoundText);
        if (YouthConnectSingleTone.getInstance().pendingReport != null && YouthConnectSingleTone.getInstance().pendingReport.size() > 0) {
            tvNoRecordFoundText.setVisibility(View.GONE);
            mAdapter = new FeedbackAdapter(getActivity(), YouthConnectSingleTone.getInstance().pendingReport, 0);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.updateItems(false);
        } else {
            tvNoRecordFoundText.setVisibility(View.VISIBLE);
        }

        search = (SearchView) view.findViewById(R.id.searchView1);
        search = (SearchView) view.findViewById(R.id.searchView1);
        search.setQueryHint("Search Here");

        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

        return view;
    }

    private void setQuestionAnswerList(String filterText){
        if(YouthConnectSingleTone.getInstance().submitedReport == null){
            YouthConnectSingleTone.getInstance().submitedReport = new ArrayList<Feedback>();
        }

        final List<Feedback> feedbackList = new ArrayList<Feedback>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < YouthConnectSingleTone.getInstance().submitedReport.size(); i++){
                String report_title = YouthConnectSingleTone.getInstance().submitedReport.get(i).getReportTitle();
                if(report_title != null &&
                        filterText != null &&
                        filterText.length() > 0 &&
                        report_title.length() > 0 &&
                        report_title.toLowerCase().contains(filterText.toLowerCase())){
                    feedbackList.add(YouthConnectSingleTone.getInstance().submitedReport.get(i));
                }
            }
        } else{
            feedbackList.addAll(YouthConnectSingleTone.getInstance().submitedReport);
        }

        // create an Object for Adapter
        mAdapter = new FeedbackAdapter(getActivity(), feedbackList, 1);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();

        if(getView() != null) {
            if (feedbackList.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                TextView tvNoRecordFoundText = (TextView) getView().findViewById(R.id.tvNoRecordFoundText);
                tvNoRecordFoundText.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                TextView tvNoRecordFoundText = (TextView) getView().findViewById(R.id.tvNoRecordFoundText);
                tvNoRecordFoundText.setVisibility(View.GONE);
            }
        }

        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.updateItems(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() != null) {
            TextView tvNoRecordFoundText = (TextView) getView().findViewById(R.id.tvNoRecordFoundText);
            if (YouthConnectSingleTone.getInstance().pendingReport != null && YouthConnectSingleTone.getInstance().pendingReport.size() > 0) {
                tvNoRecordFoundText.setVisibility(View.GONE);
                mAdapter = new FeedbackAdapter(getActivity(), YouthConnectSingleTone.getInstance().pendingReport, 0);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.updateItems(false);
            } else {
                tvNoRecordFoundText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onActivityItemClick(View v, int position) {
        //TODO Goto Details Page of Feedback to send

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
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
package com.lipl.youthconnect.youth_connect.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DummyContent;
import com.lipl.youthconnect.youth_connect.adapter.PagerAdapter;
import com.lipl.youthconnect.youth_connect.adapter.QNARecyclerAdapter;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interfaces
 * to handle interaction events.
 * Use the {@link QNAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QNAFragment extends Fragment implements AbsListView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CAMP_CODE = "campcode";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParamCampCode;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private PagerAdapter mAdapter;
    private boolean isToRedirectToAnswered = false;
    private boolean isToRedirectToPending = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QNAFragment newInstance(String param1, String param2) {
        QNAFragment fragment = new QNAFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CAMP_CODE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public QNAFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamCampCode = getArguments().getString(ARG_CAMP_CODE);
            mParam2 = getArguments().getString(ARG_PARAM2);

            if(mParam2 != null && mParam2.trim().length() > 0){
                if(mParam2.equalsIgnoreCase("1")){
                    isToRedirectToPending = true;
                    isToRedirectToAnswered = false;
                } else if(mParam2.equalsIgnoreCase("2")){
                    isToRedirectToAnswered = true;
                    isToRedirectToPending = false;
                } else{
                    isToRedirectToAnswered = false;
                    isToRedirectToPending = false;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setPage(view);
        return view;
    }

    private void setPage(View view){
        if(view == null || getChildFragmentManager() == null){
            return;
        }
        QNARecyclerAdapter recyclerAdapter = new QNARecyclerAdapter(getChildFragmentManager(), getActivity());
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        viewPager.setAdapter(recyclerAdapter);
        tabLayout = (TabLayout) view.findViewById(R.id.tabView);
        tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_FORUM));
        tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_PENDING));
        tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_ANSWERED));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        switch (tab.getPosition()) {
                            case 0:
                                YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_FORUM;
                                break;
                            case 1:
                                YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_PENDING;
                                break;
                            case 2:
                                YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED;
                                break;
                            default:
                                break;
                        }
                    }
                });

        if(isToRedirectToAnswered == false && isToRedirectToPending == false){
            /*if(YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA == Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED){
                isToRedirectToAnswered = true;
            }
            if(YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA == Constants.FRAGMENT_QA_SUB_FRAGMENT_PENDING){
                isToRedirectToPending = true;
            }*/

            int tab = getActivity().getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.CURRENT_FRAGMENT_OF_QA, 0);
            if(tab == 1){
                isToRedirectToPending = true;
                isToRedirectToAnswered = false;
            } else if(tab == 2){
                isToRedirectToPending = false;
                isToRedirectToAnswered = true;
            } else {
                isToRedirectToPending = false;
                isToRedirectToAnswered = false;
            }
        }

        if(YouthConnectSingleTone.getInstance().IS_FROM_QUESTION_ASK_WITH_SUCCESS == 1){
            isToRedirectToPending = true;
        }

        if(isToRedirectToAnswered){
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED;
            viewPager.setCurrentItem(2);
        } else if(isToRedirectToPending){
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_PENDING;
            viewPager.setCurrentItem(1);
        } else{
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_FORUM;
            viewPager.setCurrentItem(0);
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if(YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA == Constants.FRAGMENT_QA_SUB_FRAGMENT_PENDING){
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            tabLayout.getTabAt(1).select();
                        }
                    }, 100);
        } else if(YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA == Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED){
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            tabLayout.getTabAt(2).select();
                        }
                    }, 100);
        } else {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            tabLayout.getTabAt(0).select();
                        }
                    }, 100);
        }

        viewPager.getAdapter().notifyDataSetChanged();

        if(getActivity() != null &&
                getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).setVisibilityOfFloatingIcon(false);
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
            // Notify the active callbacks interfaces (the activity, if the
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
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("gif--", "fragment back key is clicked");
                    if(YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity == Constants.FRAGMENT_QA_FORUM){
                        getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_QA_FORUM, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } else if(YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity == Constants.FRAGMENT_QA_ANSWERED){
                        getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_QA_ANSWERED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    } else if(YouthConnectSingleTone.getInstance().currentFragmentOnMainActivity == Constants.FRAGMENT_QA_PENDING){
                        getActivity().getSupportFragmentManager().popBackStack(Constants.FRAGMENT_QA_PENDING, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
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

        YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_MAIN_ACTIVITY = Constants.SECTION_QA;
        setPage(getView());
    }
}
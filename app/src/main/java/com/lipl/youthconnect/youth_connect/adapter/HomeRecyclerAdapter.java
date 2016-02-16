package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.fragment.DashboardFragment;
import com.lipl.youthconnect.youth_connect.fragment.ShowcaseFragment;

/**
 * Created by luminousinfoways on 10/09/15.
 */
public class HomeRecyclerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private FragmentManager fm;

    public HomeRecyclerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {

        FragmentTransaction ft = fm.beginTransaction();
        switch (position){
            case 0:
                DashboardFragment fragment = DashboardFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_HOME_DASHBOARD_PAGE);
                ft.commitAllowingStateLoss();
                ft.attach(fragment);
                return fragment;
            case 1:
                ShowcaseFragment fragmentShowcase = ShowcaseFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_HOME_SHOWCASE_PAGE);
                ft.commitAllowingStateLoss();
                ft.attach(fragmentShowcase);
                return fragmentShowcase;
            //case 2:
                //return ShowcaseFragment.newInstance("", "");
            default:
                DashboardFragment fragmentDashboard = DashboardFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_HOME_DASHBOARD_PAGE);
                ft.commitAllowingStateLoss();
                ft.attach(fragmentDashboard);
                return fragmentDashboard;
        }
    }

    @Override
    public int getItemPosition(Object object) {

        if (object instanceof UpdateableFragment) {
            ((UpdateableFragment) object).update();
        }

        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return Constants.TAB_TITLE_HOME_DASHBOARD;
            case 1:
                return Constants.TAB_TITLE_HOME_SHOWCASE;
            //case 2:
                //return Constants.TAB_TITLE_HOME_SHOWCASE;
            default:
                return Constants.TAB_TITLE_HOME_DASHBOARD;
        }
    }

    public interface UpdateableFragment {
        public void update();
    }
}
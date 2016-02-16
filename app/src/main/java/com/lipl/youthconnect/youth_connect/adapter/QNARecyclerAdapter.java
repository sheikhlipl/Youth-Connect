package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.lipl.youthconnect.youth_connect.fragment.AnsweredFragment;
import com.lipl.youthconnect.youth_connect.fragment.PendingFragment;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.fragment.ForumFragment;

/**
 * Created by luminousinfoways on 10/09/15.
 */
public class QNARecyclerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private FragmentManager fm;

    public QNARecyclerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        FragmentTransaction ft = fm.beginTransaction();
        switch (position){
            case 0:
                ForumFragment fragment = ForumFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_FORUM);
                ft.commitAllowingStateLoss();
                ft.attach(fragment);
                return fragment;
            case 1:
                PendingFragment fragmentPending = PendingFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_PENDING);
                ft.commitAllowingStateLoss();
                ft.attach(fragmentPending);
                return fragmentPending;
            case 2:
                AnsweredFragment answeredFragment = AnsweredFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_ANSWERED);
                ft.commitAllowingStateLoss();
                ft.attach(answeredFragment);
                return answeredFragment;
            default:
                ForumFragment fragmentDefault = ForumFragment.newInstance("", "");
                ft.addToBackStack(Constants.FRAGMENT_QA_PAGE_FORUM);
                ft.commitAllowingStateLoss();
                ft.attach(fragmentDefault);
                return fragmentDefault;
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return Constants.TAB_TITLE_FORUM;
            case 1:
                return Constants.TAB_TITLE_PENDING;
            case 2:
                return Constants.TAB_TITLE_ANSWERED;
            default:
                return Constants.TAB_TITLE_FORUM;
        }
    }

    public interface UpdateableFragment {
        public void update();
    }
}
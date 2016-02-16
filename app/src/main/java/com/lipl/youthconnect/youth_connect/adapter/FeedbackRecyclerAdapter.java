package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.fragment.SendFeedbackFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackNodalPendingFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackNodalSubmittedFragment;
import com.lipl.youthconnect.youth_connect.pojo.Feedback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 10/09/15.
 */
public class FeedbackRecyclerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private int user_type;
    private List<Feedback> feedbackList;
    private static final int USER_TYPE_ADMIN = 1;
    private static final int USER_TYPE_NODAL_OFFICER = 2;

    public FeedbackRecyclerAdapter(FragmentManager fm, Context context, int user_type, List<Feedback> feedbackList) {
        super(fm);
        this.mContext = context;
        this.user_type = user_type;
        this.feedbackList = feedbackList;
    }

    @Override
    public Fragment getItem(int position) {

        if(user_type == USER_TYPE_ADMIN){
            switch (position){
                case 0:
                    return SendFeedbackFragment.newInstance("", "");
                case 1:
                    return ViewFeedbackFragment.newInstance("", "");
                default:
                    return SendFeedbackFragment.newInstance("", "");
            }
        } else if(user_type == USER_TYPE_NODAL_OFFICER){
            switch (position){
                case 0:
                    return ViewFeedbackNodalPendingFragment.newInstance("", new ArrayList<Feedback>(feedbackList));
                case 1:
                    return ViewFeedbackNodalSubmittedFragment.newInstance("", new ArrayList<Feedback>(feedbackList));
                /*case 2:
                    return ViewFeedbackNodalExpiredFragment.newInstance("", new ArrayList<Feedback>(feedbackList));*/
                default:
                    return ViewFeedbackNodalPendingFragment.newInstance("", new ArrayList<Feedback>(feedbackList));
            }
        } else{
            switch (position){
                case 0:
                    return SendFeedbackFragment.newInstance("", "");
                case 1:
                    return ViewFeedbackFragment.newInstance("", "");
                default:
                    return SendFeedbackFragment.newInstance("", "");
            }
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
        return user_type;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(user_type == USER_TYPE_NODAL_OFFICER){
            List<Feedback> submittedList = YouthConnectSingleTone.getInstance().submitedReport;
            List<Feedback> pendingList = YouthConnectSingleTone.getInstance().pendingReport;
            int number_of_pending_in_list = 0;
            if(pendingList != null) {
                number_of_pending_in_list = pendingList.size();
            }
            int number_of_submitted_in_list = 0;
            if(submittedList != null){
                number_of_submitted_in_list = submittedList.size();
            }
            switch (position){
                case 0:
                    return Constants.TAB_TITLE_FEEDBACK_PENDING+ " (" + number_of_pending_in_list + ")";
                case 1:
                    return Constants.TAB_TITLE_FEEDBACK_SUBMITTED+ " (" + number_of_submitted_in_list + ")";
                /*case 2:
                    return Constants.TAB_TITLE_FEEDBACK_EXPIRED;*/
                default:
                    return Constants.TAB_TITLE_FEEDBACK_PENDING+ " (" + number_of_pending_in_list + ")";
            }
        } else{
            switch (position){
                case 0:
                    return Constants.TAB_TITLE_FEEDBACK_SEND_FEEDBACK;
                case 1:
                    return Constants.TAB_TITLE_FEEDBACK_VIEW_FEEDBACK;
                default:
                    return Constants.TAB_TITLE_FEEDBACK_SEND_FEEDBACK;
            }
        }
    }

    public interface UpdateableFragment {
        public void update();
    }
}
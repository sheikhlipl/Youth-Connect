package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.fragment.FileUploadFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFileFragment;

/**
 * Created by luminousinfoways on 10/09/15.
 */
public class FileRecyclerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public FileRecyclerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return FileUploadFragment.newInstance("", "");
            case 1:
                return ViewFileFragment.newInstance("", "");
            default:
                return FileUploadFragment.newInstance("", "");
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
                return Constants.TAB_TITLE_HOME_FILE_UPLOAD;
            case 1:
                return Constants.TAB_TITLE_HOME_VIEW_FILE;
            default:
                return Constants.TAB_TITLE_HOME_FILE_UPLOAD;
        }
    }

    public interface UpdateableFragment {
        public void update();
    }
}
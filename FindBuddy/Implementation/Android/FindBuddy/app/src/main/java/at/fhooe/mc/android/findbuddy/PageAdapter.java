package at.fhooe.mc.android.findbuddy;

/**
 * Created by Laurenz on 03.02.2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Laurenz on 03.02.2018.
 */

public class PageAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ParticipatedActivitiesTab tab1 = new ParticipatedActivitiesTab();
                return tab1;
            case 1:
                CreatedActivitiesTab tab2 = new CreatedActivitiesTab();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}

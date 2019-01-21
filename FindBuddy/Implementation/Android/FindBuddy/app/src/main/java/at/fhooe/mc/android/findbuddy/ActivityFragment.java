package at.fhooe.mc.android.findbuddy;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Helper.ActivityAdapter;

import at.fhooe.mc.android.findbuddy.Interfaces.CustomItemClickListener;

/**
 * David
 * Activity Liste
 */

public class ActivityFragment extends Fragment {

    public ActivityFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.activity_fragment, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Teilgenommmen"));
        tabLayout.addTab(tabLayout.newTab().setText("Erstellt"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final PageAdapter adapter = new PageAdapter(getChildFragmentManager() , tabLayout.getTabCount());

        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });


        return rootView;
    }

}

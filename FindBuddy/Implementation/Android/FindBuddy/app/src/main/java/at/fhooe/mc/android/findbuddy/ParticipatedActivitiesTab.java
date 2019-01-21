package at.fhooe.mc.android.findbuddy;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Entities.UserData;
import at.fhooe.mc.android.findbuddy.Helper.ActivityAdapter;
import at.fhooe.mc.android.findbuddy.Interfaces.CustomItemClickListener;
import at.fhooe.mc.android.findbuddy.Interfaces.OnGetDataListener;

/**
 * Created by Laurenz on 03.02.2018.
 */
public class ParticipatedActivitiesTab extends Fragment {

    String [] activityIds;
    ArrayList<MyActivity> activityList;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private FirebaseAuth mAuth;
    private UserData mUserData;

    DatabaseReference participantsActivitiesRef, activityRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.created_activities_tab, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUserData = new UserData(mAuth);


        //Aufbau Firebase    get Instance Root Table get References Entities
        participantsActivitiesRef = FirebaseDatabase.getInstance().getReference("ParticipatedActivities/" + mAuth.getInstance().getCurrentUser().getUid());
        activityRef = FirebaseDatabase.getInstance().getReference("activities");

        recyclerView = rootView.findViewById(R.id.myRecyclerView);

        //setting linear layout
        layoutManager = new LinearLayoutManager(rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


        //gets called when data in database changes
        participantsActivitiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberOfActivities = (int)dataSnapshot.getChildrenCount();
                activityIds = new String[numberOfActivities];
                activityList = new ArrayList<>();

                int counter = 0;
                // Get every Instance
                for (DataSnapshot createdActivitiesSnapshot : dataSnapshot.getChildren()) {
                    String activity = createdActivitiesSnapshot.getValue(String.class);
                    activityIds[counter] = activity;

                    Log.v("TEST HAHA",  "" + activityIds[counter]);


                    counter++;
                }

                activityList.clear();

                for(String id : activityIds){
                    readDataOnce(id, new OnGetDataListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(DataSnapshot data) {

                            MyActivity activity = data.getValue(MyActivity.class);
                            activityList.add(activity);
                            initializeAdapter();
                            Log.v("Part Activities:",  "" + activityList.size());
                        }

                        @Override
                        public void onFailed(DatabaseError databaseError) {
                            Log.w("ERROR", "loadActivity:onCancelled", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadActivity:onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * Using OnGetDataListener Interface to make sure the data is loaded.
     * Only needed if we create database helper class
     * @param child
     * @param listener
     */
    public void readDataOnce(String child, final OnGetDataListener listener){
        listener.onStart();

        //Set listener for the single activityId and retrieve the data
        Log.v("Async101", "Start loading activity data");
        activityRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("Async101", "Activity data loaded.");
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    /**
     * create the adapter and implement the custom OnClickListener
     */
    private void initializeAdapter() {
        ActivityAdapter adapter = new ActivityAdapter(activityList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent((Activity) getContext(), DetailActivity.class);

                //get the activity at the the tapped position and pass on it's id to the detail screen
                i.putExtra("ActivityID", activityList.get(position).getId());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}

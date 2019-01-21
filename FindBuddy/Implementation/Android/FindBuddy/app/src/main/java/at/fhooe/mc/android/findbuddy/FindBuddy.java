package at.fhooe.mc.android.findbuddy;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Helper.MyCalendar;
import at.fhooe.mc.android.findbuddy.Interfaces.PermissionListener;

public class FindBuddy extends AppCompatActivity implements PermissionListener {

    //reference to the firebase database
    DatabaseReference activityRef;

    //Liste von Aktivitis
    ArrayList<MyActivity> activityList;

    private boolean mLocationPermissionGranted,mFilePermissionGranted;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    //bundle to transfer data between activity and fragment
    Bundle bundle = new Bundle();

    private List<android.support.v4.app.Fragment> fragments = new ArrayList<>(3);
    private static final String TAG_FRAGMENT_FINDBUDDY = "tag_fragment_findbuddy";
    private static final String TAG_FRAGMENT_ACTIVITIES = "tag_fragment_activities";
    private static final String TAG_FRAGMENT_PROFILE = "tag_fragment_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_buddy);
        //Aufbau Firebase    get Instance Root Table get References Entities
        activityRef = FirebaseDatabase.getInstance().getReference("activities");
        activityList = new ArrayList<>();

        checkLocationPermission();

        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot activitySnapshot : dataSnapshot.getChildren()) {
                    //
                    MyActivity activity = activitySnapshot.getValue(MyActivity.class);

                    //not sure if needed
                    //MyCalendar cal = new MyCalendar();

                    activityList.add(activity);
                }
                //Argumente an Fragments
                bundle.putSerializable("ACTIVITY_LIST", activityList);
                switchFragment(0, TAG_FRAGMENT_FINDBUDDY);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        buildFragmentList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationPermissionGranted) {

            //locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationPermissionGranted) {

            //locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //gets called when data in databse changes
        activityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Clean All
                activityList.clear();
                // GEt every Instance
                for (DataSnapshot activitySnapshot : dataSnapshot.getChildren()) {
                    //
                    MyActivity activity = activitySnapshot.getValue(MyActivity.class);

                    activityList.add(activity);
                }
                //Argumente an Fragments
                bundle.putSerializable("ACTIVITY_LIST", activityList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     }
    //Tab Bar Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_findbuddy:
                    switchFragment(0, TAG_FRAGMENT_FINDBUDDY);
                    return true;
                case R.id.navigation_activities:
                    switchFragment(1, TAG_FRAGMENT_ACTIVITIES);
                    return true;
                case R.id.navigation_profile:
                    switchFragment(2, TAG_FRAGMENT_PROFILE);
                    return true;
            }
            return false;
        }
    };

    private void buildFragmentList() {
        fragments.add(new FindBuddyFragment());
        fragments.add(new ActivityFragment());
        fragments.add(new ProfileFragment());
    }
    //
    private void switchFragment(int pos, String tag){
         Fragment frag = getFragmentManager().findFragmentByTag(fragments.get(pos).getTag());
         if (pos == 0 || pos == 1) {
            if (!(frag != null && frag.isVisible())) {

                if(fragments.get(pos).getArguments() == null){
                    fragments.get(pos).setArguments(bundle);
                }
             }
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_fragmentholder, fragments.get(pos)).commit();
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    public void checkLocationPermission() {
        /*
         * Request location permission, so that we can get the location od the device.
         * The result of the permission request is handled by a callback, onRequestPermssionResult.
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
        }else{
            //if we need to show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(FindBuddy.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            }
        }



    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        mFilePermissionGranted = false;
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                break;





        }

        //capture an FindBuddyFragment instance to the updateLocationUI() method.
        FindBuddyFragment mapFragment = (FindBuddyFragment)getSupportFragmentManager().findFragmentById(R.id.frame_fragmentholder);
        mapFragment.updateLocationUI();
    }}

    @Override
    public boolean getLocationPermission() {
        return mLocationPermissionGranted;
    }
}

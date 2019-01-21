package at.fhooe.mc.android.findbuddy;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Entities.UserData;
import at.fhooe.mc.android.findbuddy.Helper.CircleTransform;
import at.fhooe.mc.android.findbuddy.Helper.Data;
import at.fhooe.mc.android.findbuddy.Helper.IconConverter;
import at.fhooe.mc.android.findbuddy.Helper.OnMapAndViewReadyListener;
import at.fhooe.mc.android.findbuddy.Interfaces.CustomItemClickListener;
import at.fhooe.mc.android.findbuddy.Interfaces.OnGetDataListener;

public class DetailActivity extends AppCompatActivity implements OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener{

    private String[] userIds,profilURI;
    private String activityId;
    private DatabaseReference activityRef, participantsRef, userRef;
    private StorageReference mStorageRef;
    private MyActivity currentActivity;
    private GoogleMap map;
    private UserData mUserData;
    private FirebaseAuth mAuth;
    private TextView name,datumStart,datumEnd,memberActual,memberMax,category,zeit,information_activity;
    private ImageView[] IMGS;

    //used to check the users relation to the activity
    private String currentUserId;
    private String creator;
    private boolean isParticipant;

    //Menu items
    private MenuItem participateItem;
    private MenuItem leaveItem;
    private MenuItem deleteItem;

    private RecyclerView horizontal_recycler_view;
    private ArrayList android_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Firebase instanzen
        //ID OF ACTIVITY
        activityId = getIntent().getStringExtra("ActivityID");
        //PARTICIPANTS FOR ACTIVITY
        participantsRef = FirebaseDatabase.getInstance().getReference("Participants");
        //REFERENCE TO ACTIVITS
        activityRef = FirebaseDatabase.getInstance().getReference("activities");
        //REFERENCE TO USER
        userRef = FirebaseDatabase.getInstance().getReference("User");
        //STORAGE REFERENCE FOR IMAGES
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUserData = new UserData(mAuth);
        currentUserId = mAuth.getInstance().getCurrentUser().getUid();


        //setup Toolbar
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        name = (TextView) findViewById(R.id.ActivityName);

        datumStart = (TextView) findViewById(R.id.AcitvityDate);
        datumEnd = (TextView) findViewById(R.id.ActivityDateEnd);
        memberActual = (TextView) findViewById(R.id.MemberActual);
        memberMax = (TextView) findViewById(R.id.MemberMax);
        category = (TextView) findViewById(R.id.categoryName);
        zeit = (TextView) findViewById(R.id.ActivityDateEnd);
        information_activity= (TextView) findViewById(R.id.information_activity);


        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        horizontal_recycler_view = findViewById(R.id.horizontal_recycler_view);
        android_version = new ArrayList();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Set listener for the single activityId and retrieve the data
        //Log.v("Async101", "Start loading activity data");
        activityRef.child(activityId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.v("Async101", "Activity data loaded.");
                currentActivity = dataSnapshot.getValue(MyActivity.class);
                setTitle("");
                name.setText(currentActivity.getName());
                datumStart.setText(  currentActivity.getStartDate().toString());
                datumEnd.setText(  currentActivity.getEndDate().toString());
                memberMax.setText(""+ currentActivity.getMaxParticipants());
                category.setText(currentActivity.getCategory());
                information_activity.setText(currentActivity.getInformations());
                creator = currentActivity.getCreator();

                initializeMap();
                //Gets the USER IDS of the Activity
                readDataOnce(activityId, new OnGetDataListener() {
                    @Override
                    public void onStart() { }

                    @Override
                    public void onSuccess(DataSnapshot data) {
                        int numberOfParticipants = (int)data.getChildrenCount();
                        Log.v("NUMBER OF USERS",""+numberOfParticipants);
                        userIds = new String[numberOfParticipants];
                        memberActual.setText(""+numberOfParticipants);
                        int counter = 0;
                        // Get every Instance
                        for (DataSnapshot userId : data.getChildren()) {
                            String user = userId.getValue(String.class);
                            userIds[counter] = user;
                            Log.v("Loading User IDS",  "User " + counter+ userIds[counter]);
                            counter++;
                        }
                         downloadImage(userIds);

                        isParticipant = checkIfUserParticipated();
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void onFailed(DatabaseError databaseError) {
                        Log.w("ERROR", "Failed to load User IDs", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadActivity:onCancelled", databaseError.toException());
            }
        });

    }

    public void downloadImage(String[]userIds) {
        int numberOfParticipants = userIds.length;
        Log.v("NUMBER OF USER IDS",""+numberOfParticipants);
        profilURI = new String[numberOfParticipants];



        for(int i =0; i<userIds.length;i++){
            StorageReference riversRef = mStorageRef.child("ProfilPictures").child(userIds[i]);
            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                @Override
                public void onSuccess(Uri uri) {

                    int k=0;
                    //Change Profil Pic
                    Log.v("LOADING URI OF USER", +k + " " +uri.toString());
                    setData(uri.toString());
                    initViews();
                    //userIds[k] = uri;
                    //Picasso.with(getApplicationContext()).load(uri).transform(new CircleTransform()).into(IMGS[k]);
                    k++;
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });}



    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);


        DataAdapter adapter = new DataAdapter(getApplicationContext(), android_version, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(getApplicationContext(), GuestProfileActivity.class);
                Log.v("Clicked", "picture" + position);
                //get the activity at the the tapped position and pass on it's id to the detail screen
                String[] revUserIds = userIds;
                Collections.reverse(Arrays.asList(revUserIds));

                i.putExtra("UserID", revUserIds[position]);
                Log.v("Clicked user id",  revUserIds[position]);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.participate:
                mUserData.setParticipatedActivities(activityId);
                mUserData.setParticipantsOfActivity(activityId);
                participateItem.setVisible(false);
                leaveItem.setVisible(true);
                return true;
            case R.id.leave:
                mUserData.leaveActivity(activityId);
                participateItem.setVisible(true);
                leaveItem.setVisible(false);
                return true;
            case R.id.delete:
                mUserData.deleteActivity(activityId);
                Intent i = new Intent(this, FindBuddy.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        participateItem = menu.findItem(R.id.participate);
        participateItem.setVisible(false);
        leaveItem = menu.findItem(R.id.leave);
        leaveItem.setVisible(false);
        deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(false);

        if (creator.equals(currentUserId)){
            Log.v("USER",  "is the creator!");
            deleteItem.setVisible(true);
        }
        if(isParticipant){
            Log.v("USER",  "is a participant!");
            leaveItem.setVisible(true);
        }else{
            Log.v("USER",  "hasnt joined yet!");
            participateItem.setVisible(true);
        }
        return true;
    }

    public void setData(String uriUser){
        Log.v("INSIDE SET DATA ",""+uriUser);

            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_version_name("");
            androidVersion.setAndroid_image_url(uriUser);
            Log.v("SETTING UP URI OF USER", "USER "+androidVersion.getAndroid_image_url());
            android_version.add(androidVersion);


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
        participantsRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
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
     * readusername
     * @param child
     * @param listener
     */
    public void readUserName(String child, final OnGetDataListener listener){
        listener.onStart();

        //Set listener for the single activityId and retrieve the data
        Log.v("Async101", "Start loading activity data");
        participantsRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
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
     * Get the map and register for callback.
     */
    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
    }

     /**
     * Called when the map is ready to set the marker and position of the map.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMarker();
        setCameraPosition();
    }

    /**
     * Moves the camera to the location of the current activity.
     */
    private void setCameraPosition() {
        //Wait until map is ready.
        if(map == null){
            return;
        }
         //Center Camera on marker.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentActivity.getLatitude(), currentActivity.getLongitude()), 16f));
    }

    /**
     * Gets location and name from the current activity and sets a marker for it on the map.
     */
    private void setMarker() {
        Log.v("Async101", "Trying to set Marker.");
        IconConverter markerIcon = new IconConverter(this);
        map.addMarker(new MarkerOptions()
            .position(new LatLng(currentActivity.getLatitude(), currentActivity.getLongitude()))
            .title(currentActivity.getName())
            .icon(markerIcon.getCategoryIcon(currentActivity.getCategory())));
    }


    //Checks if the user already participated and sets the boolean isParticipant accordingly
    private boolean checkIfUserParticipated() {
        for (String user : userIds){
            if(currentUserId.equals(user)){
                return true;
            }
        }
        return false;
    }
}

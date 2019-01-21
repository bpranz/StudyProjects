package at.fhooe.mc.android.findbuddy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Locale;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Helper.IconConverter;
import at.fhooe.mc.android.findbuddy.Helper.MaterialDialog;
import at.fhooe.mc.android.findbuddy.Interfaces.PermissionListener;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by David on 19.12.17.
 * This Fragment contains the mapView and handles the user input.
 *
 */

public class FindBuddyFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener{

    private static final String TAG = FindBuddyFragment.class.getSimpleName();
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //list of the activites of the database
    ArrayList<MyActivity> activityList;

    private MapView mapView;
    private GoogleMap googleMap;
    private boolean addMode= false;
    private Marker newActivityMarker = null;
    private CameraPosition mCameraPosition;
    private IconConverter markerIcon;

    //entry points to the places API
    //private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    //entry point to the fused location provider
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //A default location (Hagenberg) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(48.3687, 14.5120);
    private static final int DEFAULT_ZOOM = 15;
    private PermissionListener permissionListener;
    private boolean mLocationPermissionGranted;

    //the geographical location where the device is currently located (the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    //Keys for storing activity states
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //buttons to add_activity_menu activity
    FloatingActionButton addActivityButton;
    FloatingActionButton cancelButton;

    //ArrayLists for each category to filter markers.
    ArrayList<Marker> markerListDefault;
    ArrayList<Marker> markerListEducation;
    ArrayList<Marker> markerListRelaxation;
    ArrayList<Marker> markerListFood;
    ArrayList<Marker> markerListMeetUp;
    ArrayList<Marker> markerListParty;
    ArrayList<Marker> markerListShopping;
    ArrayList<Marker> markerListSport;
    ArrayList<Marker> markerListEntertainment;

    //Filter dialog
    CheckBox checkBoxDefault, checkBoxEducation, checkBoxRelaxation, checkBoxFood, checkBoxMeetUp, checkBoxParty, checkBoxShopping, checkBoxSport, checkBoxEntertainment;



    public FindBuddyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View rootView = inflater.inflate(R.layout.findbuddy_fragment, container, false);

        //retrieve list of activities from FindBuddy activity
        activityList = (ArrayList<MyActivity>) getArguments().getSerializable("ACTIVITY_LIST");

        //Init marker lists.
        markerListDefault = new ArrayList<>();
        markerListEducation = new ArrayList<>();
        markerListRelaxation = new ArrayList<>();
        markerListFood = new ArrayList<>();
        markerListMeetUp = new ArrayList<>();
        markerListParty = new ArrayList<>();
        markerListShopping = new ArrayList<>();
        markerListSport = new ArrayList<>();
        markerListEntertainment = new ArrayList<>();

        //Retrieve location and camera position from saved instance state.
        if(savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);

        //Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        //Build Map.
        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        markerIcon = new IconConverter(getContext());

        //Add buttons to fragment.
        addActivityButton  = rootView.findViewById(R.id.addButton);
        cancelButton  = rootView.findViewById(R.id.cancelButton);
        addActivityButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        //setup Toolbar
        Toolbar myToolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);



        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.find_buddy_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                showSearchWidget();
                return true;
            case R.id.filter:
                filterTheMarkers();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            permissionListener = (PermissionListener) context;
        } catch (ClassCastException castException){
            Log.e("Exception: %s", castException.getMessage());
        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), DEFAULT_ZOOM));
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);

        //use a custom info window adapter to handle multiple lines of text in the info window contents.
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            //Return null, so getInfoContents() is called next.
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                //inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        //Prompt the user for permission.
        mLocationPermissionGranted = permissionListener.getLocationPermission();

        if (!mLocationPermissionGranted){
            Log.e("PERMISSION:", "permission NOT granted!");
        }

        //Turn on the my location layer and the related control on the map.
        updateLocationUI();

        //Get the current location of the device and set the position on the map.
        getDeviceLocation();
    }

    /**
     * Get the best and most recent location of the device, which may be null in rare cases when
     * a location is not available.
     */
    private void getDeviceLocation() {
        try {
            if(mLocationPermissionGranted){
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            //Set the map's current position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null){
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    public void updateLocationUI() {
        if (googleMap == null){
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                mLocationPermissionGranted = permissionListener.getLocationPermission();
            }
        } catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMapClickListener(this);

        //Set the markers for all activities.
        if (activityList != null){
            for (MyActivity activity : activityList) {
                addMarkerToMap(activity);
            }
        }

        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d("Marker", "started dragging");
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                newActivityMarker.setPosition(marker.getPosition());
                Log.d("MarkerPosition", newActivityMarker.toString());
            }
        });
    }

    /**
     * Makes sure that the user can create only one marker.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if(addMode){
            if (newActivityMarker != null) {
                newActivityMarker.remove();
                newActivityMarker = null;
            }else{
                newActivityMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Neue Aktivität").icon(markerIcon.getCategoryIcon("Default")));
                newActivityMarker.setDraggable(true);
            }
        }
    }

    /**
     * Manages the button visibility according to the addMode state.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addButton:
                if(addMode){
                    if(newActivityMarker !=null) {
                        saveLocation();
                        addMode = false;
                        cancelButton.setVisibility(View.INVISIBLE);
                    }else {
                        Toast.makeText(this.getContext(), "Standort auswählen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this.getContext(), "Standort auswählen", Toast.LENGTH_SHORT).show();
                    addMode = true;
                    cancelButton.setVisibility(View.VISIBLE);
                    addActivityButton.setImageResource(R.drawable.ic_done_white_24dp);
                }
                break;
            case R.id.cancelButton:
                addMode = false;
                cancelButton.setVisibility(View.INVISIBLE);
                if(newActivityMarker !=null){
                    newActivityMarker.remove();
                    newActivityMarker = null;
                }
                addActivityButton.setImageResource(R.drawable.ic_add_white_24dp);
                break;
        }

    }



    /**
     * Passes on the location data of the set marker to the AddActivity.
     */
    private void saveLocation() {
        Intent i = new Intent(this.getContext(), AddActivity.class);
        Log.d("SavingMarkerPosition", newActivityMarker.toString());
        i.putExtra("POSITION", newActivityMarker.getPosition());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(newActivityMarker !=null){
            newActivityMarker.remove();
            newActivityMarker = null;
        }
        startActivity(i);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent i = new Intent(this.getContext(), DetailActivity.class);
        MyActivity clickedActivity = (MyActivity) marker.getTag();
        i.putExtra("ActivityID", clickedActivity.getId());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    /**
     * Display the search widget for the autocompletion search.
     */
    public void showSearchWidget(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build((Activity) getContext());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    /**
     * Adds a marker for an activity to the map and sets the activity as tag.
     * @param activity
     */
    void addMarkerToMap(MyActivity activity){
        if(googleMap != null){
            Marker activityMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(activity.getLatitude(), activity.getLongitude()))
                    .title(activity.getName())
                    .snippet(activity.getCategory())
                    .icon(markerIcon.getCategoryIcon(activity.getCategory())));
            activityMarker.setTag(activity);
            addMarkerToCategoryList(activityMarker);
        }
    }

    /**
     * Sorts a marker to his category list.
     * @param marker
     */
    void addMarkerToCategoryList(Marker marker){
        switch (marker.getSnippet()){
            case "Bildung":
                markerListEducation.add(marker);
                break;
            case "Entspannung":
                markerListRelaxation.add(marker);
                break;
            case "Essen":
                markerListFood.add(marker);
                break;
            case "Meetup":
                markerListMeetUp.add(marker);
                break;
            case "Party":
                markerListParty.add(marker);
                break;
            case "Shopping":
                markerListShopping.add(marker);
                break;
            case "Sport":
                markerListSport.add(marker);
                break;
            case "Unterhaltung":
                markerListEntertainment.add(marker);
                break;
            default:
                markerListDefault.add(marker);
        }

    }

    /**
     * Creates a MaterialDialog and shows checkboxes to filter the marker.
     */
    void filterTheMarkers() {
        final MaterialDialog dialog = new MaterialDialog(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View checkBoxView = inflater.inflate(R.layout.filter_dialog_view, null);
        checkBoxDefault = checkBoxView.findViewById(R.id.checkBox1);
        checkBoxDefault.setChecked(true);
        checkBoxEducation = checkBoxView.findViewById(R.id.checkBox2);
        checkBoxEducation.setChecked(true);
        checkBoxRelaxation = checkBoxView.findViewById(R.id.checkBox3);
        checkBoxRelaxation.setChecked(true);
        checkBoxFood = checkBoxView.findViewById(R.id.checkBox4);
        checkBoxFood.setChecked(true);
        checkBoxMeetUp = checkBoxView.findViewById(R.id.checkBox5);
        checkBoxMeetUp.setChecked(true);
        checkBoxParty = checkBoxView.findViewById(R.id.checkBox6);
        checkBoxParty.setChecked(true);
        checkBoxShopping = checkBoxView.findViewById(R.id.checkBox7);
        checkBoxShopping.setChecked(true);
        checkBoxSport = checkBoxView.findViewById(R.id.checkBox8);
        checkBoxSport.setChecked(true);
        checkBoxEntertainment = checkBoxView.findViewById(R.id.checkBox9);
        checkBoxEntertainment.setChecked(true);

        dialog.setTitle(getString(R.string.filter_dialog_info))
                .setCustomView(checkBoxView)

                .setupPositiveButton(getString(R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displaySelectedMarkers();
                        dialog.dismiss();
                    }

                })
                .setupNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


        dialog.show();

    }

    /**
     * Sets the markers visibility according the the filter choice.
     */
    public void displaySelectedMarkers() {

        for(Marker marker : markerListEducation) {
            marker.setVisible(checkBoxEducation.isChecked());
        }

        for(Marker marker : markerListRelaxation) {
            marker.setVisible(checkBoxRelaxation.isChecked());
        }

        for(Marker marker : markerListFood) {
            marker.setVisible(checkBoxFood.isChecked());
        }

        for(Marker marker : markerListMeetUp) {
            marker.setVisible(checkBoxMeetUp.isChecked());
        }

        for(Marker marker : markerListParty) {
            marker.setVisible(checkBoxParty.isChecked());
        }

        for(Marker marker : markerListShopping) {
            marker.setVisible(checkBoxShopping.isChecked());
        }

        for(Marker marker : markerListSport) {
            marker.setVisible(checkBoxSport.isChecked());
        }

        for(Marker marker : markerListEntertainment) {
            marker.setVisible(checkBoxEntertainment.isChecked());
        }

        for(Marker marker : markerListDefault) {
            marker.setVisible(checkBoxDefault.isChecked());
        }
    }

}

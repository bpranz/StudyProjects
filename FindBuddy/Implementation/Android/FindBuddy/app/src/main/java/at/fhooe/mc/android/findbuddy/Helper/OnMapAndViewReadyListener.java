package at.fhooe.mc.android.findbuddy.Helper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;


/**
 * Created by David on 01.02.18.
 *
 * Helper class that will delay triggering the OnMapReady callback until both the GoogleMap and the
 * View having completed initialization.
 */

public class OnMapAndViewReadyListener implements OnGlobalLayoutListener, OnMapReadyCallback {

    /**
     * A listener that needs to wait for both the GoogleMap and the View to be initialized.
     */
    public interface OnGlobalLayoutAndMapReadyListener {
        void onMapReady(GoogleMap googleMap);
    }

    private final SupportMapFragment mapFragment;
    private final View mapView;
    private final OnGlobalLayoutAndMapReadyListener devCallback;

    private boolean isViewReady;
    private boolean isMapReady;
    private GoogleMap googleMap;

    public OnMapAndViewReadyListener(SupportMapFragment mapFragment, OnGlobalLayoutAndMapReadyListener devCallback){
        this.mapFragment = mapFragment;
        mapView = mapFragment.getView();
        this.devCallback = devCallback;
        isViewReady = false;
        isMapReady = false;
        googleMap = null;
        
        registerListeners();
    }

    private void registerListeners() {
        //View layout.
        if((mapView.getWidth()!= 0) && (mapView.getHeight() !=0)) {
            //View has already completed layout.
            isViewReady = true;
        }else {
            //Map has not undergone layout, register a view observer.
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        //If the GoogleMap is already ready it will still fire the callback later.
        mapFragment.getMapAsync(this);
    }

    /**
     * Gets called when Map is initialized.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // NOTE: The GoogleMap API specifies the listener is removed just prior to invocation.
        this.googleMap = googleMap;
        isMapReady = true;
        fireCallbackIfReady();
    }

    /**
     * Gets called when View is initialized.
     */
    @Override
    public void onGlobalLayout() {
        //Remove Listener
        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        isViewReady = true;
        fireCallbackIfReady();
    }

    /**
     * The onMapReady gets called when View and Map are initialized.
     */
    private void fireCallbackIfReady() {
        if(isViewReady && isMapReady){
            devCallback.onMapReady(googleMap);
        }
    }
}

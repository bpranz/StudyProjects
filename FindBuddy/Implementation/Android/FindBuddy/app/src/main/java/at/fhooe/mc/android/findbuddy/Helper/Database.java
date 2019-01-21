package at.fhooe.mc.android.findbuddy.Helper;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import at.fhooe.mc.android.findbuddy.Interfaces.OnGetDataListener;

/**
 * Created by David on 03.02.18.
 *
 * Not needed yet.
 */

public class Database {

    /**
     * Using OnGetDataListener Interface to make sure the data is loaded.
     * could be moved to its own DataBaseClass
     * @param child
     * @param listener
     */
    public void readDataOnce(String child, final OnGetDataListener listener){
        listener.onStart();

        //Set listener for the single activityId and retrieve the data
        Log.v("Async101", "Start loading activity data");
        FirebaseDatabase.getInstance().getReference().child(child).addListenerForSingleValueEvent(new ValueEventListener() {
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

}

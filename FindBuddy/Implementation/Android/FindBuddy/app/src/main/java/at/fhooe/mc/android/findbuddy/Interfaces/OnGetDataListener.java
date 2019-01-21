package at.fhooe.mc.android.findbuddy.Interfaces;

/**
 * Created by David on 03.02.18.
 */

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Interface to make sure the requested data is loaded.
 * only needed if we create a database helper class
 */
public interface OnGetDataListener {
    void onStart();
    void onSuccess(DataSnapshot data);
    void onFailed(DatabaseError databaseError);

}

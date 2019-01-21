package at.fhhgb.findbuddy.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.fhhgb.findbuddy.firebase.Entities.MyActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseReference activityRef;
    List<MyActivity> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityRef = FirebaseDatabase.getInstance().getReference("activities");
        activityList = new ArrayList<>();

        writeNewActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();

        activityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activityList.clear();

                for (DataSnapshot activitySnapshot : dataSnapshot.getChildren()) {
                    MyActivity activity = activitySnapshot.getValue(MyActivity.class);
                    MyCalendar cal = new MyCalendar();
                    activityList.add(activity);
                    Log.i("START_DATE", cal.convertDateToString(activity.getStartDate()));
                    Log.i("END_DATE", cal.convertDateToString(activity.getEndDate()));
                    Log.i("COMBINED_DATE", cal.getDateAsString(activity.getStartDate(), activity.getEndDate()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void writeNewActivity() {
        String id = "Activity1";
        String name = "Laufen gehen";
        String category = "Laufen";
        MyCalendar cal = new MyCalendar();
        Date startDate = cal.setDate(25, 3, 2018, 17, 00);
        Date endDate = cal.setDate(25, 3, 2018, 18, 00);
        int maxParticipants = 5;
        String location = "FH Hagenberg";
        double latitude = 48.368556;
        double longitude = 14.515002;

        MyActivity activity = new MyActivity(id, name, category, startDate, endDate, maxParticipants, location, latitude, longitude);

        activityRef.child(activity.getId()).setValue(activity);
    }
}

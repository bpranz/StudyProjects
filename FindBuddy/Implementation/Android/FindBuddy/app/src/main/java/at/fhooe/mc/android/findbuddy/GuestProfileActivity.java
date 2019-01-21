package at.fhooe.mc.android.findbuddy;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.Calendar;

import at.fhooe.mc.android.findbuddy.Helper.CircleTransform;

/**
 * Created by Laurenz on 06.02.2018.
 */

public class GuestProfileActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private TextView userName, userAge,profil,numberOfCreatedActivities,numberOfParticipatedActivities;
    private ImageView profilpic;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_fragment2);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        profilpic = (ImageView) findViewById(R.id.profilImageGuest);
        userName = (TextView) findViewById(R.id.userNameGuest);
        profil  = (TextView) findViewById(R.id.profilttextUserGuest);
        UserID = getIntent().getStringExtra("UserID");
        numberOfCreatedActivities = findViewById(R.id.guestcountercreate);
        numberOfParticipatedActivities = findViewById(R.id.guestcounterpart);

        getData();
        downloadImage();

    }

    @Override
    public void onStart() {

        super.onStart();
    }

    public void getData() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //ProfilAge
        final DatabaseReference profilText = database.getReference("User/" +UserID+ "/profiltext");
        //Username
        final DatabaseReference profilUserName = database.getReference("User/" + UserID + "/Name");
        //Birthday
        final DatabaseReference profilUserBirthday = database.getReference("User/" + UserID + "/Geburtstag");

        //CreatedActivities
        final DatabaseReference usercreatedact = database.getReference("CreatedActivities/" +UserID);
        //ParticipatedActivities
        final DatabaseReference userParticipatedAct = database.getReference("ParticipatedActivities/" + UserID);
        // Read from the database
        profilText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                profil.setText(value);
                Log.d("Test1", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Test", "Failed to read value.", error.toException());
            }
        });

        //Username
        profilUserName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                userName.setText(value);
                Log.d("Test1", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Test", "Failed to read value.", error.toException());
            }
        });

        //Username
        profilUserBirthday.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                String helper;
                helper = userName.getText().toString();
                userName.setText(helper+" , "+ getAge(Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(3, 5)), Integer.parseInt(value.substring(6, 10))));
                Log.d("Test1", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Test", "Failed to read value.", error.toException());
            }
        });

        //numberOfCreatedActivities
        usercreatedact.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfCreatedActivities.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //numberOfParticipatedActivities
        userParticipatedAct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfParticipatedActivities.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void downloadImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



        mStorageRef.child("ProfilPictures").child(UserID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Change Profil Pic
                Picasso.with(getApplicationContext()).load(uri).transform(new CircleTransform()).into(profilpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private String getAge(int day, int month, int year) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}

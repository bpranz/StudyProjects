package at.fhooe.mc.android.findbuddy.Entities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;


import at.fhooe.mc.android.findbuddy.LoginActivtiy;
import at.fhooe.mc.android.findbuddy.R;
import at.fhooe.mc.android.findbuddy.SignUpActivity;

/**
 * Created by Laurenz on 02.02.2018.
 */

public class UserData {

    private String userName;
    private String userBirthday;
    private String userEmail;
    private String userProfilText;
    private StorageReference userProfilPicture;
    private FirebaseAuth mAuth;
    private DatabaseReference myRefname;
    private FirebaseDatabase mdatabase;

    public UserData(FirebaseAuth _Auth) {
      this.mAuth= _Auth.getInstance();
      mdatabase = FirebaseDatabase.getInstance();

    }


    public String getUserID() {
        return mAuth.getUid();
    }

    public String getUserName() {
        return mAuth.getCurrentUser().getDisplayName();
    }

    public void setUserName(String userName) {
        myRefname = mdatabase.getReference("User/"+mAuth.getUid()+"/Name");
        myRefname.setValue(userName);
        this.userName = userName;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        myRefname = mdatabase.getReference("User/"+mAuth.getUid()+"/Geburtstag");
        myRefname.setValue(userBirthday);
        this.userBirthday = userBirthday;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        myRefname = mdatabase.getReference("User/"+mAuth.getUid()+"/Email");
        myRefname.setValue(userEmail);
        this.userEmail = userEmail;
    }

    public String getUserProfilText() {
        return userProfilText;
    }

    public void setUserProfilText(String userProfilText) {
        myRefname = mdatabase.getReference("User/" + mAuth.getInstance().getCurrentUser().getUid() + "/profiltext");
        myRefname.setValue(userProfilText);
        this.userProfilText = userProfilText;
    }

    public StorageReference getUserProfilPicture() {
        return userProfilPicture;
    }

    public void setUserProfilPicture(StorageReference userProfilPicture) {
        this.userProfilPicture = userProfilPicture;
    }

    public void setCreatorOfActivity(String activityID) {
        myRefname = mdatabase.getReference("CreatedActivities/" + mAuth.getInstance().getCurrentUser().getUid() );
        myRefname.child(activityID).setValue(activityID);

    }
    public void setParticipatedActivities(String activityID) {
        myRefname = mdatabase.getReference("ParticipatedActivities/" + mAuth.getInstance().getCurrentUser().getUid() );
        myRefname.child(activityID).setValue(activityID);

    }

    public void setParticipantsOfActivity(String activityID) {
        myRefname = mdatabase.getReference("Participants/" + activityID );
        myRefname.child(mAuth.getInstance().getCurrentUser().getUid()).setValue(mAuth.getInstance().getCurrentUser().getUid());

    }

    public void leaveActivity(String activityId){
        //remove participants entry
        myRefname = mdatabase.getReference("Participants/");
        myRefname.child(activityId).removeValue();

        //remove participatedActivities entry
        myRefname = mdatabase.getReference("ParticipatedActivities/" + mAuth.getInstance().getCurrentUser().getUid());
        myRefname.child(activityId).removeValue();
    }

    public void deleteActivity(String activityId){
        //remove participants entry
        myRefname = mdatabase.getReference("Participants/");
        myRefname.child(activityId).removeValue();

        //remove participatedActivities entry
        myRefname = mdatabase.getReference("ParticipatedActivities/" + mAuth.getInstance().getCurrentUser().getUid());
        myRefname.child(activityId).removeValue();

        //remove created activities entry
        myRefname = mdatabase.getReference("CreatedActivities/" + mAuth.getInstance().getCurrentUser().getUid());
        myRefname.child(activityId).removeValue();

        //remove activity
        myRefname = mdatabase.getReference("activities/");
        myRefname.child(activityId).removeValue();
    }

    public void getData(){
        myRefname = mdatabase.getReference("activities/");


    }
}

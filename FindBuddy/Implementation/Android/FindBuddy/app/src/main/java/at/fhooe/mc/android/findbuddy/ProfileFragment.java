package at.fhooe.mc.android.findbuddy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import at.fhooe.mc.android.findbuddy.Entities.UserData;
import at.fhooe.mc.android.findbuddy.Helper.Chat_Activity;
import at.fhooe.mc.android.findbuddy.Helper.CircleTransform;

import static android.app.Activity.RESULT_OK;

/**
 * Created by David on 19.12.17.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_REQUEST = 0;
    private TextView userName, userAge, numberOfParticipatedActivities, numberOfCreatedActivities;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private ImageView profilpic, edit;
    private EditText userprofiltext;
    private UserData mUserData;


    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment1, container, false);
        mAuth = FirebaseAuth.getInstance();
        mUserData = new UserData(mAuth);


        mStorageRef = FirebaseStorage.getInstance().getReference();

        profilpic = (ImageView) rootView.findViewById(R.id.profilImage);
        edit = (ImageView) rootView.findViewById(R.id.edit);
        userName = (TextView) rootView.findViewById(R.id.userName);
        numberOfCreatedActivities = rootView.findViewById(R.id.created_activities);
        numberOfParticipatedActivities = rootView.findViewById(R.id.participated_activities);

        userprofiltext = (EditText) rootView.findViewById(R.id.profiltext);
        edit.setOnClickListener(this);
        profilpic.setOnClickListener(this);

        getData();
        downloadImage();

        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

     /**
     * Returns the Age
     *
     * @param day
     * @param month
     * @param year
     * @return
     */
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

    public void getData() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //ProfilAge
        final DatabaseReference profilText = database.getReference("User/" + mAuth.getInstance().getCurrentUser().getUid() + "/profiltext");
        //Username
        final DatabaseReference profilUserName = database.getReference("User/" + mAuth.getInstance().getCurrentUser().getUid() + "/Name");
        //Birthday
        final DatabaseReference profilUserBirthday = database.getReference("User/" + mAuth.getInstance().getCurrentUser().getUid() + "/Geburtstag");
        //CreatedActivities
        final DatabaseReference usercreatedact = database.getReference("CreatedActivities/" + mAuth.getInstance().getCurrentUser().getUid());
        //ParticipatedActivities
        final DatabaseReference userParticipatedAct = database.getReference("ParticipatedActivities/" + mAuth.getInstance().getCurrentUser().getUid());
        // Read from the database
        profilText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                userprofiltext.setText(value);
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

        usercreatedact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Log.v("TEST HAHA",  ""+dataSnapshot.getChildrenCount());


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

    //Saves User Data
    public void saveData() {
        //ProfilText
        mUserData.setUserProfilText(userprofiltext.getText().toString());
    }

    /**
     * SignOut User
     */
    public void SignOutUser() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent intent = new Intent(getActivity(),
                LoginActivtiy.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

    }

    /**
     * Wird aufgerufen wenn ImageView aufgerufen wird um das Profilbild zu ändern
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        uploadImage(selectedImage);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        //Change Profil Pic
                        Picasso.with(getActivity()).load(selectedImage).transform(new CircleTransform()).into(profilpic);
                        // profilpic.setImageBitmap(bitmap);
                    } catch (IOException e) {

                    }
                    break;
            }
    }

    /**
     * When User has Chosen Profil Pic the Method is called and Uploads File to Firebase
     *
     * @param pathImage
     */
    public void uploadImage(Uri pathImage) {
        Uri file = pathImage;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference riversRef = mStorageRef.child("ProfilPictures").child(user.getUid());
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }

    public void downloadImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        StorageReference riversRef = mStorageRef.child("ProfilPictures").child(user.getUid());

        mStorageRef.child("ProfilPictures").child(user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Change Profil Pic
                Picasso.with(getActivity()).load(uri).transform(new CircleTransform()).into(profilpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    /**
     * OnClick Listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Change ProfilPic
            case R.id.profilImage:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                break;
            case R.id.edit:

                PopupMenu popup = new PopupMenu(getActivity(), edit);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.profil_fragment_edit, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_dropdown1:
                                saveData();
                                return true;

                            case R.id.action_dropdown2:
                                SignOutUser();
                                return true;

                        }
                        return true;
                    }
                });
                 popup.show();//showing popup menu

        }
    }
}






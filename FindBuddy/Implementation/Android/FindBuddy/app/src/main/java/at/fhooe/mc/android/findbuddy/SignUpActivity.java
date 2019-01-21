package at.fhooe.mc.android.findbuddy;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import at.fhooe.mc.android.findbuddy.Entities.UserData;
import at.fhooe.mc.android.findbuddy.Helper.MyCalendar;

/**
 * Created by Laurenz on 25.01.2018.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email, name, password;
    Button geburtstag;
    private FirebaseAuth mAuth;
    private UserData mUserData;
    final Calendar startCal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findViewById(R.id.signIn).setOnClickListener(this);
        findViewById(R.id.SignUpBackLogin).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUserData = new UserData(mAuth);

        name = (EditText) findViewById(R.id.signUpUserName);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        geburtstag = (Button) findViewById(R.id.birthdaybutton);
        geburtstag.setOnClickListener(this);

        name.setOnClickListener(this);
        email.setOnClickListener(this);
        password.setOnClickListener(this);
        geburtstag.setOnClickListener(this);


    }



    /**
     * Create User Profil on SignUp
     */
    private void registerUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (emailText.isEmpty()) {
            Log.i("ERROR : SignUpActivity", "Email Field Emtpy");
            email.setError("No Email");
            email.requestFocus();
            return;
        }

        if (passwordText.isEmpty()) {
            Log.i("ERROR : SignUpActivity", "Password Field Empty");
            password.setError("No Password");
            password.requestFocus();
            return;
        }

        //Creates new User from User Input (Email,Password)
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("User", "createUserWithEmail:success");
                             //Save User Data

                            mUserData.setUserBirthday(geburtstag.getText().toString());
                            mUserData.setUserName(name.getText().toString().trim());
                            mUserData.setUserEmail(email.getText().toString().trim());
                            //Sucess Message
                            Context context = getApplicationContext();
                            CharSequence text = "Nutzer erstellt!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            //Switch To new Activity
                            Intent intent = new Intent(SignUpActivity.this, FindBuddy.class);
                            startActivity(intent);
                            SignUpActivity.this.finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Nein", "createUserWithEmail:failure", task.getException());
                            Context context = getApplicationContext();
                            CharSequence text = "Nutzer nicht erstellt!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                    }
                });
    }

    /**
     * Pick Date on SignUp
     */
    final DatePickerDialog.OnDateSetListener startDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            startCal.set(Calendar.YEAR, year);
            startCal.set(Calendar.MONTH, monthOfYear);
            startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(geburtstag, startCal);
        }
    };

    /**
     * Set Button Text to Calender Date
     *
     * @param button
     * @param calendar
     */
    private void updateDate(Button button, Calendar calendar) {
        MyCalendar cal = new MyCalendar();
        button.setText(cal.getDateOnlyAsString(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //SignInButton
            case R.id.signIn:
                registerUser();
                //saveData();
                break;
            //Back to Login Screen
            case R.id.SignUpBackLogin:
                Intent intent = new Intent(this, LoginActivtiy.class);

                startActivity(intent);
                SignUpActivity.this.finish();
                break;
            //Opens Date Picker

            case R.id.birthdaybutton:
                new DatePickerDialog(SignUpActivity.this, startDatePicker, startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH)).show();
                break;


        }
    }
}

package at.fhooe.mc.android.findbuddy;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Laurenz on 25.01.2018.
 */

public class LoginActivtiy extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    EditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_login);
        findViewById(R.id.loginSignUp).setOnClickListener(this);
        findViewById(R.id.signIn).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        // mAuth.signOut();
        email = (EditText) findViewById(R.id.emailLogin);
        password= (EditText) findViewById(R.id.passwordLogin);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

       //Log.v("Ok",mAuth.getCurrentUser().getUid());
        //Log.v("Oke",mAuth.getCurrentUser().getEmail());
        if(mAuth.getCurrentUser()==null){
            Context context = getApplicationContext();
            CharSequence text = "Nicht regrestriert, Erstellen Sie ein Konto";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else
            updateUI(currentUser);
    }

    /**
     * Sign In the User
     */
    public void signIn(){
        String emailText =  email.getText().toString().trim();
        String passwordText =  password.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Hi", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                             Context context = getApplicationContext();
                            CharSequence text = "Something went wrong";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                         }

                        // ...
                    }
                });

    }

    private void updateUI(FirebaseUser currentUser) {

        Intent intent = new Intent(LoginActivtiy.this, FindBuddy.class);

        startActivity(intent);
        LoginActivtiy.this.finish();
    }

    @Override
    public void onClick(View v) {

    switch(v.getId()){
        case R.id.loginSignUp:
             Intent intent = new Intent(LoginActivtiy.this, SignUpActivity.class);

            startActivity(intent);
            LoginActivtiy.this.finish();
         break;
        case R.id.signIn:
            signIn();
            break;
    }
    }
}

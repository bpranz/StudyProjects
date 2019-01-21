package at.fhooe.mc.android.findbuddy.Helper;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import at.fhooe.mc.android.findbuddy.R;


/**
 * Created by Laurenz on 03.02.2018.
 */

public class Chat_Activity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton fab;
    private EditText input;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);
        fab.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Change ProfilPic
            case R.id.fab:
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
                break;
        }
    }
}
package at.fhooe.mc.android.findbuddy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Entities.UserData;
import at.fhooe.mc.android.findbuddy.Helper.MyCalendar;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button addButtonStartDate;
    Button addButtonStartTime;
    Button addButtonEndDate;
    Button addButtonEndTime;
    Spinner categorySpinner;
    final Calendar startCal = Calendar.getInstance();
    final Calendar endCal = Calendar.getInstance();
    LatLng position;
    DatabaseReference activityRef, createdRef;

    String currentUserID;

    //Connection to UserData
    private FirebaseAuth mAuth;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //Connection to UserData
        mAuth = FirebaseAuth.getInstance();
        userData = new UserData(mAuth);

        activityRef = FirebaseDatabase.getInstance().getReference("activities");
        createdRef = FirebaseDatabase.getInstance().getReference("createdActivities");

        //Get Tap Marker information
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras == null) {
                position = null;
            } else {
                position = (LatLng) extras.get("POSITION");
            }
        } else {
            position = (LatLng) savedInstanceState.get("POSITION");
        }

        Toolbar addToolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(addToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Aktivität erstellen");

        addButtonStartDate = findViewById(R.id.addButtonStartDate);
        addButtonStartTime = findViewById(R.id.addButtonStartTime);

        final DatePickerDialog.OnDateSetListener startDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCal.set(Calendar.YEAR, year);
                startCal.set(Calendar.MONTH, monthOfYear);
                startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(addButtonStartDate, startCal);
            }
        };

        addButtonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, startDatePicker, startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener startTimePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startCal.set(Calendar.MINUTE, minute);
                updateTime(addButtonStartTime, startCal);
            }
        };

        addButtonStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddActivity.this, startTimePicker, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), true).show();
            }
        });

        addButtonEndDate = findViewById(R.id.addButtonEndDate);
        addButtonEndTime = findViewById(R.id.addButtonEndTime);

        final DatePickerDialog.OnDateSetListener endDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                endCal.set(Calendar.YEAR, year);
                endCal.set(Calendar.MONTH, monthOfYear);
                endCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(addButtonEndDate, endCal);
            }
        };

        addButtonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, endDatePicker, endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener endTimePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endCal.set(Calendar.MINUTE, minute);
                updateTime(addButtonEndTime, endCal);
            }
        };

        addButtonEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddActivity.this, endTimePicker, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), true).show();
            }
        });


        String [] categoryArray = this.getResources().getStringArray(R.array.category_array);

        categorySpinner = (Spinner) findViewById(R.id.category_spinner);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_item_selected, categoryArray);
        //Specifiy layout for choices list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        currentUserID = mAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_activity_menu, menu);
        return true;
    }

    /**
     * Gets called when a menu item gets pressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addMenuDone:
                writeNewActivity();
                Intent i = new Intent(this, FindBuddy.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    //Set Button Text to Calender Date
    private void updateDate(Button button, Calendar calendar) {
        MyCalendar cal = new MyCalendar();
        button.setText(cal.getDateOnlyAsString(calendar.getTime()));
    }

    //Set Button Text to Time
    private void updateTime(Button button, Calendar calendar) {
        MyCalendar cal = new MyCalendar();
        button.setText(cal.getTimeOnlyAsString(calendar.getTime()));
    }

    //Store Aktivity to Firebase
    //Get Data from AddActivity
    private void writeNewActivity() {
        String id = activityRef.push().getKey();
        String name = ((EditText)findViewById(R.id.addEditTextName)).getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        int maxParticipants = Integer.parseInt(((EditText)findViewById(R.id.addEditTextMaxParticipants)).getText().toString());
        String information = ((EditText)findViewById(R.id.addEditTextInformation)).getText().toString();
        String creator = currentUserID;

        MyActivity activity = new MyActivity(id, name, category, startCal.getTime(), endCal.getTime(), maxParticipants, information, position.latitude, position.longitude, creator);

        userData.setCreatorOfActivity(id);

        activityRef.child(id).setValue(activity);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

package uk.ac.le.cityTourPlanner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageDurationActivity extends AppCompatActivity {

    private String mSelectedTripID;
    private Spinner mPlacesSpinner;
    private EditText mHoursEditText;
    private EditText mMinutesEditText;
    private DatabaseReference mRef;
    private String mPlaceKey;
    private HashMap<String, Object> mPlaceMap;
    private String mSelectedItemName;
    private String mSelectedPlaceKey;
    private boolean mDurationAssignedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_duration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectedTripID = getIntent().getStringExtra("trip_id");
        mRef = FirebaseDatabase.getInstance().getReference("Trips/SelectedPlaces/" + mSelectedTripID);

        mPlacesSpinner = (Spinner) findViewById(R.id.placeSpinner);
        mHoursEditText = findViewById(R.id.hoursEditText);
        mMinutesEditText = findViewById(R.id.minutesEditText);

        populateSpinner();

        mPlacesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mHoursEditText.setText("");
                mMinutesEditText.setText("");
                mDurationAssignedFlag=false;

                mSelectedItemName = parent.getItemAtPosition(position).toString();
                // Toast.makeText(getBaseContext(),"You selected "+parent.getItemAtPosition(position),Toast.LENGTH_LONG).show();

                if (mPlaceMap.containsKey(mSelectedItemName)) {
                    String placeIDVal = (String) mPlaceMap.get(mSelectedItemName);
                    mRef.child(placeIDVal).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("placeHours").exists() && dataSnapshot.child("placeMinutes").exists()) {
                                mDurationAssignedFlag=true;
                                invalidateOptionsMenu();
                                String hours = String.valueOf(dataSnapshot.child("placeHours").getValue());
                                String minutes = String.valueOf(dataSnapshot.child("placeMinutes").getValue());


                                mHoursEditText.setText(hours);
                                mMinutesEditText.setText(minutes);
                            }
                            else {
                               mDurationAssignedFlag=false;
                               invalidateOptionsMenu();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    // Toast.makeText(ManageDurationActivity.this,"Place Number : "+placeIDVal,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mHoursEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() == 0)
                    mHoursEditText.setError("Input must not be empty");
                else
                    mHoursEditText.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String hoursInput = mHoursEditText.getText().toString();
                int hours = 0;
                if (!hoursInput.equals("")) {
                    hours = Integer.parseInt(hoursInput);
                }


                if (hours < 1 || hours > 3)
                    mHoursEditText.setError("Please input a value between 1 and 3");
                else
                    mHoursEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 0)
                    mHoursEditText.setError("Input must not be empty");
                else
                    mHoursEditText.setError(null);

            }
        });

        mMinutesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() == 0)
                    if (s.toString().length() == 0)
                        mMinutesEditText.setError("Input must not be empty");
                    else
                        mMinutesEditText.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String minutesInput = mMinutesEditText.getText().toString();
                int minutes = -1;
                if (!minutesInput.equals("")) {
                    minutes = Integer.parseInt(minutesInput);
                }

                if (minutes < 0 || minutes > 59)
                    mMinutesEditText.setError("Please input a value between 0 and 59");
                else
                    mMinutesEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0)
                    mMinutesEditText.setError("Input must not be empty");
                else
                    mMinutesEditText.setError(null);

            }
        });


    }

    private void saveDuration() {
        String assignedHours = mHoursEditText.getText().toString();
        String assignedMinutes = mMinutesEditText.getText().toString();

        final int hours = Integer.parseInt(assignedHours);
        final int minutes = Integer.parseInt(assignedMinutes);

        if (hours < 1 || hours > 3 || minutes < 0 || minutes > 59) {
            Toast.makeText(getBaseContext(), "Invalid values entered. Please try again", Toast.LENGTH_LONG).show();
        } else {
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (mPlaceMap.containsKey(mSelectedItemName)) {
                            mSelectedPlaceKey = (String) mPlaceMap.get(mSelectedItemName);
                        }
                        mRef.child(mSelectedPlaceKey).child("placeHours").setValue(hours).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("AssignDuration", "onComplete: Duration assigned " + hours + ":" + minutes);
                                    Toast.makeText(getBaseContext(),"Duration Saved",Toast.LENGTH_LONG).show();
                                }

                                else
                                    Log.d("AssignDuration", "onComplete: Couldn't assign duration");

                            }
                        }

                        );

                        mRef.child(mSelectedPlaceKey).child("placeMinutes").setValue(minutes).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("AssignDuration", "onComplete: Duration assigned " + hours + ":" + minutes);

                                }

                                else
                                    Log.d("AssignDuration", "onComplete: Couldn't assign duration");

                            }
                        }

                        );

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(), "An error occoured in attempting to delete place", Toast.LENGTH_LONG).show();
                    Log.e("Delete Place", "onCancelled: ERROR: " + databaseError.toString());
                }
            });

        }

        //Log.d("Checking interface", "onClick: " + assignedMinutes + " " + assignedHours);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_duration_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteDurationItem = menu.findItem(R.id.deleteDurationMenuItem);

        if(mDurationAssignedFlag){
            deleteDurationItem.setVisible(true);
        }
        else{
            deleteDurationItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveDurationMenuItem:
                AlertDialog.Builder saveBuilder = new AlertDialog.Builder(this);
                saveBuilder.setTitle("Confirm Action")
                        .setMessage("Save the duration for this place?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveDuration();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                saveBuilder.show();
                break;
            case R.id.deleteDurationMenuItem:
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                deleteBuilder.setTitle("Confirm Action")
                        .setMessage("Delete the duration for this place?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDuration();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                deleteBuilder.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void deleteDuration() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (mPlaceMap.containsKey(mSelectedItemName)) {
                        mSelectedPlaceKey = (String) mPlaceMap.get(mSelectedItemName);
                    }
                    mRef.child(mSelectedPlaceKey).child("placeHours").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("DeleteDuration", "onComplete: Hours deleted ");
                                mHoursEditText.setText("");
                                invalidateOptionsMenu();
                            }

                            else
                                Log.d("DeleteDuration", "onComplete: Couldn't delete hours");

                        }
                    }

                    );

                    mRef.child(mSelectedPlaceKey).child("placeMinutes").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("DeleteDuration", "onComplete: Minutes deleted");
                                mMinutesEditText.setText("");
                                Toast.makeText(getBaseContext(),"Duration deleted",Toast.LENGTH_LONG).show();
                            }


                        else
                            Log.d("DeleteDuration", "onComplete: Couldn't delete minutes");

                        }
                    }

                    );

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "An error occoured in attempting to delete place", Toast.LENGTH_LONG).show();
                Log.e("Delete Place", "onCancelled: ERROR: " + databaseError.toString());
            }
        });
    }

    private void populateSpinner() {

        final List<String> places = new ArrayList<String>();

        mPlaceMap = new HashMap<String, Object>();


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String parentKey = snapshot.getKey();
                    String placeName = snapshot.child("placeName").getValue(String.class);
                    mPlaceKey = snapshot.getKey();
                    mPlaceMap.put(placeName, mPlaceKey);
                    places.add(placeName);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ManageDurationActivity.this, android.R.layout.simple_spinner_item, places);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mPlacesSpinner.setAdapter(arrayAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("populatespinner", "onCancelled: ERROR " + databaseError);
            }
        });


    }

}

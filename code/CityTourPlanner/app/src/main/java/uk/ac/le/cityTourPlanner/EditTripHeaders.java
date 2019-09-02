package uk.ac.le.cityTourPlanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditTripHeaders extends AppCompatActivity {

    private EditText mEditTripNameEditText;
    private EditText mEditTripDateEditText;

    private String oldTripName, oldTripDate;
    private String mModifiedTripName,mModifiedTripDate;
    private String mTripID;

    private OnSuccessListener mUpdateSuccessListener;
    private OnFailureListener mUpdateFailureListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip_headers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTripNameEditText =findViewById(R.id.EditTripNameEditText);
        mEditTripDateEditText = findViewById(R.id.EditTripDateEditText);

        mEditTripDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //close keyboard
                    hideSoftKeyboardUsingView(EditTripHeaders.this,mEditTripDateEditText);

                    DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {

                            int month=monthOfYear+1;
                            String date = dayOfMonth+"/"+month+"/"+year;
                            mEditTripDateEditText.setText(""+date);
                            hideSoftKeyboardUsingView(EditTripHeaders.this,mEditTripDateEditText);
                        }
                    };

                    Time date = new Time();
                    date.setToNow();
                    DatePickerDialog d = new DatePickerDialog(EditTripHeaders.this, dpd, date.year ,date.month, date.monthDay);
                    d.show();
                }
            }
        });

        Intent intent = getIntent();
        mTripID = intent.getStringExtra("Trip ID");
        oldTripName = intent.getStringExtra("Trip Name");
        oldTripDate = intent.getStringExtra("Trip Date");


        mEditTripNameEditText.setText(oldTripName);
        mEditTripDateEditText.setText(oldTripDate);

        mEditTripNameEditText.addTextChangedListener(mTextWatcher);
        mEditTripDateEditText.addTextChangedListener(mTextWatcher);

        mUpdateSuccessListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getApplicationContext(),"Record Successfully Updated",Toast.LENGTH_LONG).show();
            }
        };

        mUpdateFailureListener= new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed to update record",Toast.LENGTH_LONG).show();
            }
        };
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mModifiedTripName = mEditTripNameEditText.getText().toString();
            mModifiedTripDate = mEditTripDateEditText.getText().toString();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static void hideSoftKeyboardUsingView(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_trip_headers_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.updateMenuItem:
               UpdateTripHeaderDetails();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateTripHeaderDetails() {
       Map<String,Object> updatedValues = new HashMap<>();

        updatedValues.put("Trips/GeneralDetails/"+mTripID+"/tripName",mModifiedTripName);
        updatedValues.put("Trips/GeneralDetails/"+mTripID+"/tripDate",mModifiedTripDate);

        // call the update method

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.updateChildren(updatedValues).addOnSuccessListener(mUpdateSuccessListener).addOnFailureListener(mUpdateFailureListener);



    }
}



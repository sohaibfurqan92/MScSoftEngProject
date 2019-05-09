package uk.ac.le.cityTourPlanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class NewTripCreationActivity extends AppCompatActivity {

    private static final String TRIP_NAME = "uk.ac.le.cityTourPlanner.TRIP_NAME";
    private static final String TRIP_DATE = "uk.ac.le.cityTourPlanner.TRIP_DATE";

    private PlacesClient mPlacesClient;
    private PermissionListener mPermissionListener;
    private Button mCreateTripButton;
    private DatabaseReference mDatabaseReference;
    private EditText mTripNameEditText;
    private EditText mTripDateEditText;
    private TextView mPermissionsDeniedMessageTV;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip_creation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize places api
        InitializePlacesAPI(getString(R.string.googlePlacesAPIKey));

        //request permissions at runtime
       //StartTedPermissions();

        //get necessary references
        getReferencesInCode();


    }

    private void getReferencesInCode() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mCreateTripButton = (Button)findViewById(R.id.startTripButton);
        mTripNameEditText = (EditText) findViewById(R.id.tripNameEditText);
        mTripDateEditText = (EditText)findViewById(R.id.tripDateEditText);

    }



    private void InitializePlacesAPI(String API_KEY) {
        Places.initialize(getApplicationContext(), API_KEY );
        mPlacesClient = Places.createClient(this);
    }


    public void createTripButton_click(View view) {
        Intent intent = new Intent(NewTripCreationActivity.this, PlaceSearchActivity.class);
        intent.putExtra(TRIP_NAME,mTripNameEditText.getText());
        intent.putExtra(TRIP_DATE,mTripDateEditText.getText());
        startActivity(intent);
    }
}

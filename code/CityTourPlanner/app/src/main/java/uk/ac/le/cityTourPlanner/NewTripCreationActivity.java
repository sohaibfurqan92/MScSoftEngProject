package uk.ac.le.cityTourPlanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class NewTripCreationActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS_CODE =1 ;
    private String[] requestedPermissions;

    private PlacesClient mPlacesClient;
    private PermissionListener mPermissionListener;


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
        //requestedPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE};
        StartTedPermissions();



    }

    private void StartTedPermissions() {
        mPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                for(String item:deniedPermissions){
                    TedPermission.with(NewTripCreationActivity.this).setPermissions(item).check();
                }
            }
        };
        TedPermission.with(this)
                .setPermissionListener(mPermissionListener)
                .setRationaleMessage("This application needs to use certain features to work correctly. Please allow")
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE)
                .check();
    }



    private void InitializePlacesAPI(String API_KEY) {
        Places.initialize(getApplicationContext(), API_KEY );
        mPlacesClient = Places.createClient(this);
    }
}

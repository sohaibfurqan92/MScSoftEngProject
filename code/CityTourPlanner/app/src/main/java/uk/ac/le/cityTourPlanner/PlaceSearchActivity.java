package uk.ac.le.cityTourPlanner;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceSearchActivity extends AppCompatActivity implements SearchedPlacesAdapter.onItemClickListener, SavePlaceDialog.SaveTripDialogListener {

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String EXTRA_PLACE_ID = "uk.ac.le.cityTourPlanner.PLACE_ID";


    private RecyclerView mRecyclerView;
    private SearchedPlacesAdapter mSearchedPlacesAdapter;
    private ArrayList<SearchedPlacesItem> mSearchedPlacesList;
    private ArrayList<SearchedPlacesItem> mSelectedPlacesList;
    private RequestQueue mRequestQueue;
    PlacesClient mPlacesClient;
    int mSelectedPlacesCount;
    private ConstraintLayout mEmptyRecyclerLayout;
    private String mNameOfTrip;
    private String mDateOfTrip;

    private Place mPlace; //place object from Android places SDK to retrieve places in city

    //These fields will store information regarding the city from which to select nearby places
    private String mCityID;
    private double mCityLatitude;
    private double mCityLongitude;
    private String mCityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.place_search_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PlaceSearchActivity.this));
        mSearchedPlacesList = new ArrayList<>();
        mSelectedPlacesList = new ArrayList<>();
        mSelectedPlacesCount = 0;



        mRequestQueue = Volley.newRequestQueue(this);

        Places.initialize(getApplicationContext(), getString(R.string.googlePlacesAPIKey));
        mPlacesClient = Places.createClient(this);

        mEmptyRecyclerLayout = findViewById(R.id.emptyRecyclerLayout);

        HandleEmptyRecyclerView();


    }

    private void HandleEmptyRecyclerView() {

        if (!mSearchedPlacesList.isEmpty()) {

            //if data is available, don't show the empty text
            mEmptyRecyclerLayout.setVisibility(View.INVISIBLE);
            //RecyclerAdapter adapter = new RecyclerAdapter(data); // pass the data to your adapter here
            //recyclerView.setAdapter(adapter);

        } else
            mEmptyRecyclerLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.place_search_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem savePlaceSelectionMenuItem = menu.findItem(R.id.action_menu_saveplacelist);
        if (mSelectedPlacesCount > 0) {
            savePlaceSelectionMenuItem.setVisible(true);
        } else {
            savePlaceSelectionMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu_searchplace) {
            launchPlaceAutocomplete();
        }
        if (id == R.id.action_menu_saveplacelist) {
            openDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openDialog() {
        SavePlaceDialog dialog = new SavePlaceDialog();
        dialog.show(getSupportFragmentManager(), "Save place selection dialog");

    }

    private void launchPlaceAutocomplete() {


// Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);


// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {


            if (resultCode == RESULT_OK) {
                mPlace = Autocomplete.getPlaceFromIntent(data);
                Log.i("Place Details: ", "Place: " + mPlace.getName() + ", " + mPlace.getId());
                mCityID = mPlace.getId();
                mCityName = mPlace.getName();

                mSearchedPlacesList.clear();
                mCityLatitude = 0.0;
                mCityLongitude = 0.0;
                LatLng latLng = mPlace.getLatLng();
                if (latLng != null) {
                    mCityLatitude = latLng.latitude;
                    mCityLongitude = latLng.longitude;
                }

                String requestURL = generateNearbyRequestURL(mCityLatitude, mCityLongitude);

                ParseNearbySearchJSON(requestURL);

                Log.i("Place details", "Place: " + mPlace.getName() + ", " + mPlace.getId());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Error: ", status.getStatusMessage());
                TextView headingTextView = findViewById(R.id.HeadingTextView);
                TextView messageTextView = findViewById(R.id.messageTextView);

                headingTextView.setText("Error");
                messageTextView.setText(status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void ParseNearbySearchJSON(String requestURL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mEmptyRecyclerLayout.setVisibility(View.INVISIBLE);

                    JSONArray jsonArray = response.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject result = jsonArray.getJSONObject(i);

                        String placeID = result.getString("place_id");
                        String placeName = result.getString("name");
                        Log.d("placeName", "onResponse: placeName " + placeName);
                        String placeSummary = result.getString("vicinity");
                        String iconURL = result.getString("icon");

                        mSearchedPlacesList.add(new SearchedPlacesItem(placeName, placeSummary, iconURL, placeID));
                    }
                    mSearchedPlacesAdapter = new SearchedPlacesAdapter(PlaceSearchActivity.this, mSearchedPlacesList);
                    mRecyclerView.setAdapter(mSearchedPlacesAdapter);
                    mSearchedPlacesAdapter.SetOnItemClickListener(PlaceSearchActivity.this);
                    mSearchedPlacesAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    Log.d("Parse error", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error fetching results", error.toString());
                Toast.makeText(PlaceSearchActivity.this,"Server Error! If the problem persists contact administrator.",Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(request);
    }

    private String generateNearbyRequestURL(double latitude, double longitude) {
        //requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/
        // json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyBrqqD2yEMKBRABm-3hFUzZy3xzZ-hI-to";

        double radius = 50000;
        String type = PreferenceManager.getDefaultSharedPreferences(this).getString("specify_places_type_pref","point_of_interest");
        String outputFormat = "json";
        String formattedNearbyRequestURL;

        formattedNearbyRequestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
                + outputFormat + "?location=" + latitude + "," + longitude + "&radius=" + radius + "&type=" + type + "&key=" + getString(R.string.googlePlacesAPIKey);

        return formattedNearbyRequestURL;
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, PlaceDetailActivity.class);
        SearchedPlacesItem clickedItem = mSearchedPlacesList.get(position);
        detailIntent.putExtra(EXTRA_PLACE_ID, clickedItem.getPlaceID());
        startActivity(detailIntent);
    }

    @Override
    public void onCheckboxClick(int position, CheckBox chk) {
        if (chk.isChecked()) {
            mSelectedPlacesList.add(mSearchedPlacesList.get(position));
            mSelectedPlacesCount++;

        } else if (!chk.isChecked()) {
            mSelectedPlacesList.remove(mSearchedPlacesList.get(position));
            mSelectedPlacesCount--;

        }

        if (mSelectedPlacesList.size() >= 0) {
            invalidateOptionsMenu();
        }


    }

    @Override
    public void passDataToActivity(String tripName, String tripDate) {
        mNameOfTrip = tripName;
        mDateOfTrip = tripDate;
        String TripStatus = "Scheduled";
        
        SaveTrip(mCityID, mCityLatitude, mCityLongitude, mCityName, mNameOfTrip,mDateOfTrip ,mSelectedPlacesList,TripStatus );
    }

    private void SaveTrip(String CityID, double CityLatitude, double CityLongitude, String CityName, String TripName, String TripDate, List<SearchedPlacesItem> SelectedPlacesList,String TripStatus) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Trips");

        String generalDetailsPushKey = myRef.child("GeneralDetails").push().getKey();
        //String selectedPlacesPushKey = myRef.child("PlacesDetails").push().getKey();



        GeneratedTrip trip = new GeneratedTrip(CityID,CityLatitude,CityLongitude,CityName, TripName,TripDate,FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),null,TripStatus);
        myRef.child("GeneralDetails").child(generalDetailsPushKey).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Trip Created",Toast.LENGTH_LONG).show();

                //reset everything
                mSearchedPlacesList.clear();
                mSelectedPlacesList.clear();
                mSelectedPlacesCount =0;
                mSearchedPlacesAdapter.notifyDataSetChanged();

                mRecyclerView.setVisibility(View.INVISIBLE);
                mEmptyRecyclerLayout.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR:", "onFailure:"+e.getMessage());
                    }
                });

        //insert place details at appropriate location
        Map<String,Object> placeDetailsMap = new HashMap<>();
        int numberOfSelectedPlaces = SelectedPlacesList.size();
        for(int i=0; i<numberOfSelectedPlaces; i++){
            placeDetailsMap.put("Place"+(i+1),SelectedPlacesList.get(i));
        }
        //placeDetailsMap.put("TripName",trip.getTripName());
        //placeDetailsMap.put("",SelectedPlacesList);
        myRef.child("SelectedPlaces").child(generalDetailsPushKey).setValue(placeDetailsMap);



    }
}

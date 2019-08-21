package uk.ac.le.cityTourPlanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocalTime;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripPlacesActivity extends AppCompatActivity {


    private int AUTOCOMPLETE_REQUEST_CODE = 1;


    private RecyclerView mItineraryRecyclerView;
    private TripPlacesAdapter mItineraryAdapter;
    private List<SearchedPlacesItem> mSelectedPlacesList;

    //for foirebase
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    String mSelectedTripID;
    private Parcelable mListState;
    private Place mPlace;
    private String mPlaceID;
    private String mPlaceName;
    private RequestQueue mRequestQueue;
    private PlacesClient mPlacesClient;
    private double mSouthWestLat;
    private double mSouthWestLong;
    private double mNorthEastLat;
    private double mNorthEastLong;
    private String mPlaceSummary;
    private String mIconURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_places);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSelectedTripID = getIntent().getStringExtra("trip_id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectedPlacesList= new ArrayList<>();

        mItineraryRecyclerView = findViewById(R.id.itineraryRecyclerView);
        mItineraryRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mItineraryAdapter = new TripPlacesAdapter(this,mSelectedPlacesList);
        mItineraryRecyclerView.setAdapter(mItineraryAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Trips/SelectedPlaces/"+mSelectedTripID);

        mRequestQueue = Volley.newRequestQueue(this);

        Places.initialize(getApplicationContext(), getString(R.string.googlePlacesAPIKey));
        mPlacesClient = Places.createClient(this);

        getCityBounds();

        if(savedInstanceState!=null){
            mSelectedPlacesList = savedInstanceState.getParcelable("trip_places_state");
        }




        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SearchedPlacesItem place = dataSnapshot.getValue(SearchedPlacesItem.class);
//                Log.d("Fetchingchild", "onChildAdded: Name: "+place.getPlaceName());
//                Log.d("Fetchingchild", "onChildAdded: Desc: "+place.getPlaceDesc());
                mSelectedPlacesList.add(place);
                mItineraryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                mItineraryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mRef.addChildEventListener(mChildEventListener);

    }

    private void getCityBounds() {
        DatabaseReference boundsRef= FirebaseDatabase.getInstance().getReference("Trips/GeneralDetails/"+mSelectedTripID);
        boundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNorthEastLat = dataSnapshot.child("northEastLatitude").getValue(Double.class);
                mNorthEastLong = dataSnapshot.child("northEastLongitude").getValue(Double.class);
                mSouthWestLat = dataSnapshot.child("southWestLatitude").getValue(Double.class);
                mSouthWestLong = dataSnapshot.child("southWestLongitude").getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("BoundsError", "onCancelled: " + databaseError);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip_places_menu,menu);
        return true;
    }

//    private void RestorePreviousState() {
//        if(mListState!=null){
//            mItineraryRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
//        }
//
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ManageDurationsMenuItem:
                Intent intent = new Intent(TripPlacesActivity.this,ManageDurationActivity.class);
                intent.putExtra("trip_id",mSelectedTripID);
                startActivity(intent);
                break;

            case R.id.AddPlaceMenuItem:
                launchPlaceAutocomplete();
                break;
                default:
                    return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(mChildEventListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mListState = mItineraryRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("trip_places_state",mListState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState!=null){
            mListState = savedInstanceState.getParcelable("trip_places_state");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mListState!=null){
            mItineraryRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }
    }

    private void launchPlaceAutocomplete() {

// Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.VIEWPORT);


// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .setLocationRestriction(RectangularBounds.newInstance(new LatLng(mSouthWestLat, mSouthWestLong),new LatLng(mNorthEastLat, mNorthEastLong)))
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

    }
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {


            if (resultCode == RESULT_OK) {
                mPlace = Autocomplete.getPlaceFromIntent(data);
                Log.i("Place Details: ", "Place: " + mPlace.getName() + ", " + mPlace.getId());
                mPlaceID = mPlace.getId();
                mPlaceName = mPlace.getName();

                String requestURL = generateFindPlaceURL(mPlaceName);
                ParseFindPlaceJSON(requestURL);

                //Log.i("Place details", "Place: " + mPlace.getName() + ", " + mPlace.getId() +mPlace.getViewport());
            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Error: ", status.getStatusMessage());
            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private String generateFindPlaceURL(String placeName) {
        //requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/
        // json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyBrqqD2yEMKBRABm-3hFUzZy3xzZ-hI-to";

        String fields ="place_id,name,icon,formatted_address";
        String outputFormat = "json";
        String formattedPlaceSearchURL;

        formattedPlaceSearchURL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/"
                + outputFormat + "?input=" + placeName + "&inputtype=textquery&fields="+fields + "&key=" + getString(R.string.googlePlacesAPIKey);

        return formattedPlaceSearchURL;


    }

    private void ParseFindPlaceJSON(String requestURL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("candidates");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject candidates = jsonArray.getJSONObject(i);

//                        String placeID = candidates.getString("place_id");
//                        String placeName = result.getString("name");
//                        Log.d("placeName", "onResponse: placeName " + placeName);
                        mPlaceSummary = candidates.getString("formatted_address");
                        mIconURL = candidates.getString("icon");



                        final AlertDialog.Builder addPlaceBuilder = new AlertDialog.Builder(TripPlacesActivity.this);
                        addPlaceBuilder.setTitle("Confirm Action")
                                .setMessage("Do you want to add "+mPlaceName+" to this trip?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addPlace(mPlaceID,mPlaceName,mPlaceSummary,mIconURL);
                                        dialog.dismiss();

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                        addPlaceBuilder.show();
                    }



                } catch (JSONException e) {
                    Log.d("Parse error", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error fetching results", error.toString());
                Toast.makeText(TripPlacesActivity.this,"Server Error! If the problem persists contact administrator.",Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(request);
    }

    private void addPlace(final String placeID, final String placeName, final String placeSummary, final String iconURL) {
        DatabaseReference addPlaceRef = FirebaseDatabase.getInstance().getReference("Trips/SelectedPlaces/"+mSelectedTripID);
        addPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int childrencount= (int) dataSnapshot.getChildrenCount();
                Map<String,Object> placeValues = new HashMap<>();
                placeValues.put("iconURL",iconURL);
                placeValues.put("placeDesc",placeSummary);
                placeValues.put("placeID",placeID);
                placeValues.put("placeName",placeName);
                mRef.child("Place"+(childrencount+1)).setValue(placeValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getBaseContext(),"Place added to trip",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getBaseContext(),"Couldn't add place",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AddPlace", "onCancelled: "+databaseError );
            }
        });
    }
}

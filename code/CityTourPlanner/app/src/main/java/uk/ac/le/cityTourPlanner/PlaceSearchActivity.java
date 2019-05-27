package uk.ac.le.cityTourPlanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceSearchActivity extends AppCompatActivity implements SearchedPlacesAdapter.onItemClickListener {

    int AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final String EXTRA_PLACE_ID = "uk.ac.le.cityTourPlanner.PLACE_ID";

    private RecyclerView mRecyclerView;
    private SearchedPlacesAdapter mSearchedPlacesAdapter;
    private ArrayList<SearchedPlacesItem> mSearchedPlacesList;
    private ArrayList<SearchedPlacesItem> mSelectedPlacesList;
    private RequestQueue mRequestQueue;
    PlacesClient mPlacesClient;


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

        mRequestQueue = Volley.newRequestQueue(this);

        Places.initialize(getApplicationContext(), getString(R.string.googlePlacesAPIKey));
        mPlacesClient = Places.createClient(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.place_search_menu, menu);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void launchPlaceAutocomplete() {


// Set the fields to specify which types of place data to
// return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);


// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.CITIES)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Place Details: ", "Place: " + place.getName() + ", " + place.getId());
                mSearchedPlacesList.clear();
                double latitude = 0.0, longitude = 0.0;
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                }

                String requestURL = generateNearbyRequestURL(latitude, longitude);
                ParseNearbySearchJSON(requestURL);
                Log.i("Place details", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Error: ", status.getStatusMessage());
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
                Log.e("Error", error.toString());
            }
        });
        mRequestQueue.add(request);
    }

    private String generateNearbyRequestURL(double latitude, double longitude) {
        //requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/
        // json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyBrqqD2yEMKBRABm-3hFUzZy3xzZ-hI-to";

        double radius = 50000;
        String type = "point_of_interest";
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
        } else if (!chk.isChecked()) {
            mSelectedPlacesList.remove(mSearchedPlacesList.get(position));
        }
    }
}

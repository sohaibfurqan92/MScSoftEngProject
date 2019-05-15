package uk.ac.le.cityTourPlanner;

import android.app.DownloadManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;

public class PlaceSearchActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private searchedPlacesAdapter mSearchedPlacesAdapter;
    private ArrayList<searchedPlacesItem> mSearchedPlacesList;
    private RequestQueue mRequestQueue;
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        mRecyclerView = findViewById(R.id.place_search_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PlaceSearchActivity.this));
        mSearchedPlacesList = new ArrayList<>();



        mRequestQueue = Volley.newRequestQueue(this);


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
// Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mSearchedPlacesList.clear();
                double latitude = 0.0, longitude = 0.0;

                LatLng latLng = place.getLatLng();
                if(latLng!=null){
                     latitude= latLng.latitude;
                     longitude = latLng.longitude;
                }

                 String requestURL = generateRequestURL(latitude,longitude);
                parseJSON(requestURL);
                //nearbyPlaceSearch(place);
                Log.i("Place details", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR: ", "An error occurred: " + status);
            }
        });
    }

    private void parseJSON(String requestURL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for(int i=0; i<jsonArray.length();i++){
                        JSONObject result = jsonArray.getJSONObject(i);

                        String placeName = result.getString("name");
                        Log.d("placeName", "onResponse: placeName "+placeName);
                        String placeSummary = result.getString("vicinity");
                        String iconURL = result.getString("icon");

                        mSearchedPlacesList.add(new searchedPlacesItem(placeName,placeSummary,iconURL));



                    }
                    mSearchedPlacesAdapter = new searchedPlacesAdapter(PlaceSearchActivity.this,mSearchedPlacesList);
                    mRecyclerView.setAdapter(mSearchedPlacesAdapter);
                    mSearchedPlacesAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                   Log.d("Parse error",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error",error.toString());
            }
        });
        mRequestQueue.add(request);
    }

    private String generateRequestURL(double latitude, double longitude) {
        //requestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/
        // json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyBrqqD2yEMKBRABm-3hFUzZy3xzZ-hI-to";

        double radius = 50000;
        String type = "point_of_interest";
        String outputFormat = "json";
        String FormattedRequestURL;

        FormattedRequestURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/"
                + outputFormat + "?location=" + latitude + "," + longitude + "&radius=" + radius + "&type=" + type + "&key=" + getString(R.string.googlePlacesAPIKey);

        return FormattedRequestURL;
        }

}

package uk.ac.le.cityTourPlanner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final int NUMBER_OF_IMAGES = 5;

    private String placeID;
    private RequestQueue mRequestQueue;

    TextView placeNameTextView, placeAddressTextView, phoneNumTextView, ratingsTextView, websiteTextView;

    private String mWebsiteURL, mRating, mPhoneNo, mPlaceAddress, mPlaceName;

    private String[] mImageURLs;

    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        placeID = intent.getStringExtra("uk.ac.le.cityTourPlanner.PLACE_ID");

        placeNameTextView = findViewById(R.id.textViewName_details);
        placeAddressTextView = findViewById(R.id.textViewAddress_details);
        phoneNumTextView = findViewById(R.id.textViewPhoneNum_details);
        ratingsTextView = findViewById(R.id.textViewRatings_details);
        websiteTextView = findViewById(R.id.textViewWebsite_details);

        mImageURLs = new String[NUMBER_OF_IMAGES];

        mProgressDialog = new ProgressDialog(this);


        mRequestQueue = Volley.newRequestQueue(this);

        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        String detailsRequestURL = generateDetailsRequestURL();
        ParseDetailsJSON(detailsRequestURL);

        ViewPager viewPager = findViewById(R.id.place_image_viewpager);
        PlaceDetailsViewpagerAdapter viewpagerAdapter = new PlaceDetailsViewpagerAdapter(this, mImageURLs);

        viewPager.setAdapter(viewpagerAdapter);

        mProgressDialog.dismiss();



    }


    private String generateDetailsRequestURL() {
        //DetailsRequestURL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&" +
        //"fields=name,rating,formatted_phone_number&key=YOUR_API_KEY";

        double radius = 50000;
        String type = "point_of_interest";
        String outputFormat = "json";
        String FormattedDetailsRequestURL;

        FormattedDetailsRequestURL = "https://maps.googleapis.com/maps/api/place/details/"
                + outputFormat + "?placeid=" + placeID + "&fields=name,formatted_address,photos,opening_hours,international_phone_number,rating,website"
                + "&key=" + getString(R.string.googlePlacesAPIKey);

        return FormattedDetailsRequestURL;
    }

    private void ParseDetailsJSON(String requestURL) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestURL, null, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                try {
                    mProgressDialog.setMessage("Getting place details...");
                    JSONObject result = response.getJSONObject("result");
                    mPlaceName = "Name: " + result.getString("name");
                    placeNameTextView.setText(mPlaceName);
                    mPlaceAddress = "Address: " + result.getString("formatted_address");
                    placeAddressTextView.setText(mPlaceAddress);
                    mPhoneNo = "Phone Number: " + result.getString("international_phone_number");
                    phoneNumTextView.setText(mPhoneNo);
                    mRating = "Rating:" + result.getString("rating");
                    ratingsTextView.setText(mRating);
                    mWebsiteURL = result.getString("website");

                    String linkedText = "<b>Website: </b> " +
                            String.format("<a href=\"%s\">Click Here</a> ", mWebsiteURL);

                    websiteTextView.setText(Html.fromHtml(linkedText));
                    websiteTextView.setMovementMethod(LinkMovementMethod.getInstance());

                    JSONArray photos = result.getJSONArray("photos");


                    for (int i = 0; i < NUMBER_OF_IMAGES; i++) {
                        JSONObject photo = photos.getJSONObject(i);
                        String photoReference = photo.getString("photo_reference");
                        mImageURLs[i] = generatePhotosRequestURL(photoReference);
                    }
                } catch (JSONException e) {
                    Log.e("Parse error", e.toString());
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

    private String  generatePhotosRequestURL(String photoReference) {
        //https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&
        // photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU
        // &key=YOUR_API_KEY

        int maxWidth = 500;
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + maxWidth + "&photoreference=" + photoReference + "&key=" + getString(R.string.googlePlacesAPIKey);
    }

}


package uk.ac.le.cityTourPlanner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ItinararyActivity extends AppCompatActivity {


    private RecyclerView mItineraryRecyclerView;
    private ItinararyAdapter mItineraryAdapter;
    private List<SearchedPlacesItem> mSelectedPlacesList;

    //for foirebase
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    String mSelectedTripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinarary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSelectedTripID = getIntent().getStringExtra("trip_id");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectedPlacesList= new ArrayList<>();

        mItineraryRecyclerView = findViewById(R.id.itineraryRecyclerView);
        mItineraryRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mItineraryAdapter = new ItinararyAdapter(getApplicationContext(),mSelectedPlacesList);
        mItineraryRecyclerView.setAdapter(mItineraryAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Trips/SelectedPlaces/"+mSelectedTripID);


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(mChildEventListener);
    }
}

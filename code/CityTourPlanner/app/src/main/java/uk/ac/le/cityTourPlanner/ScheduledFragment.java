package uk.ac.le.cityTourPlanner;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduledFragment extends Fragment {

    private RecyclerView mScheduledTripsRecyclerView;
    private ScheduledTripRecyclerViewAdapter mScheduledTripAdapter;
    private List<GeneratedTrip> mTripsList;

    //for foirebase
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private TextView defaultTextView;
    private Parcelable mListState;

    public static final String EXTRA_TRIP_ID = "uk.ac.le.cityTourPlanner.TRIP_ID";


    public ScheduledFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_scheduled, container, false);

        mTripsList= new ArrayList<>();

        defaultTextView = view.findViewById(R.id.EmptyRecyclerViewTextView);

        mScheduledTripsRecyclerView= view.findViewById(R.id.scheduledTripsRecyclerView);
        mScheduledTripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mScheduledTripAdapter = new ScheduledTripRecyclerViewAdapter(getContext(),mTripsList);
        mScheduledTripsRecyclerView.setAdapter(mScheduledTripAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Trips/GeneralDetails/");

        if(savedInstanceState!=null){
            RestorePreviousState();
        }

        //implement childlistener
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GeneratedTrip generatedTrip  =dataSnapshot.getValue(GeneratedTrip.class);
                    if(generatedTrip.getTripStatus().equals("Scheduled")){
                        generatedTrip.setTripID(dataSnapshot.getKey());
                        mTripsList.add(generatedTrip);
                        mScheduledTripAdapter.notifyDataSetChanged();
                        if(dataSnapshot.hasChildren()){
                            HandleEmptyRecyclerView();
                        }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GeneratedTrip trip = dataSnapshot.getValue(GeneratedTrip.class);
                trip.setTripID(dataSnapshot.getKey());
                mTripsList.remove(trip);
                 mScheduledTripAdapter.notifyDataSetChanged();

                defaultTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                mScheduledTripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //query database for all trips that have been created by the current user
   Query currentUserQuery = mRef.orderByChild("createdBy").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail());
currentUserQuery.addChildEventListener(mChildEventListener);


     HandleEmptyRecyclerView();

        return view;
    }

    private void RestorePreviousState() {
        if(mListState!=null){
            mScheduledTripsRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTripsList.size()>0){
            HandleEmptyRecyclerView();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mListState = mScheduledTripsRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }


    private void HandleEmptyRecyclerView(){
            //if data is available, don't show the empty text
        if(mTripsList.size()>0)
            defaultTextView.setVisibility(View.INVISIBLE);
            //RecyclerAdapter adapter = new RecyclerAdapter(data); // pass the data to your adapter here
            //recyclerView.setAdapter(adapter);
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(mChildEventListener);
    }

}

package uk.ac.le.cityTourPlanner;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PastFragment extends Fragment {

    private RecyclerView mPastTripsRecyclerView;
    private PastTripRecyclerViewAdapter mPastTripAdapter;
    private List<GeneratedTrip> mPastTripsList;

    //for foirebase
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private TextView defaultTextView;

    String mCurrentFormattedDate;
    private Parcelable mListState;


    public PastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past, container, false);
        mPastTripsList = new ArrayList<>();
        defaultTextView =view.findViewById(R.id.pastFragmentTextView);
        mPastTripsRecyclerView= view.findViewById(R.id.PastTripsRecyclerView);
        mPastTripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPastTripAdapter = new PastTripRecyclerViewAdapter(getContext(),mPastTripsList);
        mPastTripsRecyclerView.setAdapter(mPastTripAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Trips/GeneralDetails");

       GetCurrentFormattedDate();

        if(savedInstanceState!=null){
            RestorePreviousState();
        }

        //implement childlistener
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GeneratedTrip generatedTrip  =dataSnapshot.getValue(GeneratedTrip.class);
                generatedTrip.setTripID(dataSnapshot.getKey());
                String tripDate = generatedTrip.getTripDate();
                if(CompareDates(mCurrentFormattedDate,tripDate)<0){
                    String TripID = generatedTrip.getTripID();
                    Map<String,Object> modifiedValues= new HashMap<>();
                    modifiedValues.put("Trips/GeneralDetails/"+TripID+"/tripStatus","Past");
                    FirebaseDatabase.getInstance().getReference().updateChildren(modifiedValues)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Trip Status changed", "onSuccess: changed to past");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("Error changing status ", "onFailure: Status not changed "+e);
                        }
                    });

                    if(generatedTrip.getTripStatus().equals("Past")){

                        mPastTripsList.add(generatedTrip);
                        mPastTripAdapter.notifyDataSetChanged();
                        if(dataSnapshot.hasChildren()){
                            HandleEmptyRecyclerView();
                        }
                    }
                }

            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GeneratedTrip trip = dataSnapshot.getValue(GeneratedTrip.class);
                trip.setTripID(dataSnapshot.getKey());
                mPastTripsList.remove(trip);
                mPastTripAdapter.notifyDataSetChanged();

                defaultTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //query database for all trips that have been created by the current user
        Query currentUserQuery = mRef.orderByChild("createdBy").equalTo(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        currentUserQuery.addChildEventListener(mChildEventListener);

        return view;
    }

    private int CompareDates(String currentDate, String tripDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date TripDateObj = null,CurrentDateObj = null;
        try{
            CurrentDateObj = format.parse(currentDate);
            TripDateObj = format.parse(tripDate);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return TripDateObj.compareTo(CurrentDateObj);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(mChildEventListener);
    }

    private void HandleEmptyRecyclerView() {
        //if data is available, don't show the empty text
        defaultTextView.setVisibility(View.INVISIBLE);
        //RecyclerAdapter adapter = new RecyclerAdapter(data); // pass the data to your adapter here
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPastTripsList.size()>0){
            HandleEmptyRecyclerView();
        }
    }

    private void GetCurrentFormattedDate(){
        Date c = Calendar.getInstance().getTime();
        //System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        mCurrentFormattedDate = df.format(c);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mListState = mPastTripsRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

    private void RestorePreviousState() {
        if(mListState!=null){
            mPastTripsRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }

    }

}

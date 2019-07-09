package uk.ac.le.cityTourPlanner;


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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrashFragment extends Fragment {

    private RecyclerView mTrashTripsRecyclerView;
    private TrashTripRecyclerViewAdapter mTrashTripAdapter;
    private List<GeneratedTrip> mTrashTripsList;

    //for foirebase
    private ChildEventListener mChildEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private TextView defaultTextView;

    String mCurrentFormattedDate;
    private Parcelable mListState;


    public TrashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trash, container, false);

        mTrashTripsList = new ArrayList<>();
        defaultTextView = view.findViewById(R.id.trashFragmentTextView);
        mTrashTripsRecyclerView= view.findViewById(R.id.trashTripsRecyclerView);
        mTrashTripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTrashTripAdapter = new TrashTripRecyclerViewAdapter(getContext(),mTrashTripsList);
        mTrashTripsRecyclerView.setAdapter(mTrashTripAdapter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef=mFirebaseDatabase.getReference("Trips/GeneralDetails");

        GetCurrentFormattedDate();

        if(savedInstanceState!=null){
            RestorePreviousState();
        }

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GeneratedTrip generatedTrip  =dataSnapshot.getValue(GeneratedTrip.class);
                if(generatedTrip.getTripStatus().equals("Deleted")){
                    generatedTrip.setTripID(dataSnapshot.getKey());
                    mTrashTripsList.add(generatedTrip);
                    mTrashTripAdapter.notifyDataSetChanged();
                    if(dataSnapshot.hasChildren()){
                        HandleEmptyRecyclerView();
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               /* GeneratedTrip trip = dataSnapshot.getValue(GeneratedTrip.class);
                trip.setTripID(dataSnapshot.getKey());
                mTrashTripsList.remove(trip);
                mTrashTripAdapter.notifyDataSetChanged();

                defaultTextView.setVisibility(View.VISIBLE);*/

                GeneratedTrip generatedTrip  =dataSnapshot.getValue(GeneratedTrip.class);
                if(generatedTrip.getTripStatus().equals("Deleted")){
                    generatedTrip.setTripID(dataSnapshot.getKey());
                    mTrashTripsList.add(generatedTrip);
                    mTrashTripAdapter.notifyDataSetChanged();
                    if(dataSnapshot.hasChildren()){
                        HandleEmptyRecyclerView();
                    }
                }
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

        Query currentUserQuery = mRef.orderByChild("createdBy").equalTo(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        currentUserQuery.addChildEventListener(mChildEventListener);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRef.removeEventListener(mChildEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mTrashTripsList.size()>0){
            mTrashTripAdapter.notifyDataSetChanged();
            HandleEmptyRecyclerView();
        }
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

    private void HandleEmptyRecyclerView() {
        //if data is available, don't show the empty text
        defaultTextView.setVisibility(View.INVISIBLE);
        //RecyclerAdapter adapter = new RecyclerAdapter(data); // pass the data to your adapter here
        //recyclerView.setAdapter(adapter);
    }

    private void GetCurrentFormattedDate(){
        Date c = Calendar.getInstance().getTime();
        //System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        mCurrentFormattedDate = df.format(c);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mListState = mTrashTripsRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

    private void RestorePreviousState() {
        if(mListState!=null){
            mTrashTripsRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
        }

    }

}

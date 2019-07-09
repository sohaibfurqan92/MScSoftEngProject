package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastTripRecyclerViewAdapter extends RecyclerView.Adapter<PastTripRecyclerViewAdapter.PastTripViewHolder> {

    private Context mContext;
    private List<GeneratedTrip> mPastTripList;

    public PastTripRecyclerViewAdapter(Context context, List<GeneratedTrip> pastTripList) {
        mContext = context;
        mPastTripList = pastTripList;
    }

    @NonNull
    @Override
    public PastTripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.past_trip_item,viewGroup,false);
        return new PastTripRecyclerViewAdapter.PastTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PastTripViewHolder pastTripViewHolder, int i) {

        final GeneratedTrip generatedTrip = mPastTripList.get(i);
        pastTripViewHolder.PastTripNameTextView.setText(generatedTrip.getTripName());
        pastTripViewHolder.PastTripDateTextView.setText(generatedTrip.getTripDate());

        pastTripViewHolder.PastTripOptionsMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an object of PopUpMenu
                PopupMenu popupMenu = new PopupMenu(mContext,pastTripViewHolder.PastTripOptionsMenuTextView);
                //inflate menu from xml resource
                popupMenu.inflate(R.menu.past_trips_options_menu);
                //add click listener for individual menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.ReschedulePastTripMenuItem:
                                //TODO
                                break;
                            case R.id.DeletePastTripMenuItem:
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Are you sure you want to delete this trip?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String TripID = generatedTrip.getTripID();
                                                Map<String,Object> modifiedValues= new HashMap<>();
                                                modifiedValues.put("Trips/GeneralDetails/"+TripID+"/tripStatus","Deleted");
                                                FirebaseDatabase.getInstance().getReference().updateChildren(modifiedValues)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(mContext,"Trip moved to trash",Toast.LENGTH_LONG).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(mContext,"Error deleting trip",Toast.LENGTH_LONG).show();
                                                        Log.d("Error deleting trip", "onFailure: Delete trip "+e);
                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert).show();
                                break;
                        }
                        return false;
                    }
                });

                //display popup
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPastTripList.size();
    }

    public class PastTripViewHolder extends RecyclerView.ViewHolder{

        public TextView PastTripNameTextView;
        public TextView PastTripDateTextView;
        public TextView PastTripOptionsMenuTextView;


        public PastTripViewHolder(@NonNull View itemView) {
            super(itemView);

            PastTripNameTextView = (TextView)itemView.findViewById(R.id.deletedTripNameTextView);
            PastTripDateTextView = (TextView)itemView.findViewById(R.id.deletedTripDateTextView);
            PastTripOptionsMenuTextView = (TextView)itemView.findViewById(R.id.deletedTripOptionsTextView);
        }
    }
}

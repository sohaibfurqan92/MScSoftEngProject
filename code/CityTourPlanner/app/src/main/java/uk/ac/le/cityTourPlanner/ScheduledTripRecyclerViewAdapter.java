package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScheduledTripRecyclerViewAdapter extends RecyclerView.Adapter<ScheduledTripRecyclerViewAdapter.ScheduledTripViewHolder> {

    private Context mContext;
    private List<GeneratedTrip> mTripList;
    private onItemClickListener mListener;

    public ScheduledTripRecyclerViewAdapter(Context context, List<GeneratedTrip> tripList) {
        mContext = context;
        mTripList = tripList;
    }

    public void SetOnItemClickListener(ScheduledTripRecyclerViewAdapter.onItemClickListener listener){
        mListener = listener;
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ScheduledTripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.scheduled_trip_item,viewGroup,false);
        return new ScheduledTripViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ScheduledTripViewHolder viewHolder, int i) {
        final GeneratedTrip generatedTrip = mTripList.get(i);
        viewHolder.ScheduledTripNameTextView.setText(generatedTrip.getTripName());
        viewHolder.ScheduledTripDateTextView.setText(generatedTrip.getTripDate());
       viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TripPlacesActivity.class);
                intent.putExtra("trip_id",generatedTrip.getTripID());
                mContext.startActivity(intent);
            }
        });

        viewHolder.ScheduledTripOptionsMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an object of PopUpMenu
                PopupMenu popupMenu = new PopupMenu(mContext,viewHolder.ScheduledTripOptionsMenuTextView);
                //inflate menu from xml resource
                popupMenu.inflate(R.menu.scheduled_trips_options_menu);
                //add click listener for individual menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.EditScheduledTripDetailsMenuItem:

                                Intent intent = new Intent(mContext,EditTripHeaders.class);
                                intent.putExtra("Trip ID",generatedTrip.getTripID());
                                intent.putExtra("Trip Name",generatedTrip.getTripName());
                                intent.putExtra("Trip Date",generatedTrip.getTripDate());
                                mContext.startActivity(intent);
                                break;
                            case R.id.DeleteScheduledTripMenuItem:
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

                                                //delete from selected places

                                                //FirebaseDatabase.getInstance().getReference("Trips/SelectedPlaces").child(TripID).removeValue();
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
        return mTripList.size();
    }

    public class ScheduledTripViewHolder extends RecyclerView.ViewHolder{
        public TextView ScheduledTripNameTextView;
        public TextView ScheduledTripDateTextView;
        public TextView ScheduledTripOptionsMenuTextView;
        public ImageView ScheduledTripImageView;

        public ScheduledTripViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            ScheduledTripNameTextView = (TextView)itemView.findViewById(R.id.ScheduledTripNameTextView);
            ScheduledTripDateTextView = (TextView)itemView.findViewById(R.id.ScheduledTripDateTextView);
            ScheduledTripOptionsMenuTextView = (TextView)itemView.findViewById(R.id.ScheduledTripOptionsTextView);
            ScheduledTripImageView = (ImageView)itemView.findViewById(R.id.ScheduledTripIconImageView);


        }
    }


}

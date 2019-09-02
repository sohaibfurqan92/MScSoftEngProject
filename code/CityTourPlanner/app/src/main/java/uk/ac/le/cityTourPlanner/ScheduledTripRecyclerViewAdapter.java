package uk.ac.le.cityTourPlanner;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                popupMenu.inflate(R.menu.scheduled_trips_context_menu);
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
                            case R.id.AssignStartTimeMenuItem:
                                String tripID = generatedTrip.getTripID();
                                setTripStartTime(tripID);
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

    private void setTripStartTime(final String tripID) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View tripSummaryDialog = layoutInflater.inflate(R.layout.time_picker_layout,null);
        final TimePicker timePicker = (TimePicker) tripSummaryDialog.findViewById(R.id.tripStartTimePicker);
        final TextView currentStartTimeTextView = tripSummaryDialog.findViewById(R.id.assignedTimeTextView);
       final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Trips/GeneralDetails/"+tripID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("tripStartTime").exists()){
                    currentStartTimeTextView.setText(dataSnapshot.child("tripStartTime").getValue(String.class));
                }
                else
                    currentStartTimeTextView.setText("Not assigned");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        timePicker.setIs24HourView(true);
        AlertDialog.Builder startTimePicker = new AlertDialog.Builder(mContext);
        startTimePicker.setView(tripSummaryDialog);
        startTimePicker.setCancelable(false)
                .setTitle("Select Start Time")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour, minute;
                        if(Build.VERSION.SDK_INT>=23){
                            hour=timePicker.getHour();
                            minute=timePicker.getMinute();
                        }
                        else{
                            hour=timePicker.getCurrentHour();
                            minute = timePicker.getCurrentMinute();
                        }
                        //Toast.makeText(mContext,"Time selected:\n "+hour+":"+minute,Toast.LENGTH_LONG).show();

                        reference.child("tripStartTime").setValue(hour+":"+minute);
                        ((Activity)mContext).finish();
                        Toast.makeText(mContext,"Start time set to "+hour+":"+minute,Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = startTimePicker.create();
        alertDialog.show();
    }


}

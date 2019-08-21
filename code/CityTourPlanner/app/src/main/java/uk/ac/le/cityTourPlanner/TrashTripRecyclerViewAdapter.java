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

import java.util.List;

public class TrashTripRecyclerViewAdapter extends RecyclerView.Adapter<TrashTripRecyclerViewAdapter.DeletedTripViewHolder> {

    private Context mContext;
    private List<GeneratedTrip> mDeletedTripList;

    public TrashTripRecyclerViewAdapter(Context context, List<GeneratedTrip> deletedTripList) {
        mContext = context;
        mDeletedTripList = deletedTripList;
    }

    @NonNull
    @Override
    public DeletedTripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trash_trip_item,viewGroup,false);
        return new TrashTripRecyclerViewAdapter.DeletedTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeletedTripViewHolder deletedTripViewHolder, int i) {

        final GeneratedTrip generatedTrip = mDeletedTripList.get(i);
        deletedTripViewHolder.TrashTripNameTextView.setText(generatedTrip.getTripName());
        deletedTripViewHolder.TrashTripDateTextView.setText(generatedTrip.getTripDate());

        deletedTripViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TripPlacesActivity.class);
                intent.putExtra("trip_id",generatedTrip.getTripID());
                mContext.startActivity(intent);
            }
        });

        deletedTripViewHolder.TrashTripOptionsMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an object of PopUpMenu
                PopupMenu popupMenu = new PopupMenu(mContext,deletedTripViewHolder.TrashTripOptionsMenuTextView);
                //inflate menu from xml resource
                popupMenu.inflate(R.menu.trash_trips_context_menu);
                //add click listener for individual menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.PermanentlyDeleteTripMenuItem:
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Are you sure you want to delete this trip?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String TripID = generatedTrip.getTripID();
                                                FirebaseDatabase.getInstance().getReference("Trips/GeneralDetails").child(TripID).removeValue();
                                                FirebaseDatabase.getInstance().getReference("Trips/SelectedPlaces").child(TripID).removeValue()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(mContext,"Trip permanently deleted!",Toast.LENGTH_LONG).show();
                                                                mDeletedTripList.remove(deletedTripViewHolder.getAdapterPosition());
                                                                notifyDataSetChanged();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(mContext,"Couldn't delete trip!",Toast.LENGTH_LONG).show();
                                                                Log.e("Error permanently del!", "onFailure:"+e);
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
        return mDeletedTripList.size();
    }

    public class DeletedTripViewHolder extends RecyclerView.ViewHolder{

        public TextView TrashTripNameTextView;
        public TextView TrashTripDateTextView;
        public TextView TrashTripOptionsMenuTextView;
        public ImageView TrashTripImageView;


        public DeletedTripViewHolder(@NonNull View itemView) {
            super(itemView);

            TrashTripNameTextView = (TextView)itemView.findViewById(R.id.TrashTripNameTextView);
            TrashTripDateTextView = (TextView)itemView.findViewById(R.id.TrashTripDateTextView);
            TrashTripOptionsMenuTextView = (TextView)itemView.findViewById(R.id.TrashTripOptionsTextView);
            TrashTripImageView = (ImageView)itemView.findViewById(R.id.TrashTripIconImageView);
        }
    }
}

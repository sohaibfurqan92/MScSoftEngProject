package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ItinararyAdapter extends RecyclerView.Adapter<ItinararyAdapter.ItinararyViewHolder> {

    private Context mContext;
    private List<SearchedPlacesItem> mPlaceList;

    public ItinararyAdapter(Context context, List<SearchedPlacesItem> placeList) {
        mContext = context;
        mPlaceList = placeList;
    }

    @NonNull
    @Override
    public ItinararyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chosen_places_item,viewGroup,false);
        return new ItinararyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItinararyViewHolder itinararyViewHolder, int i) {
        final SearchedPlacesItem selectedPlace = mPlaceList.get(i);
        String placeIconURL = selectedPlace.getIconURL();

        Picasso.with(mContext).load(placeIconURL).fit().centerInside().into(itinararyViewHolder.ChosenPlaceIconImageView);
        itinararyViewHolder.ChosenPlaceNameTextView.setText(selectedPlace.getPlaceName());
        itinararyViewHolder.ChosenPlaceDescTextView.setText(selectedPlace.getPlaceDesc());

        itinararyViewHolder.ChosenPlaceOptionsMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an object of PopUpMenu
                PopupMenu popupMenu = new PopupMenu(mContext,itinararyViewHolder.ChosenPlaceOptionsMenuTextView);
                //inflate menu from xml resource
                popupMenu.inflate(R.menu.selected_places_options_menu);
                //add click listener for individual menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.DeleteSelectedPlaceMenuItem:
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Are you sure you want to delete this place?")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                //delete from selected places
                                                //String TripID = selectedPlace.getTripID();
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
       return mPlaceList.size();
    }

    public class ItinararyViewHolder extends RecyclerView.ViewHolder{

        public TextView ChosenPlaceNameTextView;
        public TextView ChosenPlaceDescTextView;
        public TextView ChosenPlaceOptionsMenuTextView;
        public ImageView ChosenPlaceIconImageView;

        public ItinararyViewHolder(@NonNull View itemView) {
            super(itemView);

            ChosenPlaceNameTextView = (TextView)itemView.findViewById(R.id.chosenPlaceNameTextView);
            ChosenPlaceDescTextView = (TextView)itemView.findViewById(R.id.chosenPLaceDescTextView);
            ChosenPlaceOptionsMenuTextView = (TextView)itemView.findViewById(R.id.chosenPlaceOptionsTextView);
            ChosenPlaceIconImageView = (ImageView)itemView.findViewById(R.id.chosenPlaceIconImageView);


        }
    }
}

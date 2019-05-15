package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class searchedPlacesAdapter extends RecyclerView.Adapter<searchedPlacesAdapter.searchedPlacesViewHolder> {
    private Context mContext;
    private ArrayList<searchedPlacesItem> mPlacesList;

    public searchedPlacesAdapter(Context context, ArrayList<searchedPlacesItem> placesList ){
        mContext = context;
        mPlacesList = placesList;
    }

    @NonNull
    @Override
    public searchedPlacesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.searched_places_item,viewGroup,false);
        return new searchedPlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(searchedPlacesViewHolder searchedPlacesViewHolder, int i) {
        searchedPlacesItem searchedPlacesItem = mPlacesList.get(i);
        String placeName = searchedPlacesItem.getPlaceName();
        String placeDesc = searchedPlacesItem.getPlaceDesc();
        String placeIconURL = searchedPlacesItem.getIconURL();


        searchedPlacesViewHolder.mTextViewPlaceName.setText(placeName);
        searchedPlacesViewHolder.mTextViewPlaceDesc.setText(placeDesc);
        Picasso.with(mContext).load(placeIconURL).fit().centerInside().into(searchedPlacesViewHolder.mImageViewIcon);



    }

    @Override
    public int getItemCount() {
        return mPlacesList.size();
    }

    public class searchedPlacesViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextViewPlaceName;
        public TextView mTextViewPlaceDesc;
        public ImageView mImageViewIcon;


        public searchedPlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.icon_imageview);
            mTextViewPlaceName = itemView.findViewById(R.id.place_name_textview);
            mTextViewPlaceDesc = itemView.findViewById(R.id.place_summary_textview);
        }
    }
}

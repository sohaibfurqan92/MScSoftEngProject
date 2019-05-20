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


public class SearchedPlacesAdapter extends RecyclerView.Adapter<SearchedPlacesAdapter.searchedPlacesViewHolder> {
    private Context mContext;
    private ArrayList<SearchedPlacesItem> mPlacesList;
    private onItemClickListener mListener;

    public SearchedPlacesAdapter(Context context, ArrayList<SearchedPlacesItem> placesList ){
        mContext = context;
        mPlacesList = placesList;
    }

    public void SetOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public interface onItemClickListener{
        void onItemClicked(int position);
        void onAddClicked(int position);
    }

    @NonNull
    @Override
    public searchedPlacesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.searched_places_item,viewGroup,false);
        return new searchedPlacesViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(searchedPlacesViewHolder searchedPlacesViewHolder, int i) {
        SearchedPlacesItem searchedPlacesItem = mPlacesList.get(i);
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

    public static class searchedPlacesViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextViewPlaceName;
        public TextView mTextViewPlaceDesc;
        public ImageView mImageViewIcon;
        public ImageView mImageViewAddPlace;


        public searchedPlacesViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.icon_imageview);
            mTextViewPlaceName = itemView.findViewById(R.id.place_name_textview);
            mTextViewPlaceDesc = itemView.findViewById(R.id.place_summary_textview);
            mImageViewAddPlace= itemView.findViewById(R.id.add_place_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClicked(position);
                        }
                    }
                }
            });

            mImageViewAddPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}

package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SearchedPlacesAdapter extends RecyclerView.Adapter<SearchedPlacesAdapter.searchedPlacesViewHolder> {
    private Context mContext;
    private ArrayList<SearchedPlacesItem> mPlacesList;  //to hold list of all places
    private onItemClickListener mListener;

    public SearchedPlacesAdapter(Context context, ArrayList<SearchedPlacesItem> placesList ){
        mContext = context;
        mPlacesList = placesList;
    }

    public void SetOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public interface onItemClickListener{
        void onItemClick(int position);
        void onCheckboxClick(int position, CheckBox chk);
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
        public CheckBox mCheckBoxPlace;


        public searchedPlacesViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            mImageViewIcon = itemView.findViewById(R.id.icon_imageview);
            mTextViewPlaceName = itemView.findViewById(R.id.place_name_textview);
            mTextViewPlaceDesc = itemView.findViewById(R.id.place_summary_textview);
            mCheckBoxPlace= itemView.findViewById(R.id.placeCheckBox);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mCheckBoxPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            CheckBox checkBox = (CheckBox)v;
                            listener.onCheckboxClick(position,checkBox);
                        }
                    }
                }
            });

        }
    }
}

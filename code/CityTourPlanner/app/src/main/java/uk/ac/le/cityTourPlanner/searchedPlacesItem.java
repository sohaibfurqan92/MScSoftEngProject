package uk.ac.le.cityTourPlanner;

public class SearchedPlacesItem {
    private String mPlaceID;
    private String mPlaceName;
    private String mPlaceDesc;
    private String mIconURL;

    public SearchedPlacesItem(String placeName, String placeDescription, String iconURL, String placeID){
        mPlaceID = placeID;
        mPlaceName=placeName;
        mPlaceDesc=placeDescription;
        mIconURL = iconURL;
    }

    public String getPlaceName() {
        return mPlaceName;
    }


    public String getPlaceDesc() {
        return mPlaceDesc;
    }

    public String getIconURL() { return mIconURL; }

    public String getPlaceID(){return mPlaceID;}

}

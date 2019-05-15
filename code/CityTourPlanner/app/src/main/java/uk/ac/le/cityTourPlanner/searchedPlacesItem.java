package uk.ac.le.cityTourPlanner;

public class searchedPlacesItem {
    private String mPlaceName;
    private String mPlaceDesc;
    private String mIconURL;

    public searchedPlacesItem(String placeName, String placeDescription, String iconURL){
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
}

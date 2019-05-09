package uk.ac.le.cityTourPlanner;

public class searchedPlacesItem {
    private String mPlaceName;
    private String mPlaceDesc;

    public searchedPlacesItem(String placeName, String placeDescription){
        mPlaceName=placeName;
        mPlaceDesc=placeDescription;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String placeName) {
        mPlaceName = placeName;
    }

    public String getPlaceDesc() {
        return mPlaceDesc;
    }

    public void setPlaceDesc(String placeDesc) {
        mPlaceDesc = placeDesc;
    }
}

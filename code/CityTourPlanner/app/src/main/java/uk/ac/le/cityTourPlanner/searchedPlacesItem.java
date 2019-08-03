package uk.ac.le.cityTourPlanner;

public class SearchedPlacesItem {

        private String placeID;
        private String placeName;
        private String placeDesc;
        private String iconURL;

    public SearchedPlacesItem() {
    }

    public SearchedPlacesItem(String placeName, String placeDescription, String iconURL, String placeID){
            this.placeID = placeID;
            this.placeName =placeName;
            placeDesc =placeDescription;
            this.iconURL = iconURL;
        }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceDesc() {
        return placeDesc;
    }

    public void setPlaceDesc(String placeDesc) {
        this.placeDesc = placeDesc;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }
}



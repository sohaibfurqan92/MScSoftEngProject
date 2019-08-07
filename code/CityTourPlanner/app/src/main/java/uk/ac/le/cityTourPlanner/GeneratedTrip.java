package uk.ac.le.cityTourPlanner;

import java.util.List;

//POJO class for inserting trip data to firebase

/*
* place object = id, latlng,name
* */
public class GeneratedTrip {
    private String cityID;
    private double cityLatitude;
    private double cityLongitude;
    private String cityName;
    private String placeIconURL;
    private String TripID;
    private String tripName;
    private String tripDate;
    private String createdBy;
    private List<SearchedPlacesItem> SelectedPlaces;
    private String tripStatus;



    public GeneratedTrip(String cityID, double cityLatitude, double cityLongitude, String cityName, String tripName, String tripDate, String createdBy, List<SearchedPlacesItem> selectedPlaces, String tripStatus) {
        this.cityID = cityID;
        this.cityLatitude = cityLatitude;
        this.cityLongitude = cityLongitude;
        this.cityName = cityName;
        this.tripName = tripName;
        this.tripDate = tripDate;
        this.createdBy = createdBy;
        SelectedPlaces = selectedPlaces;
        this.tripStatus =tripStatus;
    }

    public GeneratedTrip(){}

    public String getTripID() {
        return TripID;
    }

    public void setTripID(String tripID) {
        TripID = tripID;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setSelectedPlaces(List<SearchedPlacesItem> selectedPlaces) {
        SelectedPlaces = selectedPlaces;
    }


    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }


    public String getTripName() {
        return tripName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public List<SearchedPlacesItem> getSelectedPlaces() {
        return SelectedPlaces;
    }

    public String getTripStatus() {
        return tripStatus;
    }


    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public double getCityLatitude() {
        return cityLatitude;
    }

    public void setCityLatitude(double cityLatitude) {
        this.cityLatitude = cityLatitude;
    }

    public double getCityLongitude() {
        return cityLongitude;
    }

    public void setCityLongitude(double cityLongitude) {
        this.cityLongitude = cityLongitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GeneratedTrip){
            GeneratedTrip trip = (GeneratedTrip) obj;
            return this.TripID.equals(trip.getTripID());
        }
        else
            return false;
    }

    public String getPlaceIconURL() {
        return placeIconURL;
    }

    public void setPlaceIconURL(String placeIconURL) {
        this.placeIconURL = placeIconURL;
    }
}

package uk.ac.le.cityTourPlanner;

import java.util.List;

//POJO class for inserting trip data to firebase

/*
* place object = id, latlng,name
* */
public class GeneratedTrip {
    private String CityID;
    private double CityLatitude;
    private double CityLongitude;
    private String CityName;

    private String TripID;
    private String tripName;
    private String tripDate;
    private String CreatedBy;
    private List<SearchedPlacesItem> SelectedPlaces;
    private String TripStatus;

    public GeneratedTrip(String cityID, double cityLatitude, double cityLongitude, String cityName, String tripName, String tripDate, String createdBy, List<SearchedPlacesItem> selectedPlaces, String tripStatus) {
        CityID = cityID;
        CityLatitude = cityLatitude;
        CityLongitude = cityLongitude;
        CityName = cityName;
        this.tripName = tripName;
        this.tripDate = tripDate;
        CreatedBy = createdBy;
        SelectedPlaces = selectedPlaces;
        TripStatus=tripStatus;
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
        CreatedBy = createdBy;
    }

    public void setSelectedPlaces(List<SearchedPlacesItem> selectedPlaces) {
        SelectedPlaces = selectedPlaces;
    }


    public void setTripStatus(String tripStatus) {
        TripStatus = tripStatus;
    }


    public String getTripName() {
        return tripName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public List<SearchedPlacesItem> getSelectedPlaces() {
        return SelectedPlaces;
    }

    public String getTripStatus() {
        return TripStatus;
    }

    public String getCityID() {
        return CityID;
    }

    public void setCityID(String cityID) {
        CityID = cityID;
    }

    public double getCityLatitude() {
        return CityLatitude;
    }

    public void setCityLatitude(double cityLatitude) {
        CityLatitude = cityLatitude;
    }

    public double getCityLongitude() {
        return CityLongitude;
    }

    public void setCityLongitude(double cityLongitude) {
        CityLongitude = cityLongitude;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
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
}

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


    private String TripName;
    private String TripDate;
    private String CreatedBy;
    private List<SearchedPlacesItem> SelectedPlaces;
    private String TripStatus;

    public GeneratedTrip(String cityID, double cityLatitude, double cityLongitude, String cityName, String tripName, String tripDate, String createdBy, List<SearchedPlacesItem> selectedPlaces, String tripStatus) {
        CityID = cityID;
        CityLatitude = cityLatitude;
        CityLongitude = cityLongitude;
        CityName = cityName;
        TripName = tripName;
        TripDate = tripDate;
        CreatedBy = createdBy;
        SelectedPlaces = selectedPlaces;
        TripStatus=tripStatus;
    }

    public GeneratedTrip(){}


    public void setTripName(String tripName) {
        TripName = tripName;
    }


    public void setTripDate(String tripDate) {
        TripDate = tripDate;
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
        return TripName;
    }

    public String getTripDate() {
        return TripDate;
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
}

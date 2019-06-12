package uk.ac.le.cityTourPlanner;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

//POJO class for inserting trip data to firebase

public class GeneratedTrip {
    private String TripName;
    private String TripDate;
    private String CreatedBy;
    private List<SearchedPlacesItem> SelectedPlaces;
    private Place CityPlaceObject;
    private String TripStatus;

    public GeneratedTrip(String tripName, String tripDate, String createdBy, List<SearchedPlacesItem> selectedPlaces, Place place,String tripStatus) {
        TripName = tripName;
        TripDate = tripDate;
        CreatedBy = createdBy;
        SelectedPlaces = selectedPlaces;
        CityPlaceObject = place;
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

    public void setCityPlaceObject(Place placeObject) {
        CityPlaceObject = placeObject;
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

    public Place getCityPlaceObject() {
        return CityPlaceObject;
    }

    public String getTripStatus() {
        return TripStatus;
    }


}

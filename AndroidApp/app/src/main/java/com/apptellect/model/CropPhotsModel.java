package com.apptellect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Karthi on 9/20/2018.
 */

public class CropPhotsModel implements Serializable {

    @SerializedName("photoCoordinates")
    @Expose
    private PhotoCoordinates photoCoordinates;

    @SerializedName("location")
    @Expose
    private String location;

    public PhotoCoordinates getPhotoCoordinates() {
        return photoCoordinates;
    }

    public void setPhotoCoordinates(PhotoCoordinates photoCoordinates) {
        this.photoCoordinates = photoCoordinates;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

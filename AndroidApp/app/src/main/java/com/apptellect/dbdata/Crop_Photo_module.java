package com.apptellect.dbdata;

import android.graphics.Bitmap;

/**
 * Created by Karthi on 9/21/2018.
 */

public class Crop_Photo_module {
    private String cropphotoid;
    private String cropid;
    private String photolatitude;
    private String photolongitude;
    private Bitmap bitmap;
    private String date;

    public Crop_Photo_module() {
    }

    public Crop_Photo_module(String cropphotoid, String cropid, String croplatitude, String croplongitude, Bitmap bitmap) {
        this.cropphotoid = cropphotoid;
        this.cropid = cropid;
        this.photolatitude = photolatitude;
        this.photolongitude = photolongitude;
        this.bitmap = bitmap;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getCropphotoid() {
        return cropphotoid;
    }

    public void setCropphotoid(String cropphotoid) {
        this.cropphotoid = cropphotoid;
    }

    public String getCropid() {
        return cropid;
    }

    public void setCropid(String cropid) {
        this.cropid = cropid;
    }

    public String getPhotolatitude() {
        return photolatitude;
    }

    public void setPhotolatitude(String photolatitude) {
        this.photolatitude = photolatitude;
    }

    public String getPhotolongitude() {
        return photolongitude;
    }

    public void setPhotolongitude(String photolongitude) {
        this.photolongitude = photolongitude;
    }


}

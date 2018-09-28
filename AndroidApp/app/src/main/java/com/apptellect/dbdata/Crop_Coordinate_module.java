package com.apptellect.dbdata;

/**
 * Created by Karthi on 9/21/2018.
 */

public class Crop_Coordinate_module
{
    private String cropcoorid;
    private String cropid;
    private String croplatitude;
    private String croplongitude;

    public Crop_Coordinate_module()
    {

    }

    public Crop_Coordinate_module(String cropcoorid, String cropid, String croplatitude, String croplongitude)
    {
        this.cropcoorid = cropcoorid;
        this.cropid = cropid;
        this.croplatitude = croplatitude;
        this.croplongitude = croplongitude;
    }


    public String getCropcoorid() {
        return cropcoorid;
    }

    public void setCropcoorid(String cropcoorid) {
        this.cropcoorid = cropcoorid;
    }

    public String getCropid() {
        return cropid;
    }

    public void setCropid(String cropid) {
        this.cropid = cropid;
    }

    public String getCroplatitude() {
        return croplatitude;
    }

    public void setCroplatitude(String croplatitude) {
        this.croplatitude = croplatitude;
    }

    public String getCroplongitude() {
        return croplongitude;
    }

    public void setCroplongitude(String croplongitude) {
        this.croplongitude = croplongitude;
    }

}

package com.apptellect.dbdata;

/**
 * Created by Karthi on 9/21/2018.
 */

public class CropTable
{
    private String cropid;
    private String croparea;
    private String cropcurrency;
    private String cropsubtype;
    private String farmerowner;
    private String harvestdate;
    private String maxyeildvalue;
    private String seedingdate;
    private String statue;
    private String typeofcrop;

    public CropTable()
    {

    }

    public CropTable(String cropid,String croparea,String cropcurrenc,String cropsubtype,String farmerowner,String harvestdate,String maxyeildvalue,String seedingdate,String statue,String typeofcrop)
    {
        this.cropid = cropid;
        this.croparea = croparea;
        this.cropcurrency = cropcurrency;
        this.cropsubtype = cropsubtype;
        this.farmerowner = farmerowner;
        this.harvestdate = harvestdate;
        this.maxyeildvalue = maxyeildvalue;
        this.seedingdate = seedingdate;
        this.statue = statue;
        this.typeofcrop = typeofcrop;

    }

    public String getCropid() {
        return cropid;
    }

    public void setCropid(String cropid) {
        this.cropid = cropid;
    }

    public String getCroparea() {
        return croparea;
    }

    public void setCroparea(String croparea) {
        this.croparea = croparea;
    }

    public String getCropcurrency() {
        return cropcurrency;
    }

    public void setCropcurrency(String cropcurrency) {
        this.cropcurrency = cropcurrency;
    }

    public String getCropsubtype() {
        return cropsubtype;
    }

    public void setCropsubtype(String cropsubtype) {
        this.cropsubtype = cropsubtype;
    }

    public String getFarmerowner() {
        return farmerowner;
    }

    public void setFarmerowner(String farmerowner) {
        this.farmerowner = farmerowner;
    }

    public String getHarvestdate() {
        return harvestdate;
    }

    public void setHarvestdate(String harvestdate) {
        this.harvestdate = harvestdate;
    }

    public String getMaxyeildvalue() {
        return maxyeildvalue;
    }

    public void setMaxyeildvalue(String maxyeildvalue) {
        this.maxyeildvalue = maxyeildvalue;
    }

    public String getSeedingdate() {
        return seedingdate;
    }

    public void setSeedingdate(String seedingdate) {
        this.seedingdate = seedingdate;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public String getTypeofcrop() {
        return typeofcrop;
    }

    public void setTypeofcrop(String typeofcrop) {
        this.typeofcrop = typeofcrop;
    }
}

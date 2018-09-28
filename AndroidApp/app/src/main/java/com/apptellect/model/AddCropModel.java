package com.apptellect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddCropModel implements Serializable{
    @SerializedName("cropId")
    @Expose
    private String cropId;
    @SerializedName("farmerOwner")
    @Expose
    private String farmerOwner;
    @SerializedName("typeOfCrop")
    @Expose
    private String typeOfCrop;
    @SerializedName("cropSubType")
    @Expose
    private String cropSubType;
    @SerializedName("cropArea")
    @Expose
    private String cropArea;
    @SerializedName("maxYieldValue")
    @Expose
    private String maxYieldValue;

    @SerializedName("cropCurrency")
    @Expose
    private String cropCurrency;
    @SerializedName("seedingDate")
    @Expose
    private String seedingDate;
    @SerializedName("harvestDate")
    @Expose
    private String harvestDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("location")
    @Expose
    private String location;
//    @SerializedName("farmerOwner")
//    @Expose
//    private FarmerModel farmerOwner;

    @SerializedName("cropCoordinates")
    @Expose
    private ArrayList<CropCoordinates> arrayList_cropCoordinates;

    @SerializedName("precipitationHistory")
    @Expose
    private ArrayList<PrecipitationHistory> arrayList_precipitationHistory;

    @SerializedName("cropPledges")
    @Expose
    private ArrayList<CropPledges> arrayList_cropPledges;


    @SerializedName("cropPhotos")
    @Expose
    private ArrayList<CropPhotsModel> arrayList_cropPhotos;

    public String getFarmerOwner() {
        return farmerOwner;
    }

    public void setFarmerOwner(String farmerOwner) {
        this.farmerOwner = farmerOwner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<CropPhotsModel> getArrayList_cropPhotos() {
        return arrayList_cropPhotos;
    }

    public void setArrayList_cropPhotos(ArrayList<CropPhotsModel> arrayList_cropPhotos) {
        this.arrayList_cropPhotos = arrayList_cropPhotos;
    }

    public ArrayList<PrecipitationHistory> getArrayList_precipitationHistory() {
        return arrayList_precipitationHistory;
    }

    public void setArrayList_precipitationHistory(ArrayList<PrecipitationHistory> arrayList_precipitationHistory) {
        this.arrayList_precipitationHistory = arrayList_precipitationHistory;
    }

    public ArrayList<CropPledges> getArrayList_cropPledges() {
        return arrayList_cropPledges;
    }

    public void setArrayList_cropPledges(ArrayList<CropPledges> arrayList_cropPledges) {
        this.arrayList_cropPledges = arrayList_cropPledges;
    }

    public ArrayList<CropCoordinates> getArrayList_cropCoordinates() {
        return arrayList_cropCoordinates;
    }

    public void setArrayList_cropCoordinates(ArrayList<CropCoordinates> arrayList_cropCoordinates) {
        this.arrayList_cropCoordinates = arrayList_cropCoordinates;
    }


    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getTypeOfCrop() {
        return typeOfCrop;
    }

    public void setTypeOfCrop(String typeOfCrop) {
        this.typeOfCrop = typeOfCrop;
    }

    public String getCropSubType() {
        return cropSubType;
    }

    public void setCropSubType(String cropSubType) {
        this.cropSubType = cropSubType;
    }

    public String getCropArea() {
        return cropArea;
    }

    public void setCropArea(String cropArea) {
        this.cropArea = cropArea;
    }

    public String getMaxYieldValue() {
        return maxYieldValue;
    }

    public void setMaxYieldValue(String maxYieldValue) {
        this.maxYieldValue = maxYieldValue;
    }

    public String getCropCurrency() {
        return cropCurrency;
    }

    public void setCropCurrency(String cropCurrency) {
        this.cropCurrency = cropCurrency;
    }

    public String getSeedingDate() {
        return seedingDate;
    }

    public void setSeedingDate(String seedingDate) {
        this.seedingDate = seedingDate;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
//    public FarmerModel getFarmerOwner() {
//        return farmerOwner;
//    }
//
//    public void setFarmerOwner(FarmerModel farmerOwner) {
//        this.farmerOwner = farmerOwner;
//    }
}

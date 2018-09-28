package com.apptellect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CropModel {
    @SerializedName("cropId")
    @Expose
    private String cropId;
    @SerializedName("typeOfCrop")
    @Expose
    private String typeOfCrop;
    @SerializedName("farmer")
    @Expose
    private String farmer;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;



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

    public String getFarmer() {
        return farmer;
    }

    public void setFarmer(String farmer) {
        this.farmer = farmer;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

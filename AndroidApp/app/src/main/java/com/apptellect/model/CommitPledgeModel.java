package com.apptellect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommitPledgeModel {
    @SerializedName("pledgeId")
    @Expose
    private String pledgeId;
    @SerializedName("cropId")
    @Expose
    private String cropId;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public String getPledgeId() {
        return pledgeId;
    }

    public void setPledgeId(String pledgeId) {
        this.pledgeId = pledgeId;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
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

package com.apptellect.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PledgeModel{
        @SerializedName("pledgeId")
        @Expose
        private String pledgeId;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("pledgeCurrency")
        @Expose
        private String pledgeCurrency;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("donor")
        @Expose
        private String donor;


        public String getPledgeId() {
            return pledgeId;
        }

        public void setPledgeId(String pledgeId) {
            this.pledgeId = pledgeId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getPledgeCurrency() {
            return pledgeCurrency;
        }

        public void setPledgeCurrency(String pledgeCurrency) {
            this.pledgeCurrency = pledgeCurrency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDonor() {
            return donor;
        }

        public void setDonor(String donor) {
            this.donor = donor;
        }
}

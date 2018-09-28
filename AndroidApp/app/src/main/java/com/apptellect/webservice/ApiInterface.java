package com.apptellect.webservice;

import com.apptellect.model.AddCropModel;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.model.CommitPledgeModel;
import com.apptellect.model.DonorModel;
import com.apptellect.model.FarmerModel;
import com.apptellect.model.PledgeModel;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {


    @POST("/api/Farmer")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<FarmerModel> createFarmer(@Body FarmerModel farmerModel);

    @POST("/api/Donor")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<DonorModel> createDonor(@Body DonorModel donorModel);

//    @GET("/api/tx_createNewCrop")
//    Call<List<CropModel>> getListOfCrop();


    @POST("/api/Crop")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AddCropModel> createnewcrop(@Body AddCropModel addNewCropModel);

    @PUT("/api/Crop/{id}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AddCropModel> updatecrop(@Path("id")String cropID,@Body AddCropModel addNewCropModel);

    @GET("/api/Crop/{id}")
    Call<List<AddNewCropModel>> getListOfCropsId(@Path("id")String farmerId);

    @GET("/api/Crop")
    Call<List<AddNewCropModel>> getListOfAllCrops();

    @GET("/api/Crop/{id}")
    Call<AddNewCropModel> getCropDetails(@Path("id")String cropId);

    @GET("/api/Crop")
    Call<List<AddNewCropModel>> getListOfCropsByFarmerID(@Query("filter") String filter);

    @POST("/api/Pledge")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<PledgeModel> createPledge(@Body PledgeModel pledgeModel);

    @GET("/api/Donor")
    Call<List<DonorModel>> getListOfDonor();
    @GET("/api/Farmer/{id}")
    Call<FarmerModel> getFarmerDetails(@Path("id")String farmerId);

    @GET("/api/Donor/{id}")
    Call<DonorModel> getDonorDetails(@Path("id")String donorId);

    @PUT("/api/Farmer/{id}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<FarmerModel> updateFarmer(@Body FarmerModel farmerModel,@Path("id")String farmerId);

    @PUT("/api/Donor/{id}")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<DonorModel> updateDonor(@Body DonorModel donorModel,@Path("id")String donorId);

    @GET("/api/Pledge")
    Call<List<PledgeModel>> getListOfPledgeDetails(@Query("filter") String filter);

    @GET("/api/Donor/{id}")
    Call<List<DonorModel>> getListOfDonorId(@Path("id")String donorId);

    @GET("/api/Pledge/{id}")
    Call<PledgeModel> getPledgeIdDetails(@Path("id")String pledgeId);

    @PUT("/api/Pledge/{id}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<PledgeModel> updatePledge(@Body PledgeModel pledgeModel,@Path("id")String pledgeId);

    @POST("/api/tx_commitPledgeToCrop")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CommitPledgeModel> createCommitPledge(@Body CommitPledgeModel commitPledgeModel);

    @PUT("/api/Pledge/{id}")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CommitPledgeModel> updateCommitPledge(@Body CommitPledgeModel commitPledgeModel,@Path("id")String pledgeId);

    @GET("/api/tx_commitPledgeToCrop/{id}")
    Call<CommitPledgeModel> getCommitPledgeID(@Path("id")String transactionId);

    @GET("/api/tx_commitPledgeToCrop")
    Call<List<CommitPledgeModel>> getCommitPledge();


}

package com.apptellect.webservice;

import com.apptellect.model.DonorModel;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TwilioApi {

    @FormUrlEncoded
    @POST("/protected/json/phones/verification/start")
    Call<ResponseBody> sendMessage(
            @Header("X-Authy-API-Key") String apikey,
            @Field("phone_number") String phoneNo,
            @Field("country_code") String countryCode,
            @Field("via") String via
    );

    @GET("/protected/json/phones/verification/check")
    Call<ResponseBody> checkVerificationCode(@Query("api_key") String apikey,@Query("verification_code") String verificationCode,
                                             @Query("phone_number") String phoneNumber,@Query("country_code") String country_code);
}

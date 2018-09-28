package com.apptellect.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apptellect.R;
import com.apptellect.model.DonorModel;
import com.apptellect.model.FarmerModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;
import com.apptellect.webservice.TwilioApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VerificationCodeActivity extends AppCompatActivity {
    boolean isProfileDetailsContain = false, isFailed = false;
    private EditText edittextCode;
    private TextView txtMsg, txtTimer;
    private String mRole = "", mMobileNumber = "", mCountryCode = "";
    private Button btnSubmit, btnEditNo;
    private Utils mUtils;
    private ApiInterface mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verification_code);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        txtMsg = findViewById(R.id.txt_msg);
        txtTimer = findViewById(R.id.txt_timer);
        edittextCode = findViewById(R.id.edittext_code);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnEditNo = findViewById(R.id.btnEditNo);
        if (getIntent().getStringExtra("MOBILE_NO") != null) {
            txtMsg.setText("Please type the verification " + System.getProperty("line.separator") + "code sent to " + getIntent().getStringExtra("MOBILE_NO"));
        }
        if (getIntent().getStringExtra("NUMBER") != null) {
            mMobileNumber = getIntent().getStringExtra("NUMBER");
        }
        if (getIntent().getStringExtra("COUNTRY_CODE") != null) {
            mCountryCode = getIntent().getStringExtra("COUNTRY_CODE");
        }
        if (getIntent().getStringExtra("ROLE") != null) {
            mRole = getIntent().getStringExtra("ROLE");
        }
        //Timer for enter verification code
        new CountDownTimer(40000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                isFailed = true;
                txtTimer.setVisibility(View.GONE);
                edittextCode.setFocusable(false);
                edittextCode.setVisibility(View.GONE);
                btnSubmit.setText("Send Code Again");
            }

        }.start();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFailed) {
                    if (isValidation()) {
                        checkVerificationCode(mMobileNumber, mCountryCode, edittextCode.getText().toString().trim());
                    }
                } else {
                    finish();
                }

            }
        });
        btnEditNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    //check the profile details already register
    public boolean getProfileDetails() {
        if (mUtils.getRoleType().equalsIgnoreCase("Farmer")) {
            Call<FarmerModel> call = mApiService.getFarmerDetails(mUtils.getMobileNo());
            call.enqueue(new Callback<FarmerModel>() {
                @Override
                public void onResponse(Call<FarmerModel> call, Response<FarmerModel> response) {
                    FarmerModel farmerModel = response.body();
                    if (farmerModel != null) {
                        isProfileDetailsContain = true;
                    }


                }

                @Override
                public void onFailure(Call<FarmerModel> call, Throwable t) {
                }
            });
        } else if (mUtils.getRoleType().equalsIgnoreCase("Donor")) {
            Call<DonorModel> call = mApiService.getDonorDetails(mUtils.getMobileNo());
            call.enqueue(new Callback<DonorModel>() {
                @Override
                public void onResponse(Call<DonorModel> call, Response<DonorModel> response) {
                    DonorModel donorModel = response.body();
                    if (donorModel != null) {
                        isProfileDetailsContain = true;
                    }
                }

                @Override
                public void onFailure(Call<DonorModel> call, Throwable t) {
                }
            });
        }
        return isProfileDetailsContain;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileDetails();
    }

    //check the validation
    public boolean isValidation() {
        if (edittextCode.getText().toString().trim().length() == 0) {
            Toast.makeText(VerificationCodeActivity.this, "Please enter a verification code", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edittextCode.getText().toString().trim().length() < 4) {
            Toast.makeText(VerificationCodeActivity.this, "Please enter a correct verification code", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void checkVerificationCode(String phoneNo, String countryCode, String verificationCode) {
        mUtils.ShowDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.authy.com")
                .build();
        TwilioApi api = retrofit.create(TwilioApi.class);
        api.checkVerificationCode(Utils.TwilioAPIKEY, verificationCode, phoneNo, countryCode).enqueue(new Callback<ResponseBody>() {
            public AlertDialog alertDialog;

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mUtils.DismissDialog();
                if (response.isSuccessful()) {
                    if (isProfileDetailsContain) {
                        mUtils.setIsRegister(true);
                        if (mUtils.getRoleType().equalsIgnoreCase("Farmer")) {
                            Intent intentCropMain = new Intent(VerificationCodeActivity.this, CropMainActivity.class);
                            intentCropMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentCropMain);
                        } else if (mUtils.getRoleType().equalsIgnoreCase("Donor")) {
                            Intent intentDonor = new Intent(VerificationCodeActivity.this, DonorMainActivity.class);
                            intentDonor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentDonor);
                        }
                    } else {
                        Intent intent = new Intent(VerificationCodeActivity.this, RegistrationActivity.class);
                        intent.putExtra("ROLE", mRole);
                        startActivity(intent);
                    }
                } else {
                    alertDialog = new AlertDialog.Builder(VerificationCodeActivity.this)
                            .setTitle("Alert !")
                            .setMessage("Please enter correct code")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();

                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error-->", "" + t.toString());
                mUtils.DismissDialog();
            }
        });
    }
}

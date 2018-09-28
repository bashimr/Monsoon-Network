package com.apptellect.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.model.ContactInfo;
import com.apptellect.model.DonorModel;
import com.apptellect.model.FarmerModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private EditText edittextFirstName, edittextLastName, edittextEmailId, edittextMiddleName,
            edittextAddress, edittextCity, edittextAddress2;
    private Button btnSubmit, btnClear;
    private Spinner spinnerCountry, spinnerDonorType;
    private Utils mUtils;
    private ApiInterface mApiService;
    private LinearLayout linearContainerDonor, linearNameContainer;
    private String mRole="",mCountry="",mDonorType="";
    private boolean isProfileEdit =false;
     String[] country=null,donorType=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils=new Utils(this);
        edittextFirstName =findViewById(R.id.edittext_FirstName);
        edittextMiddleName =findViewById(R.id.edittext_middlename);
        edittextLastName =findViewById(R.id.edittext_LastName);
        edittextEmailId =findViewById(R.id.edittext_email_id);
        edittextAddress =findViewById(R.id.edittext_address);
        edittextAddress2 =findViewById(R.id.edittext_address2);
        edittextCity =findViewById(R.id.edittext_city);
        spinnerDonorType =findViewById(R.id.spinner_donor_type);
        spinnerCountry =findViewById(R.id.spinner_country);
        btnSubmit =findViewById(R.id.btnSubmit);
        btnClear =findViewById(R.id.btnClear);
        linearContainerDonor =findViewById(R.id.linear_container_donor);
        linearNameContainer =findViewById(R.id.linear_name_container);
        if(mUtils.getRoleType()!=null){
            mRole=mUtils.getRoleType();
            if(mUtils.getRoleType().equalsIgnoreCase("Farmer")){
                linearContainerDonor.setVisibility(View.GONE);
                linearNameContainer.setVisibility(View.VISIBLE);
            }else{
                linearContainerDonor.setVisibility(View.VISIBLE);
                linearNameContainer.setVisibility(View.GONE);
            }
        }
        if(getIntent().getBooleanExtra("IS_PROFILE_EDIT",false)){
            isProfileEdit =getIntent().getBooleanExtra("IS_PROFILE_EDIT",false);
            btnSubmit.setText("Update");
            getProfileDetails();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dropdownDataLoad();
            }
        }, 100);


        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittextFirstName.setText("");
                edittextLastName.setText("");
                edittextMiddleName.setText("");
                edittextAddress.setText("");
                edittextCity.setText("");
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUtils.checkConnection(RegistrationActivity.this)){

                        mUtils.setIsRegister(true);
                            if(mRole.equalsIgnoreCase("Farmer")){
                                if(isValidation()) {
                                    mUtils.ShowDialog();
                                    FarmerModel farmerModel = new FarmerModel();
                                    farmerModel.setFarmerId(mUtils.getMobileNo());
                                    farmerModel.setFirstName(edittextFirstName.getText().toString());
                                    List<String> listMiddle = Arrays.asList(edittextMiddleName.getText().toString().trim());
                                    farmerModel.setMiddleName(listMiddle);
                                    farmerModel.setLastName(edittextLastName.getText().toString().trim());
                                    ContactInfo contactInfo = new ContactInfo();
                                    contactInfo.setEmail(edittextEmailId.getText().toString().trim());
                                    contactInfo.setStreet1(edittextAddress.getText().toString().trim());
                                    contactInfo.setStreet2(edittextAddress2.getText().toString().trim());
                                    contactInfo.setCity(edittextCity.getText().toString().trim());
                                    contactInfo.setCountry(spinnerCountry.getSelectedItem().toString());
                                    contactInfo.setPhoneNumber(mUtils.getMobileNo());
                                    farmerModel.setContactInfo(contactInfo);

                                    if (isProfileEdit) {
                                        Call<FarmerModel> call = mApiService.updateFarmer(farmerModel, mUtils.getMobileNo());
                                        call.enqueue(new Callback<FarmerModel>() {
                                            @Override
                                            public void onResponse(Call<FarmerModel> call, Response<FarmerModel> response) {
                                                mUtils.DismissDialog();
                                                if (response.code() == 200) {
                                                    Intent intentCropMain = new Intent(RegistrationActivity.this, CropMainActivity.class);
                                                    startActivity(intentCropMain);
                                                    finish();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<FarmerModel> call, Throwable t) {
                                                Log.d("Result Error-->", "" + t.toString());
                                                mUtils.DismissDialog();
                                            }
                                        });
                                    } else {
                                        Call<FarmerModel> call = mApiService.createFarmer(farmerModel);
                                        call.enqueue(new Callback<FarmerModel>() {
                                            @Override
                                            public void onResponse(Call<FarmerModel> call, Response<FarmerModel> response) {
                                                mUtils.DismissDialog();
                                                if (response.code() == 200) {
                                                    Intent intentCropMain = new Intent(RegistrationActivity.this, CropMainActivity.class);
                                                    intentCropMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intentCropMain);
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<FarmerModel> call, Throwable t) {
                                                Log.d("Result Error-->", "" + t.toString());
                                                mUtils.DismissDialog();
                                            }
                                        });
                                    }
                                }

                            }else {
                                if (isDonorValidation()) {
                                    mUtils.ShowDialog();
                                    DonorModel donorModel = new DonorModel();
                                    donorModel.setDonorId(mUtils.getMobileNo());
                                    donorModel.setDonorType(spinnerDonorType.getSelectedItem().toString());
                                    ContactInfo contactInfo = new ContactInfo();
                                    contactInfo.setEmail(edittextEmailId.getText().toString().trim());
                                    contactInfo.setStreet1(edittextAddress.getText().toString().trim());
                                    contactInfo.setStreet2(edittextAddress2.getText().toString().trim());
                                    contactInfo.setCity(edittextCity.getText().toString().trim());
                                    contactInfo.setCountry(spinnerCountry.getSelectedItem().toString());
                                    contactInfo.setPhoneNumber(mUtils.getMobileNo());
                                    donorModel.setContact(contactInfo);
                                    mUtils.setDonorType(spinnerDonorType.getSelectedItem().toString());
                                    if (isProfileEdit) {
                                        Call<DonorModel> donorModelCall = mApiService.updateDonor(donorModel, mUtils.getMobileNo());
                                        donorModelCall.enqueue(new Callback<DonorModel>() {
                                            @Override
                                            public void onResponse(Call<DonorModel> call, Response<DonorModel> response) {

                                                mUtils.DismissDialog();
                                                if (response.code() == 200) {
                                                    Intent intentDonor = new Intent(RegistrationActivity.this, DonorMainActivity.class);
                                                    intentDonor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intentDonor);
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<DonorModel> call, Throwable t) {
                                                Log.d("Result Error-->", "" + t.toString());
                                                mUtils.DismissDialog();
                                            }
                                        });
                                    } else {
                                        Call<DonorModel> donorModelCall = mApiService.createDonor(donorModel);
                                        donorModelCall.enqueue(new Callback<DonorModel>() {
                                            @Override
                                            public void onResponse(Call<DonorModel> call, Response<DonorModel> response) {
                                                mUtils.DismissDialog();
                                                if (response.code() == 200) {
                                                    Intent intentDonor = new Intent(RegistrationActivity.this, CriteriaSelectionActivity.class);
                                                    intentDonor.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intentDonor);
                                                }

                                            }
                                            @Override
                                            public void onFailure(Call<DonorModel> call, Throwable t) {
                                                Log.d("Result Error-->", "" + t.toString());
                                                mUtils.DismissDialog();
                                            }
                                        });
                                    }

                                }
                            }
                }else{
                    mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
                }



            }
        });
    }
    public boolean isValidation(){
        if(edittextFirstName.getText().toString().equals("")){
            edittextFirstName.setError("Please enter a first name");
            edittextFirstName.requestFocus();
            return false;

        }else if(edittextLastName.getText().toString().trim().length()==0){

            edittextLastName.setError("Please enter a last name");
            edittextLastName.requestFocus();
            return false;
        }else if(!isValidEmail(edittextEmailId.getText().toString().trim())){
            edittextEmailId.setError("Please enter a correct email");
            edittextEmailId.requestFocus();
            return false;
        }
        return true;
    }
    public boolean isDonorValidation(){
        if(!isValidEmail(edittextEmailId.getText().toString().trim())){
            edittextEmailId.setError("Please enter a correct email");
            edittextEmailId.requestFocus();
            return false;
        }
        return true;
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public void dropdownDataLoad(){
        country = new String[]{
                "BANGLADESH",
                "BHUTAN",
                "INDIA",
                "MALDIVES",
                "PAKISTAN",
                "SRILANKA"
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,country
        ){

            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);

                return view;

            }
        };
        spinnerCountry.setAdapter(spinnerArrayAdapter);



        donorType = new String[]{
                "INDIVIDUAL",
                "NONPROFIT",
                "GOVERNMENT",
                "FORPROFIT",

        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerDonorArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,donorType
        ){

            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);

                return view;

            }
        };
        spinnerDonorType.setAdapter(spinnerDonorArrayAdapter);

    }

    public void getProfileDetails(){
        mUtils.ShowDialog();
        if(mUtils.getRoleType().equalsIgnoreCase("Farmer")) {
            Call<FarmerModel> call = mApiService.getFarmerDetails(mUtils.getMobileNo());
            call.enqueue(new Callback<FarmerModel>() {
                @Override
                public void onResponse(Call<FarmerModel> call, Response<FarmerModel> response) {
                    FarmerModel farmerModel = response.body();
                    if (farmerModel!=null) {
                      if(farmerModel.getFirstName()!=null&&farmerModel.getFirstName().length()>0){
                          edittextFirstName.setText(farmerModel.getFirstName());
                      }
                        if(farmerModel.getMiddleName()!=null&&farmerModel.getMiddleName().size()>0){
                            edittextMiddleName.setText(farmerModel.getMiddleName().toString());
                        }
                        if(farmerModel.getLastName()!=null&&farmerModel.getLastName().length()>0){
                            edittextLastName.setText(farmerModel.getLastName());
                        }
                        if(farmerModel.getContactInfo()!=null){
                          ContactInfo contactInfo=farmerModel.getContactInfo();
                            if(contactInfo.getEmail()!=null&&contactInfo.getEmail().length()>0){
                                edittextEmailId.setText(contactInfo.getEmail());
                            }
                            if(contactInfo.getStreet1()!=null&&contactInfo.getStreet1().length()>0){
                                edittextAddress.setText(contactInfo.getStreet1());
                            }
                            if(contactInfo.getStreet1()!=null&&contactInfo.getStreet1().length()>0){
                                edittextAddress.setText(contactInfo.getStreet1());
                            }
                            if(contactInfo.getStreet2()!=null&&contactInfo.getStreet2().length()>0){
                                edittextAddress2.setText(contactInfo.getStreet2());
                            }
                            if(contactInfo.getCity()!=null&&contactInfo.getCity().length()>0){
                                edittextCity.setText(contactInfo.getCity());
                            }
                            if(contactInfo.getCity()!=null&&contactInfo.getCity().length()>0){
                                edittextCity.setText(contactInfo.getCity());
                            }
                            if(contactInfo.getCountry()!=null&&contactInfo.getCountry().length()>0){
                                mCountry=contactInfo.getCountry();

                            }
                        }
                        mUtils.DismissDialog();

                    }
                }

                @Override
                public void onFailure(Call<FarmerModel> call, Throwable t) {
                    mUtils.DismissDialog();
                }
            });
        }else if(mUtils.getRoleType().equalsIgnoreCase("Donor")){
            Call<DonorModel> call = mApiService.getDonorDetails(mUtils.getMobileNo());
            call.enqueue(new Callback<DonorModel>() {
                @Override
                public void onResponse(Call<DonorModel> call, Response<DonorModel> response) {
                    DonorModel donorModel = response.body();
                    if (donorModel!=null) {
                        if(donorModel.getFirstName()!=null&&donorModel.getFirstName().length()>0){
                            edittextFirstName.setText(donorModel.getFirstName());
                        }
                        if(donorModel.getMiddleName()!=null&&donorModel.getMiddleName().size()>0){
                            edittextMiddleName.setText(donorModel.getMiddleName().toString());
                        }
                        if(donorModel.getLastName()!=null&&donorModel.getLastName().length()>0){
                            edittextLastName.setText(donorModel.getLastName());
                        }
                        if(donorModel.getDonorType()!=null&&donorModel.getDonorType().length()>0){
                            mDonorType=donorModel.getDonorType();
                        }
                        if(isProfileEdit){
                            int aPosition=0;
                            if(mDonorType!=null&&mDonorType.length()>0){
                                for(int i=0; i < donorType.length; i++)
                                    if(donorType[i].contains(mDonorType))
                                        aPosition = i;
                            }
                            Log.d("Data-->","mDonorType--"+mDonorType+aPosition);
                            spinnerDonorType.setSelection(aPosition);

                        }
                        if(donorModel.getContact()!=null){
                            ContactInfo contactInfo=donorModel.getContact();
                            if(contactInfo.getEmail()!=null&&contactInfo.getEmail().length()>0){
                                edittextEmailId.setText(contactInfo.getEmail());
                            }
                            if(contactInfo.getStreet1()!=null&&contactInfo.getStreet1().length()>0){
                                edittextAddress.setText(contactInfo.getStreet1());
                            }
                            if(contactInfo.getStreet1()!=null&&contactInfo.getStreet1().length()>0){
                                edittextAddress.setText(contactInfo.getStreet1());
                            }
                            if(contactInfo.getStreet2()!=null&&contactInfo.getStreet2().length()>0){
                                edittextAddress2.setText(contactInfo.getStreet2());
                            }
                            if(contactInfo.getCity()!=null&&contactInfo.getCity().length()>0){
                                edittextCity.setText(contactInfo.getCity());
                            }
                            if(contactInfo.getCity()!=null&&contactInfo.getCity().length()>0){
                                edittextCity.setText(contactInfo.getCity());
                            }
                            if(contactInfo.getCountry()!=null&&contactInfo.getCountry().length()>0){
                                mCountry=contactInfo.getCountry();
                                if(isProfileEdit){
                                    int aPosition=0;
                                    if(mCountry!=null&&mCountry.length()>0){
                                        for(int i=0; i < country.length; i++)
                                            if(country[i].contains(mCountry))
                                                aPosition = i;
                                    }
                                    Log.d("Data-->","mCountry--"+mCountry+aPosition);
                                    spinnerCountry.setSelection(aPosition);

                                }
                            }
                        }

                    }
                    mUtils.DismissDialog();
                }

                @Override
                public void onFailure(Call<DonorModel> call, Throwable t) {
                    mUtils.DismissDialog();
                }
            });
        }

    }
}

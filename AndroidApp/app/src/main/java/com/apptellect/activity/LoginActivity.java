package com.apptellect.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apptellect.R;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;
import com.apptellect.webservice.TwilioApi;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private Button btnSend;
    private EditText editMobileNo;
    private String mCountryCode = "";
    private Utils mUtils;
    private ApiInterface mApiService;
    private Spinner spinnerProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        //check the country code
        if (!GetCountryZipCode().equalsIgnoreCase("")) {
            mCountryCode = GetCountryZipCode();
        } else {
            mCountryCode = "+";
            Toast.makeText(LoginActivity.this, "Please enter a mobile number with country code", Toast.LENGTH_SHORT).show();
        }
        spinnerProfile = findViewById(R.id.spinner_profile);
        btnSend = findViewById(R.id.btnSend);
        editMobileNo = findViewById(R.id.edittext_mobileno);
        editMobileNo.setText(mCountryCode);
        Selection.setSelection(editMobileNo.getText(), editMobileNo.getText().length());
        dropdownRole();
        editMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith(mCountryCode)) {
                    editMobileNo.setText(mCountryCode);
                    Selection.setSelection(editMobileNo.getText(), editMobileNo.getText().length());

                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUtils.checkConnection(LoginActivity.this)) {
                    if (isValidation()) {
                        mUtils.setRoleType(spinnerProfile.getSelectedItem().toString());
                        String mobileNo = editMobileNo.getText().toString();
                        if (mobileNo.contains("-")) {
                            String[] separated = mobileNo.split("-");
                            String countryCode = separated[0];
                            String number = separated[1].replace("+", "");
                            mobileNo = mobileNo.replace("-", "");
                            mUtils.setMobileNo(mobileNo);
                            sendMessage(number, countryCode);
                        } else if (mobileNo.startsWith("+1")) {
                            //Country US
                            mUtils.setMobileNo(mobileNo);
                            String number = mobileNo.substring(2);
                            sendMessage(number, "1");

                        } else {
                            //For other countries, assuem two digit country code for first beta
                            mUtils.setMobileNo(mobileNo);
                            String countryCode = mobileNo.substring(1, 3);
                            String number = mobileNo.substring(3);
                            sendMessage(number, countryCode);
                        }
                    }
                } else {
                    mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
                }
            }
        });
    }

    //get the country code using TelephonyManager
    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                if (!CountryZipCode.equalsIgnoreCase("0") || CountryZipCode != null) {
                    CountryZipCode = "+" + CountryZipCode + "-";
                } else {
                    CountryZipCode = "+";
                }
                if (CountryZipCode.equals("")) {
                    CountryZipCode = "+";
                }
                break;
            }
        }
        return CountryZipCode;
    }

    //validation
    public boolean isValidation() {
        if (editMobileNo.getText().toString().trim().length() < 10) {
            Toast.makeText(LoginActivity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spinnerProfile.getSelectedItem().toString().equalsIgnoreCase("Select Role")) {
            Toast.makeText(LoginActivity.this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spinnerProfile.getSelectedItem().toString().equalsIgnoreCase("Developer")) {
            Toast.makeText(LoginActivity.this, "Please select a another user", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spinnerProfile.getSelectedItem().toString().equalsIgnoreCase("Monitor")) {
            Toast.makeText(LoginActivity.this, "Please select a another user", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //binding role to spinner
    public void dropdownRole() {
        final String[] role = new String[]{
                "Select Role",
                "Farmer",
                "Donor",
                "Developer",
                "Monitor",
        };
        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, role) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;

            }
        };
        spinnerProfile.setAdapter(spinnerArrayAdapter);
    }

    //Twilio SMS
    private void sendMessage(final String mobileNo, final String countryCode) {
        mUtils.ShowDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.authy.com")
                .build();
        TwilioApi api = retrofit.create(TwilioApi.class);
        api.sendMessage(Utils.TwilioAPIKEY, mobileNo, countryCode, "sms").enqueue(new Callback<ResponseBody>() {
            public AlertDialog alertDialog;

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mUtils.DismissDialog();
                if (response.isSuccessful()) {
                    Intent verificationIntent = new Intent(LoginActivity.this, VerificationCodeActivity.class);
                    verificationIntent.putExtra("MOBILE_NO", editMobileNo.getText().toString());
                    verificationIntent.putExtra("ROLE", spinnerProfile.getSelectedItem().toString());
                    verificationIntent.putExtra("COUNTRY_CODE", countryCode);
                    verificationIntent.putExtra("NUMBER", mobileNo);
                    startActivity(verificationIntent);
                } else {
                    alertDialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Alert !")
                            .setMessage("We are facing issue with sending SMS to your number.Please " +"\n" +
                                                               "check your number and ensure that you are giving proper country code.")
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
                mUtils.DismissDialog();
            }
        });
    }


}

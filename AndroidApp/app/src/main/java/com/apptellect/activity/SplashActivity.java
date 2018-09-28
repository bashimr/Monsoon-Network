package com.apptellect.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.apptellect.R;
import com.apptellect.utilities.Utils;

public class SplashActivity extends AppCompatActivity {
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mUtils = new Utils(this);
        if (mUtils.isRegister()) {
            if (mUtils.getRoleType() != null && mUtils.getRoleType().equalsIgnoreCase("Farmer")) {
                Intent intentLogin = new Intent(SplashActivity.this, CropMainActivity.class);
                startActivity(intentLogin);
                finish();
            } else if (mUtils.getRoleType() != null && mUtils.getRoleType().equalsIgnoreCase("Donor")) {
                Intent intentDonor = new Intent(SplashActivity.this, DonorMainActivity.class);
                startActivity(intentDonor);
                finish();
            }

        } else {
            Intent intentCropMainActivity = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intentCropMainActivity);
            finish();
        }
    }
}

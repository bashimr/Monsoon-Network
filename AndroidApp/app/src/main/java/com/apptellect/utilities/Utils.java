package com.apptellect.utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


import com.apptellect.R;
import com.apptellect.dbdata.CropManagement_DatabaseHelper;
import com.apptellect.dbdata.Crop_Photo_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

public class Utils {
    private Context mContext;
    ProgressDialog progressDialog;
    AlertDialog alert;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TwilioAPIKEY="ohpRtPvoxbBT2hNWKmwBkcxJdBeZPbF4";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public Utils(Context context) {
        mContext = context;
        pref = mContext.getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();
    }
    public int getRandomNo(){
        Random r = new Random();
        int randomNo = r.nextInt(1000+1);
        return randomNo;
    }
    public boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            //Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }
    public void ShowDialog() {
        progressDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    public void DismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }
    }
    public void ShowAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        //Setting message manually and performing action on button click
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Activity) mContext).finish();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        if (title.length() == 0)
            alert.setTitle(mContext.getResources().getString(R.string.app_name));
        else
            alert.setTitle(title);
        if (alert.isShowing()) {
            alert.dismiss();
        } else {
            alert.show();
        }
    }
    /*Shared perference value*/
    public void setMobileNo(String number){
       editor.putString("MOBILE_NO",number);
       editor.commit();
    }
    public void setUserID(String id){
        editor.putString("USERID",id);
        editor.commit();
    }
    public String getUserId(){
        return pref.getString("USERID","");
    }
    public String getMobileNo(){
        return pref.getString("MOBILE_NO","");
    }
    public void setRoleType(String type){
        editor.putString("ROLE_TYPE",type);
        editor.commit();
    }
    public String getRoleType(){
        return pref.getString("ROLE_TYPE","");
    }

    public void setDonorType(String type){
        editor.putString("DONOR_TYPE",type);
        editor.commit();
    }
    public String getDonorType(){
        return pref.getString("DONOR_TYPE","");
    }
    public void setIsRegister(boolean isRegister){
        editor.putBoolean("IS_REGISTER",isRegister);
        editor.commit();
    }
    public boolean isRegister(){
        return pref.getBoolean("IS_REGISTER",false);
    }


    public static byte[] getPictureByteOfArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.app.AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static Bitmap byteToBitmap(Cursor cursor){

        byte[] blob = cursor.getBlob(cursor.getColumnIndex(CropManagement_DatabaseHelper.CROP_PHOTO));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }
}

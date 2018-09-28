package com.apptellect.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.apptellect.R;
import com.apptellect.dbdata.CropManagement_DatabaseHelper;
import com.apptellect.dbdata.CropTable;
import com.apptellect.dbdata.Crop_Coordinate_module;
import com.apptellect.dbdata.Crop_Photo_module;
import com.apptellect.model.AddCropModel;
import com.apptellect.model.CropCoordinates;
import com.apptellect.model.CropPhotsModel;
import com.apptellect.model.CropPledges;
import com.apptellect.model.PrecipitationHistory;
import com.apptellect.utilities.IdManager;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCropActivity extends AppCompatActivity {
    public static ArrayList<CropPledges> croppledgesList = new ArrayList<CropPledges>();
    public static ArrayList<PrecipitationHistory> preciptationhistoryList = new ArrayList<PrecipitationHistory>();
    private ArrayList<CropCoordinates> arrayList_coordinates = new ArrayList<CropCoordinates>();
    private ArrayList<LatLng> maplatlangList = new ArrayList<LatLng>();
    private ArrayList<String> picturedatesList = new ArrayList<String>();
    private ArrayList<CropPhotsModel> imagelatlang2List = new ArrayList<CropPhotsModel>();
    private ArrayList<Bitmap> imagebitmapList = new ArrayList<Bitmap>();
    private Spinner spinnerCrops;
    private Spinner spinnerCurrency;
    private ImageView imgGps;
    private ArrayList<String> cropsList = new ArrayList<String>();
    private ArrayList<String> currencyList = new ArrayList<String>();
    private ArrayList<String> statusList = new ArrayList<String>();
    private EditText edtSeedingdate;
    private EditText edtHarvestdate;
    private EditText edtCroparea;
    private EditText edtYield;
    private Spinner spinnerStatus;
    private ApiInterface mApiService;
    private Button btnSubmit;
    private Utils mUtils;
    private String strCroptype = "";
    private String strCropsubtype = "";
    private String strCropareainunits = "Acres";
    private String strCurrency = "";
    private String strStatus = "";
    private LinearLayout linearlayoutImages;
    private String strSeedingdate;
    private String strHavestdate;
    private CropManagement_DatabaseHelper cropManagementDatabaseHelper;

    // showing the captured images
    public void addImages(ArrayList<Bitmap> arrayList_image_files2) {
        imagebitmapList = arrayList_image_files2;
        if (!(imagebitmapList.size() == 0)) {
            for (int i = 0; i < imagebitmapList.size(); i++) {
                linearlayoutImages.addView(dynamicImages(imagebitmapList.get(i), i));
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if ((ArrayList<Bitmap>) data.getSerializableExtra("imagebitmapList") != null)
                    addImages((ArrayList<Bitmap>) data.getSerializableExtra("imagebitmapList"));
                imagelatlang2List = (ArrayList<CropPhotsModel>) data.getSerializableExtra("image_coordinates");
                maplatlangList = (ArrayList<LatLng>) data.getSerializableExtra("maplatlangList");
                picturedatesList = (ArrayList<String>) data.getSerializableExtra("picturedatesList");
                arrayList_coordinates = (ArrayList<CropCoordinates>) data.getSerializableExtra("coordinatesList");

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        getSupportActionBar().setTitle(R.string.crop_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearlayoutImages = findViewById(R.id.linear_images);
        spinnerCrops = findViewById(R.id.spinner_crops);
        spinnerStatus = findViewById(R.id.spinner_status);
        edtCroparea = findViewById(R.id.edt_crop_area);
        spinnerCurrency = findViewById(R.id.spinner_currency);
        edtSeedingdate = findViewById(R.id.edt_seeding_date);
        edtHarvestdate = findViewById(R.id.edt_harvest_date);
        edtYield = findViewById(R.id.edt_yield);
        imgGps = findViewById(R.id.img_gps);
        btnSubmit = findViewById(R.id.btn_submit);
        cropsList.add("SELECT");
        cropsList.add("COTTON");
        cropsList.add("WHEAT");
        cropsList.add("VEGETABLE");
        cropsList.add("FRUIT");
        cropsList.add("SUGARCANE");
        cropsList.add("RICE");
        currencyList.add("SELECT");
        currencyList.add("US_DOLLAR");
        currencyList.add("INDIAN_RUPEE");
        currencyList.add("PAKISTANI_RUPEE");
        currencyList.add("SRILANKA_RUPEE");
        currencyList.add("BHUTANESE_NGULTRUM");
        currencyList.add("BANGLADESHI_TAKA");
        currencyList.add("MALDIVIAN_RUFIYAA");
        currencyList.add("CANADIAN_DOLLAR");
        statusList.add("SELECT");
        statusList.add("PRESEED");
        statusList.add("SEEDED");
        statusList.add("HARVESTED");
        statusList.add("SOLD");
        statusList.add("REPORTED_DESTROYED");
        statusList.add("REPORTED_DAMAGED");
        statusList.add("NOT_VARIFIABLE");
        statusList.add("UNDER_INVESTIGATION");
        statusList.add("FRADULANT");
        statusList.add("FUNDED");
        statusList.add("VOID");
        ArrayAdapter<String> adapterCropList = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cropsList);
        adapterCropList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrops.setAdapter(adapterCropList);
        spinnerCrops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCroptype = cropsList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<String> adapterCurrencyList = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, currencyList);
        adapterCropList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapterCurrencyList);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCurrency = currencyList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ArrayAdapter<String> adapterStatusList = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, statusList);
        adapterCropList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatusList);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strStatus = statusList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        edtSeedingdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date(edtSeedingdate);
            }
        });
        edtHarvestdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date(edtHarvestdate);
            }
        });
        imgGps.setOnClickListener(new View.OnClickListener() {
            //recapturing coordinates on map
            public AlertDialog dialogReCapture;

            @Override
            public void onClick(View view) {
                if (strCroptype.equals("") || strCroptype.equals("SELECT")) {
                    Toast.makeText(getApplicationContext(), "please select the crop type", Toast.LENGTH_SHORT).show();

                } else {
                    dialogReCapture = new AlertDialog.Builder(AddCropActivity.this)
                            .setTitle("Alert !")
                            .setMessage("Previous location will be erased, if you proceed.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(AddCropActivity.this, CreatGpsCoordinatesActivity.class);
                                    intent.putExtra("image", "0");
                                    intent.putExtra("croptype", strCroptype);
                                    startActivityForResult(intent, 1);


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogReCapture.dismiss();
                                }
                            })
                            .create();
                    if (arrayList_coordinates.size() > 0) {
                        dialogReCapture.show();
                    } else {
                        Intent intent = new Intent(AddCropActivity.this, CreatGpsCoordinatesActivity.class);
                        intent.putExtra("image", "0");
                        intent.putExtra("croptype", strCroptype);
                        startActivityForResult(intent, 1);

                    }
                }

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUtils.checkConnection(AddCropActivity.this)) {
                    if (isValidation()) {
                        try {
                            DateFormat originalFormat = new SimpleDateFormat("d-m-yyy", Locale.ENGLISH);
                            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date dateSeeding = null;
                            Date dateHarvest = null;
                            try {
                                dateSeeding = originalFormat.parse(edtSeedingdate.getText().toString());
                                dateHarvest = originalFormat.parse(edtHarvestdate.getText().toString());
                                strSeedingdate = targetFormat.format(dateSeeding);
                                strHavestdate = targetFormat.format(dateHarvest);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mUtils.ShowDialog();
                        AddCropModel addNewCropModel = new AddCropModel();
                        addNewCropModel.setCropId(IdManager.getNewID());
                        addNewCropModel.setTypeOfCrop(strCroptype);
                        addNewCropModel.setCropSubType(strCropsubtype);
                        addNewCropModel.setCropArea(edtCroparea.getText().toString() + " " + strCropareainunits);
                        addNewCropModel.setSeedingDate(strSeedingdate);
                        addNewCropModel.setHarvestDate(strHavestdate);
                        addNewCropModel.setStatus(strStatus);
                        addNewCropModel.setCropCurrency(strCurrency);
                        addNewCropModel.setMaxYieldValue(edtYield.getText().toString());
                        addNewCropModel.setArrayList_cropCoordinates(arrayList_coordinates);
                        addNewCropModel.setArrayList_cropPledges(croppledgesList);
                        addNewCropModel.setArrayList_cropPhotos(imagelatlang2List);
                        addNewCropModel.setArrayList_precipitationHistory(preciptationhistoryList);
                        addNewCropModel.setFarmerOwner(mUtils.getMobileNo());
                        addNewCropModel.setArrayList_cropPledges(croppledgesList);
                        Log.d("Request-->", "" + addNewCropModel.toString());
                        final String cropID = addNewCropModel.getCropId();
                        final String formerID = addNewCropModel.getFarmerOwner();
                        Call<AddCropModel> call = mApiService.createnewcrop(addNewCropModel);
                        call.enqueue(new Callback<AddCropModel>() {
                            @Override
                            public void onResponse(Call<AddCropModel> call, Response<AddCropModel> response) {
                                mUtils.DismissDialog();
                                new Store_DB(cropID, formerID).execute();

                            }

                            @Override
                            public void onFailure(Call<AddCropModel> call, Throwable t) {
                                Log.d("Result Error-->", "" + t.toString());
                                mUtils.DismissDialog();
                            }
                        });


                    }
                } else {
                    mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
                }


            }
        });
        cropManagementDatabaseHelper = new CropManagement_DatabaseHelper(this);


    }

    public View dynamicImages(Bitmap bitmap, final int id) {
        final LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.custom_image, null);
        ImageView imge_captured = (ImageView) myView.findViewById(R.id.imge_captured);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        imge_captured.setLayoutParams(layoutParams);
        imge_captured.setImageBitmap(bitmap);
        imge_captured.setId(id);
        return myView;

    }

    public boolean isValidation() {
        if (strCroptype.equals("") || strCroptype.equals("SELECT")) {
            Toast.makeText(getApplicationContext(), "please select the crop type", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtCroparea.getText().toString().trim().length() == 0) {
            edtCroparea.setError("Please enter the crop area");
            edtCroparea.requestFocus();
            return false;
        } else if (edtSeedingdate.getText().toString().trim().length() == 0) {

            Toast.makeText(getApplicationContext(), "please select the seeding date", Toast.LENGTH_SHORT).show();
            edtSeedingdate.requestFocus();
            return false;
        } else if (strStatus.equals("") || strStatus.equals("SELECT")) {
            Toast.makeText(getApplicationContext(), "please select the status.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtHarvestdate.getText().toString().trim().length() == 0) {

            Toast.makeText(getApplicationContext(), "please select the harvest date", Toast.LENGTH_SHORT).show();
            edtHarvestdate.requestFocus();
            return false;
        } else if (strCurrency.equals("") || strCurrency.equals("SELECT")) {
            Toast.makeText(getApplicationContext(), "please select the currency.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtYield.getText().toString().trim().length() == 0) {
            edtYield.setError("Please enter the yield value");
            edtYield.requestFocus();
            return false;
        }
        return true;
    }

    public void date(final EditText editText) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    public class Store_DB extends AsyncTask<Void, Void, Boolean> {

        private Crop_Photo_module crop_photo_module;
        private Crop_Coordinate_module crop_coordinate_module;
        private String cropID;
        private String formerId;

        public Store_DB(String strcropid, String formerID) {
            this.cropID = strcropid;
            this.formerId = formerID;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            CropTable cropTable = new CropTable();
            List<Crop_Photo_module> listcrop_photo_module = new ArrayList<>();
            List<Crop_Coordinate_module> listcrop_coordinate_modules = new ArrayList<>();
            cropTable.setCroparea(edtCroparea.getText().toString() + " " + strCropareainunits);
            cropTable.setCropcurrency(strCurrency);
            cropTable.setCropid(cropID);
            cropTable.setFarmerowner(formerId);
            cropTable.setHarvestdate(strHavestdate);
            cropTable.setMaxyeildvalue(edtYield.getText().toString());
            cropTable.setSeedingdate(strSeedingdate);
            cropTable.setStatue(strStatus);
            for (int i = 0; i < imagebitmapList.size(); i++) {
                crop_photo_module = new Crop_Photo_module();
                crop_photo_module.setBitmap(imagebitmapList.get(i));
                crop_photo_module.setCropid(cropID);
                crop_photo_module.setCropphotoid(IdManager.getNewID());
                crop_photo_module.setDate(picturedatesList.get(i));
                crop_photo_module.setPhotolatitude(imagelatlang2List.get(i).getPhotoCoordinates().getLattitude());
                crop_photo_module.setPhotolongitude(imagelatlang2List.get(i).getPhotoCoordinates().getLongitude());
                listcrop_photo_module.add(crop_photo_module);
            }
            for (int i = 0; i < maplatlangList.size(); i++) {
                crop_coordinate_module = new Crop_Coordinate_module();
                crop_coordinate_module.setCroplatitude(String.valueOf(maplatlangList.get(i).latitude));
                crop_coordinate_module.setCroplongitude(String.valueOf(maplatlangList.get(i).longitude));
                crop_coordinate_module.setCropcoorid(IdManager.getNewID());
                crop_coordinate_module.setCropid(cropID);
                listcrop_coordinate_modules.add(crop_coordinate_module);
            }
            cropManagementDatabaseHelper.insertListOfCropPhotoTableData(listcrop_photo_module);
            cropManagementDatabaseHelper.insertListOfCropCoorTableData(listcrop_coordinate_modules);
            boolean is_success = cropManagementDatabaseHelper.insertCropTableData(cropTable);
            return is_success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Intent intentCropMain = new Intent(AddCropActivity.this, CropMainActivity.class);
                intentCropMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCropMain);
            } else {
            }
        }
    }


}

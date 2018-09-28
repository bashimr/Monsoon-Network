package com.apptellect.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.apptellect.model.AddNewCropModel;
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

public class EditCropActivity extends AppCompatActivity {
    public static ArrayList<CropPledges> croppledgesList = new ArrayList<CropPledges>();
    public static ArrayList<PrecipitationHistory> preciptationhistoryList = new ArrayList<PrecipitationHistory>();
    private ArrayList<CropCoordinates> coordinatesList = new ArrayList<CropCoordinates>();
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
    private EditText edtSeedingDate;
    private EditText edtHarvestDate;
    private EditText edtCropArea;
    private EditText edtYield;
    private Spinner spinnerStatus;
    private ApiInterface mApiService;
    private Button btnSubmit;
    private Utils mUtils;
    private String strCropType = "";
    private String strCropSubType = "";
    private String strCropAreaInUnits = "Acres";
    private String strCurrency = "";
    private String strStatus = "";
    private LinearLayout imagesLinearLayout;
    private String strseedingdate;
    private String strhavestdate;
    private CropManagement_DatabaseHelper cropmanagementDatabaseHelper;
    private String strcropId = "";
    private Button btnReset;
    private AddNewCropModel cropModel;

    // showing the captured images
    public void addImages(ArrayList<Bitmap> arrayList_image_files2) {
        imagebitmapList = arrayList_image_files2;
        if (!(imagebitmapList.size() == 0)) {
            for (int i = 0; i < imagebitmapList.size(); i++) {
                imagesLinearLayout.addView(dynamicImages(imagebitmapList.get(i), i));
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
                if ((ArrayList<Bitmap>) data.getSerializableExtra("imagefilesList") != null)
                    imagelatlang2List = (ArrayList<CropPhotsModel>) data.getSerializableExtra("image_coordinates");
                maplatlangList = (ArrayList<LatLng>) data.getSerializableExtra("maplatlangList");
                picturedatesList = (ArrayList<String>) data.getSerializableExtra("picturedatesList");
                coordinatesList = (ArrayList<CropCoordinates>) data.getSerializableExtra("coordinatesList");
                addImages((ArrayList<Bitmap>) data.getSerializableExtra("imagefilesList"));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crop);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        getSupportActionBar().setTitle(R.string.crop_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imagesLinearLayout = findViewById(R.id.linear_images);
        spinnerCrops = findViewById(R.id.spinner_crops);
        spinnerStatus = findViewById(R.id.spinner_status);
        edtCropArea = findViewById(R.id.edt_crop_area);
        spinnerCurrency = findViewById(R.id.spinner_currency);
        edtSeedingDate = findViewById(R.id.edt_seeding_date);
        edtHarvestDate = findViewById(R.id.edt_harvest_date);
        edtYield = findViewById(R.id.edt_yield);
        imgGps = findViewById(R.id.img_gps);
        btnSubmit = findViewById(R.id.btn_submit);
        btnReset = (Button) findViewById(R.id.btn_reset);
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
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, cropsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCrops.setAdapter(dataAdapter);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, currencyList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(dataAdapter3);
        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, statusList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(dataAdapter4);
        edtSeedingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date(edtSeedingDate, "seeding");
            }
        });
        edtHarvestDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date(edtHarvestDate, "harvest");
            }
        });
        imgGps.setOnClickListener(new View.OnClickListener() {
            public AlertDialog dialog;

            @Override
            public void onClick(View view) {
                if (strCropType.equals("") || strCropType.equals("SELECT")) {
                    Toast.makeText(getApplicationContext(), "please select the crop type", Toast.LENGTH_SHORT).show();

                } else {
                    dialog = new AlertDialog.Builder(EditCropActivity.this)
                            .setTitle("Alert !")
                            .setMessage("Previous location will be erased, if you proceed.")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(EditCropActivity.this, EditGpsCoordinatesActivity.class);
                                    intent.putExtra("image", "0");
                                    intent.putExtra("croptype", strCropType);
                                    intent.putExtra("cropData", cropModel);
                                    startActivityForResult(intent, 1);

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    if (coordinatesList.size() > 0 && EditGpsCoordinatesActivity.ischanged) {
                        dialog.show();
                    } else {
                        Intent intent = new Intent(EditCropActivity.this, EditGpsCoordinatesActivity.class);
                        intent.putExtra("image", "0");
                        intent.putExtra("croptype", strCropType);
                        intent.putExtra("cropData", cropModel);
                        startActivityForResult(intent, 1);
                    }


                }

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUtils.checkConnection(EditCropActivity.this)) {
                    if (isValidation()) {
                        mUtils.ShowDialog();
                        AddCropModel addNewCropModel = new AddCropModel();
                        addNewCropModel.setCropId(strcropId);
                        addNewCropModel.setTypeOfCrop(cropsList.get(spinnerCrops.getSelectedItemPosition()));
                        addNewCropModel.setCropSubType(strCropSubType);
                        addNewCropModel.setCropArea(edtCropArea.getText().toString() + " " + strCropAreaInUnits);
                        addNewCropModel.setSeedingDate(strseedingdate);
                        addNewCropModel.setHarvestDate(strhavestdate);
                        addNewCropModel.setStatus(strStatus);
                        addNewCropModel.setCropCurrency(strCurrency);
                        addNewCropModel.setMaxYieldValue(edtYield.getText().toString());
                        if (coordinatesList.size() > 0)
                            addNewCropModel.setArrayList_cropCoordinates(coordinatesList);
                        else
                            addNewCropModel.setArrayList_cropCoordinates(cropModel.getArrayList_cropCoordinates());
                        addNewCropModel.setArrayList_cropPledges(croppledgesList);
                        addNewCropModel.setArrayList_cropPhotos(imagelatlang2List);
                        addNewCropModel.setArrayList_precipitationHistory(preciptationhistoryList);
                        addNewCropModel.setFarmerOwner(mUtils.getMobileNo());
                        addNewCropModel.setArrayList_cropPledges(croppledgesList);
                        Log.d("Request-->", "" + addNewCropModel.toString());
                        final String cropID = addNewCropModel.getCropId();
                        final String formerID = addNewCropModel.getFarmerOwner();
                        Call<AddCropModel> call = mApiService.updatecrop(strcropId, addNewCropModel);
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
        cropmanagementDatabaseHelper = new CropManagement_DatabaseHelper(this);
        edtitCrop();
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtitCrop();
            }
        });


        EditGpsCoordinatesActivity.ischanged=false;

    }

    private void edtitCrop() {
        cropModel = (AddNewCropModel) getIntent().getSerializableExtra("cropData");
        for (int i = 0; i < cropsList.size(); i++) {
            if (cropsList.get(i).equals(cropModel.getTypeOfCrop())) {
                spinnerCrops.setSelection(i);
            }

        }
        for (int i = 0; i < statusList.size(); i++) {
            if (statusList.get(i).equals(cropModel.getStatus())) {
                spinnerStatus.setSelection(i);
            }

        }
        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).equals(cropModel.getCropCurrency())) {
                spinnerCurrency.setSelection(i);
            }

        }
        edtCropArea.setText(cropModel.getCropArea());
        edtSeedingDate.setText(cropModel.getSeedingDate());
        edtHarvestDate.setText(cropModel.getHarvestDate());
        edtYield.setText(cropModel.getMaxYieldValue());
        strseedingdate = cropModel.getSeedingDate();
        strhavestdate = cropModel.getHarvestDate();
        strcropId = cropModel.getCropId();
        spinnerCrops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCropType = cropsList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCurrency = currencyList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strStatus = statusList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


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
        imge_captured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditCropActivity.this, CreatGpsCoordinatesActivity.class);
                intent.putExtra("image", "1");
                intent.putExtra("position", String.valueOf(view.getId()));
                intent.putExtra("taken_image", imagebitmapList);
                intent.putExtra("image_coordinates", imagelatlang2List);
            }

        });
        return myView;

    }

    public boolean isValidation() {
        if (strCropType.equals("") || strCropType.equals("SELECT")) {
            Toast.makeText(getApplicationContext(), "please select the crop type", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtCropArea.getText().toString().trim().length() == 0) {
            edtCropArea.setError("Please enter the crop area");
            edtCropArea.requestFocus();
            return false;
        } else if (edtSeedingDate.getText().toString().trim().length() == 0) {

            Toast.makeText(getApplicationContext(), "please select the seeding date", Toast.LENGTH_SHORT).show();
            edtSeedingDate.requestFocus();
            return false;
        } else if (strStatus.equals("") || strStatus.equals("SELECT")) {
            Toast.makeText(getApplicationContext(), "please select the status.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtHarvestDate.getText().toString().trim().length() == 0) {

            Toast.makeText(getApplicationContext(), "please select the harvest date", Toast.LENGTH_SHORT).show();
            edtHarvestDate.requestFocus();
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

    public void date(final EditText editText, final String type) {
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
                        try {
                            DateFormat originalFormat = new SimpleDateFormat("d-m-yyy", Locale.ENGLISH);
                            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            Date date_1 = null;
                            try {
                                date_1 = originalFormat.parse(editText.getText().toString());
                                if (type.equals("seeding"))
                                    strseedingdate = targetFormat.format(date_1);
                                else
                                    strhavestdate = targetFormat.format(date_1);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
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
            cropTable.setCroparea(edtCropArea.getText().toString() + " " + strCropAreaInUnits);
            cropTable.setCropcurrency(strCurrency);
            cropTable.setCropid(cropID);
            cropTable.setFarmerowner(formerId);
            cropTable.setHarvestdate(strhavestdate);
            cropTable.setMaxyeildvalue(edtYield.getText().toString());
            cropTable.setSeedingdate(strseedingdate);
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
            cropmanagementDatabaseHelper.insertListOfCropPhotoTableData(listcrop_photo_module);
            cropmanagementDatabaseHelper.insertListOfCropCoorTableData(listcrop_coordinate_modules);
            boolean is_success = cropmanagementDatabaseHelper.insertCropTableData(cropTable);
            return is_success;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Intent intentCropMain = new Intent(EditCropActivity.this, CropMainActivity.class);
                intentCropMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentCropMain);
            } else {
            }
        }
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
}

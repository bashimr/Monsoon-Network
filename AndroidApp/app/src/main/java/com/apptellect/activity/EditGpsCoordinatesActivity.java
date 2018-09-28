package com.apptellect.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apptellect.AI.Watson;
import com.apptellect.AI.WatsonResponse;
import com.apptellect.R;
import com.apptellect.adapter.CustomInfoWindowGoogleMap;
import com.apptellect.adapter.InfoWindowData;
import com.apptellect.dbdata.CropManagement_DatabaseHelper;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.model.CropCoordinates;
import com.apptellect.model.CropImageModel;
import com.apptellect.model.CropPhotsModel;
import com.apptellect.model.PhotoCoordinates;
import com.apptellect.utilities.AwsProcessor;
import com.apptellect.utilities.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EditGpsCoordinatesActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    static public final int REQUEST_LOCATION = 1;
    static final Integer GPS_SETTINGS = 0x7;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int ACCURACY = 500;
    private static final float MIN_DISTANCE_FOR_UPDATE = 1f;
    private static final long MIN_TIME_FOR_UPDATE = 1000;
    public static boolean ischanged;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ArrayList<String> picturedatesList = new ArrayList<String>();
    ArrayList<String> picturedatesduplicateList = new ArrayList<String>();
    LocationRequest mLocationRequest;
    PendingResult<LocationSettingsResult> locationSettingResult;
    GoogleApiClient client;
    private ArrayList<CropPhotsModel> imagelatlang2List = new ArrayList<CropPhotsModel>();
    private ArrayList<CropPhotsModel> imagelatlang2duplicateList = new ArrayList<CropPhotsModel>();
    private ArrayList<CropCoordinates> coordinatesList = new ArrayList<CropCoordinates>();
    private ArrayList<LatLng> maplatlangList = new ArrayList<LatLng>();
    private GoogleMap mMap;
    private LocationManager locationManager;
    //    private Location latlong;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnPin;
    private ArrayList<LatLng> latLngArrayList = new ArrayList<LatLng>();
    private ArrayList<Bitmap> imagefilesList = new ArrayList<Bitmap>();
    private ArrayList<Bitmap> imagefilesduplicateList = new ArrayList<Bitmap>();
    private Location location;
    private boolean isonce;
    private Button btnClear;
    private TextView txtAccuracy;
    private TextView txtLatLong;
    private Button btnCapture;
    private String userChoosenTask;
    private Activity activity;
    private File destination;
    private Location locationGPS;
    private LatLng latLng_camera;
    private float distance = 3;
    private int latlang_position = 0;
    private boolean ispicked;
    private AddNewCropModel cropModel;
    private CropManagement_DatabaseHelper cropManagementDatabaseHelper;
    private CropPhotsModel cropPhotsModel;
    private Button btnSaveBounndry;
    private Button btnJoin;
    private boolean isjoned;
    private CropImageModel cropImageModel;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @SuppressLint("MissingPermission")
    public Location getLocation(String provider) {
        if (locationManager.isProviderEnabled(provider)) {
            locationManager.requestLocationUpdates(provider,
                    MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);

        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_coordinates);
        verifyStoragePermissions(this);
        activity = this;
        cropManagementDatabaseHelper = new CropManagement_DatabaseHelper(this);
        cropModel = (AddNewCropModel) getIntent().getSerializableExtra("cropData");
        btnCapture = findViewById(R.id.btn_capture);
        txtAccuracy = findViewById(R.id.txt_accuracy);
        txtLatLong = findViewById(R.id.txt_lat_long);
        btnPin = findViewById(R.id.btn_pin);
        btnClear = findViewById(R.id.btn_clear);
        btnSaveBounndry = (Button) findViewById(R.id.btn_save_bounndry);
        btnJoin = (Button) findViewById(R.id.btn_join);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        btnJoin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (coordinatesList.size() > 2) {
                    isjoned = true;
                    mMap.addPolyline(new PolylineOptions()
                            .add(latLngArrayList.get(0), latLngArrayList.get(latLngArrayList.size() - 1)));
                    Toast.makeText(getApplicationContext(), "Now Click The Save Boundry", Toast.LENGTH_SHORT).show();
                    btnPin.setEnabled(false);
                }

            }
        });
        btnSaveBounndry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(imagefilesList.size() == 0) || !(coordinatesList.size() == 0)) {
                    if (coordinatesList.size() > 2) {
                        if (isjoned) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("imagefilesList", imagefilesList);
                            returnIntent.putExtra("image_coordinates", imagelatlang2List);
                            returnIntent.putExtra("picturedatesList", picturedatesList);
                            returnIntent.putExtra("maplatlangList", maplatlangList);
                            returnIntent.putExtra("coordinatesList", coordinatesList);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "First Join The End Points", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Take Atleast 3 Points", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            }

        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            public AlertDialog dialog;

            @Override
            public void onClick(View view) {
                mMap.clear();
                isjoned = false;
                ischanged = true;
                txtLatLong.setText("");
                btnPin.setEnabled(true);
                if (latLngArrayList.size() > 0) {
                    maplatlangList.remove(maplatlangList.size() - 1);
                    latLngArrayList.remove(latLngArrayList.size() - 1);
                    coordinatesList.remove(coordinatesList.size() - 1);
                    for (int i = 0; i < latLngArrayList.size(); i++) {
                        mMap.setInfoWindowAdapter(null);
                        mMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("land coordinate"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngArrayList.get(i).latitude, latLngArrayList.get(i).longitude), 25));
                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                        );
                        polyline1.setPoints(latLngArrayList);
                    }
                } else {
                    dialog = new AlertDialog.Builder(EditGpsCoordinatesActivity.this)
                            .setTitle("Alert !")
                            .setMessage("Are You Sure ? To Delete Captured Locations")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (!(imagefilesList.size() == 0)) {
                                        imagefilesList.clear();
                                        if (!(imagelatlang2List.size() == 0)) {
                                            imagelatlang2List.clear();
                                            picturedatesList.clear();
                                        }
                                    }
                                    if (!(imagefilesduplicateList.size() == 0)) {
                                        imagefilesduplicateList.clear();
                                        if (!(imagelatlang2duplicateList.size() == 0)) {
                                            imagelatlang2duplicateList.clear();
                                            picturedatesduplicateList.clear();
                                        }
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    if (imagefilesList.size() > 0 || imagefilesduplicateList.size() > 0)
                        dialog.show();
                    else
                        Toast.makeText(getApplicationContext(), "Nothing To Clear", Toast.LENGTH_SHORT).show();

                }
                if (imagefilesList.size() > 0) {
                    for (int i = 0; i < imagefilesList.size(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(Double.parseDouble(imagelatlang2List.get(i).getPhotoCoordinates().getLattitude()),
                                Double.parseDouble(imagelatlang2List.get(i).getPhotoCoordinates().getLongitude())))
                                .title("Captured Pic")
                                .snippet("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.camere_marker));
                        InfoWindowData info = new InfoWindowData();
                        info.setImage(imagefilesList.get(i));
                        info.setDate("Date & Time : " + picturedatesList.get(i));
                        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(EditGpsCoordinatesActivity.this);
                        mMap.setInfoWindowAdapter(customInfoWindow);
                        Marker m = mMap.addMarker(markerOptions);
                        m.setTag(info);
                    }
                }
                if (imagefilesduplicateList.size() > 0) {
                    for (int i = 0; i < imagefilesduplicateList.size(); i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(Double.parseDouble(imagelatlang2duplicateList.get(i).getPhotoCoordinates().getLattitude()),
                                Double.parseDouble(imagelatlang2duplicateList.get(i).getPhotoCoordinates().getLongitude())))
                                .title("Captured Pic")
                                .snippet("")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.camere_marker));
                        InfoWindowData info = new InfoWindowData();
                        info.setImage(imagefilesduplicateList.get(i));
                        info.setDate("Date & Time : " + picturedatesduplicateList.get(i));
                        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(EditGpsCoordinatesActivity.this);
                        mMap.setInfoWindowAdapter(customInfoWindow);
                        Marker m = mMap.addMarker(markerOptions);
                        m.setTag(info);
                    }
                }
                if (coordinatesList.size() > 2) {
                    btnJoin.setEnabled(true);
                } else {
                    btnJoin.setEnabled(false);
                }
            }
        });
        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isjoned = false;
                ischanged = true;
                addMarkerSubmit(maplatlangList);
            }
//            }
        });
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLngArrayList.size() >= 3)
                    selectImage();
                else {
                    Toast.makeText(getApplicationContext(), "First Take The Valid Coordinates.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo",
                "Cancel"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EditGpsCoordinatesActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utils.checkPermission(activity);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void addMarkerSubmit(ArrayList<LatLng> arrayList_map_latlang) {
        {
            askForGPS();
            if (locationGPS != null) {
                if (locationGPS.getAccuracy() < ACCURACY) {
                    if (distance > 2) {
                        ispicked = true;
                        txtAccuracy.setText("accuracy " + locationGPS.getAccuracy() + " m");
                        double latitude = locationGPS.getLatitude();
                        double longitude = locationGPS.getLongitude();
                        LatLng sydney = new LatLng(latitude, longitude);
                        arrayList_map_latlang.add(sydney);
                        mMap.setInfoWindowAdapter(null);
                        mMap.addMarker(new MarkerOptions().position(sydney).title("land coordinate"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sydney.latitude, sydney.longitude), 25));
                        latLngArrayList.add(sydney);
                        CropCoordinates cropCoordinates = new CropCoordinates();
                        cropCoordinates.setLatitude(String.valueOf(sydney.latitude));
                        cropCoordinates.setLongitude(String.valueOf(sydney.longitude));
                        coordinatesList.add(cropCoordinates);
                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                        );
                        polyline1.setPoints(latLngArrayList);
                        Toast.makeText(getApplicationContext(), latLngArrayList.size() + " Location Obtained.", Toast.LENGTH_SHORT).show();
                        if (latLngArrayList.size() == 8) {
                            mMap.addPolyline(new PolylineOptions()
                                    .add(latLngArrayList.get(0), latLngArrayList.get(7)));
                            btnPin.setEnabled(false);

                        } else if (latLngArrayList.size() >= 4) {
                        }
                        if (latLngArrayList.size() > 1) {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(latLngArrayList.get(0));
                            if (latLngArrayList.size() == 2)
                                builder.include(latLngArrayList.get(1));
                            if (latLngArrayList.size() == 3)
                                builder.include(latLngArrayList.get(2));
                            if (latLngArrayList.size() == 4)
                                builder.include(latLngArrayList.get(3));
                            if (latLngArrayList.size() == 5)
                                builder.include(latLngArrayList.get(4));
                            if (latLngArrayList.size() == 6)
                                builder.include(latLngArrayList.get(5));
                            if (latLngArrayList.size() == 7)
                                builder.include(latLngArrayList.get(6));
                            /**initialize the padding for map boundary*/
                            int padding = 50;
                            /**create the bounds from latlngBuilder to set into map camera*/
                            LatLngBounds bounds = builder.build();
                            /**create the camera with bounds and padding to set into map*/
                            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                        }

                    } else {
                        txtAccuracy.setText("accuracy level is high " + locationGPS.getAccuracy() + "m");
                        Toast.makeText(getApplicationContext(), "accuray level is more than 10 M", Toast.LENGTH_SHORT).show();
                    }
                }
                if (coordinatesList.size() > 2) {
                    btnJoin.setEnabled(true);
                } else {
                    btnJoin.setEnabled(false);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Location information is being gathered. Please wait!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (getIntent().getStringExtra("image").equals("1")) {
            imagefilesList = (ArrayList<Bitmap>) getIntent().getSerializableExtra("imagefilesList");
            imagelatlang2List = (ArrayList<CropPhotsModel>) getIntent().getSerializableExtra("image_coordinates");
            latlang_position = Integer.parseInt(getIntent().getStringExtra("position"));
            LatLng latLng = null;
            try {
                latLng = new LatLng(Double.parseDouble(imagelatlang2List.get(latlang_position).getPhotoCoordinates().getLattitude()),
                        Double.parseDouble(imagelatlang2List.get(latlang_position).getPhotoCoordinates().getLongitude()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (latlang_position >= 0) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng)
                        .title("Captured Pic")
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.camere_marker));
                InfoWindowData info = new InfoWindowData();
                info.setDate("Date & Time : " + "");
                info.setTransport("Reach the site by bus, car and train.");
                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(EditGpsCoordinatesActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(info);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                );
                polyline1.setPoints(latLngArrayList);
            }
        }
        if (cropModel.getArrayList_cropCoordinates().size() > 0 && cropModel.getArrayList_cropCoordinates() != null) {
            coordinatesList = cropModel.getArrayList_cropCoordinates();
            if (coordinatesList.size() > 2) {
                btnJoin.setEnabled(true);
                isjoned = true;
            }
            for (int i = 0; i < coordinatesList.size(); i++) {
                latLngArrayList.add(new LatLng(Double.parseDouble(coordinatesList.get(i).getLatitude()),
                        Double.parseDouble(coordinatesList.get(i).getLongitude())));
                maplatlangList.add(new LatLng(Double.parseDouble(coordinatesList.get(i).getLatitude()),
                        Double.parseDouble(coordinatesList.get(i).getLongitude())));
            }
        }
        if (latLngArrayList.size() > 0) {
            for (int i = 0; i < latLngArrayList.size(); i++) {
                mMap.setInfoWindowAdapter(null);
                mMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title("land coordinate"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngArrayList.get(i).latitude, latLngArrayList.get(i).longitude), 25));
                Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                );
                polyline1.setPoints(latLngArrayList);
            }
            mMap.addPolyline(new PolylineOptions()
                    .add(latLngArrayList.get(0), latLngArrayList.get(latLngArrayList.size() - 1)));

        }
        if (cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).size() > 0) {
            for (int i = 0; i < cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).size(); i++) {
                imagefilesduplicateList.add(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getBitmap());
                cropPhotsModel = new CropPhotsModel();
                cropPhotsModel.setLocation("");
                PhotoCoordinates photoCoordinates = new PhotoCoordinates();
                photoCoordinates.setLattitude(String.valueOf(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getPhotolatitude()));
                photoCoordinates.setLongitude(String.valueOf(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getPhotolongitude()));
                cropPhotsModel.setPhotoCoordinates(photoCoordinates);
                imagelatlang2duplicateList.add(cropPhotsModel);
                picturedatesduplicateList.add(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getDate());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(Double.parseDouble(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getPhotolatitude()),
                        Double.parseDouble(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getPhotolongitude())))
                        .title("Captured Pic")
                        .snippet("")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.camere_marker));
                InfoWindowData info = new InfoWindowData();
                info.setImage(cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getBitmap());
                info.setDate("Date & Time : " + cropManagementDatabaseHelper.getAllCropPhotoTableData(cropModel.getCropId()).get(i).getDate());
                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(EditGpsCoordinatesActivity.this);
                mMap.setInfoWindowAdapter(customInfoWindow);
                Marker m = mMap.addMarker(markerOptions);
                m.setTag(info);
            }
        }
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (!ispicked)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 23));
                ispicked = true;
            }
        });
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);


        } else {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    locationGPS = getLocation(LocationManager.GPS_PROVIDER);
                    askForGPS();
                } catch (Exception e) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION);
                }
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!(imagefilesList.size() == 0) || !(coordinatesList.size() == 0)) {
            if (coordinatesList.size() > 2) {
                if (isjoned) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("imagefilesList", imagefilesList);
                    returnIntent.putExtra("image_coordinates", imagelatlang2List);
                    returnIntent.putExtra("picturedatesList", picturedatesList);
                    returnIntent.putExtra("maplatlangList", maplatlangList);
                    returnIntent.putExtra("coordinatesList", coordinatesList);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "First Join The End Points", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Take Atleast 3 Points", Toast.LENGTH_SHORT).show();

            }

        } else {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            return true;
        }
        return false;
    }


    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (locationGPS != null) {
            latLng_camera = new LatLng(locationGPS.getLatitude(), locationGPS.getLongitude());
        }
        if (resultCode == activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void askForGPS() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        locationSettingResult = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        locationSettingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(activity, GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.menu_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_satelite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.menu_Terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String base64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        String s = getIntent().getStringExtra("croptype");
        ImageProcessor processor = new ImageProcessor(s.toLowerCase().trim());
        processor.execute(bitmap);

    }


    @Override
    public void onLocationChanged(Location location) {
        locationGPS = location;
        txtAccuracy.setText("accuracy " + location.getAccuracy() + " m");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            locationGPS = getLocation(LocationManager.GPS_PROVIDER);
            askForGPS();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    private void SaveImage(Bitmap bitmap) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imagefilesList.add(bitmap);
        Date date = Calendar.getInstance().getTime();
        String formattedDate = simpleDateFormat.format(date);
        picturedatesList.add(String.valueOf(formattedDate));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng_camera)
                .title("Captured Pic")
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.camere_marker));
        InfoWindowData info = new InfoWindowData();
        info.setImage(bitmap);
        info.setDate("Date & Time : " + date);
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(EditGpsCoordinatesActivity.this);
        mMap.setInfoWindowAdapter(customInfoWindow);
        Marker m = mMap.addMarker(markerOptions);
        m.setTag(info);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_camera));
        mMap.addPolyline(new PolylineOptions()
                .add(latLngArrayList.get(0), latLngArrayList.get(latLngArrayList.size() - 1)));
        isjoned = true;
        btnPin.setEnabled(false);
        btnJoin.setEnabled(true);
        cropImageModel = new CropImageModel();
        cropImageModel.setImage(destination);
        cropImageModel.setImageName(destination.getName());
        AwsFile awsFile = new AwsFile();
        awsFile.execute(cropImageModel);


    }

    private class AwsFile extends AsyncTask<CropImageModel, Void, CropImageModel> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditGpsCoordinatesActivity.this);
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();
        }

        @Override
        protected CropImageModel doInBackground(CropImageModel... files) {
            CropImageModel models = files[0];
            String retVal = AwsProcessor.postFile(models.getImageName(), models.getImage());
            if (retVal.equals(AwsProcessor.ERROR)) {
                models.setSuccess(false);
                models.setImageLocation(retVal);
            } else {
                models.setSuccess(true);
                models.setImageLocation(retVal);
            }
            return models;
        }

        @Override
        protected void onPostExecute(CropImageModel cropImageModels) {
            super.onPostExecute(cropImageModels);
            progressDialog.dismiss();
            if (cropImageModels.getImageLocation().contains(AwsProcessor.ERROR)) {
                Toast.makeText(getApplicationContext(), "Some Of Your Image Not Uploaded.", Toast.LENGTH_SHORT).show();

            }
            Log.d("cropImageModels", "values" + cropImageModels.getImageLocation());
            CropPhotsModel cropPhotsModel = new CropPhotsModel();
            cropPhotsModel.setLocation(cropImageModels.getImageLocation());
            PhotoCoordinates photoCoordinates = new PhotoCoordinates();
            photoCoordinates.setLattitude(String.valueOf(latLng_camera.latitude));
            photoCoordinates.setLongitude(String.valueOf(latLng_camera.longitude));
            cropPhotsModel.setPhotoCoordinates(photoCoordinates);
            imagelatlang2List.add(cropPhotsModel);
            ischanged = true;


        }
    }


    private class ImageProcessor extends AsyncTask<Bitmap, Void, WatsonResponse> {
        String contexName;
        Bitmap bitmap;
        ProgressDialog progressDialog;

        public ImageProcessor(String name) {
            contexName = name;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditGpsCoordinatesActivity.this);
            progressDialog.setMessage("Analysing the image...");
            progressDialog.show();
        }

        @Override
        protected WatsonResponse doInBackground(Bitmap... bitmaps) {
            bitmap = bitmaps[0];
            destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            try (FileOutputStream out = new FileOutputStream(destination)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Watson.isImageContains(contexName, destination);
        }

        @Override
        protected void onPostExecute(WatsonResponse response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            if (response.isValid) {
                SaveImage(bitmap);
            } else {
                AlertDialog dialog = new AlertDialog.Builder(EditGpsCoordinatesActivity.this)
                        .setTitle("Invalid Image")
                        .setMessage("We expected the photo of " + response.RequestedType + " but instead our AI engine sees the following: " + response.ResponseMessage + ".")
                        .setPositiveButton("Save Anyway", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SaveImage(bitmap);
                            }
                        })
                        .setNegativeButton("Take Again?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectImage();
                            }
                        })
                        .create();
                dialog.show();
            }
        }

    }
}

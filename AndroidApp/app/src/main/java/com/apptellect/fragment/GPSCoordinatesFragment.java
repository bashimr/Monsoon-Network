package com.apptellect.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apptellect.R;
import com.apptellect.activity.EditGpsCoordinatesActivity;
import com.apptellect.model.CropCoordinates;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link GPSCoordinatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GPSCoordinatesFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "arrayList_latlong";
    private static final String ARG_PARAM2 = "param2";
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    // TODO: Rename and change types of parameters
    private ArrayList<CropCoordinates> mParam1 = new ArrayList<CropCoordinates>();
    private ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    private String mParam2;
    private GoogleMap mMap;
    private AlertDialog dialog;
    private boolean isavailable;


    public GPSCoordinatesFragment() {
        // Required empty public constructor
    }

    public static GPSCoordinatesFragment newInstance(ArrayList<CropCoordinates> arrayList_latlong) {
        GPSCoordinatesFragment fragment = new GPSCoordinatesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, arrayList_latlong);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (ArrayList<CropCoordinates>) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gpscoordinates, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(true);

        if (mParam1.size() > 0) {
            for (int i = 0; i < mParam1.size(); i++) {
                latLngs.add(new LatLng(Double.parseDouble(mParam1.get(i).getLatitude()),
                        Double.parseDouble(mParam1.get(i).getLongitude())));
            }

            // Instantiating the class MarkerOptions to plot marker on the map
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting latitude and longitude of the marker position
            markerOptions.position(new LatLng(latLngs.get(0).latitude, latLngs.get(0).longitude));
            // Setting titile of the infowindow of the marker
            markerOptions.title("Position");
            // Setting the content of the infowindow of the marker
            markerOptions.snippet("Latitude:" + latLngs.get(0).latitude + "," + "Longitude:" + latLngs.get(0).longitude);
            // Instantiating the class PolylineOptions to plot polyline in the map
            PolylineOptions polylineOptions = new PolylineOptions();
            // Setting the color of the polyline
            polylineOptions.color(Color.BLACK);
            // Setting the width of the polyline
            polylineOptions.width(5);
            // Setting points of polyline
            polylineOptions.addAll(latLngs);
            // Adding the polyline to the map
            mMap.addPolyline(polylineOptions);
            // Adding the marker to the map
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngs.get(0).latitude, latLngs.get(0).longitude), 25));
            mMap.addPolyline(new PolylineOptions()
                    .add(latLngs.get(0), latLngs.get(latLngs.size() - 1)));
        } else {
            dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Coordinates Not Found !")
                    .setMessage("Add Your Coordinates ")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.dismiss();

                        }
                    })
                    .create();
            if (!isavailable)
            dialog.show();

        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mParam1.size() > 0) {
            } else {
                if (getActivity()!=null) {
                    dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("Coordinates Not Found !")
                            .setMessage("Add Your Coordinates ")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();

                                }
                            })
                            .create();
                    dialog.show();
                    isavailable=true;
                }

            }

        }
    }
}

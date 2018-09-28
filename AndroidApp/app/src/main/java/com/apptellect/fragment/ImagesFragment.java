package com.apptellect.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;


import com.apptellect.R;
import com.apptellect.adapter.GalleryExpandableListAdapter;
import com.apptellect.dbdata.CropManagement_DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link ImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private ExpandableListView lvExp;
    private CropManagement_DatabaseHelper cropManagement_databaseHelper;
    private ExpandableListAdapter listAdapter;
    private boolean isavailable;


    public ImagesFragment() {
        // Required empty public constructor
    }


    public static ImagesFragment newInstance(String param1) {
        ImagesFragment fragment = new ImagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_images, container, false);
        cropManagement_databaseHelper=new CropManagement_DatabaseHelper(getActivity());
        lvExp=view.findViewById(R.id.lvExp);
        if (!(cropManagement_databaseHelper.getAllCropPhotoTableData(mParam1).size() ==0)){
            listAdapter = new GalleryExpandableListAdapter(getActivity(),cropManagement_databaseHelper.getAllCropPhotoTableData(mParam1));
            lvExp.setAdapter(listAdapter);
        }
        else {
            if (!isavailable)
            Toast.makeText(getActivity(), "There's No Any Recent Pictures.", Toast.LENGTH_SHORT);
        }
        // setting list adapter

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity()!=null) {
                if ((cropManagement_databaseHelper.getAllCropPhotoTableData(mParam1).size() == 0)) {
                    Toast.makeText(getActivity(), "There's No Any Recent Pictures.", Toast.LENGTH_SHORT).show();
                    isavailable=true;
                }
            }
        }
    }
}

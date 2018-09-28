package com.apptellect.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.activity.ContractDetailsActivity;
import com.apptellect.utilities.Utils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CropDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CropDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CROP_ID = "cropid";
    private static final String CROP_TYPE = "croptype";
    private static final String CROP_AREA = "croparea";
    private static final String SEEDING_DATE = "seedingdate";
    private static final String HARVEST_DATE = "harvestdate";
    private static final String CURRENCY = "currency";
    private static final String YIELD_VALUE = "yield_value";

    // TODO: Rename and change types of parameters
    private String croptype;
    private String croparea;
    private String seedingdate;
    private String harvestdate;
    private String currency;
    private String yield_value;
    private String cropId;
    private EditText edittext_crop_type;
    private EditText edittext_crop_are;
    private EditText edittext_seeding_date;
    private EditText edittext_harvest_date;
    private EditText edittext_currency;
    private EditText edittext_yield_value;
    private Button btnSubmitContract;
    private Utils utils;


    public CropDetailsFragment() {
        // Required empty public constructor
    }


    public static CropDetailsFragment newInstance(String cropdId,String CROP_TYPE1, String CROP_AREA1,String SEEDING_DATE1,
                                                  String HARVEST_DATE1, String CURRENCY1, String YIELD_VALUE1) {
        CropDetailsFragment fragment = new CropDetailsFragment();
        Bundle args = new Bundle();
        args.putString(CROP_ID, cropdId);
        args.putString(CROP_TYPE, CROP_TYPE1);
        args.putString(CROP_AREA, CROP_AREA1);
        args.putString(SEEDING_DATE, SEEDING_DATE1);
        args.putString(HARVEST_DATE, HARVEST_DATE1);
        args.putString(CURRENCY, CURRENCY1);
        args.putString(YIELD_VALUE, YIELD_VALUE1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cropId = getArguments().getString(CROP_ID);
            croptype = getArguments().getString(CROP_TYPE);
            croparea = getArguments().getString(CROP_AREA);
            seedingdate = getArguments().getString(SEEDING_DATE);
            harvestdate = getArguments().getString(HARVEST_DATE);
            currency = getArguments().getString(CURRENCY);
            yield_value = getArguments().getString(YIELD_VALUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_crop_details, container, false);
        utils=new Utils(getActivity());
        edittext_crop_type=view.findViewById(R.id.edittext_crop_type);
        edittext_crop_are=view.findViewById(R.id.edittext_crop_are);
        edittext_seeding_date=view.findViewById(R.id.edittext_seeding_date);
        edittext_harvest_date=view.findViewById(R.id.edittext_harvest_date);
        edittext_currency=view.findViewById(R.id.edittext_currency);
        edittext_yield_value=view.findViewById(R.id.edittext_yield_value);
        btnSubmitContract=view.findViewById(R.id.btnSubmitContract);
        if(utils.getRoleType().equalsIgnoreCase("Farmer")){
            btnSubmitContract.setVisibility(View.GONE);
        }else if(utils.getRoleType().equalsIgnoreCase("Donor")){
            btnSubmitContract.setVisibility(View.VISIBLE);
            btnSubmitContract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(), ContractDetailsActivity.class);
                    intent.putExtra("CROP_TYPE",croptype);
                    intent.putExtra("CROP_ID",cropId);
                    startActivity(intent);
                }
            });
        }


        edittext_crop_type.setText(croptype);
        edittext_crop_are.setText(croparea);
        edittext_seeding_date.setText(seedingdate);
        edittext_harvest_date.setText(harvestdate);
        edittext_currency.setText(currency);
        edittext_yield_value.setText(yield_value);

        return view;
    }




    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

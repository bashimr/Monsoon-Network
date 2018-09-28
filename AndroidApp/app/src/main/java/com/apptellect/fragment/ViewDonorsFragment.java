package com.apptellect.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.apptellect.R;
import com.apptellect.activity.CropMainActivity;
import com.apptellect.adapter.CropAdapter;
import com.apptellect.adapter.DonorAdapter;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.model.DonorModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewDonorsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewDonorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewDonorsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public ViewDonorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewDonorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewDonorsFragment newInstance(String param1, String param2) {
        ViewDonorsFragment fragment = new ViewDonorsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private Utils mUtils;
    private ApiInterface mApiService;
    private DonorAdapter mDonorAdapter;
    private List<DonorModel> mDonorModelList;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_donors, container, false);
        // Inflate the layout for this fragment
        mUtils=new Utils(getActivity());
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        getListOfCrop();
        return v;
    }
    public void getListOfCrop() {
        if (mUtils.checkConnection(getActivity())) {
            mUtils.ShowDialog();
            Call<List<DonorModel>> cropModelCall = mApiService.getListOfDonorId(mUtils.getMobileNo());
            cropModelCall.enqueue(new Callback<List<DonorModel>>() {
                @Override
                public void onResponse(Call<List<DonorModel>> call, Response<List<DonorModel>> response) {
                    mDonorModelList = new ArrayList<>();
                    mDonorModelList = response.body();
                    if (mDonorModelList != null && mDonorModelList.size() > 0) {
                        Log.d("result--->",""+mDonorModelList.size());
                        mDonorAdapter = new DonorAdapter(getActivity(), mDonorModelList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mDonorAdapter);
                    }else{
                        Toast.makeText(getActivity(),"No data",Toast.LENGTH_SHORT).show();
                    }
                    mUtils.DismissDialog();

                }

                @Override
                public void onFailure(Call<List<DonorModel>> call, Throwable t) {
                    mUtils.DismissDialog();
                }
            });
        } else {
            mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
        }

    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

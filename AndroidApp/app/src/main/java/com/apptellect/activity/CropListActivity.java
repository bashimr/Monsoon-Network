package com.apptellect.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.Toast;

import com.apptellect.R;
import com.apptellect.adapter.CropAdapter;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropListActivity extends AppCompatActivity {
    Utils mUtils;
    ApiInterface mApiService;
    List<AddNewCropModel> mCropModelList;
    RecyclerView recyclerView;
    CropAdapter mCropAdapter;
    ListView listviewMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        getListOfCrop();
        listviewMenu = (ListView) findViewById(R.id.listview_menu);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    //get the listofcrop
    public void getListOfCrop() {
        if (mUtils.checkConnection(this)) {
            mUtils.ShowDialog();
            Call<List<AddNewCropModel>> cropModelCall = mApiService.getListOfAllCrops();
            cropModelCall.enqueue(new Callback<List<AddNewCropModel>>() {
                @Override
                public void onResponse(Call<List<AddNewCropModel>> call, Response<List<AddNewCropModel>> response) {
                    mCropModelList = new ArrayList<>();
                    mCropModelList= response.body();
                    if (mCropModelList != null && mCropModelList.size() > 0) {
                        mCropAdapter = new CropAdapter(CropListActivity.this, mCropModelList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mCropAdapter);
                    } else {
                        Toast.makeText(CropListActivity.this, "No data", Toast.LENGTH_SHORT).show();
                    }
                    mUtils.DismissDialog();

                }

                @Override
                public void onFailure(Call<List<AddNewCropModel>> call, Throwable t) {
                    mUtils.DismissDialog();
                }
            });
        } else {
            mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
        }

    }
}

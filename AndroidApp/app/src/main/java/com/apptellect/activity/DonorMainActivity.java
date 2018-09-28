package com.apptellect.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.apptellect.R;
import com.apptellect.adapter.CriteriaItemBaseAdapter;
import com.apptellect.adapter.CropAdapter;
import com.apptellect.adapter.MenuAdapter;
import com.apptellect.adapter.PledgeAdapter;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.model.CriteriaItemModel;
import com.apptellect.model.PledgeModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonorMainActivity extends AppCompatActivity {
    private  ListView listViewCriteria;
    DrawerLayout mDrawerLayout;
    private ListView listviewMenu;
    private MenuAdapter menuAdapter;
    ImageView imageMenu;
    private Utils mUtils;
    private ApiInterface mApiService;
    private List<AddNewCropModel> mCropModelList;
    private List<PledgeModel> mPledgeModelList;
    private RecyclerView recyclerView;
    private CropAdapter mCropAdapter;
    private PledgeAdapter mPledgeAdapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view = getSupportActionBar().getCustomView();
        fab=findViewById(R.id.fab);
        imageMenu =(ImageView)findViewById(R.id.image_menu);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        listViewCriteria= (ListView)findViewById(R.id.listViewCriteria);
        listviewMenu=(ListView)findViewById(R.id.listview_menu);
        List<String> items=new ArrayList<>();
        items.add("Criteria");
        items.add("My Pledges");
        items.add("Profile");
        items.add("Logout");
        menuAdapter=new MenuAdapter(this,items);
        listviewMenu.setAdapter(menuAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DonorMainActivity.this,CropListActivity.class);
                startActivity(intent);
            }
        });


        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawers();
                }else{
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.donor_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getListOfPledge(){
        if (mUtils.checkConnection(this)) {
            mUtils.ShowDialog();
            String filter = "{\"donor\":\"" + mUtils.getMobileNo() + "\"}";
            Call<List<PledgeModel>> cropModelCall = mApiService.getListOfPledgeDetails(filter);
            cropModelCall.enqueue(new Callback<List<PledgeModel>>() {
                @Override
                public void onResponse(Call<List<PledgeModel>> call, Response<List<PledgeModel>> response) {
                    mPledgeModelList = new ArrayList<>();
                   // mPledgeModelList = response.body();

                    if(response.body()!=null){
                        Log.d("result--->",""+response.body().size());
                        for(PledgeModel pledgeModel:response.body()){
                            if(pledgeModel.getDonor().contains("resource:monsoon.apptellect.com.Donor#")){
                                String donor=pledgeModel.getDonor().replace("resource:monsoon.apptellect.com.Donor#","");
                                pledgeModel.setDonor(donor);
                                mPledgeModelList.add(pledgeModel);
                            }
                        }
                    }


                    if (mPledgeModelList != null && mPledgeModelList.size() > 0) {

                        mPledgeAdapter = new PledgeAdapter(DonorMainActivity.this, mPledgeModelList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mPledgeAdapter);
                    }
                    mUtils.DismissDialog();

                }

                @Override
                public void onFailure(Call<List<PledgeModel>> call, Throwable t) {
                    mUtils.DismissDialog();
                }
            });
        }else {
            mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getListOfPledge();
    }
}

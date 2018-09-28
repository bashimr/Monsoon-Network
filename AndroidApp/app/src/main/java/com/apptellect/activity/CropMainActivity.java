package com.apptellect.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.apptellect.R;
import com.apptellect.adapter.CropAdapter;
import com.apptellect.adapter.MenuAdapter;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CropMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Utils mUtils;
    ApiInterface mApiService;
    List<AddNewCropModel> mCropModelList;
    RecyclerView recyclerView;
    CropAdapter mCropAdapter;
    ListView listviewMenu;
    MenuAdapter menuAdapter;
    ImageView imageMenu;
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view = getSupportActionBar().getCustomView();
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils = new Utils(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        imageMenu = (ImageView) findViewById(R.id.image_menu);
        getListOfCrop();
        listviewMenu = (ListView) findViewById(R.id.listview_menu);
        List<String> items = new ArrayList<>();
        items.add("My Crops");
        items.add("Profile");
        items.add("Logout");
        menuAdapter = new MenuAdapter(this, items);
        listviewMenu.setAdapter(menuAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CropMainActivity.this, AddCropActivity.class);
                intent.putExtra("from", "edit");
                startActivity(intent);
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawers();
                } else {
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
        getMenuInflater().inflate(R.menu.crop_main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_mycrop) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
        } else if (id == R.id.nav_logout) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getListOfCrop() {
        if (mUtils.checkConnection(this)) {
            mUtils.ShowDialog();

            String filter = "{\"farmerOwner\":\"" + mUtils.getMobileNo() + "\"}";
            Call<List<AddNewCropModel>> cropModelCall = mApiService.getListOfCropsByFarmerID(filter);
            cropModelCall.enqueue(new Callback<List<AddNewCropModel>>() {
                @Override
                public void onResponse(Call<List<AddNewCropModel>> call, Response<List<AddNewCropModel>> response) {
                    mCropModelList = new ArrayList<>();
                    String mobile = "resource:monsoon.apptellect.com.Farmer#" + mUtils.getMobileNo();

                    for (AddNewCropModel model :
                            response.body()) {
                        if (model.getFarmerOwner() != null) {
                            if (model.getFarmerOwner().endsWith(mobile)) {
                                mCropModelList.add(model);
                            }
                        }
                    }
                    if (mCropModelList != null && mCropModelList.size() > 0) {
                        mCropAdapter = new CropAdapter(CropMainActivity.this, mCropModelList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mCropAdapter);
                    } else {
                        Toast.makeText(CropMainActivity.this, "No data", Toast.LENGTH_SHORT).show();
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

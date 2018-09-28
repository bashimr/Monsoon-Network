package com.apptellect.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.apptellect.R;
import com.apptellect.adapter.MenuAdapter;
import com.apptellect.fragment.CropDetailsFragment;
import com.apptellect.fragment.GPSCoordinatesFragment;
import com.apptellect.fragment.ImagesFragment;
import com.apptellect.fragment.WeatherForecastFragment;
import com.apptellect.model.AddNewCropModel;

import java.util.ArrayList;
import java.util.List;


public class DonorSwipeActivity extends AppCompatActivity {

    // Tab titles
    private String[] tabs;
    int tabSize=0;
    private TabLayout tabLayout;
    private ViewPager viewPager;
   TapFragmentManager fragmentManger;
    List<Fragment> fragments;
    private ListView listviewMenu;
    private MenuAdapter menuAdapter;
    private DrawerLayout mDrawerLayout;
    ImageView imageMenu;
    public static String CROP_DATA = "cropData";

    AddNewCropModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_swipe);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        View view = getSupportActionBar().getCustomView();
        model = (AddNewCropModel) getIntent().getSerializableExtra(CROP_DATA);
        viewPager=(ViewPager)findViewById(R.id.view_pager) ;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        imageMenu =(ImageView)findViewById(R.id.image_menu);
        fragments= getFragments();
        commonTabLayoutDesign();
        listviewMenu =(ListView)findViewById(R.id.listview_menu);
        List<String> items=new ArrayList<>();
        items.add("Criteria");
        items.add("My Pledges");
        items.add("Profile");
        items.add("Logout");
        menuAdapter=new MenuAdapter(this,items);
        listviewMenu.setAdapter(menuAdapter);

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
    public void commonTabLayoutDesign(){
        tabs = new String[]{"GPS Coordinates", "Crop Details", "Images","Weather Forecast"};
        //setting Tab layout (number of Tabs = number of ViewPager pages)
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        for (int i = 0; i < 4; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
        }

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        fragmentManger=new TapFragmentManager(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentManger);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    //Common Header Tab design
    public  class TapFragmentManager extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        public TapFragmentManager(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);

        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }


    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(GPSCoordinatesFragment.newInstance(model.getArrayList_cropCoordinates()));
        fList.add(CropDetailsFragment.newInstance(model.getCropId(),model.getTypeOfCrop(),
                model.getCropArea(), model.getSeedingDate(),
                model.getHarvestDate(), model.getCropCurrency(),
                model.getMaxYieldValue()));
        fList.add(ImagesFragment.newInstance(model.getCropId()));
        if (model.getArrayList_cropCoordinates().size() > 0) {
            fList.add(WeatherForecastFragment
                    .newInstance(model.getArrayList_cropCoordinates().get(0).getLatitude(),
                            model.getArrayList_cropCoordinates().get(0).getLongitude()));
        } else {
            fList.add(WeatherForecastFragment
                    .newInstance(null, null));
        }
        return fList;
    }
}

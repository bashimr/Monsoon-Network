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
import com.apptellect.fragment.ViewDonorsFragment;
import com.apptellect.fragment.ViewHistoryFragment;
import com.apptellect.fragment.WeatherForecastFragment;
import com.apptellect.model.AddNewCropModel;

import java.util.ArrayList;
import java.util.List;

public class CropSwipeActivity extends AppCompatActivity {

    public static String CROP_DATA = "cropData";

    AddNewCropModel model;
    int tabSize = 0;
    TapFragmentManager fragmentManger;
    List<Fragment> fragments;
    ImageView image_menu;
    // Tab titles
    private String[] tabs;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListView listviewMenu;
    private MenuAdapter menuAdapter;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_swipe);
        model = (AddNewCropModel) getIntent().getSerializableExtra(CROP_DATA);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        viewPager = findViewById(R.id.view_pager);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        image_menu = findViewById(R.id.image_menu);
        fragments = getFragments();
        commonTabLayoutDesign();
        listviewMenu = (ListView) findViewById(R.id.listview_menu);
        List<String> items = new ArrayList<>();
        items.add("My Crops");
        items.add("Profile");
        items.add("Logout");
        menuAdapter = new MenuAdapter(this, items);
        listviewMenu.setAdapter(menuAdapter);
        image_menu.setOnClickListener(new View.OnClickListener() {
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

    public void commonTabLayoutDesign() {
        tabs = new String[]{"GPS Coordinates", "Crop Details", "Images", "Weather Forecast", "View Donors"};
        //setting Tab layout (number of Tabs = number of ViewPager pages)
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        for (int i = 0; i < tabs.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
        }
        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        fragmentManger = new TapFragmentManager(getSupportFragmentManager(), fragments);
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
        //viewPager.setOffscreenPageLimit(4);
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
        fList.add(ViewDonorsFragment.newInstance("Fragment 5", ""));
        return fList;
    }
    //Common Header Tab design
    public class TapFragmentManager extends FragmentPagerAdapter {
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
}

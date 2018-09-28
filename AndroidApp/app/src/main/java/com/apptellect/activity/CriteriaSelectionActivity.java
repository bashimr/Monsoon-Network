package com.apptellect.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.apptellect.R;
import com.apptellect.adapter.CriteriaItemBaseAdapter;
import com.apptellect.model.CriteriaItemModel;

import java.util.ArrayList;
import java.util.List;

public class CriteriaSelectionActivity extends AppCompatActivity {
    private  ListView listViewCriteria;
    private List<CriteriaItemModel> criteriaItemModelList;
    private CriteriaItemBaseAdapter  criteriaItemBaseAdapter;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criteria_selection);
        listViewCriteria= (ListView)findViewById(R.id.listViewCriteria);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        criteriaItemModelList= this.getInitViewItemDtoList();
        criteriaItemBaseAdapter = new CriteriaItemBaseAdapter(getApplicationContext(), criteriaItemModelList);
        criteriaItemBaseAdapter.notifyDataSetChanged();
        listViewCriteria.setAdapter(criteriaItemBaseAdapter);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDonorMain=new Intent(CriteriaSelectionActivity.this,DonorMainActivity.class);
                startActivity(intentDonorMain);
            }
        });
    }
    // Return an initialize list of CriteriaItemModel
    private List<CriteriaItemModel> getInitViewItemDtoList()
    {
        String itemTitle[] = {"Crops", "Crops", "Crops", "Locations", "Locations", "Locations", "Disaster Type", "Disaster Type", "Disaster Type"};
        String itemTextArr[] = {"Wheat", "Mango", "Apple", "BANGLADESH", "INDIA", "MALDIVES", "Floods", "Fires", "Earthquake"};

        List<CriteriaItemModel> ret = new ArrayList<CriteriaItemModel>();

        int length = itemTextArr.length;

        for(int i=0;i<length;i++)
        {
            String itemText = itemTextArr[i];
            String title = itemTitle[i];

            CriteriaItemModel criteriaItemModel= new CriteriaItemModel();
            criteriaItemModel.setChecked(false);
            criteriaItemModel.setItemText(itemText);
            criteriaItemModel.setItemTitle(title);

            ret.add(criteriaItemModel);
        }

        return ret;
    }
}

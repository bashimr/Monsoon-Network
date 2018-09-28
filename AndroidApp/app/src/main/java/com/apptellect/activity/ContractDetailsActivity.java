package com.apptellect.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apptellect.R;
import com.apptellect.model.AddCropModel;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.model.CommitPledgeModel;
import com.apptellect.model.CropModel;
import com.apptellect.model.DonorModel;
import com.apptellect.model.PledgeModel;
import com.apptellect.utilities.Utils;
import com.apptellect.webservice.ApiClient;
import com.apptellect.webservice.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractDetailsActivity extends AppCompatActivity {
    private Spinner spinner_pledge,spinner_status;
    private Button btnSubmit;
    private Utils mUtils;
    private ApiInterface mApiService;
    private EditText edittext_amt;
     String[] Status=null,Currency=null;
     private boolean mIsEdit=false;
     private String mDonorType="",mPledge_Id="",mCropId="";
     private CheckBox checkbox_terms;
    private TextView txt_crop_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mApiService = ApiClient.getClient().create(ApiInterface.class);
        mUtils=new Utils(this);
        spinner_pledge=findViewById(R.id.spinner_pledge);
        spinner_status=findViewById(R.id.spinner_status);
        btnSubmit=findViewById(R.id.btnSubmit);
        edittext_amt=findViewById(R.id.edittext_amt);
        checkbox_terms=findViewById(R.id.checkbox_terms);
        txt_crop_type=findViewById(R.id.txt_crop_type);
        dropdownCurrency();
        if(getIntent().getStringExtra("PLEDGE_ID")!=null&&getIntent().getStringExtra("PLEDGE_ID").length()>0){
            mPledge_Id=getIntent().getStringExtra("PLEDGE_ID");
            getPledgeDetails(getIntent().getStringExtra("PLEDGE_ID"));
            btnSubmit.setText("Update");
            mIsEdit=true;
        }
        if(getIntent().getStringExtra("CROP_TYPE")!=null&&getIntent().getStringExtra("CROP_TYPE").length()>0){
            txt_crop_type.setText(getIntent().getStringExtra("CROP_TYPE"));
        }
        if(getIntent().getStringExtra("CROP_ID")!=null&&getIntent().getStringExtra("CROP_ID").length()>0){
            mCropId=getIntent().getStringExtra("CROP_ID");
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUtils.checkConnection(ContractDetailsActivity.this)) {
                    if (isValidation()) {
                        mUtils.ShowDialog();
                        if (mIsEdit) {
                            PledgeModel pledgeModel = new PledgeModel();
                            pledgeModel.setPledgeCurrency(spinner_pledge.getSelectedItem().toString());
                            pledgeModel.setAmount(Integer.parseInt(edittext_amt.getText().toString()));
                            pledgeModel.setStatus(spinner_status.getSelectedItem().toString());
                            pledgeModel.setDonor(mUtils.getMobileNo());
                            Call<PledgeModel> pledgeModelCall = mApiService.updatePledge(pledgeModel, mPledge_Id);
                            pledgeModelCall.enqueue(new Callback<PledgeModel>() {
                                @Override
                                public void onResponse(Call<PledgeModel> call, Response<PledgeModel> response) {
                                    //Toast.makeText(ContractDetailsActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                                    commitPledgeToCrop(mPledge_Id,mCropId);
                                }

                                @Override
                                public void onFailure(Call<PledgeModel> call, Throwable t) {
                                    mUtils.DismissDialog();
                                }
                            });
                        } else {
                            final  PledgeModel pledgeModel = new PledgeModel();
                            pledgeModel.setPledgeId(String.valueOf(mUtils.getRandomNo()));
                            pledgeModel.setPledgeCurrency(spinner_pledge.getSelectedItem().toString());
                            pledgeModel.setAmount(Integer.parseInt(edittext_amt.getText().toString()));
                            pledgeModel.setStatus(spinner_status.getSelectedItem().toString());
                            pledgeModel.setDonor(mUtils.getMobileNo());
                            Call<PledgeModel> pledgeModelCall = mApiService.createPledge(pledgeModel);
                            pledgeModelCall.enqueue(new Callback<PledgeModel>() {
                                @Override
                                public void onResponse(Call<PledgeModel> call, Response<PledgeModel> response) {
                                    commitPledgeToCrop(pledgeModel.getPledgeId(),mCropId);

                                }
                                @Override
                                public void onFailure(Call<PledgeModel> call, Throwable t) {
                                    mUtils.DismissDialog();
                                }
                            });
                        }
                    }

                }else{
                    mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    public void dropdownCurrency(){
         Currency = new String[]{
                "US_DOLLAR",
                "INDIAN_RUPEE",
                "PAKISTANI_RUPEE",
                "SRILANKA_RUPEE",
                "BHUTANESE_NGULTRUM",
                "BANGLADESHI_TAKA",
                "MALDIVIAN_RUFIYAA",
        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,Currency)
        {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);

                return view;

            }
        };
        spinner_pledge.setAdapter(spinnerArrayAdapter);

        Status = new String[]{
                "NONCOMMITTED",
                "COMMITTED",
                "IN_REVIEW",
                "PAYMENT_AUTHORIZED",
                "EXPIRED",
                "PAID",
                "VOID",
        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapterStatus= new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_dropdown_item,Status)
        {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);

                return view;

            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub

                View view = super.getView(position, convertView, parent);

                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);

                return view;

            }
        };
        spinner_status.setAdapter(spinnerArrayAdapterStatus);
    }

    public void getPledgeDetails(String pledgeId){
        if (mUtils.checkConnection(this)) {
            mUtils.ShowDialog();
            Call<PledgeModel> pledgeModelCall = mApiService.getPledgeIdDetails(pledgeId);
            pledgeModelCall.enqueue(new Callback<PledgeModel>() {
                @Override
                public void onResponse(Call<PledgeModel> call, Response<PledgeModel> response) {
                    PledgeModel pledgeModel=response.body();
                    if(pledgeModel!=null){
                      if(pledgeModel.getAmount()!=null){
                          edittext_amt.setText(String.valueOf(pledgeModel.getAmount()));
                      }
                      if(pledgeModel.getPledgeCurrency()!=null&&pledgeModel.getPledgeCurrency().length()>0){
                          int aPosition=0;
                              for(int i=0; i < Currency.length; i++)
                                  if(Currency[i].contains(pledgeModel.getPledgeCurrency()))
                                      aPosition = i;

                          Log.d("Data-->","Currency--"+Currency+aPosition);
                          spinner_pledge.setSelection(aPosition);
                      }
                        if(pledgeModel.getStatus()!=null&&pledgeModel.getStatus().length()>0){
                            int aPosition=0;
                            for(int i=0; i < Status.length; i++)
                                if(Status[i].contains(pledgeModel.getStatus()))
                                    aPosition = i;

                            Log.d("Data-->","Status--"+Status+aPosition);
                            spinner_status.setSelection(aPosition);
                        }
                        getTransactionID();
                    }

                }

                @Override
                public void onFailure(Call<PledgeModel> call, Throwable t) {
                    mUtils.DismissDialog();
                }
            });
        } else {
            mUtils.ShowAlert("Internet Error", getResources().getString(R.string.CheckInternet));
        }
    }
    public boolean isValidation(){
        if(edittext_amt.getText().toString().trim().length()==0){
            Toast.makeText(ContractDetailsActivity.this, "Please enter a amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    public void commitPledgeToCrop(String mPledge_Id,String cropId){
        if (!mIsEdit) {
            CommitPledgeModel commitPledgeModel=new CommitPledgeModel();
            commitPledgeModel.setPledgeId(mPledge_Id);
            commitPledgeModel.setCropId(cropId);
            Call<CommitPledgeModel>  commitPledgeModelCall=mApiService.createCommitPledge(commitPledgeModel);
            commitPledgeModelCall.enqueue(new Callback<CommitPledgeModel>() {
                @Override
                public void onResponse(Call<CommitPledgeModel> call, Response<CommitPledgeModel> response) {
                    mUtils.DismissDialog();
                    finish();
                }

                @Override
                public void onFailure(Call<CommitPledgeModel> call, Throwable t) {

                }
            });
         }else{
            CommitPledgeModel commitPledge=new CommitPledgeModel();
            commitPledge.setCropId(cropId);
            Call<CommitPledgeModel>  commitPledgeModelCall=mApiService.updateCommitPledge(commitPledge,mPledge_Id);
            commitPledgeModelCall.enqueue(new Callback<CommitPledgeModel>() {
                @Override
                public void onResponse(Call<CommitPledgeModel> call, Response<CommitPledgeModel> response) {
                    mUtils.DismissDialog();
                    finish();
                }

                @Override
                public void onFailure(Call<CommitPledgeModel> call, Throwable t) {

                }
            });
        }

    }

    public void getTransactionID(){
        Call<List<CommitPledgeModel>>  commitPledgeModelCall=mApiService.getCommitPledge();
        commitPledgeModelCall.enqueue(new Callback<List<CommitPledgeModel>>() {
            @Override
            public void onResponse(Call<List<CommitPledgeModel>> call, Response<List<CommitPledgeModel>> response) {
                List<CommitPledgeModel> commitPledgeModels=response.body();
                if(commitPledgeModels!=null&&commitPledgeModels.size()>0){
                    for(CommitPledgeModel commitPledgeModel:commitPledgeModels){
                        if(commitPledgeModel.getPledgeId().equalsIgnoreCase(mPledge_Id)){
                            mCropId=commitPledgeModel.getCropId();
                        }
                    }
                    getCropDetails(mCropId);
                }

            }

            @Override
            public void onFailure(Call<List<CommitPledgeModel>> call, Throwable t) {

            }
        });

    }

    public void getCropDetails(String cropID){
        Call<AddNewCropModel>  commitPledgeModelCall=mApiService.getCropDetails(cropID);
        commitPledgeModelCall.enqueue(new Callback<AddNewCropModel>() {
            @Override
            public void onResponse(Call<AddNewCropModel> call, Response<AddNewCropModel> response) {
                AddNewCropModel addNewCropModel=response.body();
                if(addNewCropModel!=null){
                    if(addNewCropModel.getCropId()!=null){
                        mCropId=addNewCropModel.getCropId();
                    }
                    if(addNewCropModel.getTypeOfCrop()!=null&&addNewCropModel.getTypeOfCrop().length()>0){
                        txt_crop_type.setText(addNewCropModel.getTypeOfCrop());
                    }
                }
                mUtils.DismissDialog();
            }

            @Override
            public void onFailure(Call<AddNewCropModel> call, Throwable t) {

            }
        });
    }

}

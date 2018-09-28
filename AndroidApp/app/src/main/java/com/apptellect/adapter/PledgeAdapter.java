package com.apptellect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.activity.ContractDetailsActivity;
import com.apptellect.model.PledgeModel;
import com.apptellect.utilities.Utils;

import java.util.List;

public class PledgeAdapter extends RecyclerView.Adapter<PledgeAdapter.MyViewHolder> {

    private Context mContext;
    private List<PledgeModel> pledgeModelList;
    private Utils mUtils;

    public PledgeAdapter(Context mContext, List<PledgeModel> pledgeModelList) {
        this.mContext = mContext;
        this.pledgeModelList = pledgeModelList;
        mUtils = new Utils(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pledge, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PledgeModel pledgeModel = pledgeModelList.get(position);
        holder.txtPledgeId.setText("# " + pledgeModel.getPledgeId());
        holder.btnEdit.setTag(pledgeModel.getPledgeId());
        holder.txtType.setText(pledgeModel.getDonor());
        if (pledgeModel.getStatus() != null) {
            holder.txtStaus.setText(pledgeModel.getStatus());
        }
        if (pledgeModel.getAmount() != null) {
            holder.txtAmtValue.setText(String.valueOf(pledgeModel.getAmount()));
        }
        if (pledgeModel.getPledgeCurrency() != null) {
            holder.txtPledgeValue.setText(pledgeModel.getPledgeCurrency());
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pledgeId=view.getTag().toString();
                Log.d("pledgeId-->",""+pledgeId);
                if (mUtils.getRoleType().equalsIgnoreCase("Donor")) {
                    Intent intentContractDetails = new Intent(mContext, ContractDetailsActivity.class);
                    intentContractDetails.putExtra("PLEDGE_ID", pledgeId);
                    intentContractDetails.putExtra("PLEDGE_ID", pledgeId);
                    mContext.startActivity(intentContractDetails);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return pledgeModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private  Button btnEdit;
        public TextView txtPledgeId, txtType, txtAmtValue, txtPledgeValue, txtStaus;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            txtPledgeId = (TextView) view.findViewById(R.id.txt_pledge_id);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtAmtValue = (TextView) view.findViewById(R.id.txt_amt_value);
            txtPledgeValue = (TextView) view.findViewById(R.id.txt_pledge_value);
            txtStaus = (TextView) view.findViewById(R.id.txt_staus);
            btnEdit = (Button) view.findViewById(R.id.btn_edit);
        }
    }
}

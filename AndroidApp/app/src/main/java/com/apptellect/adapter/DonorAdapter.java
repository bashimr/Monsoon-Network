package com.apptellect.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.model.DonorModel;
import com.apptellect.utilities.Utils;

import java.util.List;

public class DonorAdapter extends  RecyclerView.Adapter<DonorAdapter.MyViewHolder> {

    private Context mContext;
    private List<DonorModel> donorModelList;
    private Utils mUtils;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtType, txtDonorId, txtNo;
        public ImageView thumbnail;
        private CardView card_view;
        private LinearLayout linear_container;

        public MyViewHolder(View view) {
            super(view);
            txtType = (TextView) view.findViewById(R.id.txt_type);
            txtDonorId = (TextView) view.findViewById(R.id.txt_donor_id);
            txtNo = (TextView) view.findViewById(R.id.txt_no);
            card_view = (CardView) view.findViewById(R.id.card_view);
        }
    }


    public DonorAdapter(Context mContext, List<DonorModel> donorModelList) {
        this.mContext = mContext;
        this.donorModelList = donorModelList;
        mUtils = new Utils(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_donor, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonorModel donorModel = donorModelList.get(position);
        holder.txtDonorId.setText("# " + donorModel.getDonorId());
        holder.txtType.setText(donorModel.getDonorType());
        if((donorModel.getContact().getPhoneNumber()!=null)){
            holder.txtNo.setText(donorModel.getContact().getPhoneNumber());
        }

    }



    @Override
    public int getItemCount() {
        return donorModelList.size();
    }
}
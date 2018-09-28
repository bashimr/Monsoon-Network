package com.apptellect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.activity.CropSwipeActivity;
import com.apptellect.activity.DonorSwipeActivity;
import com.apptellect.activity.EditCropActivity;
import com.apptellect.model.AddNewCropModel;
import com.apptellect.utilities.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.MyViewHolder> {

    private Context mContext;
    private List<AddNewCropModel> cropModelList;
    private Utils mUtils;

    public CropAdapter(Context mContext, List<AddNewCropModel> cropModelList) {
        this.mContext = mContext;
        this.cropModelList = cropModelList;
        mUtils = new Utils(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_crop_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final AddNewCropModel cropModel = cropModelList.get(position);
        holder.txtCropId.setText("# " + cropModel.getCropId());
        holder.txtCropType.setText(cropModel.getTypeOfCrop());
        if (cropModel.getStatus() != null) {
            holder.txtStaus.setText(cropModel.getStatus());
        }
        if(mUtils.getRoleType().equalsIgnoreCase("Donor")){
            holder.btn_edit.setVisibility(View.GONE);
        }else if(mUtils.getRoleType().equalsIgnoreCase("Farmer")){
            holder.btn_edit.setVisibility(View.VISIBLE);
        }
        if (cropModel.getTypeOfCrop().equalsIgnoreCase("WHEAT")) {
            holder.cardView.setBackgroundResource(R.drawable.card_bg_wheat);
        } else if (cropModel.getTypeOfCrop().equalsIgnoreCase("COTTON")) {
            holder.cardView.setBackgroundResource(R.drawable.card_bg_cotton);
        } else {
            holder.cardView.setBackgroundResource(R.drawable.card_bg_cotton);
        }
        if (cropModel.getSeedingDate() != null && cropModel.getSeedingDate().length() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(cropModel.getSeedingDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy");
            String targetdatevalue = targetFormat.format(sourceDate);
            holder.txtSeedingDate.setText(targetdatevalue);
        }

        if (cropModel.getHarvestDate() != null && cropModel.getHarvestDate().length() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date sourceDate = null;
            try {
                sourceDate = dateFormat.parse(cropModel.getSeedingDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy");
            String targetdatevalue = targetFormat.format(sourceDate);
            holder.txtHarvestDate.setText(targetdatevalue);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUtils.getRoleType() != null) {
                    if (mUtils.getRoleType().equalsIgnoreCase("Farmer")) {
                        Intent intentCropSwipe = new Intent(mContext, CropSwipeActivity.class);
                        intentCropSwipe.putExtra(CropSwipeActivity.CROP_DATA, cropModel);
                        mContext.startActivity(intentCropSwipe);
                    } else if (mUtils.getRoleType().equalsIgnoreCase("Donor")){
                        Intent intentCropSwipe = new Intent(mContext, DonorSwipeActivity.class);
                        intentCropSwipe.putExtra(CropSwipeActivity.CROP_DATA, cropModel);
                        mContext.startActivity(intentCropSwipe);
                    }
                }
            }
        });
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUtils.getRoleType().equalsIgnoreCase("Farmer")) {
                    Intent intentCropEdit = new Intent(mContext, EditCropActivity.class);
                    intentCropEdit.putExtra(CropSwipeActivity.CROP_DATA, cropModel);
                    mContext.startActivity(intentCropEdit);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return cropModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final Button btn_edit;
        public TextView txtCropType, txtCropId, txtSeedingDate, txtHarvestDate, txtStaus;
        public ImageView thumbnail;
        private CardView cardView;
        private LinearLayout linear_container;

        public MyViewHolder(View view) {
            super(view);
            txtCropType = (TextView) view.findViewById(R.id.txt_crop_type);
            btn_edit = (Button) view.findViewById(R.id.btn_edit);
            txtCropId = (TextView) view.findViewById(R.id.txt_crop_id);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            txtStaus = (TextView) view.findViewById(R.id.txt_staus);
            txtSeedingDate = (TextView) view.findViewById(R.id.txt_seeding_date);
            txtHarvestDate = (TextView) view.findViewById(R.id.txt_harvest_date);
            cardView = (CardView) view.findViewById(R.id.card_view);
            linear_container = (LinearLayout) view.findViewById(R.id.linear_container);
        }
    }
}

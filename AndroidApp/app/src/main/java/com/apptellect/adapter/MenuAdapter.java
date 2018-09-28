package com.apptellect.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.activity.CropListActivity;
import com.apptellect.activity.CropMainActivity;
import com.apptellect.activity.DonorMainActivity;
import com.apptellect.activity.LoginActivity;
import com.apptellect.activity.RegistrationActivity;
import com.apptellect.utilities.Utils;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
   private List<String> itemList;
   private  Context context;
    LayoutInflater layoutInflater;
    private Utils utils;

    public MenuAdapter(Context context,List<String> itemList) {
       this.itemList=itemList;
       this.context=context;
        layoutInflater = LayoutInflater.from(context);
        utils=new Utils(context);

    }

    public int getCount() {
        return itemList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        convertView= layoutInflater.inflate(R.layout.row_menu, null);
        TextView title, detail;
        ImageView imageViewIcon;
        LinearLayout linear_container;
        title = (TextView) convertView.findViewById(R.id.title);
        imageViewIcon=(ImageView)convertView.findViewById(R.id.image_menu);
        linear_container=(LinearLayout)convertView.findViewById(R.id.linear_container);
        title.setText(itemList.get(position));
        linear_container.setTag(itemList.get(position));
        if(itemList.get(position).equalsIgnoreCase("MyCrop")){
            imageViewIcon.setBackground(context.getResources().getDrawable(R.mipmap.ic_launcher));
        }else  if(itemList.get(position).equalsIgnoreCase("Profile")){
            imageViewIcon.setBackground(context.getResources().getDrawable(R.drawable.profile));
        }else  if(itemList.get(position).equalsIgnoreCase("MyPledges")){
            imageViewIcon.setBackground(context.getResources().getDrawable(R.drawable.profile));
        }else{
            imageViewIcon.setBackground(context.getResources().getDrawable(R.drawable.logout));
        }
        linear_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag().toString().equalsIgnoreCase("Logout")){
                    utils.setIsRegister(false);
                    Intent intent=new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }else if(v.getTag().toString().equalsIgnoreCase("My Crops")){
                    if(utils.getRoleType().equalsIgnoreCase("Farmer")){
                        Intent intent=new Intent(context, CropMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }

                }else  if(v.getTag().toString().equalsIgnoreCase("Profile")){
                    Intent intent=new Intent(context, RegistrationActivity.class);
                    intent.putExtra("IS_PROFILE_EDIT",true);
                    context.startActivity(intent);
                }
                else  if(v.getTag().toString().equalsIgnoreCase("My Pledges")){
                    Intent intent=new Intent(context, DonorMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }

            }
        });

        return convertView;

}
}

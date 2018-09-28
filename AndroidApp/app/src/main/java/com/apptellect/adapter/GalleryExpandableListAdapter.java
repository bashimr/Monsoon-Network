package com.apptellect.adapter;
/**
 * Created by Karthi on 9/22/2018.
 */

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.dbdata.Crop_Photo_module;

public class GalleryExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> list_date; // header titles
    // child data in format of header title, child title
    private List<Crop_Photo_module> list_crop_photo_module;


    public GalleryExpandableListAdapter(Context context,
                                        List<Crop_Photo_module> list_crop_photo_module) {
        this._context = context;
        list_date = new ArrayList<>();
        for (int i = 0; i < list_crop_photo_module.size(); i++) {
            String currentDate = list_crop_photo_module.get(i).getDate();
            if (!list_date.contains(currentDate)) {
                list_date.add(currentDate);
            }
        }
        this.list_crop_photo_module = list_crop_photo_module;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        String currentDate = list_date.get(groupPosition);
        List<Crop_Photo_module> sortedList = new ArrayList<>();
        for (Crop_Photo_module module :
                list_crop_photo_module) {
            if (module.getDate().equals(currentDate)) {
                sortedList.add(module);
            }
        }
        return sortedList.get(childPosititon);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_gallery_photos, null);
        }
        else {

        }


        LinearLayout linearLayout=convertView.findViewById(R.id.linear_images);
        String currentDate = list_date.get(groupPosition);
        List<Crop_Photo_module> sortedList = new ArrayList<>();
        for (Crop_Photo_module module :
                list_crop_photo_module) {
            if (module.getDate().equals(currentDate)) {
                sortedList.add(module);
            }
        }

        if (!(linearLayout.getChildCount() ==0)) {
            linearLayout.removeAllViews();
        }
        linearLayout.addView(dynamicImages(sortedList.get(childPosition).getBitmap(), childPosition));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        String currentDate = list_date.get(groupPosition);
        List<Crop_Photo_module> sortedList = new ArrayList<>();
        for (Crop_Photo_module module :
                list_crop_photo_module) {
            if (module.getDate().equals(currentDate)) {
                sortedList.add(module);
            }
        }
        return sortedList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.list_date.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.list_date.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_gallery_header, null);
        }
        String currentDate = list_date.get(groupPosition);
        List<Crop_Photo_module> sortedList = new ArrayList<>();
        for (Crop_Photo_module module :
                list_crop_photo_module) {
            if (module.getDate().equals(currentDate)) {
                sortedList.add(module);
            }
        }
        TextView txt_header = convertView
                .findViewById(R.id.txt_header);
        txt_header.setText(currentDate);


        ExpandableListView mExpandableListView = (ExpandableListView) parent;

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    public View dynamicImages(Bitmap bitmap, final int id) {
        final LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
        View myView = inflater.inflate(R.layout.custom_image, null);
        ImageView imge_captured = (ImageView) myView.findViewById(R.id.imge_captured);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        imge_captured.setLayoutParams(layoutParams);
        imge_captured.setImageBitmap(bitmap);
        imge_captured.setId(id);
        imge_captured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });
        return myView;

    }
}
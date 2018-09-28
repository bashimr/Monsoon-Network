package com.apptellect.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apptellect.R;
import com.apptellect.model.CriteriaItemModel;

import java.util.List;

public class CriteriaItemBaseAdapter extends BaseAdapter {

    private List<CriteriaItemModel> criteriaItemModelList = null;

    private Context ctx = null;

    public CriteriaItemBaseAdapter(Context ctx, List<CriteriaItemModel> criteriaItemModelList) {
        this.ctx = ctx;
        this.criteriaItemModelList = criteriaItemModelList;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(criteriaItemModelList!=null)
        {
            ret = criteriaItemModelList.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int itemIndex) {
        Object ret = null;
        if(criteriaItemModelList!=null) {
            ret = criteriaItemModelList.get(itemIndex);
        }
        return ret;
    }

    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @Override
    public View getView(int itemIndex, View convertView, ViewGroup viewGroup) {

        ListViewItemViewHolder viewHolder = null;

        if(convertView!=null)
        {
            viewHolder = (ListViewItemViewHolder) convertView.getTag();
        }else
        {
            convertView = View.inflate(ctx, R.layout.row_criteria, null);
            CheckBox listItemCheckbox = (CheckBox) convertView.findViewById(R.id.list_view_item_checkbox);
            TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            LinearLayout linear_container=(LinearLayout)convertView.findViewById(R.id.linear_container);
            viewHolder = new ListViewItemViewHolder(convertView);

            viewHolder.setItemCheckbox(listItemCheckbox);

            viewHolder.setTitleTextView(txt_title);
            viewHolder.setlinearLayoutContainer(linear_container);

            convertView.setTag(viewHolder);
        }

        CriteriaItemModel criteriaItemModel = criteriaItemModelList.get(itemIndex);
        viewHolder.getItemCheckbox().setChecked(criteriaItemModel.isChecked());
        viewHolder.getItemCheckbox().setText(criteriaItemModel.getItemText());

        if(itemIndex==0||itemIndex==3||itemIndex==6){
            viewHolder.getTitleTextView().setVisibility(View.VISIBLE);
            viewHolder.getTitleTextView().setText(criteriaItemModel.getItemTitle());
            viewHolder.getContainer().setVisibility(View.VISIBLE);
        }else {
            viewHolder.getTitleTextView().setVisibility(View.GONE);
            viewHolder.getContainer().setVisibility(View.GONE);
        }


        return convertView;
    }
    public class ListViewItemViewHolder extends RecyclerView.ViewHolder {

        private CheckBox itemCheckbox;

        private TextView itemTextView,itemTitle;
        private LinearLayout linearLayoutContainer;

        public ListViewItemViewHolder(View itemView) {
            super(itemView);
        }

        public CheckBox getItemCheckbox() {
            return itemCheckbox;
        }

        public void setItemCheckbox(CheckBox itemCheckbox) {
            this.itemCheckbox = itemCheckbox;
        }

        public TextView getItemTextView() {
            return itemTextView;
        }

        public void setItemTextView(TextView itemTextView) {
            this.itemTextView = itemTextView;
        }
        public TextView getTitleTextView() {
            return itemTitle;
        }

        public void setTitleTextView(TextView itemTitle) {
            this.itemTitle = itemTitle;
        }

        public LinearLayout getContainer() {
            return linearLayoutContainer;
        }

        public void setlinearLayoutContainer(LinearLayout itemTitle) {
            this.linearLayoutContainer = itemTitle;
        }
    }


}
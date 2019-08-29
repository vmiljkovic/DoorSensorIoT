package com.iot.doorsensor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BTGroup extends RecyclerView.Adapter<BTGroup.ViewHolder> {
    private ArrayList<GroupInfo> mDataset;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout l) {
            super(l);
            mLinearLayout = l;
        }
    }

    public BTGroup(ArrayList<GroupInfo> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.group_list_item, parent, false);
        LinearLayout lv = v.findViewById(R.id.groupLayout);
        return new ViewHolder(lv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView groupName = holder.mLinearLayout.findViewById(R.id.groupName);
        ImageView deviceImage = holder.mLinearLayout.findViewById(R.id.deviceImage);
        groupName.setText(mDataset.get(position).mGroupName);
        switch (mDataset.get(position).mIconId) {
            case 1:
                deviceImage.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.house));
                break;
            case 2:
                deviceImage.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.office));
                break;
            case 3:
                deviceImage.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.cottage));
                break;
        }

        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

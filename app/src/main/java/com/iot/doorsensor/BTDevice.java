package com.iot.doorsensor;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BTDevice extends RecyclerView.Adapter<BTDevice.ViewHolder> {
    private ArrayList<DeviceInfo> mDataset;
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

    public BTDevice(ArrayList<DeviceInfo> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.device_list_item, parent, false);
        LinearLayout lv = v.findViewById(R.id.deviceLayout);
        return new ViewHolder(lv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TextView deviceName = holder.mLinearLayout.findViewById(R.id.deviceName);
        ImageView deviceImage = holder.mLinearLayout.findViewById(R.id.deviceImage);
        CheckBox isSelected = holder.mLinearLayout.findViewById(R.id.checkb);
        deviceName.setText(mDataset.get(position).mDeviceName);
        isSelected.setChecked(mDataset.get(position).mIsSelected);
        isSelected.setVisibility(mDataset.get(position).mIsEditMode ? View.VISIBLE : View.GONE);
        isSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataset.get(position).mIsSelected = !mDataset.get(position).mIsSelected;
            }
        });
        switch (mDataset.get(position).mStatus) {
            case 0:
                deviceImage.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.sensor_gray));
                break;
            case 1:
                deviceImage.setBackground(holder.itemView.getContext().getResources().getDrawable(R.drawable.sensor_green));
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

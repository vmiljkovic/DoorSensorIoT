package com.iot.doorsensor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class AddGroupDialog extends DialogFragment {
    public interface NoticeAddGroupDialogListener {
        void onAddGroupDialogPositiveClick(DialogFragment dialog);
    }

    public String groupName;
    public int iconId;

    NoticeAddGroupDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (NoticeAddGroupDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeAddGroupDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.edit_file_name, null))
                .setPositiveButton(getResources().getString(R.string.savebtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText group_name = getDialog().findViewById(R.id.group_name);
                        ToggleButton iconHouse = getDialog().findViewById(R.id.iconHouse);
                        ToggleButton iconOffice = getDialog().findViewById(R.id.iconOffice);
                        ToggleButton iconCottage = getDialog().findViewById(R.id.iconCottage);
                        groupName = group_name.getText().toString();
                        if (iconHouse.isChecked())
                            iconId = Constants.iconHouse;
                        else if (iconOffice.isChecked())
                            iconId = Constants.iconOffice;
                        else if (iconCottage.isChecked())
                            iconId = Constants.iconCottage;

                        iconHouse.getBackground().clearColorFilter();
                        iconOffice.getBackground().clearColorFilter();
                        iconCottage.getBackground().clearColorFilter();
                        mListener.onAddGroupDialogPositiveClick(AddGroupDialog.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancelbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddGroupDialog.this.getDialog().cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        final Button n = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        n.setTextColor(getResources().getColor(R.color.colorPrimary));
        final Button p = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        p.setTextColor(getResources().getColor(R.color.colorPrimary));
        p.setVisibility(View.INVISIBLE);

        final EditText groupName = alert.findViewById(R.id.group_name);
        final ToggleButton iconHouse = alert.findViewById(R.id.iconHouse);
        final ToggleButton iconOffice = alert.findViewById(R.id.iconOffice);
        final ToggleButton iconCottage = alert.findViewById(R.id.iconCottage);

        iconHouse.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
        iconOffice.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
        iconCottage.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);

        groupName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0 && (iconHouse.isChecked() || iconOffice.isChecked() || iconCottage.isChecked()))
                    p.setVisibility(View.VISIBLE);
                else
                    p.setVisibility(View.INVISIBLE);
            }
        });
        iconHouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    iconHouse.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
                    iconCottage.setChecked(false);
                    iconOffice.setChecked(false);
                    if(groupName.getText().length() > 0)
                        p.setVisibility(View.VISIBLE);
                } else {
                    iconHouse.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
                    if(!iconHouse.isChecked() && !iconOffice.isChecked() && !iconCottage.isChecked())
                        p.setVisibility(View.INVISIBLE);
                }
            }
        });

        iconOffice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    iconOffice.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
                    iconHouse.setChecked(false);
                    iconCottage.setChecked(false);
                    if(groupName.getText().length() > 0)
                        p.setVisibility(View.VISIBLE);
                } else {
                    iconOffice.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
                    if(!iconHouse.isChecked() && !iconOffice.isChecked() && !iconCottage.isChecked())
                        p.setVisibility(View.INVISIBLE);
                }
            }
        });

        iconCottage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    iconCottage.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
                    iconHouse.setChecked(false);
                    iconOffice.setChecked(false);
                    if(groupName.getText().length() > 0)
                        p.setVisibility(View.VISIBLE);
                } else {
                    iconCottage.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
                    if(!iconHouse.isChecked() && !iconOffice.isChecked() && !iconCottage.isChecked())
                        p.setVisibility(View.INVISIBLE);
                }
            }
        });

        return alert;
    }
}

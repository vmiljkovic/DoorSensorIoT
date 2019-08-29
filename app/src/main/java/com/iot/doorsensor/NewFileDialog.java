package com.iot.doorsensor;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;


public class NewFileDialog extends DialogFragment {

    public interface NoticeNewFileDialogListener {
        void onNewFileDialogPositiveClick(DialogFragment dialog);
        void onNewFileDialogNegativeClick(DialogFragment dialog);
    }

    String mDeviceName;
    NoticeNewFileDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeNewFileDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeNewFileDialogListener");
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
                        EditText filename = getDialog().findViewById(R.id.group_name);
                        mDeviceName = filename.getText().toString();
                        mListener.onNewFileDialogPositiveClick(NewFileDialog.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancelbtn), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onNewFileDialogNegativeClick(NewFileDialog.this);
                        NewFileDialog.this.getDialog().cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        Button n = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        n.setTextColor(getResources().getColor(R.color.colorPrimary));
        n.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        Button p = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        p.setTextColor(getResources().getColor(R.color.colorPrimary));
        p.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        return alert;
    }
}

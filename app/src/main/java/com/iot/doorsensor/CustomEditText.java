package com.iot.doorsensor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    Context _Context;

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _Context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        _Context = context;
    }

    public CustomEditText(Context context) {
        super(context);
        _Context = context;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            InputMethodManager mgr = (InputMethodManager) _Context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (_Context instanceof MainActivity) {
                MainActivity main = (MainActivity)_Context;

                if (!CheckFullTime(this)) {
                    this.setText("10");
                    mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
                    return true;
                }

                dispatchKeyEvent(event);
            }

            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    private boolean CheckFullTime(TextView v) {
        String str = v.getText().toString();
        if (str.length() != 2) {
            return false;
        }

        char c0 = str.charAt(0);
        char c1 = str.charAt(1);
        return (c0 >= '1' && c0 <= '9') && (c1 >= '0' && c1 <= '9');
    }
}
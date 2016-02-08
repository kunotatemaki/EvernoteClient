package com.rukiasoft.androidapps.evernoteclient.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;

/**
 * Created by Raúl Feliz on 7/2/16.
 *
 */
public class Tools {

    public String getDateFromLong(Context context, long lDate) {
        SimpleDateFormat df = new SimpleDateFormat(Constants.FORMAT_DATE_TIME,
                context.getResources().getConfiguration().locale);
        return df.format(lDate);
    }

    public void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getStringColorFromIntColor(Context context, int color){
        int colorPin = ContextCompat.getColor(context, color);
        return String.format("#%06X", 0xFFFFFF & colorPin);
    }
}

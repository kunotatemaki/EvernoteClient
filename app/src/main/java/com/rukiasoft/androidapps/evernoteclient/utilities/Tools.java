package com.rukiasoft.androidapps.evernoteclient.utilities;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

}

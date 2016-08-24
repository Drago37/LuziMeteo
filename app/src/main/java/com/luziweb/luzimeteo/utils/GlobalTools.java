package com.luziweb.luzimeteo.utils;

import java.text.SimpleDateFormat;

/**
 * Created by Anthony on 29/07/16.
 */
public class GlobalTools {

    public static int mIntNombreJourMeteoWeek = 4;

    public static String keyAPI = "014338d0063abb9e17042b84f7a50b6a";

    public static final int REQUEST_CODE = 10;
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_VILLE = "ville";


    public static String getDay(long timestamp) {
        timestamp = timestamp * 1000;
        SimpleDateFormat formater = new SimpleDateFormat("EE");
        //System.out.println(formater.format(timestamp));
        return formater.format(timestamp);
    }

}

package com.luziweb.luzimeteo.models;

/**
 * Created by Anthony on 29/07/16.
 */
public class MeteoWeek {

    private double mTemperature;
    private double mTempMini;
    private double mTempMax;
    private String mVille;
    private String mTemps;
    private String mImageTemps;
    private String mLatitude;
    private String mLongitude;
    private long mTimestamp;
    private String mJour;

    public MeteoWeek(String jour, String imageTemps, double tempMin, double tempMax) {
        this.mJour = jour;
        this.mImageTemps = imageTemps;
        this.mTempMax = tempMax;
        this.mTempMini = tempMin;
    }

    public String getmVille() {
        return mVille;
    }

    public double getmTemperature() {
        return mTemperature;
    }

    public String getmTemps() {
        return mTemps;
    }

    public String getmImageTemps() {
        return mImageTemps;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public String getmJour() {
        return mJour;
    }

    public double getmTempMax() {
        return mTempMax;
    }

    public double getmTempMini() {
        return mTempMini;
    }

    public long getmTimestamp() {
        return mTimestamp;
    }
}

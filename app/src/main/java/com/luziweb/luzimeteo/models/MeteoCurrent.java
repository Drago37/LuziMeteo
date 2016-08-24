package com.luziweb.luzimeteo.models;

/**
 * Created by Anthony on 28/07/16.
 */
public class MeteoCurrent
{
    private double mTemperature;
    private String mVille;
    private int mId;
    private String mTemps;
    private String mImageTemps;
    private String mLatitude;
    private String mLongitude;
    private double mTempMini;
    private double mTempMax;

    public MeteoCurrent(int id, String ville, double temperature, String temps, String imageTemps) {
        this.mId = id;
        this.mTemperature = temperature;
        this.mVille = ville;
        this.mTemps = temps;
        this.mImageTemps = imageTemps;
    }

    public int getmId() {
        return mId;
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

    public double getmTempMini() {
        return mTempMini;
    }

    public double getmTempMax() {
        return mTempMax;
    }
}

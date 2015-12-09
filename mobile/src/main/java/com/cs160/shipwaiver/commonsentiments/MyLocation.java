package com.cs160.shipwaiver.commonsentiments;

/**
 * Created by forgottn on 11/19/15.
 */
public class MyLocation {
    private Double mLatitude;
    private Double mLongitude;

    public MyLocation() {
        this.mLatitude = null;
        this.mLongitude = null;
    }

    public void setLatitudeAndLongitude(Double latitude, Double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }
}

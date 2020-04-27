package com.jxcy.smartsensor.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

public class HospitalEntity implements Parcelable {
    private String name;
    private String address;
    private LatLng latLng;

    public HospitalEntity() {
    }

    protected HospitalEntity(Parcel in) {
        name = in.readString();
        address = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<HospitalEntity> CREATOR = new Creator<HospitalEntity>() {
        @Override
        public HospitalEntity createFromParcel(Parcel in) {
            return new HospitalEntity(in);
        }

        @Override
        public HospitalEntity[] newArray(int size) {
            return new HospitalEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeParcelable(latLng, flags);
    }

}

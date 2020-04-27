package com.jxcy.smartsensor.view.unit;

import android.os.Parcel;
import android.os.Parcelable;

public class Temperature implements Parcelable {

    private String dateString;
    private float temper_value;
    private float x_value;
    private float y_value;

    public Temperature(){

    }

    protected Temperature(Parcel in) {
        dateString = in.readString();
        temper_value = in.readFloat();
        x_value = in.readFloat();
        y_value = in.readFloat();
    }

    public static final Creator<Temperature> CREATOR = new Creator<Temperature>() {
        @Override
        public Temperature createFromParcel(Parcel in) {
            return new Temperature(in);
        }

        @Override
        public Temperature[] newArray(int size) {
            return new Temperature[size];
        }
    };

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public float getTemper_value() {
        return temper_value;
    }

    public void setTemper_value(float temper_value) {
        this.temper_value = temper_value;
    }

    public float getX_value() {
        return x_value;
    }

    public void setX_value(float x_value) {
        this.x_value = x_value;
    }

    public float getY_value() {
        return y_value;
    }

    public void setY_value(float y_value) {
        this.y_value = y_value;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "dateString='" + dateString + '\'' +
                ", temper_value=" + temper_value +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dateString);
        parcel.writeFloat(temper_value);
        parcel.writeFloat(x_value);
        parcel.writeFloat(y_value);
    }
}

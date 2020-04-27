package com.jxcy.smartsensor.view.unit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class HistoryTemperature implements Parcelable {
    private String startTime;
    private String endTime;
    private float maxValue;
    private List<Temperature> data;

    public HistoryTemperature(){

    }

    protected HistoryTemperature(Parcel in) {
        startTime = in.readString();
        endTime = in.readString();
        maxValue = in.readFloat();
        data = in.createTypedArrayList(Temperature.CREATOR);
    }

    public static final Creator<HistoryTemperature> CREATOR = new Creator<HistoryTemperature>() {
        @Override
        public HistoryTemperature createFromParcel(Parcel in) {
            return new HistoryTemperature(in);
        }

        @Override
        public HistoryTemperature[] newArray(int size) {
            return new HistoryTemperature[size];
        }
    };

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public List<Temperature> getData() {
        return data;
    }

    public void setData(List<Temperature> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HistoryTemperature{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", maxValue=" + maxValue +
                ", data=" + data +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeFloat(maxValue);
        parcel.writeTypedList(data);
    }
}

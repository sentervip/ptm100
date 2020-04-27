package com.jxcy.smartsensor.greendao;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DayDetailRecord implements Parcelable {
    @Id(autoincrement = true)
    Long record_id;
    private Long day_id;
    private float temperature;
    private long insertTime;

    public Long getDay_id() {
        return day_id;
    }

    public void setDay_id(Long day_id) {
        this.day_id = day_id;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    protected DayDetailRecord(Parcel in) {
        record_id = in.readLong();
        day_id = in.readLong();
        temperature = in.readFloat();
        insertTime = in.readLong();
    }

    @Generated(hash = 96230643)
    public DayDetailRecord(Long record_id, Long day_id, float temperature, long insertTime) {
        this.record_id = record_id;
        this.day_id = day_id;
        this.temperature = temperature;
        this.insertTime = insertTime;
    }

    @Generated(hash = 1314102598)
    public DayDetailRecord() {
    }


    public static final Creator<DayDetailRecord> CREATOR = new Creator<DayDetailRecord>() {
        @Override
        public DayDetailRecord createFromParcel(Parcel in) {
            return new DayDetailRecord(in);
        }

        @Override
        public DayDetailRecord[] newArray(int size) {
            return new DayDetailRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(record_id);
        dest.writeLong(day_id);
        dest.writeFloat(temperature);
        dest.writeLong(insertTime);
    }

    public Long getRecord_id() {
        return this.record_id;
    }

    public void setRecord_id(Long record_id) {
        this.record_id = record_id;
    }
}

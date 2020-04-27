package com.jxcy.smartsensor.greendao;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity
public class DayRecord implements Parcelable, Comparable<DayRecord> {
    @Id(autoincrement = true)
    Long day_id;
    private Long baby_id;
    private String curDate;
    private float maxTemperature;
    private long start_time;
    private long end_time;

    @ToMany(referencedJoinProperty = "day_id")
    List<DayDetailRecord> detailRecords;

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    protected DayRecord(Parcel in) {
        curDate = in.readString();
        maxTemperature = in.readFloat();
    }

    @Generated(hash = 434135395)
    public DayRecord(Long day_id, Long baby_id, String curDate,
                     float maxTemperature, long start_time, long end_time) {
        this.day_id = day_id;
        this.baby_id = baby_id;
        this.curDate = curDate;
        this.maxTemperature = maxTemperature;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    @Generated(hash = 234573321)
    public DayRecord() {
    }

    public static final Creator<DayRecord> CREATOR = new Creator<DayRecord>() {
        @Override
        public DayRecord createFromParcel(Parcel in) {
            return new DayRecord(in);
        }

        @Override
        public DayRecord[] newArray(int size) {
            return new DayRecord[size];
        }
    };
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 562734501)
    private transient DayRecordDao myDao;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(curDate);
        dest.writeFloat(maxTemperature);
        dest.writeLong(start_time);
        dest.writeLong(end_time);
        dest.writeLong(baby_id);
    }

    public Long getDay_id() {
        return this.day_id;
    }

    public void setDay_id(Long day_id) {
        this.day_id = day_id;
    }

    public Long getBaby_id() {
        return this.baby_id;
    }

    public void setBaby_id(Long baby_id) {
        this.baby_id = baby_id;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 563410036)
    public List<DayDetailRecord> getDetailRecords() {
        if (detailRecords == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DayDetailRecordDao targetDao = daoSession.getDayDetailRecordDao();
            List<DayDetailRecord> detailRecordsNew = targetDao
                    ._queryDayRecord_DetailRecords(day_id);
            synchronized (this) {
                if (detailRecords == null) {
                    detailRecords = detailRecordsNew;
                }
            }
        }
        return detailRecords;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1286944035)
    public synchronized void resetDetailRecords() {
        detailRecords = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    @Override
    public int compareTo(DayRecord dayRecord) {
        return (int) (this.getMaxTemperature() - dayRecord.getMaxTemperature());
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1425085465)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDayRecordDao() : null;
    }
}

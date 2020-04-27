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
public class BabyEntity implements Parcelable {
    @Id(autoincrement = true)
    Long id;
    private String nickName;
    private String babyAge;
    private int sex;
    private String head_url;
    private int isCheck = 0;

    @ToMany(referencedJoinProperty = "baby_id")
    List<DayRecord> dayRecords;

    protected BabyEntity(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        nickName = in.readString();
        babyAge = in.readString();
        sex = in.readInt();
        head_url = in.readString();
        isCheck = in.readInt();
        dayRecords = in.createTypedArrayList(DayRecord.CREATOR);
    }

    @Generated(hash = 947882738)
    public BabyEntity(Long id, String nickName, String babyAge, int sex,
            String head_url, int isCheck) {
        this.id = id;
        this.nickName = nickName;
        this.babyAge = babyAge;
        this.sex = sex;
        this.head_url = head_url;
        this.isCheck = isCheck;
    }

    @Generated(hash = 1846426286)
    public BabyEntity() {
    }

    public static final Creator<BabyEntity> CREATOR = new Creator<BabyEntity>() {
        @Override
        public BabyEntity createFromParcel(Parcel in) {
            return new BabyEntity(in);
        }

        @Override
        public BabyEntity[] newArray(int size) {
            return new BabyEntity[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1847201056)
    private transient BabyEntityDao myDao;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHead_url() {
        return head_url;
    }

    public void setHead_url(String head_url) {
        this.head_url = head_url;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "BabyEntity{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", babyAge='" + babyAge + '\'' +
                ", sex=" + sex +
                ", head_url='" + head_url + '\'' +
                ", isCheck=" + isCheck +
                ", dayRecords=" + dayRecords +
                '}';
    }

    public String getBabyAge() {
        return this.babyAge;
    }

    public void setBabyAge(String babyAge) {
        this.babyAge = babyAge;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(nickName);
        dest.writeString(babyAge);
        dest.writeInt(sex);
        dest.writeString(head_url);
        dest.writeInt(isCheck);
        dest.writeTypedList(dayRecords);
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1239578638)
    public List<DayRecord> getDayRecords() {
        if (dayRecords == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DayRecordDao targetDao = daoSession.getDayRecordDao();
            List<DayRecord> dayRecordsNew = targetDao
                    ._queryBabyEntity_DayRecords(id);
            synchronized (this) {
                if (dayRecords == null) {
                    dayRecords = dayRecordsNew;
                }
            }
        }
        return dayRecords;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1507610587)
    public synchronized void resetDayRecords() {
        dayRecords = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1457095949)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBabyEntityDao() : null;
    }
}

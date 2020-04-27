package com.jxcy.smartsensor.utils;

import android.content.Context;
import android.util.Log;

import com.hndw.smartlibrary.until.DateTools;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.greendao.DayDetailRecord;
import com.jxcy.smartsensor.greendao.DayDetailRecordDao;
import com.jxcy.smartsensor.greendao.DayRecord;
import com.jxcy.smartsensor.greendao.DayRecordDao;
import com.jxcy.smartsensor.view.unit.HistoryTemperature;
import com.jxcy.smartsensor.view.unit.Temperature;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryKit {
    private Context context;
    private DaoSession daoSession;
    private DayRecord maxRecord;
    private List<DayRecord> historyList = new ArrayList<>();

    public DayRecord getMaxRecord() {
        return maxRecord;
    }

    public void setMaxRecord(DayRecord maxRecord) {
        this.maxRecord = maxRecord;
    }

    public List<DayRecord> getHistoryList() {
        return historyList;
    }

    public QueryKit(Context context, DaoSession daoSession) {
        this.context = context;
        this.daoSession = daoSession;
    }

    /**
     * 显示时刻数值
     * 2min,5min,15min,30min
     * <=5min,直接读取、6s写入一次数据
     * >5min 读取一分钟的平均值
     * < 1H,采集每5分钟的最大值，3小时采集每15分钟的最大值，12小时采集每半小时的最大值，24小时采集每小时的最大值
     *
     * @param timeKey
     */
    public List<Temperature> queryTemperature(String timeKey, String dateString) {
        long curTime = DateTools.getCurDayTime().getTime();
        DayRecordDao recordDao = daoSession.getDayRecordDao();
        DayRecord dayRecord = recordDao.queryBuilder().where(DayRecordDao.Properties.CurDate.eq(dateString)).unique();
        DayDetailRecordDao detailRecordDao = daoSession.getDayDetailRecordDao();
        QueryBuilder<DayDetailRecord> qb = detailRecordDao.queryBuilder();
        if (dayRecord != null) {
            if (timeKey.equals(context.getResources().getString(R.string.two_min))) {
                long min_time = (curTime - (2 * 60 * 1000));
                return minCalculate(min_time, curTime, dayRecord, qb);
            } else if (timeKey.equals(context.getResources().getString(R.string.five_min))) {
                long min_time = (curTime - (5 * 60 * 1000));
                return minCalculate(min_time, curTime, dayRecord, qb);

            } else if (timeKey.equals(context.getResources().getString(R.string.fifteen_min))) {
                return maxMinCalculate(curTime, dayRecord, qb, 15);
            } else if (timeKey.equals(context.getResources().getString(R.string.thirty_min))) {
                return maxMinCalculate(curTime, dayRecord, qb, 30);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * <5min,数据查询、折线更新:取每个数值
     *
     * @param min_time
     * @param curTime
     * @param dayRecord
     * @param qb
     */
    private List<Temperature> minCalculate(long min_time, long curTime, DayRecord dayRecord, QueryBuilder<DayDetailRecord> qb) {
        List<Temperature> data = new ArrayList<>();
        List<DayDetailRecord> fiveList = qb.where(DayDetailRecordDao.Properties.Day_id.eq(dayRecord.getDay_id()), DayDetailRecordDao.Properties.InsertTime.between(min_time, curTime)).list();
        for (DayDetailRecord record : fiveList) {
            Temperature temperature = new Temperature();
            temperature.setTemper_value(record.getTemperature());
            temperature.setDateString(DateTools.getDateString(record.getInsertTime(), "HH:mm:ss"));
            data.add(temperature);
        }
        return data;
    }

    /**
     * >5min数据查询折线更新：取每分钟的平均值
     *
     * @param curTime
     * @param dayRecord
     * @param qb
     * @param i
     */
    private List<Temperature> maxMinCalculate(long curTime, DayRecord dayRecord, QueryBuilder<DayDetailRecord> qb, int i) {
        List<Temperature> data = new ArrayList<>();
        long min_time = curTime - i * 60 * 1000;
        for (int m = 1; m <= i; m++) {
            min_time += (m - 1) * 60 * 1000;
            curTime = min_time + 60 * 1000;
            List<DayDetailRecord> avList = qb.where(DayDetailRecordDao.Properties.InsertTime.between(min_time, curTime),
                    DayDetailRecordDao.Properties.Day_id.eq(dayRecord.getDay_id())).list();
            if (avList != null && avList.size() > 0) {
                float ave_value = 0;
                float sum_value = 0;
                for (DayDetailRecord ave : avList) {
                    sum_value += ave.getTemperature();
                }
                ave_value = sum_value / avList.size();
                Temperature temperature = new Temperature();
                temperature.setTemper_value(ave_value);
                temperature.setDateString(DateTools.getDateString(min_time, "dd:mm:ss"));
                data.add(temperature);
            }
        }
        return data;
    }


    /**
     * 查询历史记录
     *
     * @param dateString
     */
    public List<HistoryTemperature> queryHistoryArray(String dateString) {
        DayRecordDao recordDao = daoSession.getDayRecordDao();
        List<DayRecord> dayRecords = recordDao.queryBuilder().where(DayRecordDao.Properties.CurDate.eq(dateString)).list();
        List<HistoryTemperature> historyArray = new ArrayList<>();
        historyList.clear();
        if (dayRecords != null && dayRecords.size() > 0) {
            int len = dayRecords.size();
            Log.i("ljh", "queryDayList = " + len);
            for (DayRecord dayRecord : dayRecords) {
                Log.i("ljh", "queryDay_start = " + dayRecord.getStart_time() + " /end_time = " + dayRecord.getEnd_time() + " isOKay = " + ((dayRecord.getEnd_time() - dayRecord.getStart_time()) > 5 * 60 * 1000));
                if ((dayRecord.getEnd_time() - dayRecord.getStart_time()) > 5 * 60 * 1000) {
                    HistoryTemperature historyRecord = new HistoryTemperature();
                    historyRecord.setStartTime(DateTools.getDateString(dayRecord.getStart_time(), "HH:mm:ss"));
                    historyRecord.setEndTime(DateTools.getDateString(dayRecord.getEnd_time(), "HH:mm:ss"));
                    historyRecord.setMaxValue(dayRecord.getMaxTemperature());
                    List<DayDetailRecord> detailRecords = dayRecord.getDetailRecords();
                    if (detailRecords != null && detailRecords.size() > 0) {
                        List<Temperature> data = new ArrayList<>();
                        for (DayDetailRecord detail : detailRecords) {
                            Temperature temperature = new Temperature();
                            temperature.setTemper_value(detail.getTemperature());
                            data.add(temperature);
                        }
                        historyRecord.setData(data);
                    }
                    historyArray.add(historyRecord);
                    historyList.add(dayRecord);
                } else {
                    continue;
                }
            }
            Collections.sort(dayRecords);
            setMaxRecord(dayRecords.get(len - 1));
        }
        return historyArray;
    }


    /**
     * 更新历史记录结束时间
     *
     * @param endTime
     */
    public void updateHistoryEndTime(long endTime) {
        DayRecordDao recordDao = daoSession.getDayRecordDao();
        List<DayRecord> dayRecordList = recordDao.queryBuilder().where(DayRecordDao.Properties.End_time.le(0)).list();
        for (DayRecord dayRecord : dayRecordList) {
            if (dayRecord != null) {
                dayRecord.setEnd_time(endTime);
                recordDao.update(dayRecord);
            }
        }
    }


    /**
     * 获取最大值
     *
     * @param dateString
     * @return
     */
    public DayRecord queryHistoryMaxValue(String dateString) {
        DayRecordDao recordDao = daoSession.getDayRecordDao();
        List<DayRecord> dayRecords = recordDao.queryBuilder().where(DayRecordDao.Properties.CurDate.eq(dateString)).list();
        int len = dayRecords.size();
        if (dayRecords != null && len > 0) {
            for (int m = 0; m < dayRecords.size(); m++) {
                DayRecord dayRecord = dayRecords.get(m);
                if ((dayRecord.getEnd_time() - dayRecord.getStart_time()) < 5 * 60 * 1000) {
                    dayRecords.remove(dayRecord);
                } else {
                    continue;
                }
            }
            Collections.sort(dayRecords);
            return dayRecords.get(len - 1);
        } else {
            return null;
        }
    }


    /**
     * 查询历史记录详情
     *
     * @param day_id
     */
    public List<Temperature> queryHistoryList(String day_id) {
        List<Temperature> mlist = new ArrayList<>();
        DayDetailRecordDao detailRecordDao = daoSession.getDayDetailRecordDao();
        List<DayDetailRecord> detailRecords = detailRecordDao.queryBuilder().where(DayDetailRecordDao.Properties.Day_id.eq(day_id)).list();
        if (detailRecords != null && detailRecords.size() > 0) {
            for (DayDetailRecord detail : detailRecords) {
                Temperature temperature = new Temperature();
                temperature.setTemper_value(detail.getTemperature());
                temperature.setDateString(DateTools.formatDayTime(null, detail.getInsertTime()));
                mlist.add(temperature);
            }
        }
        return mlist;
    }
}

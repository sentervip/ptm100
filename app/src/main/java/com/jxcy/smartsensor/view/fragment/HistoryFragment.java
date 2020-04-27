package com.jxcy.smartsensor.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.DateTools;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.greendao.DayRecord;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.utils.QueryKit;
import com.jxcy.smartsensor.view.unit.HistoryTemperature;
import com.jxcy.smartsensor.view.unit.TemperatureLineView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseFragment implements CalendarView.OnDateSelectedListener,
        CalendarView.OnYearChangeListener {
    private TextView name_v, age_v;
    private View root;
    private int mYear;
    private TemperatureLineView lineView;
    private TextView max_v;
    private ImageView temp_icon;
    private SimpleDraweeView headView;
    private View top_item_v;
    private BaseFragment curFragment;
    TextView mTextMonthDay;
    TextView mTextYear;
    TextView mTextLunar;
    TextView mTextCurrentDay;
    CalendarView mCalendarView;
    RelativeLayout mRelativeTool;
    CalendarLayout mCalendarLayout;
    HistoryDetailFragment detailFragment;
    List<DayRecord> recordList = new ArrayList<>();
    DayRecord maxDay;
    DaoSession daoSession;
    QueryKit queryKit;
    private boolean mHasLoadedOnce = false;
    private boolean isPrepared = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.history_fragment_layout, container, false);
        initView(root);
        isPrepared = true;
        lazyLoad();
        return root;
    }

    private void initView(View root) {
        EventBus.getDefault().register(this);
        detailFragment = new HistoryDetailFragment();
        name_v = root.findViewById(R.id.child_name);
        age_v = root.findViewById(R.id.age_v);
        mTextMonthDay = root.findViewById(R.id.tv_month_day);
        mTextYear = root.findViewById(R.id.tv_year);
        mTextLunar = root.findViewById(R.id.tv_lunar);
        mRelativeTool = root.findViewById(R.id.rl_tool);
        mCalendarView = root.findViewById(R.id.calendarView);
        mTextCurrentDay = root.findViewById(R.id.tv_current_day);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
        root.findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });
        mCalendarLayout = root.findViewById(R.id.calendarLayout);
        mCalendarView.setOnDateSelectedListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        lineView = root.findViewById(R.id.chart_view);
        max_v = root.findViewById(R.id.max_v);
        temp_icon = root.findViewById(R.id.tem_icon);
        top_item_v = root.findViewById(R.id.top_item);
        top_item_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("dayList", (ArrayList<? extends Parcelable>) recordList);
                bundle.putParcelable("cur_day", maxDay);
                detailFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().add(R.id.content_layout, detailFragment).commit();
                curFragment = detailFragment;
            }
        });
        headView = root.findViewById(R.id.drawee_view);
        if (Contants.curBaby != null && Contants.curBaby.getHead_url() != null) {
            headView.setImageURI("file://" + Contants.curBaby.getHead_url());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        SmartApplication application = (SmartApplication) activity.getApplication();
        daoSession = application.getDaoSession();
        queryKit = new QueryKit(getContext(), daoSession);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Contants.curBaby != null) {
            updateBaByInfo();
        }
    }

    /**
     * 更新历史记录界面
     *
     * @param keyWord
     */
    private void upHistoryView(String keyWord) {
        List<HistoryTemperature> historyTemperatures = queryKit.queryHistoryArray(keyWord);
        if (historyTemperatures != null && historyTemperatures.size() > 0) {
            lineView.setHistoryTemperatures(historyTemperatures);
            DayRecord curRecord = queryKit.getMaxRecord();
            if (curRecord != null) {
                top_item_v.setVisibility(View.VISIBLE);
                float maxValue = curRecord.getMaxTemperature();
                String max_temp = String.format(getResources().getString(R.string.temp_cur_value), String.valueOf(maxValue));
                max_v.setText(max_temp);
                if (maxValue > Contants.higher_warn_value) {
                    temp_icon.setImageResource(R.drawable.unormal_temp_icon);
                } else if (maxValue < Contants.lower_warn_value) {
                    temp_icon.setImageResource(R.drawable.normal_temp_icon);
                } else {
                    temp_icon.setImageResource(R.drawable.normal_temp_icon);
                }
            } else {
                top_item_v.setVisibility(View.INVISIBLE);
            }
            recordList = queryKit.getHistoryList();
            maxDay = queryKit.getMaxRecord();
        }else {
            lineView.setHistoryTemperatures(historyTemperatures);
            top_item_v.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        if (curFragment instanceof HistoryDetailFragment) {
            getChildFragmentManager().beginTransaction().remove(curFragment).commitAllowingStateLoss();
            return true;
        } else {
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventMessage(MessageEvent event) {
        if (event.getMessageKey().equals(Contants.CUR_BABY_UPDATE_KEY)) {
            name_v.setText(Contants.curBaby.getNickName());
            age_v.setText(Contants.curBaby.getBabyAge());
        }
    }

    public void updateBaByInfo() {
        if (Contants.curBaby != null) {
            if (Contants.curBaby.getNickName() != null)
                name_v.setText(Contants.curBaby.getNickName());
            if (Contants.curBaby.getBabyAge() != null)
                age_v.setText(Contants.curBaby.getBabyAge());
        }
    }


    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        int month = calendar.getMonth();
        int day = calendar.getDay();
        mTextMonthDay.setText(month + "月" + day + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        String month_value = String.valueOf(month);
        if (month < 10) {
            month_value = "0" + month_value;
        }
        String day_value = String.valueOf(day);
        if (day < 10) {
            day_value = "0" + day_value;
        }
        String curDate = mYear + "-" + month_value + "-" + day_value;
        if (lineView != null) {
            upHistoryView(curDate);
        }
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    public void initHistoryRecords() {
        upHistoryView(DateTools.getCurDayString(null));
    }

    @Override
    protected void lazyLoad() {
        if (mHasLoadedOnce || !isPrepared)
            return;
        mHasLoadedOnce = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHasLoadedOnce = false;
        isPrepared = false;
    }

}

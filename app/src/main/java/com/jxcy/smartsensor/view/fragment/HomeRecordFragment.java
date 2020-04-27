package com.jxcy.smartsensor.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.DateTools;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.BatteryView;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.view.dialog.NoticeDialogFragment;
import com.jxcy.smartsensor.view.unit.Temperature;
import com.jxcy.smartsensor.view.unit.TemperatureLineView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class HomeRecordFragment extends BaseFragment {
    private View root;
    private TextView cur_v, max_v, tip_v, unit_v, baby_v;
    private TemperatureLineView lineView;
    private float max_value, cur_value;
    private View home_layout;
    private ImageView arrow_btn;
    private View top_layout, ploy_layout;
    float mPosY = 0, mCurPosY = 0;
    private BatteryView battery_v;
    private NoticeDialogFragment noticeDialogFragment;
    private PreferenceTool preferenceTool;
    private boolean mHasLoadedOnce = false;
    private boolean isPrepared = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_record_fragment_layout, container, false);
        initView();
        isPrepared = true;
        lazyLoad();
        return root;
    }

    private void initView() {
        baby_v = root.findViewById(R.id.cur_baby_name);
        if (Contants.curBaby != null)
            baby_v.setText(Contants.curBaby.getNickName());
        home_layout = root.findViewById(R.id.home_page_layout);
        top_layout = root.findViewById(R.id.top_layout);
        top_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                            ploy_layout.setVisibility(View.GONE);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.temperature_value_height));
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            top_layout.setLayoutParams(layoutParams);
                            arrow_btn.setImageResource(R.drawable.arrow_up_btn_selector);
                            //向下滑動
                        } else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                            //向上滑动
                            ploy_layout.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.temperature_value_height));
                            layoutParams.addRule(RelativeLayout.BELOW, R.id.cur_baby_name);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            top_layout.setLayoutParams(layoutParams);
                            arrow_btn.setImageResource(R.drawable.arrow_down_btn_selector);
                        }
                        break;
                }
                return true;
            }

        });
        ploy_layout = root.findViewById(R.id.ploy_layout);
        lineView = root.findViewById(R.id.chart_view);
        cur_v = root.findViewById(R.id.cur_temp_v);
        max_v = root.findViewById(R.id.max_temp_v);
        tip_v = root.findViewById(R.id.cut_stauts_tip);
        unit_v = root.findViewById(R.id.unit_v);
        arrow_btn = root.findViewById(R.id.arrow_down);
        arrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrowDone();
            }
        });
        battery_v = root.findViewById(R.id.battery_v);
    }

    private void arrowDone() {
        if (ploy_layout.getVisibility() == View.VISIBLE) {
            ploy_layout.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.temperature_value_height));
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            top_layout.setLayoutParams(layoutParams);
            arrow_btn.setImageResource(R.drawable.arrow_up_btn_selector);
        } else {
            ploy_layout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.temperature_value_height));
            layoutParams.addRule(RelativeLayout.BELOW, R.id.cur_baby_name);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            top_layout.setLayoutParams(layoutParams);
            arrow_btn.setImageResource(R.drawable.arrow_down_btn_selector);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeDialogFragment = new NoticeDialogFragment();
        preferenceTool = PreferenceTool.getInstance(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @SuppressLint("StringFormatMatches")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventMessage(MessageEvent event) {
        if (event != null && isAdded()) {
            if (event.getMessageKey().equals(Contants.BLE_CURRENT_VALUE_KEY)) {
                cur_value = (float) event.getObject();
                Bundle bundle = new Bundle();
                bundle.putFloat("cur_value", cur_value);
                Temperature temperature = new Temperature();
                temperature.setTemper_value(cur_value);
                temperature.setDateString(DateTools.getDateString(System.currentTimeMillis(), "HH:mm:ss"));
                lineView.addTemperature(temperature);
                if (Contants.cur_unit == Contants.unit_2) {
                    cur_value = (float) (cur_value * 1.8000 + 32.00);
                    BigDecimal bigDecimal = new BigDecimal(cur_value);
                    cur_value = bigDecimal.setScale(2, RoundingMode.HALF_UP).floatValue();
                    String vString = String.valueOf(cur_value);
                    if (vString.length() == 4) {
                        vString += "0";
                    }
                    cur_v.setTextSize(getResources().getDimension(R.dimen.temperature_f_size));
                    cur_v.setText(vString);
                    unit_v.setText(getResources().getString(R.string.unit_f));
                } else {
                    String vString = String.valueOf(cur_value);
                    if (vString.length() == 4) {
                        vString += "0";
                    }
                    cur_v.setText(vString);
                    unit_v.setText(getResources().getString(R.string.unit_c));
                }
                if (cur_value > max_value) {
                    max_value = cur_value;
                    if (Contants.cur_unit == Contants.unit_2) {
                        BigDecimal bigDecimal = new BigDecimal(max_value);
                        max_value = bigDecimal.setScale(2, RoundingMode.HALF_UP).floatValue();
                        String maxString = String.valueOf(max_value);
                        if (maxString.length() == 4) {
                            maxString += "0";
                        }
                        max_v.setText(String.format(getResources().getString(R.string.temp_cur_value_f), maxString));
                    } else {
                        String maxString = String.valueOf(max_value);
                        if (maxString.length() == 4) {
                            maxString += "0";
                        }
                        max_v.setText(String.format(getResources().getString(R.string.temp_cur_value), maxString));
                    }
                }
                if (cur_value >= Contants.higher_warn_value) {
                    home_layout.setBackgroundColor(getResources().getColor(R.color.color_warn_temp));
                    tip_v.setText(getResources().getString(R.string.temperature_higher_tip));
                    tip_v.setTextColor(getResources().getColor(R.color.color_white_100));
                    noticeDialogFragment.setArguments(bundle);
                    bundle.putInt("warn_key", 1);
                    if (!noticeDialogFragment.isAdded() && !noticeDialogFragment.isVisible()) {
                        if (!preferenceTool.getBooleanValue("done_key")) {
                            noticeDialogFragment.showNow(getFragmentManager(), null);
                            preferenceTool.editBoolean("done_key", true);
                        }
                    }
                } else if (cur_value <= Contants.lower_warn_value) {
                    home_layout.setBackgroundColor(getResources().getColor(R.color.color_warn_low_temp));
                    tip_v.setText(getResources().getString(R.string.temperature_lower_tip));
                    tip_v.setTextColor(getResources().getColor(R.color.color_white_100));
                    bundle.putInt("warn_key", -1);
                    noticeDialogFragment.setArguments(bundle);
                    if (!noticeDialogFragment.isAdded() && !noticeDialogFragment.isVisible()) {
                        if (!preferenceTool.getBooleanValue("done_key")) {
                            noticeDialogFragment.showNow(getFragmentManager(), null);
                            preferenceTool.editBoolean("done_key", true);
                        }
                    }
                } else {
                    home_layout.setBackgroundColor(getResources().getColor(R.color.color_normal_temp));
                    tip_v.setText(getResources().getString(R.string.temperature_normal_tip));
                    tip_v.setTextColor(getResources().getColor(R.color.color_white_100));
                    preferenceTool.editBoolean("done_key", false);
                }
            } else if (event.getMessageKey().equals(Contants.CUR_BABY_UPDATE_KEY)) {
                baby_v.setText(Contants.curBaby.getNickName());
            } else if (event.getMessageKey().equals(Contants.BLE_DISCONNNECT_KEY)) {
                tip_v.setText(getResources().getString(R.string.ble_disconnect));
                tip_v.setTextColor(getResources().getColor(R.color.color_cur_temp_tx));
                home_layout.setBackgroundColor(getResources().getColor(R.color.color_normal_temp));
                preferenceTool.editBoolean("done_kay", false);
            } else if (event.getMessageKey().equals(Contants.BLE_CONNNECTING_KEY)) {
                tip_v.setText(getResources().getString(R.string.ble_connecting));
                tip_v.setTextColor(getResources().getColor(R.color.color_white_100));
                home_layout.setBackgroundColor(getResources().getColor(R.color.color_normal_temp));
            } else if (event.getMessageKey().equals(Contants.BLE_WAIT_CONNECT_KEY)) {
                tip_v.setText(getResources().getString(R.string.ble_wait_connect));
                tip_v.setTextColor(getResources().getColor(R.color.color_warn_temp));
                home_layout.setBackgroundColor(getResources().getColor(R.color.color_normal_temp));
            } else if (event.getMessageKey().equals(Contants.BLE_CURRENT_BATTERY_KEY)) {
                float battery = (float) event.getObject();
                int percent = (int) (((battery - Contants.minBattery) / (Contants.maxBattery - Contants.minBattery)) * 100);
                battery_v.setPower(percent);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

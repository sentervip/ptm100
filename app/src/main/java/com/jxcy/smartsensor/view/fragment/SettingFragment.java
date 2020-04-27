package com.jxcy.smartsensor.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.view.AboutActivity;
import com.jxcy.smartsensor.view.BabyListActivity;
import com.jxcy.smartsensor.view.HospitalActivity;

import org.greenrobot.eventbus.EventBus;

import static com.jxcy.smartsensor.utils.Contants.curBaby;

public class SettingFragment extends BaseFragment implements View.OnClickListener, UnitListener {
    private View root, unit_layout, sound_layout, device_layout, high_low_layout, hospital_layout, baby_layout, about_layout, compensate_layout;
    private SoundFragment soundFragment;
    private UnitFragment unitFragment;
    private BaseFragment curFragment;
    private DeviceFragment deviceFragment;
    private TextView unit_v, sound_v;
    private PreferenceTool tool;
    private TextView childName, babyAge;
    private ImageView back_btn;
    private TemperatureSettingFragment temperatureFragment;
    private AdjustDialogFragment adjustDialogFragment;
    private Switch prompt_btn;
    private SimpleDraweeView head_icon;
    private boolean mHasLoadedOnce = false;
    private boolean isPrepared = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        init(root);
        isPrepared = true;
        lazyLoad();
        return root;
    }

    private void init(View root) {
        head_icon = root.findViewById(R.id.head_icon);
        unit_layout = root.findViewById(R.id.unit_layout);
        unit_layout.setOnClickListener(this);
        sound_layout = root.findViewById(R.id.warn_layout);
        sound_layout.setOnClickListener(this);
        device_layout = root.findViewById(R.id.device_layout);
        device_layout.setOnClickListener(this);
        high_low_layout = root.findViewById(R.id.high_low_layout);
        high_low_layout.setOnClickListener(this);
        hospital_layout = root.findViewById(R.id.hospital_layout);
        hospital_layout.setOnClickListener(this);
        baby_layout = root.findViewById(R.id.baby_layout);
        baby_layout.setOnClickListener(this);
        childName = root.findViewById(R.id.child_name);
        babyAge = root.findViewById(R.id.age_v);
        about_layout = root.findViewById(R.id.about_layout);
        about_layout.setOnClickListener(this);
        compensate_layout = root.findViewById(R.id.compensate_layout);
        compensate_layout.setOnClickListener(this);

        soundFragment = new SoundFragment();
        unitFragment = new UnitFragment();
        deviceFragment = new DeviceFragment();
        unitFragment.setUnitListener(this);
        soundFragment.setUnitListener(this);
        unit_v = root.findViewById(R.id.unit_value);
        sound_v = root.findViewById(R.id.sound_v);
        prompt_btn = root.findViewById(R.id.prompt_switch);
        prompt_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MessageEvent upEvent = new MessageEvent(Contants.PROMPT_ENABLE_KEY, isChecked);
                EventBus.getDefault().post(upEvent);
            }
        });

        if (Contants.cur_unit == Contants.unit_1) {
            unit_v.setText(getResources().getString(R.string.centigrade));
            tool.editString(Contants.UNIT_VALUE, getResources().getString(R.string.centigrade));
        } else {
            unit_v.setText(getResources().getString(R.string.fahrenheit));
            tool.editString(Contants.UNIT_VALUE, getResources().getString(R.string.fahrenheit));
        }
        if (Contants.cur_sound == Contants.sound_week) {
            sound_v.setText(getResources().getString(R.string.sound_light));
            tool.editString(Contants.SOUND_UNIT, getResources().getString(R.string.sound_light));
        } else if (Contants.cur_sound == Contants.sound_strong) {
            sound_v.setText(getResources().getString(R.string.sound_strong));
            tool.editString(Contants.SOUND_UNIT, getResources().getString(R.string.sound_strong));
        } else {
            sound_v.setText(getResources().getString(R.string.sound_default));
            tool.editString(Contants.SOUND_UNIT, getResources().getString(R.string.sound_default));
        }
        back_btn = root.findViewById(R.id.img_back);
        back_btn.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (curBaby != null)
            head_icon.setImageURI(Uri.parse("file://" + curBaby.getHead_url()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warn_layout:
                getChildFragmentManager().beginTransaction().replace(R.id.content_layout, soundFragment).commitAllowingStateLoss();
                curFragment = soundFragment;
                break;
            case R.id.unit_layout:
                getChildFragmentManager().beginTransaction().replace(R.id.content_layout, unitFragment).commitAllowingStateLoss();
                curFragment = unitFragment;
                break;
            case R.id.device_layout:
                getChildFragmentManager().beginTransaction().replace(R.id.content_layout, deviceFragment).commitAllowingStateLoss();
                curFragment = deviceFragment;
                break;
            case R.id.high_low_layout:
                if (temperatureFragment == null) {
                    temperatureFragment = new TemperatureSettingFragment();
                }
                getChildFragmentManager().beginTransaction().replace(R.id.content_layout, temperatureFragment).commitAllowingStateLoss();
                curFragment = temperatureFragment;
                break;
            case R.id.hospital_layout:
                Intent intent = new Intent(getActivity(), HospitalActivity.class);
                startActivity(intent);
                break;
            case R.id.baby_layout:
                Intent babyIntent = new Intent(getActivity(), BabyListActivity.class);
                startActivity(babyIntent);
                break;
            case R.id.about_layout:
                Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.img_back:

                break;
            case R.id.compensate_layout:
                if (adjustDialogFragment == null) {
                    adjustDialogFragment = new AdjustDialogFragment();
                }
                adjustDialogFragment.show(getChildFragmentManager(), "");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initBabyInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (curFragment != null) {
            getChildFragmentManager().beginTransaction().remove(curFragment).commitAllowingStateLoss();
        }
    }

    public void initBabyInfo() {
        if (curBaby != null) {
            childName.setText(curBaby.getNickName());
            babyAge.setText(curBaby.getBabyAge());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tool = PreferenceTool.getInstance(context);
    }

    @Override
    public boolean onBackPressed() {
        if (curFragment != null && curFragment.isAdded()) {
            getChildFragmentManager().beginTransaction().remove(curFragment).commitAllowingStateLoss();
        }
        return true;
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public void onSoundItemClick(String sound_value) {
        sound_v.setText(sound_value);
        tool.editString(Contants.SOUND_UNIT, sound_value);
        if (sound_value.equals(getResources().getString(R.string.sound_strong))) {
            Contants.cur_sound = Contants.sound_strong;
        } else if (sound_value.equals(getResources().getString(R.string.sound_light))) {
            Contants.cur_sound = Contants.sound_week;
        } else {
            Contants.cur_sound = Contants.sound_normal;
        }
        getChildFragmentManager().beginTransaction().remove(soundFragment).commitAllowingStateLoss();
    }

    @Override
    public void onUnitItemClick(String unit_value) {
        unit_v.setText(unit_value);
        tool.editString(Contants.UNIT_VALUE, unit_value);
        if (unit_value.equals(getResources().getString(R.string.centigrade))) {
            Contants.cur_unit = Contants.unit_1;
        } else {
            Contants.cur_unit = Contants.unit_2;
        }
        getChildFragmentManager().beginTransaction().remove(unitFragment).commitAllowingStateLoss();
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

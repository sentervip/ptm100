package com.jxcy.smartsensor.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.view.BaseActivity;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.adapter.SmartDataAdapter;
import com.jxcy.smartsensor.greendao.BabyEntity;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.utils.JobSchedulerManager;
import com.jxcy.smartsensor.utils.QueryKit;
import com.jxcy.smartsensor.view.dialog.BabyOptionDialog;
import com.jxcy.smartsensor.view.fragment.HistoryFragment;
import com.jxcy.smartsensor.view.fragment.HomeRecordFragment;
import com.jxcy.smartsensor.view.fragment.HospitalFragment;
import com.jxcy.smartsensor.view.fragment.NurserFragment;
import com.jxcy.smartsensor.view.fragment.SettingFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ImageView home_v, history_v, nursery_v, setting_v;
    private View home_layout, history_layout, nursery_layout, setting_layout;
    private TextView home_tx, history_tx, nursery_tx, setting_tx;
    private ViewPager viewPager;
    private SmartDataAdapter adapter;
    private HomeRecordFragment homeRecordFragment;
    private HistoryFragment historyFragment;
    private NurserFragment nurserFragment;
    private HospitalFragment hospitalFragment;
    private SettingFragment settingFragment;
    private BaseFragment curFragment;
    private List<Fragment> fragments = new ArrayList<>();
    private JobSchedulerManager mJobManager;
    private BabyOptionDialog optionDialog;
    PreferenceTool preferenceTool;
    private SmartApplication application;
    private DaoSession daoSession;
    private QueryKit queryKit;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        mJobManager = JobSchedulerManager.getJobSchedulerInstance(this);
        mJobManager.startJobScheduler();
        preferenceTool = PreferenceTool.getInstance(this);

        init();
    }

    private void init() {
        home_v = findViewById(R.id.home_icon);
        history_v = findViewById(R.id.history_icon);
        nursery_v = findViewById(R.id.nursery_icon);
        setting_v = findViewById(R.id.set_icon);

        home_layout = findViewById(R.id.home_page_layout);
        history_layout = findViewById(R.id.history_layout);
        nursery_layout = findViewById(R.id.nursery_layout);
        setting_layout = findViewById(R.id.setting_layout);

        home_tx = findViewById(R.id.home_tx);
        history_tx = findViewById(R.id.history_tx);
        nursery_tx = findViewById(R.id.nurser_tx);
        setting_tx = findViewById(R.id.setting_tx);
        viewPager = findViewById(R.id.view_pager);
        home_v.setOnClickListener(this);
        history_v.setOnClickListener(this);
        nursery_v.setOnClickListener(this);
        setting_v.setOnClickListener(this);

        home_layout.setOnClickListener(this);
        history_layout.setOnClickListener(this);
        nursery_layout.setOnClickListener(this);
        setting_layout.setOnClickListener(this);

        homeRecordFragment = new HomeRecordFragment();
        historyFragment = new HistoryFragment();
        nurserFragment = new NurserFragment();
        hospitalFragment = new HospitalFragment();
        settingFragment = new SettingFragment();
        fragments.add(homeRecordFragment);
        fragments.add(historyFragment);
        // fragments.add(nurserFragment);
//        fragments.add(hospitalFragment);
        fragments.add(settingFragment);
        adapter = new SmartDataAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        home_v.setSelected(true);
        home_tx.setSelected(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                home_v.setSelected(false);
                history_v.setSelected(false);
                nursery_v.setSelected(false);
                setting_v.setSelected(false);
                home_tx.setSelected(false);
                history_tx.setSelected(false);
                nursery_tx.setSelected(false);
                setting_tx.setSelected(false);
                switch (position) {
                    case 0:
                        home_v.setSelected(true);
                        home_tx.setSelected(true);
                        break;
                    case 1:
                        historyFragment.initHistoryRecords();
                        history_v.setSelected(true);
                        history_tx.setSelected(true);
                        break;
                    /*case 2:
                        nursery_v.setSelected(true);
                        nursery_tx.setSelected(true);
                        break;
                    case 3:
                        setting_v.setSelected(true);
                        setting_tx.setSelected(true);
                        break;*/
                    case 2:
                        setting_v.setSelected(true);
                        setting_tx.setSelected(true);
                        break;
                }
                if (curFragment != null) {
                    curFragment.onBackPressed();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Contants.curBaby == null) {
            ArrayList<BabyEntity> babyArray = getIntent().getParcelableArrayListExtra("baby_array");
            if (babyArray != null) {
                if (babyArray.size() > 1) {
                    optionDialog = new BabyOptionDialog();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("baby_array", babyArray);
                    optionDialog.setArguments(bundle);
                    optionDialog.show(getSupportFragmentManager(), "");
                } else if (babyArray.size() == 1) {
                    Contants.curBaby = babyArray.get(0);
                }
            } else {
                BabyEntity curBaby = getIntent().getParcelableExtra("baby_key");
                if (curBaby != null) {
                    Contants.curBaby = curBaby;
                }
            }
        }
        application = (SmartApplication) getApplication();
        daoSession = application.getDaoSession();
        queryKit = new QueryKit(this, daoSession);
        checkPermissions();
        if (preferenceTool.getFloatValue("higher_ware") > 0) {
            Contants.higher_warn_value = preferenceTool.getFloatValue("higher_ware");
        }
        if (preferenceTool.getFloatValue("lower_ware") > 0) {
            Contants.lower_warn_value = preferenceTool.getFloatValue("lower_ware");
        }
    }


    @TargetApi(23)
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    private final int SDK_PERMISSION_REQUEST = 127;

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SDK_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(R.string.storage_permissions_remind)
                            .setPositiveButton(getResources().getString(R.string.done_submit), (dialog1, which) ->
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1))
                            .setNegativeButton(getResources().getString(R.string.cancel), null)
                            .create()
                            .show();
                }
            }
        }
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_icon:
            case R.id.home_page_layout:
                viewPager.setCurrentItem(0);
                curFragment = homeRecordFragment;
                break;
            case R.id.history_icon:
            case R.id.history_layout:
                viewPager.setCurrentItem(1);
                curFragment = historyFragment;
                break;
            case R.id.nursery_icon:
            case R.id.nursery_layout:
                viewPager.setCurrentItem(2);
                //  curFragment = nurserFragment;
                curFragment = hospitalFragment;
                break;
            case R.id.set_icon:
            case R.id.setting_layout:
                viewPager.setCurrentItem(3);
                curFragment = settingFragment;
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁用返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (curFragment != null && (curFragment instanceof SettingFragment || curFragment instanceof HospitalFragment) || curFragment instanceof HistoryFragment) {
                curFragment.onBackPressed();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (Contants.bleDevice != null) {
            BleManager.getInstance().disconnect(Contants.bleDevice);
        }
        BleManager.getInstance().destroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventMessage(MessageEvent event) {
        if (event != null) {
            if (event.getMessageKey().equals(Contants.CUR_BABY_UPDATE_KEY)) {
                if (historyFragment != null) {
                    historyFragment.updateBaByInfo();
                }
            }
        }
    }
}

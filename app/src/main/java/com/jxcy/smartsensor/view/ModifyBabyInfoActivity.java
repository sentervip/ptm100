package com.jxcy.smartsensor.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.FrescoUtils;
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.greendao.BabyEntity;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.utils.FileUntil;
import com.jxcy.smartsensor.utils.PhotoUtils;
import com.jxcy.smartsensor.view.dialog.HeadIconDialog;
import com.jxcy.smartsensor.view.dialog.PickDialogFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModifyBabyInfoActivity extends BaseActivity implements View.OnClickListener, HeadIconDialog.HeadActionlistener, PickDialogFragment.PickerListener {
    EditText nickName;
    TextView babyAge, babySex;
    Switch switch_btn;
    Button saveBtn;
    HeadIconDialog iconDialog;
    SimpleDraweeView draweeView;
    private final int REQUEST_SYSTEM_PIC = 1;
    private final int CAMERA_RESULT = 2;
    private String mPhotoPath;
    private File file;
    private Uri photoOutputUri;
    private PickDialogFragment pickDialogFragment;
    String curAge;
    SmartApplication application;
    DaoSession daoSession;
    String head_path;
    BabyEntity curBaby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_baby_activity_layout);
        FrescoUtils.initialize(this);
        nickName = findViewById(R.id.nick_v);
        babyAge = findViewById(R.id.age_v);
        babySex = findViewById(R.id.sex_v);
        switch_btn = findViewById(R.id.enable_switch);
        switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (curBaby != null) {
                    curBaby.setIsCheck(isChecked ? 1 : 0);
                }
            }
        });
        saveBtn = findViewById(R.id.modify_btn);
        saveBtn.setOnClickListener(this);

        babyAge.setOnClickListener(this);
        babySex.setOnClickListener(this);

        draweeView = (SimpleDraweeView) findViewById(R.id.drawee_view);
        draweeView.setOnClickListener(this);
        if (pickDialogFragment == null) {
            pickDialogFragment = new PickDialogFragment();
        }
        pickDialogFragment.setPickerListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        curBaby = intent.getParcelableExtra("baby_key");
        if (curBaby != null) {
            nickName.setText(curBaby.getNickName());
            babyAge.setText(curBaby.getBabyAge());
            curAge = curBaby.getBabyAge();
            if (curBaby.getSex() == 1) {
                babySex.setText(getResources().getString(R.string.boy));
            } else {
                babySex.setText(getResources().getString(R.string.girl));
            }
            if (Contants.curBaby != null) {
                if (curBaby.getIsCheck() == 1) {
                    switch_btn.setChecked(true);
                } else {
                    switch_btn.setChecked(false);
                }
            }
            if (curBaby.getHead_url() != null || !"".equals(curBaby.getHead_url())) {
                Uri uri = Uri.parse("file://" + curBaby.getHead_url());
                FrescoUtils.showThumb(draweeView, uri, 150, 150);
            }
        }
        application = (SmartApplication) getApplication();
        daoSession = application.getDaoSession();
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_btn:
                if (nickName.getText().toString() == null) {
                    Toast.makeText(this, R.string.input_nickname_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                curBaby.setNickName(nickName.getText().toString());
                if (babySex.getText().toString() == null) {
                    curBaby.setSex(1);
                } else {
                    if (babySex.getText().toString().equals(getResources().getString(R.string.boy))) {
                        curBaby.setSex(1);
                    } else {
                        curBaby.setSex(2);
                    }
                }
                if (curAge == null) {
                    Toast.makeText(this, R.string.input_age_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                curBaby.setBabyAge(curAge);
                if (head_path != null) {
                    curBaby.setHead_url(head_path.toString());
                }
                daoSession.getBabyEntityDao().update(curBaby);
                Contants.curBaby = curBaby;
                Intent intent = new Intent();
                intent.putExtra("update_key", curBaby);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.drawee_view:
                if (iconDialog == null) {
                    iconDialog = new HeadIconDialog();
                }
                mPhotoPath = getSDPath() + File.separator + "sensor/" + getPhotoFileName();
                Bundle bundle = new Bundle();
                bundle.putString("file_path", mPhotoPath);
                iconDialog.setHeadActionlistener(this);
                iconDialog.setArguments(bundle);
                iconDialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.sex_v:
                pickDialogFragment.setMode(1);
                if (!pickDialogFragment.isAdded())
                    pickDialogFragment.showNow(getSupportFragmentManager(), "picker");
                break;
            case R.id.age_v:
                pickDialogFragment.setMode(2);
                if (!pickDialogFragment.isAdded())
                    pickDialogFragment.showNow(getSupportFragmentManager(), "picker");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SYSTEM_PIC) {
                if (null != data) {
                    Uri uri = data.getData();
                    if (uri != null)
                        displayImage(PhotoUtils.getPath(this, uri));
                }
            }
            if (requestCode == CAMERA_RESULT) {
                if (file != null)
                    displayImage(file.getPath());
            }
        }
        if (iconDialog != null) {
            iconDialog.dismiss();
        }

    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        Uri imageUri = data.getData();
        displayImage(getImagePath(imageUri));
    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri) {
        if (uri == null) return null;
        String path = null;
        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String filePath) {
        if (filePath != null) {
            FrescoUtils.showThumb(draweeView, Uri.parse("file://" + filePath), 150, 150);
            head_path = filePath;
        }
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    @Override
    public void openAlbum() {
        if (ContextCompat.checkSelfPermission(ModifyBabyInfoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ModifyBabyInfoActivity.this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/jpeg");
            startActivityForResult(intent, REQUEST_SYSTEM_PIC);//打开系统相册
        }
    }


    @Override
    public void takePhoto() {
        String file_path = FileUntil.getSystemTimeImageName();
        String parentPath = "sensor";
        file = FileUntil.makeSDFile(parentPath, file_path);
        if (Build.VERSION.SDK_INT >= 29) {
            photoOutputUri = PhotoUtils.createImageUri(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoOutputUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        } else {
            photoOutputUri = Uri.fromFile(file);
        }
        PhotoUtils.takeCamera(this, photoOutputUri, CAMERA_RESULT);
    }


    @Override
    public void itemPicked(Object value, int model) {
        if (model == 1) {
            babySex.setText((String) value);
        } else if (model == 2) {
            Date date = (Date) value;
            curAge = getCurrentAge(date);
            if (curAge == null) {
                Toast.makeText(this, R.string.input_age_tip, Toast.LENGTH_SHORT).show();
            } else {
                babyAge.setText(curAge);
            }
        }
        if (pickDialogFragment != null) {
            pickDialogFragment.dismiss();
        }
    }

    /**
     * 根据生日计算当前周岁数
     */
    public String getCurrentAge(Date birthday) {
        // 当前时间
        Calendar curr = Calendar.getInstance();
        // 生日
        Calendar born = Calendar.getInstance();
        born.setTime(birthday);
        int currYear = curr.get(Calendar.YEAR);
        int currMonth = curr.get(Calendar.MONTH);

        int bornMonth = born.get(Calendar.MONTH);
        int bornYear = born.get(Calendar.YEAR);

        int yearAge = currYear - bornYear;
        int monthAge;
        if (yearAge > 0) {
            if (currMonth < bornMonth) {
                yearAge -= 1;
            }
            if (12 - bornMonth + currMonth >= 12) {
                yearAge += ((12 - bornMonth) + currMonth) / 12;
                monthAge = ((12 - bornMonth) + currMonth) - 12;
            } else {
                monthAge = (12 - bornMonth) + currMonth;
            }
            if (monthAge > 0) {
                return String.format(getString(R.string.child_max_age), yearAge, monthAge);
            } else {
                return String.format(getString(R.string.child_year_age), yearAge);
            }
        } else {
            monthAge = currMonth - bornMonth;
            if (monthAge <= 0) {
                return null;
            }
            return String.format(getString(R.string.child_month_age), monthAge);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    if (iconDialog != null) {
                        iconDialog.dismiss();
                    }
                }
                break;
            default:
        }
    }
}

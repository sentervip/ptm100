package com.jxcy.smartsensor.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.R;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private View privacy_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);
        privacy_layout = findViewById(R.id.privacy_layout);
        privacy_layout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacy_layout:
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("ask_uri", "file:///android_asset/html/privacy.html");
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return super.handleMessage(msg);
    }
}

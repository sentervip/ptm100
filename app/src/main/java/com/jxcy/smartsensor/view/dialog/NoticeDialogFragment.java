package com.jxcy.smartsensor.view.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;

import org.greenrobot.eventbus.EventBus;

public class NoticeDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView cancel_v, done_v, notice_v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        getDialog().setCanceledOnTouchOutside(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = (int) getResources().getDimension(R.dimen.head_dialog_width);
        wlp.height = (int) getResources().getDimension(R.dimen.picker_dialog_height);
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notice_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        done_v = root.findViewById(R.id.done_btn);
        cancel_v = root.findViewById(R.id.cancel_btn);
        cancel_v.setOnClickListener(this);
        done_v.setOnClickListener(this);
        notice_v = root.findViewById(R.id.notice_v);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            float temp_v = bundle.getFloat("cur_value");
            int warn_key = bundle.getInt("warn_key");
            if (warn_key == 1) {
                String noticeString = String.format(getResources().getString(R.string.temp_higher_value), temp_v);
                notice_v.setText(noticeString);
            } else if (warn_key == -1) {
                String noticeString = String.format(getResources().getString(R.string.temp_lower_value), temp_v);
                notice_v.setText(noticeString);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                dismiss();
                break;
            case R.id.done_btn:
                MessageEvent upEvent = new MessageEvent(Contants.WARN_ENABLE_KEY, false);
                EventBus.getDefault().post(upEvent);
                dismiss();
                break;
        }
    }
}

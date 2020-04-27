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

public class HeadIconDialog extends DialogFragment implements View.OnClickListener {
    private TextView camera_v, fold_v, cancel_v;

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
        wlp.height = (int) getResources().getDimension(R.dimen.head_dialog_height);
        wlp.y = (int) getResources().getDimension(R.dimen.ble_item_height);
        wlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(wlp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.headicon_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        camera_v = root.findViewById(R.id.camera_tv);
        fold_v = root.findViewById(R.id.fold_tv);
        cancel_v = root.findViewById(R.id.cancel_btn);
        cancel_v.setOnClickListener(this);
        camera_v.setOnClickListener(this);
        fold_v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_tv:
                if (headActionlistener != null) {
                    headActionlistener.takePhoto();
                }
                break;
            case R.id.fold_tv:
                if (headActionlistener != null) {
                    headActionlistener.openAlbum();
                }
                break;
            case R.id.cancel_btn:

                dismiss();
                break;
        }
    }

    private HeadActionlistener headActionlistener;

    public void setHeadActionlistener(HeadActionlistener headActionlistener) {
        this.headActionlistener = headActionlistener;
    }

    public interface HeadActionlistener {
        void openAlbum();

        void takePhoto();
    }
}

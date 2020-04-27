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
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.adapter.HospitalEntity;
import com.jxcy.smartsensor.utils.MapUtil;

public class MapLeadDialog extends DialogFragment implements View.OnClickListener {
    private TextView baidu_v, gaoDe_v, tencent_v, cancel_v;
    private HospitalEntity hospitalEntity;
    private double latx, laty;
    private String mAddress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
        Bundle bundle = getArguments();
        hospitalEntity = bundle.getParcelable("hospital_key");
        if (hospitalEntity != null) {
            LatLng latLng = hospitalEntity.getLatLng();
            latx = latLng.latitude;
            laty = latLng.longitude;
            mAddress = hospitalEntity.getAddress() == null ? "" : hospitalEntity.getAddress();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        getDialog().setCanceledOnTouchOutside(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = (int) getResources().getDimension(R.dimen.head_dialog_width);
        wlp.height = (int) getResources().getDimension(R.dimen.lead_dialog_height);
        wlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(wlp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_lead_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        baidu_v = root.findViewById(R.id.baidu_tv);
        gaoDe_v = root.findViewById(R.id.gaode_tv);
        tencent_v = root.findViewById(R.id.tencent_tv);
        cancel_v = root.findViewById(R.id.cancel_btn);

        baidu_v.setOnClickListener(this);
        gaoDe_v.setOnClickListener(this);
        tencent_v.setOnClickListener(this);
        cancel_v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baidu_tv:
                if (MapUtil.isBaiduMapInstalled() && hospitalEntity != null) {
                    LatLng latLng = hospitalEntity.getLatLng();
                    MapUtil.openBaiDuNavi(getActivity(), 0, 0, null, latx, laty, mAddress);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.uninstall_baidu_map), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.gaode_tv:
                if (MapUtil.isGdMapInstalled()) {
                    MapUtil.openGaoDeNavi(getActivity(), 0, 0, null, latx, laty, mAddress);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.uninstall_gaode_map), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tencent_tv:
                if (MapUtil.isTencentMapInstalled()) {
                    MapUtil.openTencentMap(getActivity(), 0, 0, null, latx, laty, mAddress);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.uninstall_tencent_map), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            default:
                break;
        }
    }
}

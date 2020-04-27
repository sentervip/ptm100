package com.jxcy.smartsensor.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.exception.BleException;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.utils.Contants;

public class DeviceFragment extends BaseFragment {
    private View root;
    private TextView name_v, mac_v, rssi_v, version_v;
    private boolean mHasLoadedOnce = false;
    private boolean isPrepared = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.device_fragment_layout, container, false);
        initView(root);
        isPrepared = true;
        lazyLoad();
        return root;
    }

    private void initView(View root) {
        name_v = root.findViewById(R.id.id_value);
        mac_v = root.findViewById(R.id.mac_value);
        rssi_v = root.findViewById(R.id.rssi_value);
        version_v = root.findViewById(R.id.version_value);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Contants.bleDevice != null) {
            if (Contants.bleDevice.getName() != null) {
                name_v.setText(Contants.bleDevice.getName().toString());
            }
            if (Contants.bleDevice.getMac() != null) {
                mac_v.setText(Contants.bleDevice.getMac().toString());
            }

            if (BleManager.getInstance().isConnected(Contants.bleDevice.getMac())) {
                BleManager.getInstance().readRssi(Contants.bleDevice, new BleRssiCallback() {
                    @Override
                    public void onRssiFailure(BleException exception) {

                    }

                    @Override
                    public void onRssiSuccess(int rssi) {
                        rssi_v.setText(String.valueOf(Contants.bleDevice.getRssi()));
                    }
                });


                BleManager.getInstance().read(Contants.bleDevice, Contants.DEVICE_INFO_SERVER_UUID, Contants.SOFT_VERSION_UUID, new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        version_v.setText(new String(data));
                    }

                    @Override
                    public void onReadFailure(BleException exception) {

                    }
                });


            }
        }
    }


    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
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

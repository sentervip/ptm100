package com.jxcy.smartsensor.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.utils.Contants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class AdjustDialogFragment extends DialogFragment implements View.OnClickListener {
    private View rootView;
    private TextView saveBtn, cancelBtn;
    private SeekBar seekBar;
    private ImageView min_btn, add_btn;
    private float def_adjustValue = 0.5f;
    private TextView adjust_v;
    private final float maxValue = 5.0f;
    private final float tempValue = 0.5f;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.adjust_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        getDialog().setCanceledOnTouchOutside(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.dimAmount = 0f;
        wlp.width = (int) getResources().getDimension(R.dimen.adjust_dialog_width);
        wlp.height = (int) getResources().getDimension(R.dimen.adjust_dialog_height);
        window.setAttributes(wlp);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void initView(View rootView) {
        adjust_v = (TextView) rootView.findViewById(R.id.adjust_title);
        saveBtn = (TextView) rootView.findViewById(R.id.submit_btn);
        cancelBtn = (TextView) rootView.findViewById(R.id.cancel_btn);
        min_btn = (ImageView) rootView.findViewById(R.id.min_btn);
        add_btn = (ImageView) rootView.findViewById(R.id.add_btn);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        min_btn.setOnClickListener(this);
        seekBar = (SeekBar) rootView.findViewById(R.id.adjust_seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                float rt = tempValue + (progress * maxValue / seekBar.getMax());
                DecimalFormat decimalFormat = new DecimalFormat("0.0");
                String drt = decimalFormat.format(rt);
                def_adjustValue = Float.valueOf(drt);
                BigDecimal bigDecimal = new BigDecimal(def_adjustValue);
                def_adjustValue = bigDecimal.setScale(1, RoundingMode.HALF_UP).floatValue();
                if (def_adjustValue >= 5.0f) {
                    def_adjustValue = 5.0f;
                } else if (def_adjustValue <= 0.5f) {
                    def_adjustValue = 0.5f;
                }
                adjust_v.setText(String.format(getResources().getString(R.string.compensate_value), def_adjustValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                Contants.adjust_temperature = def_adjustValue;
                dismiss();
                break;
            case R.id.min_btn:
                def_adjustValue -= 0.5f;
                def_adjustValue = def_adjustValue <= 0.5f ? 0.5f : def_adjustValue;
                BigDecimal bigDecimal = new BigDecimal(def_adjustValue);
                def_adjustValue = bigDecimal.setScale(1, RoundingMode.HALF_UP).floatValue();
                if (def_adjustValue == 0.5f) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress((int) (def_adjustValue * 1000 / 4.5f));
                }
                adjust_v.setText(String.format(getResources().getString(R.string.compensate_value), def_adjustValue));
                break;
            case R.id.add_btn:
                def_adjustValue += 0.5f;
                def_adjustValue = def_adjustValue >= 5.0f ? 5.0f : def_adjustValue;
                BigDecimal decimal = new BigDecimal(def_adjustValue);
                def_adjustValue = decimal.setScale(1, RoundingMode.HALF_UP).floatValue();
                if (def_adjustValue == 5.0f) {
                    seekBar.setProgress(seekBar.getMax());
                } else {
                    seekBar.setProgress((int) (def_adjustValue * 1000 / 4.5f));
                }
                adjust_v.setText(String.format(getResources().getString(R.string.compensate_value), def_adjustValue));
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}

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

import com.itheima.wheelpicker.WheelPicker;
import com.itheima.wheelpicker.widgets.WheelDatePicker;
import com.jxcy.smartsensor.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PickDialogFragment extends DialogFragment implements View.OnClickListener {
    private WheelPicker sexPicker;
    private WheelDatePicker datePicker;
    private TextView cancel_v, done_v, picker_title;
    private int mode;
    private String pickedValue;
    private Date pickerDate;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.picker_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        picker_title = root.findViewById(R.id.picker_title);
        done_v = root.findViewById(R.id.done_btn);
        cancel_v = root.findViewById(R.id.cancel_btn);
        cancel_v.setOnClickListener(this);
        done_v.setOnClickListener(this);
        sexPicker = root.findViewById(R.id.sex_picker);
        datePicker = root.findViewById(R.id.date_picker);
        if (mode == 1) {
            datePicker.setVisibility(View.GONE);
            sexPicker.setVisibility(View.VISIBLE);
            sexPicker.setIndicator(true);
            sexPicker.setSelectedItemPosition(0);
            sexPicker.setItemSpace(3);
            sexPicker.setIndicatorSize(3);
            sexPicker.setVisibleItemCount(2);
            sexPicker.setIndicatorColor(getResources().getColor(R.color.color_layout_background));
            List<String> sexData = new ArrayList();
            String[] sexArray = getResources().getStringArray(R.array.sex_array);
            for (String sex : sexArray) {
                sexData.add(sex);
            }
            sexPicker.setData(sexData);
            picker_title.setText(getString(R.string.select_sex));
        } else if (mode == 2) {
            datePicker.setCyclic(true);
            datePicker.setAtmospheric(true);
            datePicker.setIndicator(true);
            datePicker.setItemSpace(3);
            datePicker.setIndicatorSize(5);
            datePicker.setVisibleItemCount(2);
            datePicker.setVisibility(View.VISIBLE);
            sexPicker.setVisibility(View.GONE);
            datePicker.setIndicatorColor(getResources().getColor(R.color.color_layout_background));
            picker_title.setText(getString(R.string.birthday_select));
        }
        sexPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                pickedValue = data.toString();
            }
        });
        datePicker.setOnDateSelectedListener(new WheelDatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(WheelDatePicker wheelDatePicker, Date date) {
                pickerDate = date;
            }
        });
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void onStart() {
        super.onStart();
        datePicker.setItemSpace(3);
        datePicker.setIndicatorSize(5);
        datePicker.setVisibleItemCount(2);

        sexPicker.setItemSpace(3);
        sexPicker.setIndicatorSize(3);
        sexPicker.setVisibleItemCount(2);
        sexPicker.setSelectedItemPosition(0);

        pickedValue = null;
        pickerDate = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                dismiss();
                break;
            case R.id.done_btn:
                try {
                    if (pickerListener != null) {
                        if (mode == 1) {
                            pickedValue = pickedValue == null ? sexPicker.getData().get(sexPicker.getCurrentItemPosition()).toString() : pickedValue;
                            pickerListener.itemPicked(pickedValue, mode);
                        } else if (mode == 2) {
                            pickerDate = pickerDate == null ? (Date) datePicker.getData().get(datePicker.getCurrentItemPosition()) : pickerDate;
                            pickerListener.itemPicked(pickerDate, mode);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                dismiss();
                break;
        }
    }

    private PickerListener pickerListener;

    public void setPickerListener(PickerListener pickerListener) {
        this.pickerListener = pickerListener;
    }

    public interface PickerListener {
        void itemPicked(Object value, int pickerModel);
    }
}

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

import com.hndw.smartlibrary.until.PreferenceTool;
import com.itheima.wheelpicker.WheelPicker;
import com.jxcy.smartsensor.R;

import java.util.ArrayList;
import java.util.List;

public class WarnDialogFragment extends DialogFragment implements View.OnClickListener {
    private WheelPicker parentPicker, numPicker;
    private TextView cancel_v, done_v;
    private String numValue, parentValue;
    private PreferenceTool preferenceTool;
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
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        preferenceTool = PreferenceTool.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.warn_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        done_v = root.findViewById(R.id.done_btn);
        cancel_v = root.findViewById(R.id.cancel_btn);
        cancel_v.setOnClickListener(this);
        done_v.setOnClickListener(this);
        parentPicker = root.findViewById(R.id.parent_picker);
        numPicker = root.findViewById(R.id.num_picker);
        numPicker.setIndicator(true);
        numPicker.setVisibleItemCount(3);
        numPicker.setIndicatorSize(3);
        numPicker.setSelectedItemPosition(5);
        numPicker.setSelectedItemTextColor(getResources().getColor(R.color.color_cur_temp_tx));
        numPicker.setIndicatorColor(getResources().getColor(R.color.color_layout_background));

        parentPicker.setIndicator(true);
        parentPicker.setVisibleItemCount(3);
        parentPicker.setIndicatorSize(3);
        parentPicker.setSelectedItemPosition(3);
        parentPicker.setIndicatorColor(getResources().getColor(R.color.color_layout_background));
        parentPicker.setSelectedItemTextColor(getResources().getColor(R.color.color_cur_temp_tx));

        List<String> numData = new ArrayList();
        String[] numArray = getResources().getStringArray(R.array.num_array);
        for (String num : numArray) {
            numData.add(num);
        }
        numPicker.setData(numData);

        List<String> tempData = new ArrayList();
        String[] tempArray = getResources().getStringArray(R.array.temp_array);
        for (String num : tempArray) {
            tempData.add(num);
        }
        parentPicker.setData(tempData);

        numPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                numValue = (String) data;
            }
        });
        parentPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker wheelPicker, Object o, int i) {
                parentValue = (String) o;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        parentPicker.setVisibleItemCount(3);
        parentPicker.setIndicatorSize(3);
        String pValue = PreferenceTool.getInstance(getContext()).getStringValue("pValue");
        List<String> pValues = parentPicker.getData();
        if (pValues != null && pValues.size() > 0) {
            for (int j = 0; j < pValues.size(); j++) {
                if (pValues.get(j).equals(pValue)) {
                    parentPicker.setSelectedItemPosition(j);
                }
            }
        } else {
            parentPicker.setSelectedItemPosition(3);
        }

        numPicker.setVisibleItemCount(3);
        numPicker.setIndicatorSize(3);
        String cValue = PreferenceTool.getInstance(getContext()).getStringValue("cValue");
        List<String> values = numPicker.getData();
        if (values != null && values.size() > 0) {
            for (int j = 0; j < values.size(); j++) {
                if (values.get(j).equals(cValue)) {
                    numPicker.setSelectedItemPosition(j);
                }
            }
        } else {
            numPicker.setSelectedItemPosition(5);
        }
        parentValue = null;
        numValue = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                dismiss();
                break;
            case R.id.done_btn:
                parentValue = parentValue == null ? parentPicker.getData().get(parentPicker.getCurrentItemPosition()).toString() : parentValue;
                numValue = numValue == null ? numPicker.getData().get(numPicker.getCurrentItemPosition()).toString() : numValue;
                if (pickerListener != null) {
                    String pickedValue = parentValue + "." + numValue;
                    PreferenceTool.getInstance(getContext()).editString("pValue", parentValue);
                    PreferenceTool.getInstance(getContext()).editString("cValue", numValue);
                    pickerListener.itemPicked(pickedValue);
                    preferenceTool.editBoolean("done_key", false);
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
        void itemPicked(String value);
    }
}

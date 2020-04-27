package com.jxcy.smartsensor.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.adapter.ListViewDivider;
import com.jxcy.smartsensor.adapter.OptionAdapter;
import com.jxcy.smartsensor.greendao.BabyEntity;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class BabyOptionDialog extends DialogFragment {
    private RecyclerView recyclerView;
    private OptionAdapter adapter;
    private List<BabyEntity> babyArray = new ArrayList<>();
    SmartApplication application;
    DaoSession daoSession;

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
        wlp.height = (int) getResources().getDimension(R.dimen.option_dialog_height);
        wlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(wlp);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.option_dialog_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListViewDivider(root.getContext(), LinearLayoutManager.VERTICAL));
        adapter = new OptionAdapter(getContext(), babyArray);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(new OptionAdapter.OptionItemListener() {
            @Override
            public void OnItemListener(BabyEntity item) {
                Contants.curBaby = item;
                boolean isCheck = item.getIsCheck() == 1;
                List<BabyEntity> upList = new ArrayList<>();
                for (BabyEntity baby : babyArray) {
                    if (baby.getId() == item.getId()) {
                        baby = item;
                    } else {
                        if (isCheck) {
                            baby.setIsCheck(0);
                        }
                    }
                    upList.add(baby);
                }
                daoSession.getBabyEntityDao().updateInTx(upList);
                MessageEvent upEvent = new MessageEvent(Contants.CUR_BABY_UPDATE_KEY, null);
                EventBus.getDefault().post(upEvent);
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        application = (SmartApplication) getActivity().getApplication();
        daoSession = application.getDaoSession();
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            List<BabyEntity> allBabys = bundle.getParcelableArrayList("baby_array");
            if (allBabys != null && allBabys.size() > 0) {
                babyArray = allBabys;
                adapter.updateData(babyArray);
            }
        }
    }
}

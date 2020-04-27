package com.jxcy.smartsensor.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.adapter.ListViewDivider;
import com.jxcy.smartsensor.adapter.UnitAdapter;
import com.jxcy.smartsensor.adapter.UnitEntity;
import com.jxcy.smartsensor.utils.Contants;

import java.util.ArrayList;
import java.util.List;

public class SoundFragment extends BaseFragment {
    private View root;
    RecyclerView recyclerView;
    UnitAdapter adapter;
    List<UnitEntity> soundList = new ArrayList<>();
    private PreferenceTool tool;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.unit_fragment_layout, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        recyclerView = root.findViewById(R.id.unit_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListViewDivider(root.getContext(), LinearLayoutManager.VERTICAL));
        tool = PreferenceTool.getInstance(getActivity());
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        soundList.clear();
        String curSound = tool.getStringValue(Contants.SOUND_UNIT);
        if(curSound==null){
            curSound = getResources().getString(R.string.sound_default);
            Contants.cur_sound = Contants.sound_normal;
        }
        String[] soundArray = getResources().getStringArray(R.array.sound_array);
        for (String sound : soundArray) {
            UnitEntity soundEntity = new UnitEntity();
            if (curSound != null && curSound.equals(sound)) {
                soundEntity.setCheck(true);
            } else {
                soundEntity.setCheck(false);
            }
            soundEntity.setUnitValue(sound);
            soundList.add(soundEntity);
        }
        adapter = new UnitAdapter(root.getContext(), soundList);
        adapter.setClickListener(new UnitAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String value) {
                if (unitListener != null) {
                    unitListener.onSoundItemClick(value);
                }
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private UnitListener unitListener;

    public void setUnitListener(UnitListener unitListener) {
        this.unitListener = unitListener;
    }

    @Override
    protected void lazyLoad() {

    }
}

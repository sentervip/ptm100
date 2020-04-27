package com.jxcy.smartsensor.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.adapter.BabyAdapter;
import com.jxcy.smartsensor.adapter.ListViewDivider;
import com.jxcy.smartsensor.greendao.BabyEntity;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.view.unit.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

public class BabyListActivity extends BaseActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private RecyclerView recyclerView;
    private BabyAdapter adapter;
    private ArrayList<BabyEntity> babyList = new ArrayList<>();
    private Button add_btn;
    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baby_activity_layout);
        fragmentManager = getSupportFragmentManager();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SmartApplication application = (SmartApplication) getApplication();
        daoSession = application.getDaoSession();
        babyList = (ArrayList<BabyEntity>) daoSession.getBabyEntityDao().loadAll();
        adapter.updateData(babyList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }


    private void initView() {
        add_btn = findViewById(R.id.add_btn);
        add_btn.setOnClickListener(this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(BabyListActivity.this));
        recyclerView.addItemDecoration(new ListViewDivider(this, LinearLayoutManager.VERTICAL));
        adapter = new BabyAdapter(this, babyList);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(new BabyAdapter.BabyItemListener() {
            @Override
            public void OnItemListener(BabyEntity item) {
                Intent intent = new Intent(BabyListActivity.this, ModifyBabyInfoActivity.class);
                intent.putExtra("baby_key", item);
                startActivityForResult(intent, 1);
            }

            @Override
            public void delItem(BabyEntity entity) {
                daoSession.getBabyEntityDao().delete(entity);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            BabyEntity upBaby = data.getParcelableExtra("update_key");
            Log.i("ljh", "update baby : " + upBaby.toString());
            boolean isCheck = upBaby.getIsCheck() == 1;
            List<BabyEntity> upList = new ArrayList<>();
            for (BabyEntity baby : babyList) {
                if (baby.getId() == upBaby.getId()) {
                    baby = upBaby;
                    baby.setIsCheck(isCheck ? 1 : 0);
                } else {
                    if (isCheck) {
                        baby.setIsCheck(0);
                    }
                }
                upList.add(baby);
            }
            adapter.updateData(upList);
            daoSession.getBabyEntityDao().updateInTx(upList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                Intent intent = new Intent(this, AddBabyActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

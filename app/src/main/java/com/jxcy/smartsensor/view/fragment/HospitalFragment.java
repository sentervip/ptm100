package com.jxcy.smartsensor.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.GpsUtil;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.adapter.HospitalAdapter;
import com.jxcy.smartsensor.adapter.HospitalEntity;
import com.jxcy.smartsensor.adapter.ListViewDivider;

import java.util.ArrayList;
import java.util.List;

public class HospitalFragment extends BaseFragment {
    private View root;
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;
    private RecyclerView recyclerView;
    private PoiSearch mPoiSearch;
    private List<HospitalEntity> hosList = new ArrayList<>();
    private HospitalAdapter adapter;
    private Handler handler;
    private ArrayList<PoiInfo> poiList = new ArrayList<>();
    private BaseFragment curFragment;
    private boolean mHasLoadedOnce = false;
    private boolean isPrepared = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.nearby_hospital_fragment_layout, container, false);
        fragmentManager = getChildFragmentManager();
        mapFragment = new MapFragment();
        initView();
        isPrepared = true;
        lazyLoad();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!GpsUtil.isOPen(getContext())) {
            GpsUtil.openGPS(getContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LatLng curLatLng = getLngAndLat(getContext());
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(listener);
        if (curLatLng != null && isAdded()) {
            mPoiSearch.searchNearby(new PoiNearbySearchOption()
                    .location(curLatLng)
                    .radius(5000)
                    .keyword(getResources().getString(R.string.hospital_key))
                    .sortType(PoiSortType.distance_from_near_to_far));
        }
    }

    private void initView() {
        handler = new Handler(this);
        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListViewDivider(root.getContext(), LinearLayoutManager.VERTICAL));
        adapter = new HospitalAdapter(getContext(), hosList);
        recyclerView.setAdapter(adapter);

        adapter.setItemListener(new HospitalAdapter.HospitalItemListener() {
            @Override
            public void OnItemListener(HospitalEntity item) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("poi_kay", poiList);
                mapFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.content_layout, mapFragment).commitAllowingStateLoss();
                curFragment = mapFragment;
            }
        });
    }

    OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult != null) {
                if (poiResult != null) {
                    List<PoiInfo> allPoi = poiResult.getAllPoi();
                    if (allPoi != null) {
                        hosList.clear();
                        for (PoiInfo info : allPoi) {
                            HospitalEntity entity = new HospitalEntity();
                            entity.setName(info.getName());
                            entity.setAddress(info.getAddress());
                            entity.setLatLng(info.getLocation());
                            hosList.add(entity);
                        }
                        handler.sendEmptyMessage(upMsg);
                    }
                    if(poiList!=null){
                        poiList.clear();
                    }
                    poiList = (ArrayList<PoiInfo>) allPoi;
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            if (poiDetailSearchResult != null) {

            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };


    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        if (curFragment != null && curFragment.isAdded()) {
            getChildFragmentManager().beginTransaction().remove(curFragment).commitAllowingStateLoss();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    private final int upMsg = 0x10;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case upMsg:
                adapter.updateData(hosList);
                break;
        }
        return super.handleMessage(msg);
    }

    @SuppressLint("MissingPermission")
    private LatLng getLngAndLat(Context context) {
        LatLng curLatLng = null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 50, locationListener);
        } else {
            curLatLng = getLngAndLatWithNetwork();
        }
        return curLatLng;
    }

    //从网络获取经纬度
    @SuppressLint("MissingPermission")
    public LatLng getLngAndLatWithNetwork() {
        LatLng curLatLng = null;
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2 * 1000, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return curLatLng;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null && isAdded()) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                        .location(latLng)
                        .radius(5000)
                        .keyword(getResources().getString(R.string.hospital_key))
                        .sortType(PoiSortType.distance_from_near_to_far));
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

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

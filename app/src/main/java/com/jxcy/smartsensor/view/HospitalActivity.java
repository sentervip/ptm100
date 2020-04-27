package com.jxcy.smartsensor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.LocationService;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.adapter.HospitalAdapter;
import com.jxcy.smartsensor.adapter.HospitalEntity;
import com.jxcy.smartsensor.adapter.ListViewDivider;
import com.jxcy.smartsensor.view.fragment.HospitalFragment;
import com.jxcy.smartsensor.view.fragment.MapFragment;

import java.util.ArrayList;
import java.util.List;

public class HospitalActivity extends BaseActivity {
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;
    private LocationService locationService;
    private RecyclerView recyclerView;
    private PoiSearch mPoiSearch;
    private List<HospitalEntity> hosList = new ArrayList<>();
    private HospitalAdapter adapter;
    private Handler handler;
    private ArrayList<PoiInfo> poiList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hospital_activity_layout);
        SmartApplication application = (SmartApplication) getApplication();
        locationService = application.getLocationService();
        fragmentManager = getSupportFragmentManager();
        mapFragment = new MapFragment();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(listener);
        LatLng curLatLng = getLngAndLat(this);
        if(curLatLng!=null) {
            mPoiSearch.searchNearby(new PoiNearbySearchOption()
                    .location(curLatLng)
                    .radius(5000)
                    .keyword(getResources().getString(R.string.hospital_key))
                    .sortType(PoiSortType.distance_from_near_to_far));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    public LocationService getLocationService() {
        return locationService;
    }

    private void initView() {
        handler = new Handler(this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListViewDivider(this, LinearLayoutManager.VERTICAL));
        adapter = new HospitalAdapter(this, hosList);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(new HospitalAdapter.HospitalItemListener() {
            @Override
            public void OnItemListener(HospitalEntity item) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("poi_kay", poiList);
                mapFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_layout, mapFragment).commitAllowingStateLoss();
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
                        for (PoiInfo info : allPoi) {
                            HospitalEntity entity = new HospitalEntity();
                            entity.setName(info.getName());
                            entity.setAddress(info.getAddress());
                            entity.setLatLng(info.getLocation());
                            hosList.add(entity);
                        }
                        handler.sendEmptyMessage(upMsg);
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
            } else {//当GPS信号弱没获取到位置的时候又从网络获取
                return getLngAndLatWithNetwork();
            }
        } else {    //从网络获取经纬度
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                curLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        return curLatLng;
    }

    //从网络获取经纬度
    @SuppressLint("MissingPermission")
    public LatLng getLngAndLatWithNetwork() {
        double latitude = 0.0;
        double longitude = 0.0;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5 * 1000, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        LatLng latLng = new LatLng(latitude, longitude);
        return latLng;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
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
}

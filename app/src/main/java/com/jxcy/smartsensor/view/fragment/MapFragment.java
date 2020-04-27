package com.jxcy.smartsensor.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.LocationService;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.adapter.HospitalEntity;
import com.jxcy.smartsensor.view.dialog.MapLeadDialog;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class MapFragment extends BaseFragment implements BaiduMap.OnMarkerClickListener,
        SensorEventListener {
    private MapView mapView;
    private BaiduMap mBaiduMap;
    private LocationService locationService;
    private SensorManager mSensorManager;
    MyLocationListenner locationListener;
    private MyLocationData locData;
    private float mCurrentZoom = 18;
    double lastX;
    /**
     * 当前Gps方向
     */
    private int mCurDir = 0;
    /**
     * 当前Gps 纬度
     */
    private double mCurLat = 0.0;
    /**
     * 当前Gps经度
     */
    private double mCurLon = 0.0;
    /**
     * 当前Gps精度
     */
    private float mCurAccuracy;
    private View rootView;
    /**
     * 地图参数设置
     */
    private UiSettings uiSettings;

    private List<PoiInfo> poiList;
    private List<LatLng> allPoints = new ArrayList<>();
    private MapLeadDialog mapLeadDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.map_fragemnt_layout, container, false);
        mapView = (MapView) rootView.findViewById(R.id.map_view);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mapLeadDialog = new MapLeadDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        SmartApplication application = (SmartApplication) getSelfActivity().getApplication();
        locationService = application.getLocationService();
        locationService.registerListener(locationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 定位SDK
        getPresenter();
        initMap(getContext());
        initListener();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = new Bundle();
        HospitalEntity hospital = new HospitalEntity();
        hospital.setLatLng(marker.getPosition());
        bundle.putParcelable("hospital_key", hospital);
        mapLeadDialog.setArguments(bundle);
        mapLeadDialog.show(getChildFragmentManager(), "");
        return false;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }


    private void initMap(Context context) {
        mBaiduMap = mapView.getMap();
        uiSettings = mBaiduMap.getUiSettings();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
        locationListener = new MyLocationListenner();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18);
        builder.overlook(90);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        mapView.showZoomControls(false);
        mBaiduMap.setOnMarkerClickListener(this::onMarkerClick);
        mBaiduMap.removeMarkerClickListener(this::onMarkerClick);
        mBaiduMap.setMyLocationEnabled(true);
    }


    /**
     * 定位SDK监听函数
     */
    class MyLocationListenner extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {
            if (location.getLocType() == BDLocation.TypeServerError || location.getLocType() == BDLocation.TypeServerDecryptError || location.getLocType() == BDLocation.TypeCriteriaException)
                return;
            mCurLat = location.getLatitude();
            mCurLon = location.getLongitude();
            mCurAccuracy = location.getRadius();
            locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(mCurDir).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            int locType = location.getLocType();
            if (locType == BDLocation.TypeGpsLocation || locType == BDLocation.TypeNetWorkLocation) {
                location(mLatLng);
            }
        }
    }

    /**
     * 定位到指定位置
     *
     * @param latLng
     */
    private void location(LatLng latLng) {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng);
        builder.zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 设置百度地图中心
     */
    private void setMapCenter(LatLng latLng) {

        MapStatus.Builder builder = new MapStatus.Builder();

        builder.target(latLng);

        builder.zoom(mCurrentZoom);

        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurDir = (int) x;
            locData = new MyLocationData.Builder().accuracy(mCurAccuracy).direction(mCurDir).latitude(mCurLat).longitude(mCurLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        Bundle bundle = getArguments();
        poiList = bundle.getParcelableArrayList("poi_kay");
        if (poiList != null) {
            if (poiList != null) {
                for (int j = 0; j < poiList.size(); j++) {
                    PoiInfo poiInfo = poiList.get(j);
                    if (poiInfo != null) {
                        View markView = getActivity().getLayoutInflater().inflate(R.layout.way_instrest_point_marker, null, false);
                        TextView markerIndexNumber = (TextView) markView.findViewById(R.id.instrest_marker_index_number);
                        markerIndexNumber.setText(String.valueOf(j + 1));
                        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(markView);
                        addMarkerInMap(markerIcon, poiInfo.getLocation(), false);
                        allPoints.add(poiInfo.getLocation());
                    }
                }
                setMapCenter(findCenter(allPoints));
            }
        }
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        mapView.getMap().clear();
        mBaiduMap.setMyLocationEnabled(false);
        mapView = null;
        super.onDestroy();
    }

    @Override
    public void onStop() {
        mSensorManager.unregisterListener(this);
        locationService.unregisterListener(locationListener);
        locationService.stop();
        super.onStop();
    }

    /**
     * 对地图事件的消息响应
     */
    private void initListener() {
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            public void onMapDoubleClick(LatLng point) {
            }
        });

        /**
         * 地图状态发生变化
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            public void onMapStatusChangeStart(MapStatus status) {
            }

            @Override
            public void onMapStatusChangeStart(MapStatus status, int reason) {

            }

            public void onMapStatusChangeFinish(MapStatus status) {
            }

            public void onMapStatusChange(MapStatus status) {
                mCurrentZoom = status.zoom;//获取手指缩放地图后的值
            }
        });
    }

    /**
     * 显示地图中心
     */
    private void coordinateConvert(List<LatLng> latLngs) {
        setMapCenter(findCenter(latLngs));
    }

    private LatLng findCenter(List<LatLng> latLngs) {
        double lanSum = 0;
        double lonSum = 0;
        for (int i = 0; i < latLngs.size(); i++) {
            lanSum += latLngs.get(i).latitude;
            lonSum += latLngs.get(i).longitude;
        }
        LatLng target = new LatLng(lanSum / latLngs.size(), lonSum / latLngs.size());
        return target;
    }


    /**
     * 添加marker到地图图层
     *
     * @param markerIcon 图标
     * @param latLng     地理位置
     * @param canDrag    是否能拖动
     */
    private void addMarkerInMap(BitmapDescriptor markerIcon, LatLng latLng,
                                boolean canDrag) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(markerIcon)
                .zIndex(2);
        Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);
        marker.setAnchor(0.5f, 0.5f);
        marker.setDraggable(canDrag);
    }

    @Override
    protected void lazyLoad() {

    }
}

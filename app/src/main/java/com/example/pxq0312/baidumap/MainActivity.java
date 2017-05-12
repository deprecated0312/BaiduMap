package com.example.pxq0312.baidumap;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListener myListener = new MyLocationListener();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private String mCurrentCity="成都市";

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    // UI相关
    private ImageButton btnLocationMode;
    private ImageButton btnSatellite;
    private ImageButton btnTraffic;
    private ImageButton btnPano;
    private boolean satellite=false;
    private boolean traffic=false;
    private boolean pano=false;
    private boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        System.out.println("test");
        setContentView(R.layout.activity_main);

        btnLocationMode= (ImageButton) findViewById(R.id.btnLocationMode);
        btnSatellite= (ImageButton) findViewById(R.id.btnSatellite);
        btnTraffic= (ImageButton) findViewById(R.id.btnTraffic);
        btnPano= (ImageButton) findViewById(R.id.btnPano);
        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE); //获取传感器管理服务
        mCurrentMode= MyLocationConfiguration.LocationMode.NORMAL;

        btnLocationMode.setOnClickListener(this);
        btnTraffic.setOnClickListener(this);
        btnSatellite.setOnClickListener(this);
        btnPano.setOnClickListener(this);
        findViewById(R.id.btnRoutePlan).setOnClickListener(this);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));


        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(pano){
                    Intent intent=new Intent(MainActivity.this,PanoDemoMain.class);
                    intent.putExtra("lat",latLng.latitude);
                    intent.putExtra("lon",latLng.longitude);
                    startActivity(intent);
                }

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    //方向传感器
    @Override
    public void onSensorChanged(SensorEvent event) {
        double x=event.values[SensorManager.DATA_X];
        if(Math.abs(x-lastX)>1.0){
            mCurrentDirection= (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX=x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //点击事件监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLocationMode:
                switch (mCurrentMode) {
                    case NORMAL:
                        btnLocationMode.setImageResource(R.drawable.location2);
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        btnLocationMode.setImageResource(R.drawable.location1);
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0).rotate(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        btnLocationMode.setImageResource(R.drawable.location3);
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                                mCurrentMode, true, null));
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btnSatellite:
                if(satellite){
                    satellite=false;
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    btnSatellite.setImageResource(R.drawable.satellite_false);
                }else {
                    satellite=true;
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    btnSatellite.setImageResource(R.drawable.satellite_true);
                }
                break;
            case R.id.btnTraffic:
                if(traffic){
                    traffic=false;
                    mBaiduMap.setTrafficEnabled(false);
                    btnTraffic.setImageResource(R.drawable.traffic_false);
                }else {
                    traffic=true;
                    mBaiduMap.setTrafficEnabled(true);
                    btnTraffic.setImageResource(R.drawable.traffic_true);
                }
                break;
            case R.id.btnRoutePlan:
                Intent intent=new Intent(MainActivity.this,RouteActivity.class);
                intent.putExtra("lat",mCurrentLat);
                intent.putExtra("lon",mCurrentLon);
                intent.putExtra("city",mCurrentCity);
                startActivity(intent);
                break;
            case R.id.btnPano:
                pano=!pano;
                if(pano){
                    btnPano.setImageResource(R.drawable.pano_true);
                }else {
                    btnPano.setImageResource(R.drawable.pano_false);
                }
                break;
        }
    }



    //定位SDK监听函数
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if(location==null||mMapView==null){
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            String str=location.getCity();
            if(str!=null){
                mCurrentCity=location.getCity();
            }
            //Log.i("MainActivity", "onReceiveLocation: "+mCurrentCity);
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }



}

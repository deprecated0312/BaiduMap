package com.example.pxq0312.baidumap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class SelectLocationActivity extends AppCompatActivity implements BaiduMap.OnMapClickListener {
    MapView mMapView;
    BaiduMap mBaiduMap;

    private ProgressDialog progressDialog; //载入对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("请稍候……");
        progressDialog.setMessage("正在加载……");
        progressDialog.show();

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
        mBaiduMap.setOnMapClickListener(this);
        Intent intent=getIntent();
        double lon=intent.getDoubleExtra("lon",0);
        double lat=intent.getDoubleExtra("lat",0);
        LatLng ll = new LatLng(lat,lon);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(50)
                .direction(100).latitude(lat)
                .longitude(lon).build();
        mBaiduMap.setMyLocationData(locData);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, null);
        mBaiduMap.setMyLocationConfiguration(config);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Intent intent=new Intent();
        intent.putExtra("lon",latLng.longitude);
        intent.putExtra("lat",latLng.latitude);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }
}

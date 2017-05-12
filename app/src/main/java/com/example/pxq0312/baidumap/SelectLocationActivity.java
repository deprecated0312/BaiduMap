package com.example.pxq0312.baidumap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class SelectLocationActivity extends AppCompatActivity implements BaiduMap.OnMapClickListener {
    MapView mMapView;
    BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

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

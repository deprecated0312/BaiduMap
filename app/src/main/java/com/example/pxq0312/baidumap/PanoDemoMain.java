package com.example.pxq0312.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.model.BaiduPanoData;
import com.baidu.lbsapi.model.BaiduPoiPanoData;
import com.baidu.lbsapi.panoramaview.PanoramaRequest;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.lbsapi.panoramaview.PanoramaViewListener;

/**
 * 全景Demo主Activity
 */
public class PanoDemoMain extends Activity {

    private static final String LTAG = "BaiduPanoSDKDemo";

    private PanoramaView mPanoView;
    //private TextView textTitle;

    private double lon;
    private double lat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 先初始化BMapManager
        initBMapManager();
        setContentView(R.layout.panodemo_main);

        initView();

        Intent intent = getIntent();
        lon=intent.getDoubleExtra("lon",0);
        lat=intent.getDoubleExtra("lat",0);
        if (intent != null) {
            testPanoByType(intent.getIntExtra("type", -1));
        }


    }

    private void initBMapManager() {
        PanoDemoApplication app = (PanoDemoApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(app);
            app.mBMapManager.init(new PanoDemoApplication.MyGeneralListener());
        }
    }

    private void initView() {
        //textTitle = (TextView) findViewById(R.id.panodemo_main_title);
        mPanoView = (PanoramaView) findViewById(R.id.panorama);
    }

    private void testPanoByType(int type) {
        mPanoView.setShowTopoLink(true);

        // 测试回调函数,需要注意的是回调函数要在setPanorama()之前调用，否则回调函数可能执行异常
        mPanoView.setPanoramaViewListener(new PanoramaViewListener() {

            @Override
            public void onLoadPanoramaBegin() {
                Log.i(LTAG, "onLoadPanoramaStart...");
            }

            @Override
            public void onLoadPanoramaEnd(String json) {
                Log.i(LTAG, "onLoadPanoramaEnd : " + json);
            }

            @Override
            public void onLoadPanoramaError(String error) {
                Log.i(LTAG, "onLoadPanoramaError : " + error);
            }

            @Override
            public void onDescriptionLoadEnd(String json) {

            }

            @Override
            public void onMessage(String msgName, int msgType) {

            }

            @Override
            public void onCustomMarkerClick(String key) {

            }
        });

//        if (type == PanoDemoActivity.PID) {
//            textTitle.setText(R.string.demo_desc_pid);
//
//            mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
//            String pid = "0900220000141205144547300IN";
//            mPanoView.setPanorama(pid);
//        } else if (type == PanoDemoActivity.GEO) {
//            textTitle.setText(R.string.demo_desc_geo);
//
//            double lat = 30.756568;
//            double lon = 103.935464;
//            mPanoView.setPanorama(lon, lat);
//        } else if (type == PanoDemoActivity.MERCATOR) {
//            textTitle.setText(R.string.demo_desc_mercator);
//
//            mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
//            int mcX = 12971348;
//            int mcY = 4826239;
//            mPanoView.setPanorama(mcX, mcY);
//        }
        //textTitle.setText(R.string.demo_desc_geo);

        //double lat = 30.756568;
       // double lon = 103.935464;
        mPanoView.setPanoramaImageLevel(PanoramaView.ImageDefinition.ImageDefinitionHigh);
        mPanoView.setPanorama(lon, lat);

    }

//    private void testPanoramaRequest() {
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                PanoramaRequest panoramaRequest = PanoramaRequest.getInstance(PanoDemoMain.this);
//
//                String pid = "01002200001307201550572285B";
//                Log.e(LTAG, "PanoramaRecommendInfo");
//                Log.i(LTAG, panoramaRequest.getPanoramaRecommendInfo(pid).toString());
//
//                String iid = "978602fdf6c5856bddee8b62";
//                Log.e(LTAG, "PanoramaByIIdWithJson");
//                Log.i(LTAG, panoramaRequest.getPanoramaByIIdWithJson(iid).toString());
//
//                // 通过百度经纬度坐标获取当前位置相关全景信息，包括是否有外景，外景PID，外景名称等
//                double lat = 40.029233;
//                double lon = 116.32085;
//                BaiduPanoData mPanoDataWithLatLon = panoramaRequest.getPanoramaInfoByLatLon(lon, lat);
//                Log.e(LTAG, "PanoDataWithLatLon");
//                Log.i(LTAG, mPanoDataWithLatLon.getDescription());
//
//                // 通过百度墨卡托坐标获取当前位置相关全景信息，包括是否有外景，外景PID，外景名称等
//                int x = 12948920;
//                int y = 4842480;
//                BaiduPanoData mPanoDataWithXy = panoramaRequest.getPanoramaInfoByMercator(x, y);
//
//                Log.e(LTAG, "PanoDataWithXy");
//                Log.i(LTAG, mPanoDataWithXy.getDescription());
//
//                // 通过百度地图uid获取该poi下的全景描述信息，以此来判断此UID下是否有内景及外景
//                String uid = "bff8fa7deabc06b9c9213da4";
//                BaiduPoiPanoData poiPanoData = panoramaRequest.getPanoramaInfoByUid(uid);
//                Log.e(LTAG, "poiPanoData");
//                Log.i(LTAG, poiPanoData.getDescription());
//            }
//        }).start();
//
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPanoView.destroy();
        super.onDestroy();
    }

}

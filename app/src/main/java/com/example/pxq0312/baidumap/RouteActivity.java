package com.example.pxq0312.baidumap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity implements OnGetSuggestionResultListener, View.OnClickListener {
    private AutoCompleteTextView etStart;
    private AutoCompleteTextView etEnd;
    private ArrayAdapter<String> sugAdapter;
    private List<String> suggest;
    private SuggestionSearch mSuggestionSearch;

    private ListView listView;
    private List<String> list;

    private boolean selectStart;
    private boolean selectEnd;
    private double startLon;
    private double startLat;
    private double endLon;
    private double endLat;

    private String mCurrentCity="成都市";
    private double mCurrentLat;
    private double mCurrentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        etStart= (AutoCompleteTextView) findViewById(R.id.etStart);
        etEnd= (AutoCompleteTextView) findViewById(R.id.etEnd);

        Intent intent=getIntent();
        mCurrentCity=intent.getStringExtra("city");
        mCurrentLat=intent.getDoubleExtra("lat",0);
        mCurrentLon=intent.getDoubleExtra("lon",0);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        sugAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        etEnd.setAdapter(sugAdapter);
        etStart.setAdapter(sugAdapter);
        etEnd.setThreshold(1);
        etStart.setThreshold(1);
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    return;
                }
                //使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(s.toString()).city(mCurrentCity));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        etStart.addTextChangedListener(textWatcher);
        etEnd.addTextChangedListener(textWatcher);
        View.OnFocusChangeListener listener=new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(v.getId()==R.id.etStart){
                    selectStart=hasFocus;
                }else {
                    selectEnd=hasFocus;
                }
            }
        };
        etStart.setOnFocusChangeListener(listener);
        etEnd.setOnFocusChangeListener(listener);
        etEnd.requestFocus();

        findViewById(R.id.btnSwap).setOnClickListener(this);
        findViewById(R.id.btnRoutePlan).setOnClickListener(this);
        findViewById(R.id.btnSelectLocation).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);

        list=new ArrayList<>();
        try {
            FileInputStream fis=openFileInput("data");
            BufferedReader br=new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line=br.readLine())!=null){
                list.add(line);
            }
            br.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> data=new ArrayList<>();;
        for(int i=list.size()-1;i>=0;i--){
            data.add(list.get(i));
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView= (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str=((TextView)view).getText().toString();
                String[] strings=str.split("->");
                etStart.setText(strings[0]);
                etEnd.setText(strings[1]);
            }
        });

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);

            }
        }
        sugAdapter = new ArrayAdapter<String>(RouteActivity.this, android.R.layout.simple_dropdown_item_1line, suggest);
        etEnd.setAdapter(sugAdapter);
        etStart.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRoutePlan:
                Intent intent=new Intent(RouteActivity.this,RoutePlanActivity.class);
                intent.putExtra("lat",mCurrentLat);
                intent.putExtra("lon",mCurrentLon);
                intent.putExtra("city",mCurrentCity);
                intent.putExtra("start",etStart.getText().toString());
                intent.putExtra("end",etEnd.getText().toString());
                intent.putExtra("startLon",startLon);
                intent.putExtra("startLat",startLat);
                intent.putExtra("endLat",endLat);
                intent.putExtra("endLon",endLon);
                int id=((RadioGroup)findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
                intent.putExtra("id",id);
                startActivity(intent);
                if(!(etStart.getText().toString().equals("地图上的点")||etEnd.getText().toString().equals("地图上的点"))){
                    try {
                        FileOutputStream fos=openFileOutput("data", Context.MODE_APPEND);
                        BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));
                        bw.write(etStart.getText().toString()+"->"+etEnd.getText().toString());
                        bw.newLine();
                        bw.close();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    list.add(etStart.getText().toString()+"->"+etEnd.getText().toString());
                    List<String> data=new ArrayList<>();;
                    for(int i=list.size()-1;i>=0;i--){
                        data.add(list.get(i));
                    }
                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
                    listView.setAdapter(adapter);
                }
                break;
            case R.id.btnSwap:
                String temp=etEnd.getText().toString();
                etEnd.setText(etStart.getText().toString());
                etStart.setText(temp);
                double t=startLat;
                startLat=endLat;
                endLat=t;
                t=startLon;
                startLon=endLon;
                endLon=t;
                break;
            case R.id.btnSelectLocation:
                Intent i=new Intent(RouteActivity.this,SelectLocationActivity.class);
                i.putExtra("lon",mCurrentLon);
                i.putExtra("lat",mCurrentLat);
                if(selectStart){
                    startActivityForResult(i,1);
                }else if(selectEnd){
                    startActivityForResult(i,2);
                }
                break;
            case R.id.clear:
                try {
                    FileOutputStream fos=openFileOutput("data", Context.MODE_PRIVATE);
                    fos.close();
                    listView.setAdapter(null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==1){
                startLat=data.getDoubleExtra("lat",0);
                startLon=data.getDoubleExtra("lon",0);
                etStart.setText("地图上的点");
            }else if(requestCode==2){
                endLat=data.getDoubleExtra("lat",0);
                endLon=data.getDoubleExtra("lon",0);
                etEnd.setText("地图上的点");
            }
        }
    }
}

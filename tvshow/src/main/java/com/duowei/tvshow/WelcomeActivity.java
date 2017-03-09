package com.duowei.tvshow;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.duowei.tvshow.bean.CityCode;
import com.duowei.tvshow.bean.CityCodes;
import com.duowei.tvshow.httputils.AsyncUtils;
import com.duowei.tvshow.httputils.DownHTTP;
import com.duowei.tvshow.httputils.VolleyResultListener;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.List;


public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences.Editor mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SharedPreferences preferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        mEdit = preferences.edit();
        int weather = preferences.getInt("weather", 0);
        Log.e("weather==",weather+"号");
        if(weather==1){
           Http_File();
        }else{
            Http_weather();
        }
//        Intent intent = new Intent(this, MultimediaActivity.class);
//        Intent intent = new Intent(this, SecondActivity.class);
//        startActivity(intent);
    }

    private void Http_weather() {
        DownHTTP.getVolley2("http://7xpqoi.com1.z0.glb.clouddn.com/citycode.txt", new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                CityCode cityCode = gson.fromJson(response, CityCode.class);
                List<CityCode.ZoneBeanXX> zone = cityCode.getZone();
                DataSupport.deleteAll(CityCodes.class);
                for(int i=0;i<zone.size();i++){
                    List<CityCode.ZoneBeanXX.ZoneBeanX> zone1 = zone.get(i).getZone();
                    for(int j=0;j<zone1.size();j++){
                        List<CityCodes> listcode = zone1.get(j).getZone();
                        DataSupport.saveAll(listcode);
                    }
                }
                mEdit.putInt("weather",1);
                mEdit.commit();
                Http_File();
            }
        });
    }

    private void Http_File() {
        AsyncUtils asyncUtils = new AsyncUtils(WelcomeActivity.this);
        asyncUtils.execute("http://7xpqoi.com1.z0.glb.clouddn.com/video2.zip");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

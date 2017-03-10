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
import com.duowei.tvshow.bean.OneDataBean;
import com.duowei.tvshow.bean.ZoneTime;
import com.duowei.tvshow.httputils.AsyncUtils;
import com.duowei.tvshow.httputils.DownHTTP;
import com.duowei.tvshow.httputils.VolleyResultListener;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.util.List;


public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences.Editor mEdit;
    private String currentVersion="";//当前版本
    private String zoneName="发发";//当前电视区号
    private String url="http://ai.wxdw.top/mobile.php?act=module&weid=175&name=light_box_manage&do=GetZoneTime&storeid=20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SharedPreferences preferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        mEdit = preferences.edit();
        int weather = preferences.getInt("weather", 0);
        currentVersion = preferences.getString("version", "");
        Log.e("weather==",weather+"号");
//        if(weather==1){
//           Http_File();
//        }else{
//            Http_weather();
//        }
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
        DownHTTP.getVolley(url, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
            @Override
            public void onResponse(String response) {
                Log.e("response==",response);
                Gson gson = new Gson();
                ZoneTime zoneTime = gson.fromJson(response, ZoneTime.class);
                String version = zoneTime.getVersion();//新版本号
                if(currentVersion.equals(version)){//版本号相同
                    toMainActivity();
                }else{
                    String down_data = zoneTime.getDown_data();//压缩包下载地址
                    List<ZoneTime.ZoneTimeBean> list_zone = zoneTime.getZone_time();//电视区域信息
                    DataSupport.deleteAll(OneDataBean.class);
                    /**找到该电视区号对应的数据信息集*/
                    for(int i=0;i<list_zone.size();i++){
                        ZoneTime.ZoneTimeBean.ZoneBean zone = list_zone.get(i).getZone();//电视区号
                        if(zoneName.equals(zone.getZone())){//如何区号等于当前电视区号
                            List<ZoneTime.ZoneTimeBean.OneDataBean> one_data = list_zone.get(i).getOne_data();
                            for(int j=0;j<one_data.size();j++){
                                String time = one_data.get(j).getTime();//起始跟结束时间
                                String ad = one_data.get(j).getAd();//动态广告词
                                String video_palce = one_data.get(j).getVideo_palce();//视频的位置
                                String image_name = one_data.get(j).getFile_name().getImage_name();//图片名字
                                String video_name = one_data.get(j).getFile_name().getVideo_name();//视频名称
                                /**插入数据库*/
                                OneDataBean oneDataBean = new OneDataBean(time, ad, video_palce, image_name, video_name);
                                oneDataBean.save();
                            }
                        }
                    }
//                    Http_File("http://192.168.1.78:3344/video.zip");
                }
            }
        });
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
//                Http_File();
            }
        });
    }

    private void Http_File(String url) {
        AsyncUtils asyncUtils = new AsyncUtils(WelcomeActivity.this);
        asyncUtils.execute(url);
    }
    private void toMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}

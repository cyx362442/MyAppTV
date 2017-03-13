package com.duowei.tvshow;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
    private String mZoneNum="";
//    private String url="http://ai.wxdw.top/mobile.php?act=module&weid=175&name=light_box_manage&do=GetZoneTime&storeid=30";
    private String url;
    private String mWeid;
    private String mWurl;
    private String mStoreid;//门店ID
    private Intent mIntent;
    private String playMode="";//播放模式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SharedPreferences preferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        mEdit = preferences.edit();
        int weather = preferences.getInt("weather", 0);
        currentVersion = preferences.getString("version", "");
        mWurl = preferences.getString("wurl", "");
        mWeid = preferences.getString("weid", "");
        mStoreid=preferences.getString("storeid","");
        mZoneNum=preferences.getString("zoneNum","");
        Log.e("====",mWurl+":"+mWeid+":"+mStoreid+":"+mZoneNum);
        if(TextUtils.isEmpty(mWurl)||TextUtils.isEmpty(mWeid)||TextUtils.isEmpty(mStoreid)||TextUtils.isEmpty(mZoneNum)){
            mIntent=new Intent(this,SettingActivity.class);
            startActivity(mIntent);
            finish();
            return;
        }
        url ="http://"+mWurl+"/mobile.php?act=module&weid="+mWeid+"&name=light_box_manage&do=GetZoneTime&storeid="+mStoreid;
//        if(weather==1){
//           Http_File();
//        }else{
//            Http_weather();
//        }
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
        DownHTTP.getVolley(this.url, new VolleyResultListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("====",error+"");
                Toast.makeText(WelcomeActivity.this,"网络异常",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onResponse(String response) {
                Log.e("response==",response);
                Gson gson = new Gson();
                ZoneTime zoneTime = gson.fromJson(response, ZoneTime.class);
                String version = zoneTime.getVersion();//新版本号
                if(currentVersion.equals(version)){//版本号相同
                    toMainActivity();
                }else{//版本号不同更新
                    mEdit.putString("version",version);
                    mEdit.commit();
                    String down_data = zoneTime.getDown_data();//压缩包下载地址
                    List<ZoneTime.ZoneTimeBean> list_zone = zoneTime.getZone_time();//电视区域信息
                    DataSupport.deleteAll(OneDataBean.class);
                    /**找到该电视区号对应的数据信息集*/
                    for(int i=0;i<list_zone.size();i++){
                        ZoneTime.ZoneTimeBean.ZoneBean zone = list_zone.get(i).getZone();//电视区号
                        if(mZoneNum.equals(zone.getZone())){//如何区号等于当前电视区号
                            Log.e("======","区号相等");
                            List<ZoneTime.ZoneTimeBean.OneDataBean> one_data = list_zone.get(i).getOne_data();
                            for(int j=0;j<one_data.size();j++){
                                String time = one_data.get(j).getTime();//起始跟结束时间
                                String ad = one_data.get(j).getAd();//动态广告词
                                String video_palce = one_data.get(j).getVideo_palce();//视频的位置
                                String image_name = one_data.get(j).getFile_name().getImage_name();//图片名称
                                String video_name = one_data.get(j).getFile_name().getVideo_name();//视频名称
                                if(TextUtils.isEmpty(ad.trim())&&video_name.equals("null")){//纯图片
                                    playMode="1";
                                }else if(!video_name.equals("null")&&TextUtils.isEmpty(ad)){//图片、视频混合
                                    playMode="2";
                                }else if(!TextUtils.isEmpty(ad.trim())){//带滚动字体
                                    playMode="3";
                                }else{
                                    playMode="4";
                                }
                                /**插入数据库*/
                                OneDataBean oneDataBean = new OneDataBean(playMode,time, ad, video_palce, image_name, video_name);
                                oneDataBean.save();
                            }
                        }
                    }
                    Log.e("======",down_data);
                    Http_File(down_data);
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
        mIntent = new Intent(this, MainActivity.class);
        startActivity(mIntent);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}

package com.duowei.tvshow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.duowei.tvshow.bean.OneDataBean;
import com.duowei.tvshow.contact.ConstsCode;
import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.fragment.VideoFragment;
import com.duowei.tvshow.jcvideoplayer.JCVideoPlayer;
import com.duowei.tvshow.utils.CurrentTime;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity {
    private ServiceBroadCast mBroadCast;
    private ImageView mImageView;
    private int[] mId;
    private File mFile;
    private VideoFragment mFragment;
    private int mLastTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        mImageView = (ImageView) findViewById(R.id.image);
        mId = new int[]{R.id.frame01,R.id.frame02,R.id.frame03,
                R.id.frame04,R.id.frame05,R.id.frame06,
                R.id.frame07,R.id.frame08,R.id.frame09,};
        ArrayList<String> listImage = FileDir.getImgPath();
        mFile = new File(listImage.get(0));
        IntentFilter intentFilter = new IntentFilter(ConstsCode.ACTION_START_HEART);
        mBroadCast = new ServiceBroadCast();
        registerReceiver(mBroadCast,intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Picasso.with(this).load(mFile).fit().centerInside().into(mImageView);
    }

    @Override
    protected void onStop() {
        JCVideoPlayer.releaseAllVideos();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadCast);
    }
    /**每分钟收一次广播 */
    public class ServiceBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("======",mLastTime+":"+CurrentTime.getTime());
            if(mLastTime>CurrentTime.getTime()){//上次时间段还未结束,返回，继续之前播放
                return;
              }
            String action = intent.getAction();
            if(action.equals(ConstsCode.ACTION_START_HEART)){
                List<OneDataBean> list = DataSupport.findAll(OneDataBean.class);
                for(OneDataBean bean:list){
                    String time = bean.time;
                    boolean newTime = isNewTime(time);
                    if(newTime==true){//找到新的时间段
                        String playMode = bean.playMode;//播放模式
                        if(playMode.equals("1")){//单图片模式
                            mFile=new File(FileDir.getVideoName()+bean.image_name);//拼接图片路径
                            Picasso.with(ShowActivity.this).load(mFile).fit().centerInside().into(mImageView);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            if(mFragment!=null){
                                transaction.remove(mFragment);
                                transaction.commit();
                            }
                        }else if(playMode.equals("2")){//视频图像混排模式
                            if(mFragment!=null){
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.remove(mFragment);
                                transaction.commit();
                            }
                            mFragment=new VideoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("videoname",bean.video_name);//图片名称
                            mFragment.setArguments(bundle);
                            int place = Integer.parseInt(bean.video_palce);//视频位置
                            mFile=new File(FileDir.getVideoName()+bean.image_name);//拼接图片路径
                            Picasso.with(ShowActivity.this).load(mFile).fit().centerInside().into(mImageView);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(mId[place-1],mFragment);
                            transaction.commit();
                        }
                        break;
                    }
                }
            }
        }
    }
    /**当前系统时间是否在某个时间段内*/
    private boolean isNewTime(String time) {
        boolean b;
        String firstTime = time.substring(0, time.indexOf("-")).replace(":","");
        String lastTime = time.substring(time.indexOf("-") + 1, time.length()).replace(":","");
        int first = Integer.parseInt(firstTime);
        int last = Integer.parseInt(lastTime);
        if(CurrentTime.getTime()>=first&&CurrentTime.getTime()<last){
            b=true;
            mLastTime=last;
        }else{
            b=false;
        }
        return b;
    }
}

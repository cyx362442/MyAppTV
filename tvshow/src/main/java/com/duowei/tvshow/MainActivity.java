package com.duowei.tvshow;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duowei.tvshow.media.MediaPlayerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MediaPlayerHelper helper;
    TextView textView;
    List<String> logList = new ArrayList<String>();
    StringBuffer buffer = new StringBuffer();
    TextView tvProgress;
    Timer timer = new Timer();
    TimerTask updateProgress = new TimerTask() {
        @Override
        public void run() {
            if(helper.getMediaPlayerState(helper.getMediaPlayer()) == MediaPlayerHelper.MediaPlayerState.PLAYING){
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 屏幕翻转之后，getCurrentPosition操作报错：非法的状态
                        tvProgress.setText(helper.getCurrentPosition() + "/" + helper.getDuration() + " - "
                                + (helper.getDuration() != 0 ? (int)(1.0f * helper.getCurrentPosition() / helper.getDuration() * 100) : 0) + "%");
                    }
                });
            }
        }
    };
    Handler mUIHandler = new Handler();
    /*
     * 屏幕方向监听器
     */
    int currentOritation;
    private OrientationEventListener mOrientationEventListener;
    private boolean autoChangeOritation = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        tvProgress = (TextView) findViewById(R.id.tvProgress);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.button10).setOnClickListener(this);
        findViewById(R.id.button11).setOnClickListener(this);
        findViewById(R.id.button12).setOnClickListener(this);
        findViewById(R.id.button13).setOnClickListener(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
//        helper = new MediaPlayerHelper(surfaceView);

        helper.setOnMediaStateChangeListener(new MediaPlayerHelper.OnMediaPlayerStateChangeListener() {

            @Override
            public void onMediaPlayerStateChange(MediaPlayer mediaPlayer, MediaPlayerHelper.MediaPlayerState mediaState) {
//                System.out.println(mediaPlayer + " -> " + mediaState);
//
//                if(mediaPlayer == helper.getMediaPlayer() && mediaState == MediaPlayerState.PREPARING)
//                    Toast.makeText(getApplicationContext(), "加载中", Toast.LENGTH_LONG).show();;

//                if(mediaState == MediaPlayerState.PLAYING)
//                    timer.schedule(updateProgress, 0, 500);
//                else if(mediaState == MediaPlayerState.COMPLETION || mediaState == MediaPlayerState.ERROR || mediaState == MediaPlayerState.PAUSED || mediaState == MediaPlayerState.STOPED)
//                    updateProgress.cancel();
            }
        });

        mOrientationEventListener = new OrientationEventListener(this) {

            @Override
            public void onOrientationChanged(int orientation) {
//                System.out.println("屏幕方向:" + orientation);

                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN) { // 没有有效的角度
                    return;
                }

                if( orientation > 350 || orientation < 10 ) { // 0度
                    currentOritation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }else if( orientation > 80 &&orientation < 100 ) { // 90度
                    currentOritation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                }else if( orientation > 170 &&orientation < 190 ) { // 180度
                    currentOritation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                } else if( orientation > 260 &&orientation < 280  ) { // 270度
                    currentOritation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    return; // 忽略其他角度
                }

                if(autoChangeOritation){
                    setRequestedOrientation(currentOritation);
                }
            }
        };
        mOrientationEventListener.enable();

        timer.schedule(updateProgress, 0, 500);

        helper.setAppendLogger(new MediaPlayerHelper.LogWrapper(null, false){
            @Override
            public int i(final String msg) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        logList.add(msg);
                        if(logList.size() > 10){
                            logList.remove(0);
                        }
                        buffer.replace(0, buffer.length(), "");
                        for(String log:logList)
                            buffer.append(log + "\n");
                        textView.setText(buffer.toString());
                    }
                });
                return -1;
            }
        });
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button1: helper.play("/sdcard/DCIM/Camera/VID_20170228_222358.mp4");break;
            case R.id.button2: helper.play("/sdcard/DCIM/Camera/VID20170227183500.mp4"); break;
            case R.id.button3:
//                helper.play("http://od2nqmk5j.bkt.clouddn.com/video2.mp4");
                helper.play("http://download.yxybb.com/bbvideo/web/d1/d13/d11/d1/f11-web.mp4");
                break;
            case R.id.button4: helper.play("/sdcard/tmp/8517151198000128.mp3"); break;
            case R.id.button5: helper.nextPlay("/sdcard/tmp/test_video_2.mp4"); break;

            case R.id.button6: helper.pause(); break;
            case R.id.button7: helper.start(); break;
            case R.id.button8: helper.stop(); break;

            case R.id.button9: {

                if(helper.getMediaPlayerState(helper.getMediaPlayer()) == MediaPlayerHelper.MediaPlayerState.PLAYING){

                    int p = helper.getCurrentPosition();
                    int d = helper.getDuration();
                    int s = d / 10;
                    if((p += s) < d){
                        helper.getLogger().i("快进十分之一（至"+p+"）");
                        helper.seekTo(p);
                    }else
                        helper.getLogger().i("取消快进操作");
                }else{
                    helper.getLogger().i("无效快进操作");
                }
            }
            break;
            case R.id.button10: {

                if(helper.getMediaPlayerState(helper.getMediaPlayer()) == MediaPlayerHelper.MediaPlayerState.PLAYING){

                    int p = helper.getCurrentPosition();
                    int d = helper.getDuration();
                    int s = d / 10;
                    if((p -= s) > 0){
                        helper.getLogger().i("快退十分之一（至"+p+"）");
                        helper.seekTo(p);
                    }else
                        helper.getLogger().i("取消快退操作");
                }else{
                    helper.getLogger().i("无效快退操作");
                }
            }
            break;
            case R.id.button11: helper.toggleMediaPlayerDisplayType();break;
            case R.id.button12: {

                if(helper.getMediaPlayerState(helper.getMediaPlayer()) == MediaPlayerHelper.MediaPlayerState.PLAYING){
                    helper.getLogger().i(helper.isLooping() ? "取消循环" : "开启循环");
                    helper.setLooging(!helper.isLooping());
                }else{
                    helper.getLogger().i("无效循环控制");
                }

                break;
            }

            case R.id.button13:
            {
                autoChangeOritation = !autoChangeOritation;

                if(autoChangeOritation){
                    setRequestedOrientation(currentOritation);
                }
            }
            break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "横屏模式", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "竖屏模式", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}

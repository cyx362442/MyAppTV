package com.duowei.mytvtest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.duowei.mytvtest.jcvideoplayer.JCVideoPlayer;

public class MainActivity extends AppCompatActivity {
    JCVideoPlayer mJcVideoPlayerStandard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJcVideoPlayerStandard = (JCVideoPlayer) findViewById(R.id.jc_video01);

        String SDPATH = Environment.getExternalStorageDirectory() + "/";
        mJcVideoPlayerStandard.setUp(SDPATH+"duowei/video/"+"Tpad_video_001.mp4",JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
    }
    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}

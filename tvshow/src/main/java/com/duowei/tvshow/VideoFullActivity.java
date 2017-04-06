package com.duowei.tvshow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.duowei.tvshow.jcvideoplayer.MyJVCPlayer;

import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoFullActivity extends AppCompatActivity {
    private ArrayList<String> videoPath;
    private MyJVCPlayer mJcVideoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_full);
        Intent intent = getIntent();
        if(intent==null){
            Toast.makeText(this,"找到不到视频",Toast.LENGTH_LONG).show();
            return;
        }
        videoPath = intent.getStringArrayListExtra("selectPaths");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mJcVideoPlayer = (MyJVCPlayer) findViewById(R.id.jcvideoplayer);
        //从第一部开始播放
        mJcVideoPlayer.setUp(videoPath.get(0), MyJVCPlayer.SCREEN_LAYOUT_NORMAL, "");
        mJcVideoPlayer.startVideo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyJVCPlayer.releaseAllVideos();
    }
}

package com.duowei.tvshow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.jcvideoplayer.JCVideoPlayer;
import com.duowei.tvshow.jcvideoplayer.JCVideoPlayerStandard;

import java.util.ArrayList;

public class VideoFullActivity extends AppCompatActivity {
    private ArrayList<String> videoPath;
    private JCVideoPlayerStandard mJcVideoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_full);
        Intent intent = getIntent();
        if(intent==null){
            Toast.makeText(this,"找到不到社频",Toast.LENGTH_LONG).show();
            return;
        }
        videoPath = intent.getStringArrayListExtra("selectPaths");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mJcVideoPlayer = (JCVideoPlayerStandard) findViewById(R.id.jcvideoplayer);
        //从第一部开始播放
        mJcVideoPlayer.setUp(videoPath.get(0), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
    }
    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}

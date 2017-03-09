package com.duowei.tvshow.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.duowei.tvshow.R;
import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.media.MediaPlayerHelper;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

    private MediaPlayerHelper mHelper;
    private ArrayList<String> mListPath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListPath = FileDir.getVideoPath();//获取下载的视频文件路径
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_main, container, false);
        SurfaceView sv = (SurfaceView) inflate.findViewById(R.id.surfaceView);
        mHelper = new MediaPlayerHelper(sv, mListPath);
        return inflate;
    }
    @Override
    public void onStart() {
        mHelper.play(mListPath.get(0));//从第一集开始播放
        mHelper.toggleMediaPlayerDisplayType();//全屏
        super.onStart();
    }
}

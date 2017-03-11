package com.duowei.tvshow.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowei.tvshow.R;
import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.jcvideoplayer.JCVideoPlayerStandard;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {

//    private ArrayList<String> listPath;
    private JCVideoPlayerStandard mJcVideoPlayer;
    private String mVideoname;

    public String getVideoname() {
        return mVideoname;
    }

    public void setVideoname(String videoname) {
        mVideoname = videoname;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        listPath = FileDir.getVideoPath();
        Bundle bundle = getArguments();
        if(bundle!=null){
            mVideoname = bundle.getString("videoname");
            Log.e("====",mVideoname);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_main, container, false);
        if(!TextUtils.isEmpty(mVideoname)){
            mJcVideoPlayer = (JCVideoPlayerStandard) inflate.findViewById(R.id.jcvideoplayer);
            //从第一部开始播放
            mJcVideoPlayer.setUp(FileDir.getVideoName()+mVideoname,JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        }
        return inflate;
    }
    @Override
    public void onStart() {
        super.onStart();
    }
}

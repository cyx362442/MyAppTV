package com.duowei.tvshow;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duowei.tvshow.bean.OneDataBean;
import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.fragment.VideoFragment;
import com.duowei.tvshow.jcvideoplayer.JCVideoPlayer;
import com.squareup.picasso.Picasso;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private ImageView mImageView;
    private int[] mId;
    private File mFile;
    private List<OneDataBean> mListImg;
    private FragmentTransaction mFt;
    private FragmentManager mFm;
    VideoFragment videoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mImageView = (ImageView) findViewById(R.id.image);
        mId = new int[]{R.id.frame01,R.id.frame02,R.id.frame03,
                R.id.frame04,R.id.frame05,R.id.frame06,
                R.id.frame07,R.id.frame08,R.id.frame09,};
        ArrayList<String> listImage = FileDir.getImgPath();
        mFile = new File(listImage.get(0));

        mListImg = DataSupport.where("playMode=?","2").find(OneDataBean.class);
        videoFragment = new VideoFragment();
        mFm = getSupportFragmentManager();
        mFt = mFm.beginTransaction();
        mFt.replace(mId[0],videoFragment);
        mFt.commit();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JCVideoPlayer.releaseAllVideos();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.remove(videoFragment).commit();
            }
        });

//        Picasso.with(this).load(new File(FileDir.getVideoName()+image_name)).fit().centerInside().into(mImageView);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Picasso.with(this).load(mFile).fit().centerInside().into(mImageView);
        String image_name = mListImg.get(0).image_name;
        Picasso.with(this).load(new File(FileDir.getVideoName()+image_name)).fit().centerInside().into(mImageView);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//    }

    @Override
    protected void onStop() {
        JCVideoPlayer.releaseAllVideos();
        super.onStop();
    }
}

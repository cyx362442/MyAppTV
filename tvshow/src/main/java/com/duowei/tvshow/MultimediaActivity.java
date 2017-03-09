package com.duowei.tvshow;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.duowei.tvshow.fragment.ImageFragment;
import com.duowei.tvshow.fragment.TopFragment;
import com.duowei.tvshow.fragment.VideoFragment;
import com.duowei.tvshow.view.TextSurfaceView;


public class MultimediaActivity extends AppCompatActivity {

    private TextSurfaceView mSufaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mSufaceView = (TextSurfaceView) findViewById(R.id.surfacview);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_top,new TopFragment());
        ft.replace(R.id.fragment_video,new VideoFragment());
        ft.replace(R.id.fragment_image,new ImageFragment());
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**滑动文字*/
        mSufaceView.setOrientation(1);//横向左滑动
        mSufaceView.setSpeed(1);//速度

        mSufaceView.setContent("   周麻婆愿景：周麻婆—中国“小食代”连锁餐饮品牌  周麻婆使命：让顾客感受家的味道，家的实惠，家的感觉" +
                "  周麻婆核心价值观：视员工为家人，视顾客为亲人，视股东为兄弟  周麻婆经营口号：在家有个好老婆，出门有个周麻婆。");
    }
}

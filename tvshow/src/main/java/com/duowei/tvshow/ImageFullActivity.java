package com.duowei.tvshow;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.fragment.ImageFragment;
import com.duowei.tvshow.view.RecyclerBanner;

import java.util.ArrayList;

public class ImageFullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);
        ArrayList<String> imgPath = FileDir.getImgPath();
        RecyclerBanner rb = (RecyclerBanner) findViewById(R.id.recyclebanner);
        rb.setDatas(imgPath);
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.frame_img,new ImageFragment());
//        ft.commit();
    }
}

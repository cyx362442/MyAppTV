package com.duowei.tvshow;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.duowei.tvshow.contact.FileDir;
import com.duowei.tvshow.fragment.ImageFragment;
import com.duowei.tvshow.fragment.TopFragment;
import com.duowei.tvshow.fragment.VideoFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ArrayList<String> listImage;
    private int[] mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mImageView = (ImageView) findViewById(R.id.image);
        mId = new int[]{R.id.frame01,R.id.frame02,R.id.frame03,
                R.id.frame04,R.id.frame05,R.id.frame06,
                R.id.frame07,R.id.frame08,R.id.frame09,};
        listImage = FileDir.getImgPath();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(mId[8],new VideoFragment());
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        File file = new File(listImage.get(0));
        Picasso.with(this).load(file).fit().centerInside().into(mImageView);
        super.onResume();
    }
}

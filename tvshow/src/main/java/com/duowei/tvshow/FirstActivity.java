package com.duowei.tvshow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.duowei.tvshow.contact.FileDir;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class FirstActivity extends AppCompatActivity {

    private ImageView mImageView;
    private ArrayList<String> mListPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        mImageView = (ImageView) findViewById(R.id.image1);
        mListPath = FileDir.getImgPath();
    }

    @Override
    protected void onResume() {
        File file = new File(mListPath.get(0));
        Picasso.with(this).load(file).fit().centerInside().into(mImageView);
        super.onResume();
    }
}

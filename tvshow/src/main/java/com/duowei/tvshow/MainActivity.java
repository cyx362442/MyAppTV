package com.duowei.tvshow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.view_image).setOnClickListener(this);
        findViewById(R.id.view_movie).setOnClickListener(this);
        findViewById(R.id.view_setting).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_movie:
                mIntent = new Intent(this, VideoFullActivity.class);
                startActivity(mIntent);
                break;
            case R.id.view_image:
                Toast.makeText(this,"view2",Toast.LENGTH_LONG).show();
                break;
            case R.id.view_setting:
                Toast.makeText(this,"view3",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
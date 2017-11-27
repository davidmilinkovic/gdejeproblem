package com.shabaton.gdejeproblem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticDataProvider.vrste.clear();
        StaticDataProvider.sluzbe.clear();

        StaticDataProvider.init();
        StaticDataProvider.thread.start();
        try {
            StaticDataProvider.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, GlavniActivity.class);
        startActivity(intent);
        finish();
    }
}

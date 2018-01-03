package com.shabaton.gdejeproblem;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.List;
import java.util.Locale;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String lng = sharedPref.getString("list_jezik", "");

        Locale locale = new Locale(lng);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        StaticDataProvider.statusi.clear();
        StaticDataProvider.init();
        StaticDataProvider.thread.start();
        try {
            StaticDataProvider.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(SplashActivity.this, GlavniActivity.class);
        startActivity(intent);
        finish();
    }
}

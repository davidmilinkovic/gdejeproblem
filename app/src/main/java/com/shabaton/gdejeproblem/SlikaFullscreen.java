package com.shabaton.gdejeproblem;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class SlikaFullscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slika_fullscreen);
        setSupportActionBar((Toolbar) findViewById(R.id.fulscreen_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView img = findViewById(R.id.img_fullscreen);
        getSupportActionBar().setTitle("");
        ProgressBar progressBar = findViewById(R.id.progressBar_fullscreen);
        Glide.with(this).load(Uri.parse(getIntent().getStringExtra("slika"))).fitCenter()
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri uri, Target<GlideDrawable> target, boolean b) {
                        progressBar.setVisibility(View.GONE);
                        Glide.with(SlikaFullscreen.this).load(R.drawable.nemaslike).into(img);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, Uri uri, Target<GlideDrawable> target, boolean b, boolean b1) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(img);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

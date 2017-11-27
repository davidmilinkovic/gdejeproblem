package com.shabaton.gdejeproblem;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class PregledProblemaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_problema);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_pregled);
        setSupportActionBar(tulbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        ((TextView)findViewById(R.id.textView2pregled)).setText(getIntent().getStringExtra("vrsta"));
        ((TextView)findViewById(R.id.txtViewLok_pregled)).setText(getIntent().getStringExtra("lokacija"));
        ((TextView)findViewById(R.id.tekstOpisPregled)).setText(getIntent().getStringExtra("opis"));

        String slika = getIntent().getStringExtra("slika");
        if(slika.length() > 0) {
            ImageView img = findViewById(R.id.imageView_pregled);
            ((TextView)findViewById(R.id.textView3pregled)).setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            Glide.with(this).load(Uri.parse(slika)).apply(RequestOptions.fitCenterTransform()).into(img);
        }

        ((ImageButton)findViewById(R.id.button3pregled)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PregledProblemaActivity.this, MapsActivity.class);
                intent.putExtra("latitude", Double.parseDouble(getIntent().getStringExtra("latitude")));
                intent.putExtra("longitude", Double.parseDouble(getIntent().getStringExtra("longitude")));
                intent.putExtra("potvrda", false);
                startActivity(intent);
            }
        });
    }
}

package com.shabaton.gdejeproblem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class PregledProblemaActivity extends BaseActivity {

    boolean isImageFitToScreen;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_problema);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_pregled);
        setSupportActionBar(tulbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getIntent().getStringExtra("vrsta"));

        ((TextView)findViewById(R.id.textView2pregled)).setText(getIntent().getStringExtra("vrsta"));
        ((TextView)findViewById(R.id.txtViewLok_pregled)).setText(getIntent().getStringExtra("lokacija"));
        ((TextView)findViewById(R.id.textViewStatusPregled)).setText(getIntent().getStringExtra("status"));
        ((TextView)findViewById(R.id.textViewStatusPregled)).setTextColor(Color.parseColor(getIntent().getStringExtra("statusBoja")));

        String opis = getIntent().getStringExtra("opis").replace("<br>", "\n");

        if(opis.length() > 0)
        {
            ((TextView)findViewById(R.id.tekstOpisPregled)).setText(opis);
            ((TextView)findViewById(R.id.tekstOpisPregled)).setTextColor(getResources().getColor(android.R.color.black));
        }
        else ((TextView)findViewById(R.id.tekstOpisPregled)).setText(R.string.pregled_content_nema_opisa);

        ProgressBar progressBar = findViewById(R.id.progressBar2);
        String slika = getIntent().getStringExtra("slika");
        if(slika.length() > 0) {
            ImageView img = findViewById(R.id.imageView_pregled);
            ((TextView)findViewById(R.id.textView3pregled)).setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(Uri.parse(slika))
                    .fitCenter()
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri uri, Target<GlideDrawable> target, boolean b) {
                            progressBar.setVisibility(View.GONE);
                            Glide.with(PregledProblemaActivity.this).load(R.drawable.nemaslike).into(img);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable glideDrawable, Uri uri, Target<GlideDrawable> target, boolean b, boolean b1) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(img);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PregledProblemaActivity.this, SlikaFullscreen.class);
                    intent.putExtra("slika", slika);
                    startActivity(intent);
                }
            });

        }
        else  progressBar.setVisibility(View.GONE);

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

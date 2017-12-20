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
import android.view.MenuItem;
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

import java.util.EventListener;

public class PregledProblemaActivity extends BaseActivity {

    boolean isImageFitToScreen;


    private ProblemViewModel.Problem p = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_problema);

        p = (ProblemViewModel.Problem) getIntent().getSerializableExtra("problem");

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_pregled);
        setSupportActionBar(tulbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(p.vrsta.naziv);

        ((TextView)findViewById(R.id.textView2pregled)).setText(p.vrsta.naziv);
        ((TextView)findViewById(R.id.txtViewLok_pregled)).setText(p.adresa);
        ((TextView)findViewById(R.id.textViewStatusPregled)).setText(p.statusi.get(0).status.naziv);
        ((TextView)findViewById(R.id.textViewStatusPregled)).setTextColor(Color.parseColor(p.statusi.get(0).status.boja));

        String opis = p.opis.replace("<br>", "\n");

        if(opis.length() > 0)
        {
            ((TextView)findViewById(R.id.tekstOpisPregled)).setText(opis);
            ((TextView)findViewById(R.id.tekstOpisPregled)).setTextColor(getResources().getColor(android.R.color.black));
        }
        else ((TextView)findViewById(R.id.tekstOpisPregled)).setText(R.string.pregled_content_nema_opisa);

        ProgressBar progressBar = findViewById(R.id.progressBar2);
        String slika = p.slika;
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
                intent.putExtra("latitude", p.latitude);
                intent.putExtra("longitude", p.longitude);
                intent.putExtra("potvrda", false);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.buttonStatusiPregled)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpisakStatusaFragment spisakStatusa = new SpisakStatusaFragment();
                spisakStatusa.prob = p;
                spisakStatusa.show(getSupportFragmentManager(), "SpisakStatusa");

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    public  void dialogZatvoren(ProblemViewModel.Problem p)
    {
        this.p = p;
        ((TextView)findViewById(R.id.textViewStatusPregled)).setText(p.statusi.get(0).status.naziv);
        ((TextView)findViewById(R.id.textViewStatusPregled)).setTextColor(Color.parseColor(p.statusi.get(0).status.boja));
    }
}

package com.shabaton.gdejeproblem;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GlavniActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
         KorisnikFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener

{

    public static final String PREF_USER_FIRST_TIME = "prvi_put";
    public GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private TextView txtNavNaslov;
    private TextView txtNavPodNaslov;
    private ImageView headerSlika;
    private boolean prijavljen;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavni);

        Boolean prviPut = Boolean.valueOf(Alati.citajPref(GlavniActivity.this, PREF_USER_FIRST_TIME, "true"));


        if(!StaticDataProvider.ucitano)
        {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
        }

        if (prviPut) {
            Intent introIntent = new Intent(this, Tour.class);
            introIntent.putExtra(PREF_USER_FIRST_TIME, prviPut);
            startActivity(introIntent);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_c);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View nav_header = navigationView.getHeaderView(0);
        txtNavNaslov = nav_header.findViewById(R.id.header_naslov);
        txtNavPodNaslov = nav_header.findViewById(R.id.header_podnaslov);
        headerSlika = nav_header.findViewById(R.id.headerSlika);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        promeniKorisnika();

        if(mAuth.getCurrentUser() != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new PocetniFragment(), "PocetniFragment").commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        else
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new KorisnikFragment(), "KorisnikFragment").commit();
            navigationView.setCheckedItem(R.id.nav_korisnik);
        }


        osveziServis();
    }

    private void osveziServis() {
        stopService(new Intent(this, ProveraStatusaService.class));
        if(FirebaseAuth.getInstance().getCurrentUser() == null) return;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean obavestenja = sharedPref.getBoolean("notif_status", false);

        if(obavestenja && mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, ProveraStatusaService.class);
            startService(intent);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 333:
                if(resultCode == RESULT_OK) {

                    navigationView.setCheckedItem(R.id.nav_moji_problemi);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MojiProblemiFragment(), "MojiProblemi").commit();

                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.fabDodaj), data.getStringExtra("poruka"), Snackbar.LENGTH_LONG).show();
                        }
                    }, 1000);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            MojiProblemiFragment mpf = (MojiProblemiFragment)getSupportFragmentManager().findFragmentByTag("MojiProblemi");
                            mpf.osveziProbleme();
                        }
                    }, 1500);

                }
                break;
            case 111:
                startActivity(getIntent());
                finish();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void promeniKorisnika() { // menja header navigacije na osnovu trenutnog korisnika
        FirebaseUser currentUser = mAuth.getCurrentUser();
        osveziServis();
        if (currentUser != null) {
            txtNavNaslov.setText(currentUser.getDisplayName());
            txtNavPodNaslov.setText(currentUser.getEmail());
            Glide.with(this).load(currentUser.getPhotoUrl()).into(headerSlika);
            prijavljen = true;

        } else {
            txtNavNaslov.setText(R.string.niste_prijavljeni);
            txtNavPodNaslov.setText("");
            headerSlika.setImageResource(android.R.drawable.sym_def_app_icon);
            prijavljen = false;
        }
    }

    private void greska(String naslov, String tekst)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(naslov)
                .setMessage(tekst)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        String tag = "";
        Class fragmentClass = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_prijava)
        {
            if(prijavljen)
            {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(this, PrijaviProblemActivity.class);
                startActivityForResult(intent, 333);
                return false;
            }
            else {
                greska(getString(R.string.popup_greska), getString(R.string.popup_greska_prijava_problema));
                fragmentClass = KorisnikFragment.class;
                navigationView.setCheckedItem(R.id.nav_korisnik);
            }
        }
        else if(id == R.id.nav_home)
        {
            if(prijavljen)
            {
                fragmentClass = PocetniFragment.class;
            }
            else {
                greska(getString(R.string.popup_greska), getString(R.string.popup_greska_pregled_problema));
                fragmentClass = KorisnikFragment.class;
                navigationView.setCheckedItem(R.id.nav_korisnik);
            }
        }
        else if (id == R.id.nav_moji_problemi)
        {
            if(prijavljen)
            {
                fragmentClass = MojiProblemiFragment.class;
                tag = "MojiProblemi";
            }
            else
            {
                greska(getString(R.string.popup_greska), getString(R.string.popup_greska_pregled_problema));
                fragmentClass = KorisnikFragment.class;
                navigationView.setCheckedItem(R.id.nav_korisnik);
            }
        }
        else if (id == R.id.nav_opcije)
        {
            Intent intent = new Intent(this, PodesavanjaActivity.class);
            startActivityForResult(intent, 111);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
        else if (id == R.id.nav_korisnik)
        {
            fragmentClass = KorisnikFragment.class;
        }
        else if(id == R.id.nav_pomoc)
        {
            Intent intent = new Intent(this, Tour.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();


        drawer.closeDrawer(GravityCompat.START);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

package net.geasoft.ksp.ksp;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GlavniActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PrijaviProblemFragment.OnFragmentInteractionListener,
        MojiProblemiFragment.OnFragmentInteractionListener,  OpcijeFragment.OnFragmentInteractionListener, KorisnikFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener

         {

             public GoogleApiClient mGoogleApiClient;
             private FirebaseAuth mAuth;

             @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //((TextView)findViewById(R.id.header_naslov)).setText("Niste prijavljeni");
        setContentView(R.layout.activity_glavni);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                 .requestEmail()
                 .build();
         // [END config_signin]

         mGoogleApiClient = new GoogleApiClient.Builder(this)
                 .enableAutoManage(this, this)
                 .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                 .build();

        mAuth = FirebaseAuth.getInstance();
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
    public void onResume()
    {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();/*
        if(currentUser != null)
        {
            ((TextView)findViewById(R.id.header_naslov)).setText(currentUser.getDisplayName());
            ((TextView)findViewById(R.id.header_podnaslov)).setText(currentUser.getEmail());
        }
        else
        {
            ((TextView)findViewById(R.id.header_naslov)).setText("Niste prijavljeni");
            ((TextView)findViewById(R.id.header_podnaslov)).setText("");
        }*/
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == R.id.nav_prijava) {
            fragmentClass = PrijaviProblemFragment.class;
        } else if (id == R.id.nav_moji_problemi) {
            fragmentClass = MojiProblemiFragment.class;
        } else if (id == R.id.nav_opcije) {
            fragmentClass = OpcijeFragment.class;
        } else if(id == R.id.nav_korisnik) {
            fragmentClass = KorisnikFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



     @Override
     public void onFragmentInteraction(Uri uri) {

     }


             @Override
             public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

             }
         }

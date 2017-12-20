package com.shabaton.gdejeproblem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private Marker tren;
    private  ActionBar ab;
    Boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_maps);
        setSupportActionBar(tulbar);

        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.pregled_content_lokacija);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng marker = new LatLng(getIntent().getDoubleExtra("latitude", 44.752993), getIntent().getDoubleExtra("longitude", 19.697438));
        editMode = getIntent().getBooleanExtra("potvrda", true);
        if(!editMode)
        {
            findViewById(R.id.frame_pretraga).setVisibility(View.GONE);
            tren = mMap.addMarker(new MarkerOptions().position(marker).title(getIntent().getStringExtra("adresa")));
            tren.showInfoWindow();
        }
        else mMap.setOnMapClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if(editMode) getMenuInflater().inflate(R.menu.menu_prijava, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_prijava_otkacaj:
                if(tren == null)
                {
                    greska(getString(R.string.popup_greska), getString(R.string.greska_nema_lokacije));
                    return true;
                }
                Intent intent = new Intent();

                Pair<String, String> p = LokacijaTekst();

                intent.putExtra("lat", tren.getPosition().latitude);
                intent.putExtra("lng", tren.getPosition().longitude);
                intent.putExtra("adresa", p.first);
                intent.putExtra("mesto", p.second);

                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        ((EditText)findViewById(R.id.editText2)).setText("");
        if(tren != null) tren.remove();
        tren = mMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.onMapReady_izabrana_lokacija)));

        ab.setTitle(LokacijaTekst().first);

    }

    private Pair<String, String> LokacijaTekst()
    {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<Address>();
        geocoder = new Geocoder(this, Locale.getDefault());
        boolean ima = true;

        try {
            addresses = geocoder.getFromLocation(tren.getPosition().latitude, tren.getPosition().longitude, 1);
        } catch (Exception e) {
            ima = false;
        }

        String adresa = tren.getPosition().latitude + ", " + tren.getPosition().longitude;
        String mesto = "";
        if(ima)
        {
            adresa = addresses.get(0).getAddressLine(0);
            mesto = addresses.get(0).getLocality();
        }
        return new Pair<>(adresa, mesto);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imageButton3)
        {
            String lokacija = ((EditText)findViewById(R.id.editText2)).getText().toString();
            if(lokacija !=  null && !lokacija.equals("")){
                new PretragaLokacije().execute(lokacija);
            }
        }
    }

    private class PretragaLokacije extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {

            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocationName(locationName[0], 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), R.string.onPostExecute_toast_nema_lokacije, Toast.LENGTH_SHORT).show();
                return;
            }

            if(tren != null) tren.remove();

            Address address = (Address) addresses.get(0);

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());


            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(getString(R.string.prijavi_problem_content_trenutna_lokacija));

            tren = mMap.addMarker(markerOptions);
            ab.setTitle(LokacijaTekst().first);

            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            ((EditText)findViewById(R.id.editText2)).setText("");

            // sklanjanje tastature
            View view = MapsActivity.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

}

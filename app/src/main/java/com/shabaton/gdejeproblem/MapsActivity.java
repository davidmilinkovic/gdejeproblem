package com.shabaton.gdejeproblem;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private Marker tren;

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

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Lokacija");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng marker = new LatLng(getIntent().getDoubleExtra("latitude", 44.752993), getIntent().getDoubleExtra("longitude", 19.697438));
        if(!getIntent().getBooleanExtra("potvrda", true))
        {
            findViewById(R.id.frame_potvrda).setVisibility(View.GONE);
            tren = mMap.addMarker(new MarkerOptions().position(marker).title("Izabrana lokacija"));
        }
        else mMap.setOnMapClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }

    @Override
    public void onMapClick(LatLng latLng) {

        ((Button)findViewById(R.id.mapa_potvrda)).setEnabled(true);
        if(tren != null) tren.remove();
        tren = mMap.addMarker(new MarkerOptions().position(latLng).title("Izabrana lokacija"));

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.mapa_potvrda){
            Intent intent = new Intent();

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

            intent.putExtra("lat", tren.getPosition().latitude);
            intent.putExtra("lng", tren.getPosition().longitude);
            intent.putExtra("adresa", adresa);
            intent.putExtra("mesto", mesto);

            setResult(RESULT_OK, intent);
            finish();
        }
    }
}

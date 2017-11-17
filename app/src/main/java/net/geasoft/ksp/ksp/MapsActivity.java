package net.geasoft.ksp.ksp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        LatLng srb = new LatLng(44.752993, 19.697438);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(srb));
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

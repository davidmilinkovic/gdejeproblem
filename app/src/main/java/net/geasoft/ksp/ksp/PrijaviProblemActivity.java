package net.geasoft.ksp.ksp;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class PrijaviProblemActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager lm;
    private LocationListener ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijavi_problem);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_prijava);
        setSupportActionBar(tulbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.checkBox:
                CheckBox boks = (CheckBox) findViewById(R.id.checkBox);
                Button dugme = (Button) findViewById(R.id.button3);
                if (boks.isChecked()) dugme.setVisibility(View.GONE);
                else dugme.setVisibility(View.VISIBLE);
                break;
            case R.id.button3:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(this, "NECE", Toast.LENGTH_LONG).show();
                    return;
                }
                Location location = lm.getLastKnownLocation("network");
                Toast.makeText(this, Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()), Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni_prijava, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.prijava_okej) {

            // pribavljanje tokena za dodavanje
            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            mUser.getToken(false)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                dodaj(idToken, mUser.getUid());
                            } else {
                                Toast.makeText(PrijaviProblemActivity.this, R.string.greska_token, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

        return super.onOptionsItemSelected(item);
    }

    private void dodaj(String token, String uid)
    {
        ProblemModel problem = new ProblemModel();
        problem.id_korisnika = 69;
        problem.id_vrste = 69;
        problem.opis = ((EditText)findViewById(R.id.editText)).getText().toString();
        problem.slika = "Nema";
        problem.opstina = "Bogac";
        problem.latitude = "44";
        problem.longitude = "44";
        ProblemModel.Dodaj(problem, this, token, uid);
    }

}

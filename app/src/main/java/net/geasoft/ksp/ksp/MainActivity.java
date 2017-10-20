package net.geasoft.ksp.ksp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity  {
    GoogleApiClient  mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public  void onResume()
    {
        super.onResume();
        Toast.makeText(this, "Resumovana", Toast.LENGTH_LONG);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) ((TextView)findViewById(R.id.textView)).setText("Prijavljeni ste kao: " + currentUser.getEmail());
        else ((TextView)findViewById(R.id.textView)).setText("Niste prijavljeni");
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                Intent k1 = new Intent(this, GooglePrijava.class);
                startActivity(k1);
                break;
        }
    }
}

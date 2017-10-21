package net.geasoft.ksp.ksp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null) {
            final String ime = mUser.getDisplayName();
            ((TextView) findViewById(R.id.textView)).setText("Prijavljeni ste kao: " + ime);
        }
        else ((TextView) findViewById(R.id.textView)).setText("Odjavljeni ste");
    }


    public void onClick(View v) throws InterruptedException {
        switch (v.getId()) {
            case R.id.button2:
                Intent k1 = new Intent(this, GooglePrijava.class);
                startActivity(k1);
                break;
        }
    }
}

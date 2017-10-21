package net.geasoft.ksp.ksp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;


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
        final String uid = mUser.getUid();
        mUser.getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {

                            String idToken = task.getResult().getToken();
                            final TextView mTextView = (TextView) findViewById(R.id.textView);

                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                            String url ="http://ksp.geasoft.net/token_validacija.php/?token="+idToken+"&uid="+uid;

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Display the first 500 characters of the response string.
                                            mTextView.setText("Response is: "+ response);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            mTextView.setText("That didn't work!");
                                        }
                                     });
                            queue.add(stringRequest);
                        } else {
                            ((TextView)findViewById(R.id.textView)).setText("Greska pri pribavljanju tokena.");
                        }
                    }
                });

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

package com.shabaton.gdejeproblem;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;



public class KorisnikFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener ,
        View.OnClickListener  {



    public KorisnikFragment() {

    }

    public static KorisnikFragment newInstance(String param1, String param2) {
        KorisnikFragment fragment = new KorisnikFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_korisnik, container, false);



        v.findViewById(R.id.sign_out_button).setOnClickListener(this);

        SignInButton btn = v.findViewById(R.id.sign_in_button);
        btn.setOnClickListener(this);
        TextView textView = (TextView)btn.getChildAt(0);
        textView.setText(R.string.prijavi_se);

        return v;
    }

    private static final String TAG = "GooglePrijava.";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            Boolean obavestenja = sharedPref.getBoolean("notif_status", false);
                            ((GlavniActivity) getActivity()).promeniKorisnika();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(((GlavniActivity)getActivity()).mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {

        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(((GlavniActivity)getActivity()).mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        ((GlavniActivity) getActivity()).promeniKorisnika();
                        updateUI(null);
                    }
                });

    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            ((TextView)getView().findViewById(R.id.korisnik_txt1)).setText(user.getDisplayName());
            ((TextView)getView().findViewById(R.id.korisnik_txt2)).setText(user.getEmail());
            Glide.with(getActivity()).load(user.getPhotoUrl()).into((ImageView)getView().findViewById(R.id.korisnik_slika));

            getView().findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            getView().findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            ((TextView)getView().findViewById(R.id.korisnik_txt1)).setText(R.string.prijava_za_aplikaciju_ksp);
            ((TextView)getView().findViewById(R.id.korisnik_txt2)).setText(R.string.da_biste_mogli);
            ((ImageView)getView().findViewById(R.id.korisnik_slika)).setImageResource(R.drawable.gloglo);

            getView().findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_in_button) {
            signIn();
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }

}

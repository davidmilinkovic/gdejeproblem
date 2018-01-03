package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class Vrsta implements Serializable {
    public final int id;
    public final String naziv;
    public final Sluzba sluzba;

    public Vrsta(int id, String naziv, Sluzba s) {
        this.id = id;
        this.naziv = naziv;
        this.sluzba = s;
    }

    @Override
    public String toString() {
        return naziv;
    }

   /* public static void Dodaj(int id_sluzbe, String naziv, PregledSluzbeVrsteFragment frag, String token, Dialog loading)
    {
        RequestQueue queue = Volley.newRequestQueue(frag.getActivity());
        String url = "https://kspclient.geasoft.net/vrsta_api.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.cancel();
                        if(response.contains("OK"))
                        {
                            Log.i("Vrsta VM", "Dodato");
                            frag.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            frag.osveziPodatke(token);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.cancel();
                        Toast.makeText(frag.getActivity(), frag.getActivity().getString(R.string.greska) + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tip", "0"); // brisanje, jer ranije verzije androida ne podrzavaju DELETE request
                params.put("id_sluzbe", Integer.toString(id_sluzbe));
                params.put("naziv", naziv);
                params.put("token", token);
                params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                return params;
            }
        };
        queue.add(postRequest);
    }

    public static void Izbrisi(int id, PregledSluzbeVrsteFragment frag, String token, Dialog loading)
    {
        RequestQueue queue = Volley.newRequestQueue(frag.getActivity());
        String url = "https://kspclient.geasoft.net/vrsta_api.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.cancel();
                        if(response.contains("OK"))
                        {
                            Log.i("Vrsta VM", "Izbrisano");
                            frag.osveziPodatke(token);
                        }
                        else
                        {
                            Toast.makeText(frag.getActivity(), response, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.cancel();
                        Toast.makeText(frag.getActivity(), frag.getActivity().getString(R.string.greska) + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tip", "1"); // brisanje, jer ranije verzije androida ne podrzavaju DELETE request
                params.put("id", Integer.toString(id));
                params.put("token", token);
                params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                return params;
            }
        };
        queue.add(postRequest);
    }*/
}




package net.geasoft.ksp.ksp;


import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ProblemModel {

    public int id;
    public int id_vrste;
    public int id_korisnika;
    public String opis;
    public String slika;
    public String opstina;
    public String latitude;
    public String longitude;


    public static void Dodaj(final ProblemModel pr, final Context kontekst, final String token, final String uid) {
        RequestQueue queue = Volley.newRequestQueue(kontekst);
        String url = "https://kspclient.geasoft.net/problem_api.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(kontekst, "Uspešno dodat problem! Odgovor servera: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(kontekst, "Greška u ProblemModel.Dodaj() metodu", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_korisnika", Integer.toString(pr.id_korisnika));
                params.put("id_vrste", Integer.toString(pr.id_korisnika));
                params.put("opis", pr.opis);
                params.put("slika", pr.slika);
                params.put("opstina", pr.opstina);
                params.put("latitude", pr.latitude);
                params.put("longitude", pr.longitude);
                params.put("token", token);
                params.put("uid", uid);
                return params;
            }
        };
        queue.add(postRequest);
    }
}

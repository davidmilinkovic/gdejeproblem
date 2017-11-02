package net.geasoft.ksp.ksp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ProblemMetode{


    public static void Dodaj(final ProblemModel pr, final Context kontekst) {
        RequestQueue queue = Volley.newRequestQueue(kontekst);
        String url = "https://ksp.geasoft.net/problem_api.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(kontekst, "Hehe uspelo, odgovor je: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(kontekst, "Hehe nije bas uspelo", Toast.LENGTH_LONG).show();
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
                return params;
            }
        };
        queue.add(postRequest);
    }
}
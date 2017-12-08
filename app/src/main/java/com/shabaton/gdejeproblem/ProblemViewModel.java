package com.shabaton.gdejeproblem;


import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProblemViewModel extends ViewModel {

    /**
     * An array of sample (dummy) items.
     */
    public MutableLiveData<List<ProblemViewModel.Problem>> problemi = null;
    public MutableLiveData<Boolean> prazna = new MutableLiveData<Boolean>();

    public Pair<MutableLiveData, MutableLiveData> dajProbleme(String token) {
        if(problemi == null) {
            prazna.postValue(false);
            problemi = new MutableLiveData<>();
            ucitajProbleme(token);
        }
        return new Pair<MutableLiveData, MutableLiveData>(problemi, prazna);
    }

    public void ucitajProbleme(String token) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Problem> lista = new ArrayList<>();
                    URL uu = new URL("https://www.kspclient.geasoft.net/problem_api.php?token="+token+"&email=" + FirebaseAuth.getInstance().getCurrentUser().getEmail()+"&uid="+FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HttpURLConnection connection = (HttpURLConnection) uu.openConnection();
                    connection.setRequestMethod("GET");
                    InputStream rd = connection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(rd);
                    String content = "";
                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        content += current;
                    }
                    Log.i("EHEHEHHEHEHEHEHEH", content);
                    JSONObject glavni = new JSONObject(content);
                    JSONArray niz = glavni.getJSONArray("redovi");
                    for (int i = 0; i < niz.length(); i++) {
                        JSONObject obj = niz.getJSONObject(i);
                        ProblemViewModel.Problem p = new ProblemViewModel.Problem();
                        p.id = obj.getInt("id");
                        int id_vrste = obj.getInt("id_vrste");
                        for (Vrsta v : StaticDataProvider.vrste) {
                            if (v.id == id_vrste) {
                                p.vrsta = v;
                                break;
                            }
                        }
                        p.opis = obj.getString("opis");
                        p.slika = obj.getString("slika");
                        p.adresa = obj.getString("adresa");
                        p.latitude = obj.getString("latitude");
                        p.longitude = obj.getString("longitude");
                        lista.add(p);
                    }
                    if(niz.length() == 0) prazna.postValue(true);
                    problemi.postValue(lista);

                } catch (Exception e) {
                    prazna.postValue(true);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static class Problem {

        public int id;
        public Vrsta vrsta;
        public String opis;
        public String slika;
        public String mesto;
        public String adresa;
        public String latitude;
        public String longitude;
        public List<StatusViewModel.Status> statusi;


        public static void Dodaj(final Problem pr, final Activity kontekst, final String token) {
            RequestQueue queue = Volley.newRequestQueue(kontekst);
            String url = "https://kspclient.geasoft.net/problem_api.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(pr.slika != "") {
                                Intent intent = new Intent(kontekst, UploadSlikeService.class);
                                intent.putExtra("putanja", pr.slika);
                                intent.putExtra("id_problema", response);
                                kontekst.startService(intent);
                            }
                            Intent povratak = new Intent();
                            povratak.putExtra("poruka", "Problem uspesno dodat!");
                            kontekst.setResult(RESULT_OK, povratak);
                            kontekst.finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(kontekst, "Greška u ProblemModel.Dodaj() metodu: " + error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    params.put("id_vrste", Integer.toString(pr.vrsta.id));
                    params.put("opis", pr.opis);
                    params.put("mesto", pr.mesto);
                    params.put("adresa", pr.adresa);
                    params.put("latitude", pr.latitude);
                    params.put("longitude", pr.longitude);
                    params.put("token", token);
                    params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }
}

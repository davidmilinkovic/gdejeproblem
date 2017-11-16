package net.geasoft.ksp.ksp;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

public class ProblemViewModel extends ViewModel {

    /**
     * An array of sample (dummy) items.
     */
    private MutableLiveData<List<ProblemViewModel.Problem>> problemi;

    public LiveData<List<ProblemViewModel.Problem>> dajProbleme(final String uid) {
        if (problemi == null) {
            problemi = new MutableLiveData<List<ProblemViewModel.Problem>>();
            ucitajProbleme(uid);
        }
        return problemi;
    }

    private void ucitajProbleme(final String uid) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Problem> lista = new ArrayList<>();
                    URL uu = new URL("https://www.kspclient.geasoft.net/problem_api.php?korisnik=" + uid);
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
                    JSONObject glavni = new JSONObject(content);
                    JSONArray niz = glavni.getJSONArray("redovi");
                    for (int i = 0; i < niz.length(); i++) {
                        JSONObject obj = niz.getJSONObject(i);
                        ProblemViewModel.Problem p = new ProblemViewModel.Problem();
                        p.id = obj.getInt("id");
                        int id_vrste = obj.getInt("id_vrste");
                        for (VrstaViewModel.Vrsta v : StaticDataProvider.vrste) {
                            if (v.id == id_vrste) {
                                p.vrsta = v;
                                break;
                            }
                        }
                        p.id_korisnika = obj.getString("id_korisnika");
                        p.opis = obj.getString("opis");
                        p.slika = obj.getString("slika");
                        p.opstina = obj.getString("opstina");
                        p.latitude = obj.getString("latitude");
                        p.longitude = obj.getString("longitude");
                        lista.add(p);
                    }

                    problemi.postValue(lista);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static class Problem {

        public int id;
        public VrstaViewModel.Vrsta vrsta;
        public String id_korisnika;
        public String opis;
        public String slika;
        public String opstina;
        public String latitude;
        public String longitude;


        public static void Dodaj(final Problem pr, final Context kontekst, final String token, final String uid) {
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
                    params.put("id_korisnika", pr.id_korisnika);
                    params.put("id_vrste", Integer.toString(pr.vrsta.id));
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
}

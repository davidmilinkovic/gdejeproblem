package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Alati {

    public static String Lat(String cir)
    {
        String lat = cir;

        String[] latinica = { "A", "B", "V", "G", "D", "Đ", "E", "Dž", "Ž", "Z", "I", "K", "Lj", "L", "M", "Nj", "J", "N", "O", "P", "R", "S", "T", "Ć", "U", "F", "H", "C", "Č", "Š", "a", "b", "v", "g", "d", "đ", "e", "dž", "ž", "z", "i", "k", "lj", "l", "m", "nj", "j", "n", "o", "p", "r", "s", "t", "ć", "u", "f", "h", "c", "č", "š" };
        String[] cirilica = { "А", "Б", "В", "Г", "Д", "Ђ", "Е", "Џ", "Ж", "З", "И", "К", "Љ", "Л", "М", "Њ", "Ј", "Н", "О", "П", "Р", "С", "Т", "Ћ", "У", "Ф", "Х", "Ц", "Ч", "Ш", "а", "б", "в", "г", "д", "ђ", "е", "џ", "ж", "з", "и", "к", "љ", "л", "м", "њ", "ј", "н", "о", "п", "р", "с", "т", "ћ", "у", "ф", "х", "ц", "ч", "ш" };

        for (int i = 0; i < 60; i++) lat = lat.replace(cirilica[i], latinica[i]);

        return lat;

    }

    public static String citajPref(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void promeniPref(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static void Sabskrajb(int id_sluzbe, Activity kontekst)
    {
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        mUser.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            RequestQueue queue = Volley.newRequestQueue(kontekst);
                            String url = "https://kspclient.geasoft.net/sluzba_api.php";
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if (response.contains("OK"))
                                            {
                                                Toast.makeText(kontekst, "Uspešno ste se prijavili za službu. Na email ćemo Vam poslati obaveštenje o prijemu kada Vas administrator potvrdi.", Toast.LENGTH_LONG).show();
                                                kontekst.finish();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(kontekst, error.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    params.put("tip", "prijava");
                                    params.put("id", Integer.toString(id_sluzbe));
                                    params.put("token", idToken);
                                    params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    return params;
                                }
                            };
                            queue.add(postRequest);
                        } else {
                            Toast.makeText(kontekst, R.string.greska_token, Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}

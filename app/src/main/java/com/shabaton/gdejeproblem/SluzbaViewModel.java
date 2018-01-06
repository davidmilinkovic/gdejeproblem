package com.shabaton.gdejeproblem;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


// pribavlja sluzbe i vrste za prijavljenog korisnika
public class SluzbaViewModel extends ViewModel {

    public MutableLiveData<List<Sluzba>> sluzbe = null;
    public MutableLiveData<List<Vrsta>> vrste = null;
    public MutableLiveData<List<Sluzba>> sveSluzbe = null;

    public MutableLiveData<List<Sluzba>> dajSluzbe(String token) {
        if(sluzbe == null) {
            sluzbe = new MutableLiveData<>();
            vrste = new MutableLiveData<>();
            ucitajSluzbe(token);
        }
        return sluzbe;
    }

    public MutableLiveData<List<Sluzba>> dajSveSluzbe(String token) {
        if(sveSluzbe == null) {
            sveSluzbe = new MutableLiveData<>();
            ucitajSveSluzbe(token);
        }
        return sveSluzbe;
    }

    public void ucitajSluzbe(String token) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Sluzba> lstSluzbe = new ArrayList<>();
                    final List<Vrsta> lstVrste = new ArrayList<>();

                    URL uu = new URL("https://www.kspclient.geasoft.net/sluzba_api.php?token="+token+"&email=" + FirebaseAuth.getInstance().getCurrentUser().getEmail()+"&uid="+FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    Log.i("Sluzbe JSON: ", content);

                    JSONObject glavni = new JSONObject(content);

                    JSONArray nizSluzbe = glavni.getJSONArray("sluzbe");
                    for (int i = 0; i < nizSluzbe.length(); i++) {
                        JSONObject sluzb = nizSluzbe.getJSONObject(i);
                        Sluzba s = new Sluzba(sluzb.getInt("id"), sluzb.getString("naziv"), sluzb.getString("ikonica"), sluzb.getString("datum"));
                        s.tip = sluzb.getInt("tip");
                        lstSluzbe.add(s);
                    }

                    JSONArray nizVrste = glavni.getJSONArray("vrste");
                    for (int i = 0; i < nizVrste.length(); i++) {
                        JSONObject vrsta = nizVrste.getJSONObject(i);
                        int idS = vrsta.getInt("id_sluzbe");
                        Sluzba ss = null;

                        for(Sluzba s : lstSluzbe)
                            if(s.id == idS) {
                                ss = s;
                                break;
                            }

                        Vrsta v = new Vrsta(vrsta.getInt("id"), vrsta.getString("naziv"), ss);
                        lstVrste.add(v);
                    }
                    StaticDataProvider.sluzbe = lstSluzbe;
                    StaticDataProvider.vrste = lstVrste;

                    sluzbe.postValue(lstSluzbe);
                    vrste.postValue(lstVrste);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();


    }

    public void ucitajSveSluzbe(String token) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Sluzba> lstSluzbe = new ArrayList<>();
                    URL uu = new URL("https://www.kspclient.geasoft.net/sluzba_api.php?tip=sve&token="+token+"&email=" + FirebaseAuth.getInstance().getCurrentUser().getEmail()+"&uid="+FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    Log.i("Sluzbe JSON: ", content);

                    JSONObject glavni = new JSONObject(content);

                    JSONArray nizSluzbe = glavni.getJSONArray("sluzbe");
                    for (int i = 0; i < nizSluzbe.length(); i++) {
                        JSONObject sluzb = nizSluzbe.getJSONObject(i);
                        Sluzba s = new Sluzba(sluzb.getInt("id"), sluzb.getString("naziv"), sluzb.getString("ikonica"), sluzb.getString("datum"));
                        s.tip = sluzb.getInt("tip");
                        lstSluzbe.add(s);
                    }
                    sveSluzbe.postValue(lstSluzbe);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();


    }
}
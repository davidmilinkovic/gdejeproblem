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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ObavestenjeViewModel extends ViewModel {

    public MutableLiveData<List<Obavestenje>> obavestenja = null;


    public MutableLiveData<List<Obavestenje>> dajObavestenja(String token, String mesto, List<Sluzba> sluzbe) {
        if (obavestenja == null) {
            obavestenja = new MutableLiveData<>();
            ucitajObavestenja(token, mesto, sluzbe);
        }
        return obavestenja;
    }


    public void ucitajObavestenja(String token, String mesto, List<Sluzba> sluzbe) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Obavestenje> lst = new ArrayList<>();

                    URL uu = new URL("https://www.portal.gdejeproblem.geasoft.net/obavestenja_api.php?token=" + token + "&email=" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "&uid=" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "&mesto=" + mesto);;
                    URI uri = new URI(uu.getProtocol(), uu.getUserInfo(), uu.getHost(), uu.getPort(), uu.getPath(), uu.getQuery(), uu.getRef());
                    uu = uri.toURL();
                    HttpURLConnection connection = (HttpURLConnection) uu.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "text/html; charset=utf-8");
                    InputStream rd = connection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(rd);
                    String content = "";
                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        content += current;
                    }
                    Log.i("Obavestenja JSON: ", content);

                    JSONArray glavni = new JSONArray(content);

                    for (int i = 0; i < glavni.length(); i++) {
                        JSONObject ob = glavni.getJSONObject(i);
                        Sluzba s = null;
                        int id_sluzbe = ob.getInt("id_sluzbe");
                        for(Sluzba ss : sluzbe)
                            if(ss.id == id_sluzbe) {
                                s = ss;
                                break;
                            }
                        Obavestenje o = new Obavestenje(ob.getInt("id"), ob.getString("naslov"), ob.getString("tekst"), s, ob.isNull("kraj"));
                        lst.add(o);
                    }
                    obavestenja.postValue(lst);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();


    }
}
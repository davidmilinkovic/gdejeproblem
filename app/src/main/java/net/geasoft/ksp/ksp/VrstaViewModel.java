package net.geasoft.ksp.ksp;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.geasoft.ksp.ksp.SluzbaListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class VrstaViewModel extends ViewModel {

    /**
     * An array of sample (dummy) items.
     */
    private MutableLiveData<List<Vrsta>> vrste;

    public LiveData<List<Vrsta>> dajVrstea(final int id) {
        if (vrste == null) {
            vrste = new MutableLiveData<List<Vrsta>>();
            ucitajVrste(id);
        }
        return vrste;
    }

    private void ucitajVrste(final int id)  {/*
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Vrsta> lista = new ArrayList<>();
                    java.net.URL uu;
                    uu =new URL("https://www.kspclient.geasoft.net/vrsta_api.php?id_sluzbe="+Integer.toString(id));
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
                    for(int i = 0; i < niz.length();i++)
                    {
                        JSONObject obj = niz.getJSONObject(i);
                        Vrsta s = new Vrsta(obj.getInt("id"), obj.getString("naziv"));
                        lista.add(s);
                    }
                    vrste.postValue(lista);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();*/
    }
    public static class Vrsta {
        public final int id;
        public final String naziv;
        public final SluzbaViewModel.Sluzba sluzba;

        public Vrsta(int id, String naziv, SluzbaViewModel.Sluzba s) {
            this.id = id;
            this.naziv = naziv;
            this.sluzba = s;
        }

        @Override
        public String toString() {
            return naziv;
        }
    }
}



package com.shabaton.gdejeproblem;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 17.11.2017.
 */

public class StatusViewModel extends ViewModel {

    /**
     * An array of sample (dummy) items.
     */
    private MutableLiveData<List<Status>> statusi;

    public LiveData<List<Status>> dajStatuse(boolean ucitaj) {
        if(statusi == null || ucitaj) {
            statusi = new MutableLiveData<List<Status>>();
            ucitajStatuse();
        }
        return statusi;
    }

    private void ucitajStatuse() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<Status> lista = new ArrayList<>();
                    URL uu = new URL("https://www.kspclient.geasoft.net/funkcije.php");
                    HttpURLConnection connection = (HttpURLConnection) uu.openConnection();

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    List<AbstractMap.SimpleEntry> params = new ArrayList<AbstractMap.SimpleEntry>();
                    params.add(new AbstractMap.SimpleEntry("funkcija", "dajStatuseJSON"));
                    params.add(new AbstractMap.SimpleEntry("email", FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(params));
                    writer.flush();
                    writer.close();
                    os.close();

                    connection.connect();

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
                        Status s = new Status();
                        s.id = obj.getInt("id");
                        s.id_problema = obj.getInt("id_problema");
                        s.naziv = obj.getString("naziv");
                        s.datum = obj.getString("datum");
                        s.boja = obj.getString("boja");

                        lista.add(s);
                    }

                    statusi.postValue(lista);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private String getQuery(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode((String) pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static class Status {

        public int id;
        public String naziv;
        public String datum;
        public int id_problema;
        public String boja;

    }

}

package com.shabaton.gdejeproblem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StaticDataProvider {

    public static boolean ucitano = false;

    public static List<Sluzba> sluzbe = new ArrayList<Sluzba>();
    public static List<Vrsta> vrste = new ArrayList<Vrsta>();
    public static List<Status> statusi = new ArrayList<Status>();

    public static  Thread thread;

    public static void init()
    {
        thread = new Thread() {
            public void run() {
                try {
                    java.net.URL uu;
                    uu =new URL("https://www.kspclient.geasoft.net/definicija.php");
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

                    JSONArray jsonStatusi = glavni.getJSONArray("statusi");

                    for(int i = 0; i < jsonStatusi.length();i++)
                    {
                        JSONObject obj = jsonStatusi.getJSONObject(i);
                        Status s = new Status(obj.getInt("id"), obj.getString("naziv"), obj.getString("boja"));
                        statusi.add(s);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ucitano = true;
            }
        };
    }

    public static Sluzba sluzba(int id)
    {
        for(Sluzba s : sluzbe)
            if(s.id == id)
                return s;
        return null;
    }

    public static Vrsta vrsta(int id)
    {
        for(Vrsta v : vrste)
            if(v.id == id)
                return v;
        return null;
    }

    public static Status status(int id)
    {
        for(Status s : statusi)
            if(s.id == id)
                return s;
        return null;
    }



}

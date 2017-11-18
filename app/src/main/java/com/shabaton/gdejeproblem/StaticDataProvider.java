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

    public static List<Sluzba> sluzbe = new ArrayList<Sluzba>();
    public static List<Vrsta> vrste = new ArrayList<Vrsta>();

    public static  Thread thread;

    public static void init()
    {
        thread = new Thread() {
            public void run() {
                try {
                    java.net.URL uu;
                    uu =new URL("https://www.kspclient.geasoft.net/sluzba_api.php");
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
                        Sluzba s = new Sluzba(obj.getInt("id"), obj.getString("naziv"), obj.getString("ikonica"));
                        sluzbe.add(s);
                    }

                    uu =new URL("https://www.kspclient.geasoft.net/vrsta_api.php");
                    connection = (HttpURLConnection) uu.openConnection();
                    connection.setRequestMethod("GET");
                    rd = connection.getInputStream();
                    isw = new InputStreamReader(rd);
                    content = "";
                    data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        content += current;
                    }
                    glavni = new JSONObject(content);
                    niz = glavni.getJSONArray("redovi");
                    for(int i = 0; i < niz.length();i++)
                    {
                        JSONObject obj = niz.getJSONObject(i);

                        int idSluzbe = obj.getInt("id_sluzbe");


                        for(Sluzba s : sluzbe)
                        {
                            if(s.id == idSluzbe) {
                                Vrsta v = new Vrsta(obj.getInt("id"), obj.getString("naziv"), s);
                                vrste.add(v);
                                break;
                            }
                        }

                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}

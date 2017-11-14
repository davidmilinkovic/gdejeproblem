package net.geasoft.ksp.ksp;

import android.arch.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by David on 14.11.2017.
 */

public class StaticDataProvider {

    public static List<SluzbaViewModel.Sluzba> sluzbe = new ArrayList<SluzbaViewModel.Sluzba>();
    public static List<VrstaViewModel.Vrsta> vrste = new ArrayList<VrstaViewModel.Vrsta>();
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
                        SluzbaViewModel.Sluzba s = new SluzbaViewModel.Sluzba(obj.getInt("id"), obj.getString("naziv"), obj.getString("ikonica"));
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
                        VrstaViewModel.Vrsta s = new VrstaViewModel.Vrsta(obj.getInt("id"), obj.getString("naziv"), obj.getInt("id_sluzbe"));
                        vrste.add(s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}

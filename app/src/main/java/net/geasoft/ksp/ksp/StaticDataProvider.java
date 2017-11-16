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
    public static List<ProblemViewModel.Problem> problemi = new ArrayList<ProblemViewModel.Problem>();


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

                        int idSluzbe = obj.getInt("id_sluzbe");


                        for(SluzbaViewModel.Sluzba s : sluzbe)
                        {
                            if(s.id == idSluzbe) {
                                VrstaViewModel.Vrsta v = new VrstaViewModel.Vrsta(obj.getInt("id"), obj.getString("naziv"), s);
                                vrste.add(v);
                                break;
                            }
                        }



                    }

                    uu =new URL("https://www.kspclient.geasoft.net/problem_api.php");
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
                        ProblemViewModel.Problem p = new ProblemViewModel.Problem();
                        p.id = obj.getInt("id");
                        int id_vrste = obj.getInt("id_vrste");
                        for(VrstaViewModel.Vrsta v : vrste)
                        {
                            if(v.id == id_vrste) {
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
                        problemi.add(p);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}

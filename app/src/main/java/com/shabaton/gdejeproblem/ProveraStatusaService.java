package com.shabaton.gdejeproblem;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

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

public class ProveraStatusaService extends Service {
    private String token = "";


    public ProveraStatusaService() {
    }

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private List<ProblemViewModel.Problem> problemi;
    private Map<Integer, Integer> mapa = new HashMap(); // id problema, id statusa

    public List<Sluzba> sluzbe = new ArrayList<Sluzba>();
    public List<Vrsta> vrste = new ArrayList<Vrsta>();
    public List<Status> statusi = new ArrayList<Status>();

    public  Thread thread;

    public void inicijalizuj(String token)
    {
        thread = new Thread() {
            public void run() {
                try {
                    java.net.URL uu;
                    uu = new URL("https://www.kspclient.geasoft.net/definicija.php");
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

                    uu = new URL("https://www.kspclient.geasoft.net/sluzba_api.php?token="+token+"&email=" + FirebaseAuth.getInstance().getCurrentUser().getEmail()+"&uid="+FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    Log.i("Sluzbe JSON: ", content);

                    glavni = new JSONObject(content);

                    JSONArray nizSluzbe = glavni.getJSONArray("sluzbe");
                    for (int i = 0; i < nizSluzbe.length(); i++) {
                        JSONObject sluzb = nizSluzbe.getJSONObject(i);
                        Sluzba s = new Sluzba(sluzb.getInt("id"), sluzb.getString("naziv"), sluzb.getString("ikonica"), sluzb.getString("datum"));
                        s.tip = sluzb.getInt("tip");
                        sluzbe.add(s);
                    }

                    JSONArray nizVrste = glavni.getJSONArray("vrste");
                    for (int i = 0; i < nizVrste.length(); i++) {
                        JSONObject vrsta = nizVrste.getJSONObject(i);
                        int idS = vrsta.getInt("id_sluzbe");
                        Sluzba ss = sluzba(idS);
                        Vrsta v = new Vrsta(vrsta.getInt("id"), vrsta.getString("naziv"), ss);
                        vrste.add(v);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private Sluzba sluzba(int id)
    {
        for(Sluzba s : sluzbe)
            if(s.id == id)
                return s;
        return null;
    }

    private Vrsta vrsta(int id)
    {
        for(Vrsta v : vrste)
            if(v.id == id)
                return v;
        return null;
    }

    private Status status(int id)
    {
        for(Status s : statusi)
            if(s.id == id)
                return s;
        return null;
    }

    private int DILEJ = 300000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            stopSelf();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int obavestenja = Integer.parseInt(sharedPref.getString("notif_freq", "300000"));
        Log.i("Heh", Integer.toString(obavestenja));
        if(obavestenja < 5000) obavestenja = 5000;
        DILEJ = obavestenja;

        mNotifyManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);


        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            try {
                mUser.getToken(false)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String idToken = task.getResult().getToken();
                                    inicijalizuj(idToken);
                                } else {
                                    Toast.makeText(ProveraStatusaService.this, R.string.greska_token, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(DILEJ);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (mUser == null || !sharedPref.getBoolean("notif_status", false))
                        break;
                    try {
                        mUser.getToken(false)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                                        if (task.isSuccessful()) {
                                            String idToken = task.getResult().getToken();
                                            ucitajIProveri(idToken, mUser.getEmail(), mUser.getUid());
                                        } else {
                                            Toast.makeText(ProveraStatusaService.this, R.string.greska_token, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }



                }

            }
        }).start();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void ucitajIProveri(String token, String email, String uid) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    final List<ProblemViewModel.Problem> lista = new ArrayList<>();
                    URL uu = new URL("https://www.kspclient.geasoft.net/problem_api.php?tip=svi&token="+token+"&email=" + email+"&uid="+uid);
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
                    Log.i("Pribavljeni problemi: ", content);
                    JSONObject glavni = new JSONObject(content);
                    JSONArray niz = glavni.getJSONArray("redovi");
                    for (int i = 0; i < niz.length(); i++) {
                        JSONObject prob = niz.getJSONObject(i);
                        ProblemViewModel.Problem p = new ProblemViewModel.Problem();
                        p.id = prob.getInt("id");
                        int id_vrste = prob.getInt("id_vrste");
                        p.vrsta = vrsta(id_vrste);
                        p.opis = prob.getString("opis");
                        p.slika = prob.getString("slika");
                        p.adresa = prob.getString("adresa");
                        p.latitude = prob.getString("latitude");
                        p.longitude = prob.getString("longitude");
                        p.statusi = new ArrayList<StatusEntry>();
                        JSONArray statusi = prob.getJSONArray("statusi");
                        for (int j = 0; j < statusi.length(); j++) {
                            JSONObject sta = statusi.getJSONObject(j);
                            Status s = status(sta.getInt("id"));
                            String datum = sta.getString("datum");
                            String komentar = sta.getString("komentar");
                            if(komentar.isEmpty()) komentar = "Nema komentara";
                            p.statusi.add(new StatusEntry(s, datum, komentar));
                        }
                        lista.add(p);
                    }
                    problemi = lista;
                    connection.disconnect();

                    for(ProblemViewModel.Problem p : lista)
                    {
                        Integer statusUMapi = mapa.get(p.id);
                        Integer trenutniStatus = p.statusi.get(0).status.id;
                        if(statusUMapi != null)
                        {
                            if(statusUMapi != trenutniStatus) {
                                mapa.put(p.id, trenutniStatus);
                                Log.i("StatusService", "Promena ID-a statusa, sada je " + Integer.toString(trenutniStatus));

                                mBuilder.setContentTitle(getString(R.string.app_name))
                                        .setContentText(getString(R.string.service_promenjen_status))
                                        .setSmallIcon(R.mipmap.ic_launcher);

                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                mBuilder.setSound(alarmSound);
                                mBuilder.setAutoCancel(true);
                                mBuilder.setVibrate(new long[] { 300, 300, 300, 300 });
                                mBuilder.setLights(Color.BLUE, 1000, 1000);

                                Intent resultIntent = new Intent(ProveraStatusaService.this, PregledProblemaActivity.class);
                                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                resultIntent.putExtra("problem", p);

                                PendingIntent resultPendingIntent =
                                        PendingIntent.getActivity(
                                                ProveraStatusaService.this,
                                                0,
                                                resultIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        );
                                mBuilder.setContentIntent(resultPendingIntent);
                                mNotifyManager.notify(p.id, mBuilder.build());
                            }
                        }
                        else
                            mapa.put(p.id, p.statusi.get(0).status.id);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

}

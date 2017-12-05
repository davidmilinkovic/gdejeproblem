package com.shabaton.gdejeproblem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Pair;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadSlikeService extends Service {
    public UploadSlikeService() {
    }

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mNotifyManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Gde je problem?")
                .setContentText("Postavljanje fotografije na server je u toku...")
                .setSmallIcon(R.mipmap.ic_launcher);
        mNotifyManager.notify(1, mBuilder.build());

        String putanja = intent.getStringExtra("putanja");
        String id_problema = intent.getStringExtra("id_problema");
        String naziv = putanja.substring(putanja.lastIndexOf('/')+1);
        Pair<String, String> kod = Enkodiraj(putanja);
        makeRequest(naziv, kod.first, kod.second, id_problema);
        return super.onStartCommand(intent, flags, startId);
    }

    protected Pair<String, String> Enkodiraj(String putanja) {

        Bitmap bitmap = BitmapFactory.decodeFile(putanja);

        int sirina = bitmap.getWidth();
        int visina = bitmap.getHeight();

        int novaSirina = 69;
        int novaVisina = 69;

        int maxDimenzija = 1200;

        if(sirina >= visina && sirina > maxDimenzija)
        {
            novaSirina = maxDimenzija;
            novaVisina = (int)(((float)visina / sirina) * maxDimenzija);
        }
        else if(visina > sirina && visina > maxDimenzija)
        {
            novaVisina = maxDimenzija;
            novaSirina = (int)(((float)sirina / visina) * maxDimenzija);
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, novaSirina, novaVisina, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] array = stream.toByteArray();
        String encoded_string = Base64.encodeToString(array, 0);

        Bitmap mali = Bitmap.createScaledBitmap(bitmap, 300, (int)(((float)visina / sirina) * 300), false);
        stream = new ByteArrayOutputStream();
        mali.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        bitmap.recycle();
        mali.recycle();
        array = stream.toByteArray();
        String encoded_string_t = Base64.encodeToString(array, 0);

        return new Pair<>(encoded_string, encoded_string_t);
    }



    private void makeRequest(String naziv, String data, String dataT, String id_problema)  {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://kspclient.geasoft.net/upload_slike.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mBuilder.setContentText("Postavljanje fotografije završeno");
                        mNotifyManager.notify(1, mBuilder.build());
                        UploadSlikeService.this.stopSelf();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("kodirana_slika", data);
                map.put("kodirana_slika_t", dataT);
                map.put("naziv", naziv);
                map.put("id_problema", id_problema);
                return map;
            }
        };
        requestQueue.add(request);
    }
}

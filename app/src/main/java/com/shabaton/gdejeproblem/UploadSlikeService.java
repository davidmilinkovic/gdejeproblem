package com.shabaton.gdejeproblem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.media.ExifInterface;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
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
        mBuilder.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.upload_slike_u_toku))
                .setSmallIcon(R.mipmap.ic_launcher);
        mNotifyManager.notify(1, mBuilder.build());

        String putanja = intent.getStringExtra("putanja");
        String id_problema = intent.getStringExtra("id_problema");
        Pair<String, String> kod = null;
        try {
            kod = Enkodiraj(putanja);
        } catch (IOException e) {
            e.printStackTrace();
        }
        makeRequest(kod.first, kod.second, id_problema);

        return START_STICKY;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public static Bitmap RotirajBitmap(Bitmap bmp, float ugao)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(ugao);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    protected Pair<String, String> Enkodiraj(String putanja) throws IOException {

        ExifInterface exif = new ExifInterface(putanja);
        int rotacija = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Bitmap bitmap = BitmapFactory.decodeFile(putanja);
        bitmap = RotirajBitmap(bitmap, exifToDegrees(rotacija));

        int sirina = bitmap.getWidth();
        int visina = bitmap.getHeight();

        int novaSirina = sirina;
        int novaVisina = visina;

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


        int thumbWidth = sirina;
        if(sirina > 300) thumbWidth = 300;

        Bitmap mali = Bitmap.createScaledBitmap(bitmap, thumbWidth, (int)(((float)visina / sirina) * thumbWidth), false);
        stream = new ByteArrayOutputStream();
        mali.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        mali = RotirajBitmap(bitmap, exifToDegrees(rotacija));

        bitmap.recycle();
        mali.recycle();

        array = stream.toByteArray();
        String encoded_string_t = Base64.encodeToString(array, 0);

        return new Pair<>(encoded_string, encoded_string_t);
    }



    private void makeRequest(String data, String dataT, String id_problema) {


        Thread thread = new Thread() {
            public void run() {
                try {
                    URL uu = new URL("https://www.kspclient.geasoft.net/upload_slike.php");
                    HttpURLConnection connection = (HttpURLConnection) uu.openConnection();

                    connection.setReadTimeout(100000);
                    connection.setConnectTimeout(150000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    List<AbstractMap.SimpleEntry> params = new ArrayList<AbstractMap.SimpleEntry>();
                    params.add(new AbstractMap.SimpleEntry("kodirana_slika", data));
                    params.add(new AbstractMap.SimpleEntry("kodirana_slika_t", dataT));
                    params.add(new AbstractMap.SimpleEntry("naziv", id_problema+".jpg"));
                    params.add(new AbstractMap.SimpleEntry("id_problema", id_problema));
                    Log.i("AUH", id_problema);

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
                    if(content.contains("OK"))
                    {
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        mBuilder.setContentText(getString(R.string.postavljanje_fotografije_zavrseno));
                        mNotifyManager.notify(1, mBuilder.build());
                        UploadSlikeService.this.stopSelf();
                    }
                    Log.i("Upload slike", content);

                } catch (Exception e) {
                    Log.e("Upload slike", e.getMessage());
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
}

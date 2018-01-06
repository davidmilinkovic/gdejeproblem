package com.shabaton.gdejeproblem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import java.util.List;

public class NotifService extends Service {
    public NotifService() {
    }


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            stopSelf();

        int DILEJ = 10000;

        mNotifyManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);

        new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(DILEJ);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }




                }

            }
        }).start();

        return START_STICKY;
    }
}

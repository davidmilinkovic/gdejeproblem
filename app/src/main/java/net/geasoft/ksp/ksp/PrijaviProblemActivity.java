package net.geasoft.ksp.ksp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import  android.Manifest;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.support.v4.app.NotificationCompat.Builder;

public class PrijaviProblemActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager lm;
    private Location curLocation;
    private boolean imaSlike = false;
    private File slikaFajl;
    private ImageView img;
    private String izabranId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijavi_problem);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_prijava);
        setSupportActionBar(tulbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        img = (ImageView)findViewById(R.id.imageView);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);



        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.checkBox:
                CheckBox boks = (CheckBox) findViewById(R.id.checkBox);
                FrameLayout frejm = (FrameLayout) findViewById(R.id.lokacija_frame);
                if (boks.isChecked()) {
                    for (int i = 0; i < frejm.getChildCount(); i++) {
                        View vv = frejm.getChildAt(i);
                        vv.setVisibility(View.GONE);
                        vv.postInvalidate();
                    }
                    frejm.setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.txtViewLok)).setText("Izaberite lokaciju problema");
                    pribaviLokaciju();
                }
                else {
                    for (int i = 0; i < frejm.getChildCount(); i++) {
                        View vv = frejm.getChildAt(i);
                        vv.setVisibility(View.VISIBLE);
                        vv.postInvalidate();
                    }
                    frejm.setVisibility(View.VISIBLE);
                    curLocation = null;
                }
                break;
            case R.id.button3:
                Intent intent = new Intent(this, MapsActivity.class);
                startActivityForResult(intent, 420);
                break;
            case R.id.fab:
                izaberiSliku();
                break;
            case R.id.button2:
                Intent inten = new Intent(this, SluzbaListActivity.class);
                startActivityForResult(inten, 666);
                break;
            case R.id.footer_potvrda:
                final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                mUser.getToken(false)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String idToken = task.getResult().getToken();
                                    dodaj(idToken, mUser.getUid());
                                } else {
                                    Toast.makeText(PrijaviProblemActivity.this, R.string.greska_token, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 10: // lokacija
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pribaviLokaciju();
                else {
                    CheckBox boks = (CheckBox) findViewById(R.id.checkBox);
                    boks.setChecked(false);
                    FrameLayout frejm = (FrameLayout) findViewById(R.id.lokacija_frame);
                    frejm.setVisibility(View.VISIBLE);
                }
                return;
            case 69: // kamera
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    kameraIntent();
                return;
            case 70: // WRITE_EXTERNAL_STORAGE za kameru
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    kameraIntent();
                return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    private  void pribaviLokaciju()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 10);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, 10);
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
            }, 10);
            return;
        }
        try {
            curLocation = lm.getLastKnownLocation("network");
        } catch (Exception ex){
            try {
                curLocation = lm.getLastKnownLocation("gps");
            } catch (Exception e) {
                dbg("Nemoguće pribaviti trenutnu lokaciju.");
            }
        }

    }


    private NotificationManager mNotifyManager;
    private Builder mBuilder;

    private void dodaj(String token, String uid)
    {
        ProblemViewModel.Problem problem = new ProblemViewModel.Problem();

        if(izabranId == null) {
            greska("Greška", "Niste izabrali vrstu problema.");
            return;
        }
        int id_vrste = Integer.parseInt(izabranId);
        for(Vrsta v : StaticDataProvider.vrste)
        {
            if(v.id == id_vrste) {
                problem.vrsta = v;
                break;
            }
        }


        if(curLocation == null) {
            greska("Greška", "Niste izabrali lokaciju problema.");
            return;
        }
        problem.latitude = Double.toString(curLocation.getLatitude());
        problem.longitude = Double.toString(curLocation.getLongitude());


        if(imaSlike) {
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Gde je problem?")
                    .setContentText("Postavljanje slike na server je u toku...")
                    .setSmallIcon(R.mipmap.ic_launcher);


            AsinhroniFTPUpload task = new AsinhroniFTPUpload(slikaFajl, this);
            task.execute();
            problem.slika = "https://www.geasoft.net/" + slikaFajl.getPath().substring(slikaFajl.getPath().lastIndexOf('/')+1);
            imaSlike = false;
            ((TextView)findViewById(R.id.textView3)).setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.ic_menu_camera).into(img);



        }
        else problem.slika = "";

        problem.id_korisnika = uid;

        problem.opis = ((EditText)findViewById(R.id.editText)).getText().toString();
        problem.opstina = "Bogac";


        ProblemViewModel.Problem.Dodaj(problem, this, token, uid);

        // reset svega
        TextView txt = (TextView) findViewById(R.id.textView2);
        txt.setText("Izaberite vrstu problema");
        izabranId = null;
        ((EditText) findViewById(R.id.editText)).setText("");
        ((TextView)findViewById(R.id.txtViewLok)).setText("Izaberite lokaciju problema");
    }

    private class AsinhroniFTPUpload extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog p;
        private Context ctx;
        private File fajl;

        public AsinhroniFTPUpload(File fajl, Context ctx)
        {
            this.fajl = fajl;
            this.ctx=ctx;
            this.p=new ProgressDialog(ctx);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mNotifyManager.notify(1, mBuilder.build());
        }


        protected Boolean doInBackground(Void... voids) {
            FTPClient con = null;
            try
            {
                con = new FTPClient();
                con.connect("195.252.110.140");

                if (con.login("geasoftn", "705903272ld"))
                {
                    con.enterLocalPassiveMode(); // important!
                    con.setFileType(FTP.BINARY_FILE_TYPE);
                    FileInputStream in = new FileInputStream(fajl);
                    boolean result = con.storeFile("/public_html/" + fajl.getPath().substring(fajl.getPath().lastIndexOf('/')+1), in);
                    in.close();
                    con.logout();
                    con.disconnect();
                }
            }
            catch (Exception e)
            {
                Log.v("FTP", "Greska: "+e.getMessage());
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mBuilder.setContentText("Prijavljivanje problema završeno");
            mNotifyManager.notify(1, mBuilder.build());
        }

        private int NOTIFICATION_ID = 1;
        private NotificationManager nm;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void createNotification(String contentTitle, String contentText, Context context) {

            Log.d("createNotification", "title is [" + contentTitle +"]");

            nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //Build the notification using Notification.Builder
            Notification.Builder builder = new Notification.Builder(ctx)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText);


            //Show the notification
            nm.notify(NOTIFICATION_ID, builder.build());
        }
    }



    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    private void izaberiSliku() {

        final CharSequence[] items = {"Kamera", "Izaberi iz galerije", "Ukloni fotografiju" };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Izaberi fotografiju");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                        kameraIntent();
                } else if (item == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,SELECT_FILE);
                }
                else if(item == 2) {
                    imaSlike = false;
                    ((TextView) findViewById(R.id.textView3)).setVisibility(View.VISIBLE);
                    img.setVisibility(View.GONE);
                    Glide.with(PrijaviProblemActivity.this).load(R.drawable.ic_menu_camera).into(img);
                }
            }
        });
        builder.show();
    }

    private void kameraIntent() // provera ovlascenja, a zatim lansiranje intenta za kameru
    {
        if (ContextCompat.checkSelfPermission(PrijaviProblemActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(PrijaviProblemActivity.this, new String[]{Manifest.permission.CAMERA}, 69);
            return;
        }
        if (ContextCompat.checkSelfPermission(PrijaviProblemActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(PrijaviProblemActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 70);
            return;
        }

        try {
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/slicice/";

            File newdir = new File(dir);
            newdir.mkdirs();

            String vreme = DateFormat.getDateTimeInstance().format(new Date());

            String file = dir+vreme+".jpg";
            File fajl = new File(file);
            try {
                fajl.createNewFile();
            }
            catch (IOException e)
            {
            }

            slikaFajl = fajl;
            Uri outputUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".net.geasoft.ksp.ksp.provider", fajl);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }
        catch (Exception e) {
                Toast.makeText(PrijaviProblemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void greska(String naslov, String tekst)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(naslov)
                .setMessage(tekst)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_24dp)
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    imaSlike = true;
                    ((TextView)findViewById(R.id.textView3)).setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                    Glide.with(this).load(slikaFajl).apply(RequestOptions.fitCenterTransform()).into(img);
                }
                break;
            case SELECT_FILE:
                if(resultCode == RESULT_OK){
                    imaSlike = true;
                    Uri selectedImage = data.getData();
                    dbg(selectedImage.getPath());
                    slikaFajl = new File(getRealPathFromURI(selectedImage));
                    ((TextView)findViewById(R.id.textView3)).setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                    Glide.with(this).load(selectedImage).apply(RequestOptions.fitCenterTransform()).into(img);
                }
                break;
            case 666:
                if(resultCode == RESULT_OK) {
                    int idVrste = Integer.parseInt(data.getStringExtra("id_vrste"));
                    TextView txt = (TextView) findViewById(R.id.textView2);
                    txt.setText("Izabrana vrsta: " + data.getStringExtra("naziv_vrste"));
                    txt.setVisibility(View.VISIBLE);
                    izabranId = data.getStringExtra("id_vrste");
                }
                break;
            case 420:
                if(resultCode == RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 44);
                    double lng = data.getDoubleExtra("lng", 44);
                    String adresa = data.getStringExtra("adresa");
                    curLocation = new Location("");
                    curLocation.setLatitude(lat);
                    curLocation.setLongitude(lng);
                    ((TextView) findViewById(R.id.txtViewLok)).setText(adresa);
                    String mesto = data.getStringExtra("mesto"); // koristiti kasnije
                }
                break;

        }
    }

    private void dbg(String s)
    {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }



    public String getRealPathFromURI(Uri contentUri) {

        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj,
                null,
                null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }
}


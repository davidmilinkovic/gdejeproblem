package net.geasoft.ksp.ksp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrijaviProblemActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager lm;
    private LocationListener ll;
    private Location curLocation;
    private boolean imaSlike = false;
    private String pathSlika;
    private Uri photoFile;
    private File slika;
    private Bitmap help1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijavi_problem);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_prijava);
        setSupportActionBar(tulbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.checkBox:
                CheckBox boks = (CheckBox) findViewById(R.id.checkBox);
                Button dugme = (Button) findViewById(R.id.button3);
                if (boks.isChecked()) dugme.setVisibility(View.GONE);
                else dugme.setVisibility(View.VISIBLE);
                break;
            case R.id.button3:
                // ovde ide na mapu
                break;
            case R.id.button4:
                izaberiSliku();
                break;
            case R.id.button2:

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pribaviLokaciju();
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    private  void pribaviLokaciju()
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        }
        curLocation = lm.getLastKnownLocation("network");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni_prijava, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.prijava_okej) {

            // pribavljanje tokena za dodavanje
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

        }

        return super.onOptionsItemSelected(item);
    }

    private void dodaj(String token, String uid)
    {
        new Thread(new Runnable(){
            public void run()
            {
                PrijaviProblemActivity.FTPUpload(pathSlika);
            }
        }).start();
        pribaviLokaciju();
        ProblemModel problem = new ProblemModel();
        problem.id_korisnika = 69;
        problem.id_vrste = 69;
        problem.opis = ((EditText)findViewById(R.id.editText)).getText().toString();
        if(imaSlike) problem.slika = "https://www.geasoft.net/" + pathSlika.substring(pathSlika.lastIndexOf('/')+1);
        else problem.slika = "";
        problem.opstina = "Bogac";
        problem.latitude = Double.toString(curLocation.getLatitude());
        problem.longitude = Double.toString(curLocation.getLongitude());
        ProblemModel.Dodaj(problem, this, token, uid);
    }
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    private void izaberiSliku() {

        final CharSequence[] items = {"Kamera", "Izaberi iz galerije", "Zatvori"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Izaberi sliku");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    imaSlike = true;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    photoFile = Uri.fromFile(createImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (item == 1) {
                    imaSlike = true;
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,SELECT_FILE)
                    ;
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    try {
                        help1 = MediaStore.Images.Media.getBitmap(getContentResolver(), photoFile);
                        ((ImageView)findViewById(R.id.imageView)).setImageBitmap(ThumbnailUtils.extractThumbnail(help1, help1.getWidth(),help1.getHeight()));
                        pathSlika = photoFile.getPath();
                    } catch (IOException e) {
                        Toast.makeText(PrijaviProblemActivity.this, "IO2", Toast.LENGTH_LONG).show();
                    }

                }
                break;
            case SELECT_FILE:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    pathSlika = getRealPathFromURI(selectedImage);
                    ((ImageView)findViewById(R.id.imageView)).setImageURI(selectedImage);
                break;
        }
        }
    }


    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                proj, // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    private File createImageFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Toast.makeText(PrijaviProblemActivity.this, "IO", Toast.LENGTH_LONG).show();
        }

        pathSlika = image.getAbsolutePath();
        return image;
    }

    public static void FTPUpload(String putanja)
    {
        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("195.252.110.140");

            if (con.login("geasoftn", "705903272ld"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                FileInputStream in = new FileInputStream(new File(putanja));
                boolean result = con.storeFile("/public_html/" + putanja.substring(putanja.lastIndexOf('/')+1), in);
                in.close();
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            // JBG
        }


    }

}

package net.geasoft.ksp.ksp;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PrijaviProblemActivity extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijavi_problem);

        Toolbar tulbar = (Toolbar) findViewById(R.id.toolbar_prijava);
        setSupportActionBar(tulbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View v)
    {
        CheckBox boks = (CheckBox)findViewById(R.id.checkBox);
        Button dugme = (Button)findViewById(R.id.button3);
        if(boks.isChecked())  dugme.setVisibility(View.GONE);
        else  dugme.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meni_prijava, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.prijava_okej) {
            ProblemModel problem = new ProblemModel();
            problem.id_korisnika = 69;
            problem.id_vrste = 69;
            problem.opis = ((EditText)findViewById(R.id.editText)).getText().toString();
            problem.slika = "Nema";
            problem.opstina = "Bogac";
            problem.latitude = "44";
            problem.longitude = "44";
            ProblemMetode.Dodaj(problem, this);
        }

        return super.onOptionsItemSelected(item);
    }

}

package net.geasoft.ksp.ksp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class PrijaviProblemActivity extends AppCompatActivity  implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijavi_problem);

        Toolbar tulbar =
                (Toolbar) findViewById(R.id.toolbar_prijava);
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

}

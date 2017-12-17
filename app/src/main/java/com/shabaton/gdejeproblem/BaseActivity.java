package com.shabaton.gdejeproblem;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switch (PreferenceManager.getDefaultSharedPreferences(this).getString("list_tema", "1")) {
            case "1":
                setTheme(R.style.AppTheme_Crvena);
                break;
            case "2":
                setTheme(R.style.AppTheme_Plava);
                break;
            case "3":
                setTheme(R.style.AppTheme_Siva);
                break;
        }
        super.onCreate(savedInstanceState);

    }
}

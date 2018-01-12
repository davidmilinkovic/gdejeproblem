package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */

public class Vrsta implements Serializable {
    public final int id;
    public final String naziv;
    public final Sluzba sluzba;

    public Vrsta(int id, String naziv, Sluzba s) {
        this.id = id;
        this.naziv = naziv;
        this.sluzba = s;
    }

    @Override
    public String toString() {
        return naziv;
    }
}




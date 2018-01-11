package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class MojeSluzbeActivity extends BaseActivity {

    private SwipeRefreshLayout swajp;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moje_sluzbe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swajp = findViewById(R.id.swipe);
        swajp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                osveziSluzbe();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {getString(R.string.mojeSluzbe_prijava)};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MojeSluzbeActivity.this);
                builder.setTitle(R.string.mojeSluzbe_title);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                       if (item == 0) {
                            startActivity(new Intent(MojeSluzbeActivity.this, PrijavaZaSluzbuActivity.class));
                        }
                    }
                });
                builder.show();
            }
        });

        recyclerView = findViewById(R.id.sluzba_list);
        assert recyclerView != null;
        final MojeSluzbeAdapter adapter = new MojeSluzbeAdapter(new ArrayList<Sluzba>());
        LinearLayoutManager mng = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mng);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mng.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        osveziSluzbe();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    private void osveziSluzbe() {
        swajp.setRefreshing(true);
        SluzbaViewModel model = ViewModelProviders.of(this).get(SluzbaViewModel.class);
        FirebaseAuth.getInstance().getCurrentUser().getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            model.sluzbe = null;
                            model.vrste = null;

                            model.dajSluzbe(idToken).observe(MojeSluzbeActivity.this, new Observer<List<Sluzba>>() {
                                @Override
                                public void onChanged(@Nullable List<Sluzba> sluzbe) {
                                    swajp.setRefreshing(false);
                                    ((MojeSluzbeAdapter)recyclerView.getAdapter()).addItems(sluzbe);
                                }
                            });
                        }
                    }
                });

    }

    public class MojeSluzbeAdapter extends RecyclerView.Adapter<MojeSluzbeAdapter.MojeSluzbeHolder> {

        private List<Sluzba> mValues;
        private String[] tipovi = {getString(R.string.mojeSluzbe_tipoviJavna), getString(R.string.mojeSluzbe_tipoviKreirana), getString(R.string.mojeSluzbe_tipoviPrijavljena)};

        public MojeSluzbeAdapter(List<Sluzba> items) {
            mValues = items;
        }

        @Override
        public MojeSluzbeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mojesluzbe_list_content, parent, false);
            return new MojeSluzbeHolder(view);
        }

        public void addItems(List<Sluzba> sluzbe) {
            this.mValues = sluzbe;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final MojeSluzbeHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.txtNaziv.setText(holder.mItem.naziv);
            holder.txtTip.setText(tipovi[holder.mItem.tip]);
            holder.mImgView.setImageResource(getResources().getIdentifier(holder.mItem.ikonica, "drawable", getPackageName()));
            if(holder.mItem.tip < 2) holder.btnOdjava.setVisibility(GONE);
            else {
                holder.btnOdjava.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MojeSluzbeActivity.this);
                        builder.setTitle(R.string.mojeSluzbe_odjava);
                        builder.setMessage(getString(R.string.mojeSluzbe_odjavaPt1) + holder.mItem.naziv + getString(R.string.mojeSluzbe_odjavaPt2));
                        builder.setPositiveButton(R.string.str_da, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Odjava sa sluyzbe
                                final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                                mUser.getToken(false)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    String idToken = task.getResult().getToken();
                                                    RequestQueue queue = Volley.newRequestQueue(MojeSluzbeActivity.this);
                                                    String url = "https://kspclient.geasoft.net/sluzba_api.php";
                                                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    if (response.contains("OK")) {
                                                                        Toast.makeText(MojeSluzbeActivity.this, R.string.mojeSluzbe_uspesnaOdjava, Toast.LENGTH_LONG).show();
                                                                        osveziSluzbe();
                                                                        Intent intent=new Intent();
                                                                        intent.putExtra("imaPromena", true);
                                                                        setResult(444, intent);
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {
                                                                    Toast.makeText(MojeSluzbeActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                    ) {
                                                        @Override
                                                        protected Map<String, String> getParams() {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                            params.put("tip", "odjava");
                                                            params.put("id", Integer.toString(holder.mItem.id));
                                                            params.put("token", idToken);
                                                            params.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                            return params;
                                                        }
                                                    };
                                                    queue.add(postRequest);
                                                } else {
                                                    Toast.makeText(MojeSluzbeActivity.this, R.string.greska, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        });
                        builder.setNegativeButton(R.string.str_ne, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class MojeSluzbeHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView txtNaziv;
            public final TextView txtTip;
            public final ImageView mImgView;
            public final ImageButton btnOdjava;
            public Sluzba mItem;

            public MojeSluzbeHolder(View view) {
                super(view);
                mView = view;
                txtNaziv = (TextView) view.findViewById(R.id.naziv_sluzbe);
                txtTip = (TextView) view.findViewById(R.id.tip_sluzbe);
                mImgView = (ImageView) view.findViewById(R.id.ikonica_sluzba);
                btnOdjava = (ImageButton) view.findViewById(R.id.btnOdjava);
            }
        }
    }
}

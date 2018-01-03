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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.List;

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
                final CharSequence[] items = {"Prijavi se za postojeću službu"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MojeSluzbeActivity.this);
                builder.setTitle("Sluzbe");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                       if (item == 0) {
                            Toast.makeText(MojeSluzbeActivity.this, "Nije još implementirano", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.show();
            }
        });

        recyclerView = findViewById(R.id.sluzba_list);
        assert recyclerView != null;
        final MojeSluzbeAdapter adapter = new MojeSluzbeAdapter(StaticDataProvider.sluzbe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

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
        private String[] tipovi = {"Javna služba", "Služba koju ste kreirali", "Služba za koju ste prijavljeni"};

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
            /* ZBOGOM
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.mItem.tip == 1)
                    {
                        Intent intent = new Intent(MojeSluzbeActivity.this, PregledSluzbeActivity.class);
                        intent.putExtra("sluzba", holder.mItem);
                        startActivity(intent);
                    }
                    else if(holder.mItem.tip == 2)
                    {
                        Toast.makeText(MojeSluzbeActivity.this, "Pregled sluzbe ze koju sam prijavljen", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
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
            public Sluzba mItem;

            public MojeSluzbeHolder(View view) {
                super(view);
                mView = view;
                txtNaziv = (TextView) view.findViewById(R.id.naziv_sluzbe);
                txtTip = (TextView) view.findViewById(R.id.tip_sluzbe);
                mImgView = (ImageView) view.findViewById(R.id.ikonica_sluzba);
            }
        }
    }
}

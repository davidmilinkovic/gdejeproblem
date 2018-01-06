package com.shabaton.gdejeproblem;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

import java.util.List;

public class PrijavaZaSluzbuActivity extends BaseActivity {

    private SwipeRefreshLayout swajp;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijava_za_sluzbu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Prijava za službu");


        recyclerView = findViewById(R.id.sluzbe_risajkl);
        assert recyclerView != null;
        LinearLayoutManager mng = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mng);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mng.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        swajp = findViewById(R.id.swipe);
        swajp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                osvezi();
            }
        });
        swajp.setRefreshing(true);
        osvezi();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    private  void osvezi()
    {
        SluzbaViewModel model = ViewModelProviders.of(this).get(SluzbaViewModel.class);
        FirebaseAuth.getInstance().getCurrentUser().getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            model.sveSluzbe = null;
                            model.dajSveSluzbe(idToken).observe(PrijavaZaSluzbuActivity.this, new Observer<List<Sluzba>>() {
                                @Override
                                public void onChanged(@Nullable List<Sluzba> sluzbe) {
                                    swajp.setRefreshing(false);
                                    SubscribeSluzbaAdapter adap = new SubscribeSluzbaAdapter(sluzbe);
                                    recyclerView.setAdapter(adap);
                                }
                            });
                        }
                    }
                });
    }

    public class SubscribeSluzbaAdapter
            extends RecyclerView.Adapter<SubscribeSluzbaAdapter.SubscribeSluzbaHolder> {

        private List<Sluzba> mValues;

        public SubscribeSluzbaAdapter(List<Sluzba> items) {
            mValues = items;
        }

        @Override
        public SubscribeSluzbaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sluzba_list_content, parent, false);
            return new SubscribeSluzbaHolder(view);
        }

        public void addItems(List<Sluzba> sluzbe) {
            this.mValues = sluzbe;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final SubscribeSluzbaHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.naziv.setText(holder.mItem.naziv);
            holder.slika.setImageResource(getResources().getIdentifier(holder.mItem.ikonica, "drawable", getPackageName()));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrijavaZaSluzbuActivity.this);
                    builder.setTitle("Prijava za službu");
                    builder.setMessage("Da li ste sigurni da želite da se prijavite za službu " + holder.mItem.naziv);
                    builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Sabskrajb na datu sluzbu
                            Alati.Sabskrajb(holder.mItem.id, PrijavaZaSluzbuActivity.this);
                        }
                    });
                    builder.setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class SubscribeSluzbaHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public Sluzba mItem;
            public final TextView naziv;
            public final ImageView slika;


            public SubscribeSluzbaHolder(View view) {
                super(view);
                mView = view;
                naziv = view.findViewById(R.id.content);
                slika = view.findViewById(R.id.ikonica_sluzba);
            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }
}

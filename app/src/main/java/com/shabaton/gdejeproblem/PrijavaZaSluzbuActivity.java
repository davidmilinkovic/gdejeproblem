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
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PrijavaZaSluzbuActivity extends BaseActivity {

    private SwipeRefreshLayout swajp;
    private RecyclerView recyclerView;
    List<Sluzba> trenutna = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijava_za_sluzbu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.prijavaZaSluzbu_title);


        recyclerView = (RecyclerView)findViewById(R.id.sluzbe_risajkl);
        assert recyclerView != null;
        LinearLayoutManager mng = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mng);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mng.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        swajp = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swajp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                osvezi();
            }
        });
        swajp.setRefreshing(true);
        osvezi();

        ((TextView)findViewById(R.id.txtSluzbaPretraga)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(trenutna != null) {
                    String tekst = ((TextView)findViewById(R.id.txtSluzbaPretraga)).getText().toString();
                    List<Sluzba> nova = new ArrayList<Sluzba>();
                    for(int j = 0; j < trenutna.size(); j++)
                    {
                        Sluzba s = trenutna.get(j);
                        if(Pattern.compile(Pattern.quote(tekst), Pattern.CASE_INSENSITIVE).matcher(s.naziv).find() || tekst.isEmpty())
                            nova.add(s);
                    }
                    SubscribeSluzbaAdapter adap = new SubscribeSluzbaAdapter(nova);
                    recyclerView.setAdapter(adap);
                    if(nova.size() == 0)
                        findViewById(R.id.nijednaSluzba).setVisibility(View.VISIBLE);
                    else
                        findViewById(R.id.nijednaSluzba).setVisibility(View.INVISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                                    trenutna = sluzbe;
                                    swajp.setRefreshing(false);
                                    SubscribeSluzbaAdapter adap = new SubscribeSluzbaAdapter(sluzbe);
                                    recyclerView.setAdapter(adap);

                                    if(sluzbe.size() == 0)
                                        findViewById(R.id.nijednaSluzba).setVisibility(View.VISIBLE);
                                    else
                                        findViewById(R.id.nijednaSluzba).setVisibility(View.INVISIBLE);
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
                    builder.setTitle(R.string.prijavaZaSluzbu_title);
                    builder.setMessage(getString(R.string.prijavaZaSluzbu_jesiSiguran) + holder.mItem.naziv+"?");
                    builder.setPositiveButton(R.string.str_da, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Sabskrajb na datu sluzbu
                            Alati.Sabskrajb(holder.mItem.id, PrijavaZaSluzbuActivity.this);
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

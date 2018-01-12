package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SluzbaListActivity extends BaseActivity {

    Boolean samoJavni = false;
    public List<Sluzba> sluzbe;
    public List<Vrsta> vrste;
    private SwipeRefreshLayout swajp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sluzba_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        View recyclerView = findViewById(R.id.sluzba_list);
        assert recyclerView != null;

        swajp = (SwipeRefreshLayout) findViewById(R.id.swajp);
        swajp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupRecyclerView((RecyclerView) recyclerView);
            }
        });

        setupRecyclerView((RecyclerView) recyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        swajp.setRefreshing(true);
        final SimpleItemRecyclerViewAdapter recyclerViewAdapter = new SimpleItemRecyclerViewAdapter(new ArrayList<Sluzba>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
        SluzbaViewModel sluzbaViewModel = ViewModelProviders.of(this).get(SluzbaViewModel.class);
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                   public void onComplete(@NonNull Task<GetTokenResult> task) {
                       if (task.isSuccessful()) {
                           String idToken = task.getResult().getToken();
                           sluzbaViewModel.sluzbe = null;
                           sluzbaViewModel.dajSluzbe(idToken).observe(SluzbaListActivity.this, new Observer<List<Sluzba>>() {
                               @Override
                               public void onChanged(@Nullable List<Sluzba> sluzbe) {
                                   SluzbaListActivity.this.sluzbe = sluzbe;
                                   SluzbaListActivity.this.vrste = sluzbaViewModel.vrste.getValue();
                                   recyclerViewAdapter.addItems(sluzbe);
                                   swajp.setRefreshing(false);
                               }
                           });
                       }
                   }
               });
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69) {
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("izabranaVrsta", data.getSerializableExtra("izabranaVrsta"));
                setResult(RESULT_OK, intent);
                finish();
            }
            else if(samoJavni) {
                setResult(RESULT_CANCELED, null);
                finish();
            }

        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Sluzba> mValues;

        public SimpleItemRecyclerViewAdapter(List<Sluzba> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sluzba_list_content, parent, false);
            return new ViewHolder(view);
        }

        public void addItems(List<Sluzba> sluzbe) {
            this.mValues = sluzbe;/* Obsolete, cudno izgleda
            if(sluzbe.size() == 1)
            {
                samoJavni = true;
                Activity context = SluzbaListActivity.this;
                Sluzba s = sluzbe.get(0);
                Intent intent = new Intent(context, SluzbaDetailActivity.class);
                intent.putExtra(SluzbaDetailActivity.ARG_ITEM_ID, Integer.toString(s.id));
                intent.putExtra("sluzba", s);
                List<Vrsta> listaVrste = new ArrayList<Vrsta>();
                for (Vrsta vv : vrste) {
                    if(vv.sluzba.id == s.id)
                        listaVrste.add(vv);
                }
                intent.putExtra("vrste", (Serializable) listaVrste);
                context.startActivityForResult(intent, 69);
            } */
            notifyDataSetChanged();

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(holder.mItem.naziv);
            holder.mImgView.setImageResource(getResources().getIdentifier(holder.mItem.ikonica, "drawable", getPackageName()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Activity context = (Activity) v.getContext();
                    Intent intent = new Intent(context, SluzbaDetailActivity.class);
                    intent.putExtra("sluzba", holder.mItem);
                    List<Vrsta> listaVrste = new ArrayList<Vrsta>();
                    for (Vrsta vv : vrste) {
                        if(vv.sluzba.id == holder.mItem.id)
                            listaVrste.add(vv);
                    }
                    intent.putExtra("vrste", (Serializable) listaVrste);
                    context.startActivityForResult(intent, 69);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public final ImageView mImgView;
            public Sluzba mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
                mImgView = (ImageView) view.findViewById(R.id.ikonica_sluzba);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}

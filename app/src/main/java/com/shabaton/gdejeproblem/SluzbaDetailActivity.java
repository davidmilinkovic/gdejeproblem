package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SluzbaDetailActivity extends BaseActivity {

    private Sluzba izabranaSluzba;

    public static final String ARG_ITEM_ID = "item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sluzba_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sluzba);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        izabranaSluzba = (Sluzba) getIntent().getSerializableExtra("sluzba");
        getSupportActionBar().setTitle(izabranaSluzba.naziv);

        View recyclerView = findViewById(R.id.vrsta_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        final VrstaViewAdapter recyclerViewAdapter = new VrstaViewAdapter(new ArrayList<Vrsta>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);

        List<Vrsta> lista = (ArrayList<Vrsta>) getIntent().getSerializableExtra("vrste");
        recyclerViewAdapter.addItems(lista);

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

    public class VrstaViewAdapter
            extends RecyclerView.Adapter<VrstaViewAdapter.VrstaViewHolder> {

        private List<Vrsta> mValues;

        public VrstaViewAdapter(List<Vrsta> items) {
            mValues = items;
        }

        @Override
        public VrstaViewAdapter.VrstaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vrsta_list_content, parent, false);
            return new VrstaViewAdapter.VrstaViewHolder(view);
        }

        public void addItems(List<Vrsta> vrste) {
            this.mValues = vrste;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final VrstaViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).naziv);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.putExtra("izabranaVrsta", holder.mItem);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class VrstaViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mContentView;
            public Vrsta mItem;

            public VrstaViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Sluzbe. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SluzbaDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SluzbaListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

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
        setupRecyclerView((RecyclerView) recyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        final SimpleItemRecyclerViewAdapter recyclerViewAdapter = new SimpleItemRecyclerViewAdapter(new ArrayList<Sluzba>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);
/*
        SluzbaViewModel model = ViewModelProviders.of(this).get(SluzbaViewModel.class);
        model.dajSluzbe().observe(SluzbaListActivity.this, new Observer<List<SluzbaViewModel.Sluzba>>() {
            @Override
            public void onChanged(@Nullable List<SluzbaViewModel.Sluzba> sluzbe) {
                recyclerViewAdapter.addItems(sluzbe);
            }
        });*/
        recyclerViewAdapter.addItems(StaticDataProvider.sluzbe);

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
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 69) {
            if(resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("id_vrste", data.getStringExtra("id_vrste"));
                intent.putExtra("naziv_vrste", data.getStringExtra("naziv_vrste"));

                setResult(RESULT_OK, intent);
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
            this.mValues = sluzbe;
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
                    intent.putExtra(SluzbaDetailActivity.ARG_ITEM_ID, Integer.toString(holder.mItem.id));
                    intent.putExtra("naslov", holder.mItem.naziv);
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

package net.geasoft.ksp.ksp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a single Sluzba detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SluzbaListActivity}.
 */
public class SluzbaDetailActivity extends AppCompatActivity {

    private SluzbaViewModel.Sluzba izabranaSluzba;
    public int trenutni_id;

    public static final String ARG_ITEM_ID = "item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sluzba_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sluzba);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        // Load the dummy content specified by the fragment
        // arguments. In a real-world scenario, use a Loader
        // to load content from a content provider.
        final int id = Integer.parseInt(getIntent().getStringExtra(ARG_ITEM_ID));
        trenutni_id = id;

        for (SluzbaViewModel.Sluzba s: StaticDataProvider.sluzbe) {
            if(s.id == id) {
                izabranaSluzba = s;
            }
        }
        getSupportActionBar().setTitle(izabranaSluzba.naziv);

        /*
                OVAKO SE VRACA ODGOVOR
                Intent intent=new Intent();
                intent.putExtra("poruka", "hehe");
                getActivity().setResult(69, intent);
                getActivity().finish();
*/
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

        final VrstaViewAdapter recyclerViewAdapter = new VrstaViewAdapter(new ArrayList<VrstaViewModel.Vrsta>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);
/*
        VrstaViewModel model = ViewModelProviders.of(this).get(VrstaViewModel.class);

        model.dajVrste(trenutni_id).observe(this, new Observer<List<VrstaViewModel.Vrsta>>() {
            @Override
            public void onChanged(@Nullable List<VrstaViewModel.Vrsta> vrste) {
                recyclerViewAdapter.addItems(vrste);
            }
        });*/

        List<VrstaViewModel.Vrsta> lista = new ArrayList<VrstaViewModel.Vrsta>();
        for (VrstaViewModel.Vrsta v : StaticDataProvider.vrste) {
            if(v.sluzba.id == trenutni_id)
                lista.add(v);
        }
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

        private List<VrstaViewModel.Vrsta> mValues;

        public VrstaViewAdapter(List<VrstaViewModel.Vrsta> items) {
            mValues = items;
        }

        @Override
        public VrstaViewAdapter.VrstaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vrsta_list_content, parent, false);
            return new VrstaViewAdapter.VrstaViewHolder(view);
        }

        public void addItems(List<VrstaViewModel.Vrsta> vrste) {
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
                    intent.putExtra("id_vrste", Integer.toString(holder.mItem.id));
                    intent.putExtra("naziv_vrste", holder.mItem.naziv);
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
            public VrstaViewModel.Vrsta mItem;

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

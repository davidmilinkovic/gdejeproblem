package net.geasoft.ksp.ksp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MojiProblemiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moji_problemi);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        View recyclerView = findViewById(R.id.moji_problemi_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        final ProblemiRecyclerAdapter recyclerViewAdapter = new ProblemiRecyclerAdapter(new ArrayList<ProblemViewModel.Problem>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(recyclerViewAdapter);

        ProblemViewModel model = ViewModelProviders.of(this).get(ProblemViewModel.class);

        model.dajProbleme(FirebaseAuth.getInstance().getCurrentUser().getUid()).observe(this, new Observer<List<ProblemViewModel.Problem>>() {
            @Override
            public void onChanged(@Nullable List<ProblemViewModel.Problem> vrste) {
                recyclerViewAdapter.addItems(vrste);
            }
        });

        // recyclerViewAdapter.addItems(StaticDataProvider.problemi);

    }




    public class ProblemiRecyclerAdapter
            extends RecyclerView.Adapter<ProblemiRecyclerAdapter.ProblemViewHolder> {

        private List<ProblemViewModel.Problem> mValues;

        public ProblemiRecyclerAdapter(List<ProblemViewModel.Problem> items) {
            mValues = items;
        }

        @Override
        public ProblemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.problemi_list_content, parent, false);
            return new ProblemViewHolder(view);
        }

        public void addItems(List<ProblemViewModel.Problem> problemi) {
            this.mValues = problemi;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final ProblemViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.txtVrsta.setText(holder.mItem.vrsta.naziv);
            holder.txtOpis.setText(holder.mItem.opis);


            Geocoder geocoder;
            List<Address> addresses = new ArrayList<Address>();
            geocoder = new Geocoder(MojiProblemiActivity.this, Locale.getDefault());
            boolean ima = true;

            double latitude = Double.parseDouble(holder.mItem.latitude);
            double longitude = Double.parseDouble(holder.mItem.longitude);

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (Exception e) {
                ima = false;
            }

            String adresa = holder.mItem.opstina;
            if(ima) adresa = addresses.get(0).getAddressLine(0);

            holder.txtLokacija.setText(adresa);

            holder.imgIkonica.setImageResource(getResources().getIdentifier(holder.mItem.vrsta.sluzba.ikonica, "drawable", getPackageName()));
            if(holder.mItem.slika.length() > 0) Picasso.with(MojiProblemiActivity.this).load(Uri.parse(holder.mItem.slika)).resize(600, 600).centerCrop().into(holder.imgSlika);
            else {
                Picasso.with(MojiProblemiActivity.this).load(R.drawable.nemaslike).resize(600, 600).centerCrop().into(holder.imgSlika);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*
                    Activity context = (Activity) v.getContext();
                    Intent intent = new Intent(context, SluzbaDetailActivity.class);
                    intent.putExtra(SluzbaDetailActivity.ARG_ITEM_ID, Integer.toString(holder.mItem.id));
                    intent.putExtra("naslov", holder.mItem.naziv);
                    context.startActivityForResult(intent, 69);*/

                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ProblemViewHolder extends RecyclerView.ViewHolder {
            public final View mView;

            public final TextView txtVrsta;
            public final TextView txtOpis;
            public final TextView txtLokacija;

            public final ImageView imgIkonica;
            public final ImageView imgSlika;
            public ProblemViewModel.Problem mItem;

            public ProblemViewHolder(View view) {
                super(view);
                mView = view;

                txtVrsta = (TextView) view.findViewById(R.id.list_problem_vrsta);
                txtOpis = (TextView) view.findViewById(R.id.list_problem_opis);
                txtLokacija = (TextView) view.findViewById(R.id.list_lokacija);

                imgIkonica = (ImageView) view.findViewById(R.id.list_ikonica_sluzba);
                imgSlika = (ImageView) view.findViewById(R.id.list_slika_problem);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + txtVrsta.getText() + "'";
            }
        }
    }


}

package net.geasoft.ksp.ksp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class MojiProblemiFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView lista;
    public String uid = "";
    public boolean ucitano = false;


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.activity_moji_problemi, container, false);
        final Activity c = getActivity();
        lista = (RecyclerView) view.findViewById(R.id.moji_problemi_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(c);
        lista.setLayoutManager(layoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ucitano = false;
                osvezi();
            }
        });


        setupRecyclerView();
        return view;
    }

    void osvezi() {
        setupRecyclerView();
        ucitalo();
    }

    void ucitalo() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView() {

        final ProblemiRecyclerAdapter recyclerViewAdapter = new ProblemiRecyclerAdapter(new ArrayList<ProblemViewModel.Problem>());
        lista.setLayoutManager(new LinearLayoutManager(getActivity()));

        lista.setAdapter(recyclerViewAdapter);

        ProblemViewModel model = ViewModelProviders.of((AppCompatActivity)getActivity()).get(ProblemViewModel.class);

        model.dajProbleme(FirebaseAuth.getInstance().getCurrentUser().getUid(), !ucitano).observe((AppCompatActivity)getActivity(), new Observer<List<ProblemViewModel.Problem>>() {
            @Override
            public void onChanged(@Nullable List<ProblemViewModel.Problem> problemi) {
                recyclerViewAdapter.addItems(problemi);
            }
        });

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
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
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

            StatusViewModel model = ViewModelProviders.of((AppCompatActivity)getActivity()).get(StatusViewModel.class);

            model.dajStatuse(uid, !ucitano).observe((AppCompatActivity)getActivity(), new Observer<List<StatusViewModel.Status>>() {
                @Override
                public void onChanged(@Nullable List<StatusViewModel.Status> statusi) {
                    for(StatusViewModel.Status s : statusi)
                    {
                        if(s.id_problema == holder.mItem.id) {
                            holder.txtStatus.setText(s.naziv);
                            holder.txtStatus.setTextColor(Color.parseColor(s.boja));
                            return;
                        }
                    }
                }
            });

            ucitano = true;

            holder.imgIkonica.setImageResource(getResources().getIdentifier(holder.mItem.vrsta.sluzba.ikonica, "drawable", getActivity().getPackageName()));
            if(holder.mItem.slika.length() > 0) Picasso.with(getActivity()).load(Uri.parse(holder.mItem.slika)).resize(600, 600).centerCrop().into(holder.imgSlika);
            else {
                Picasso.with(getActivity()).load(R.drawable.nemaslike).resize(600, 600).centerCrop().into(holder.imgSlika);
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
            public final TextView txtStatus;

            public final ImageView imgIkonica;
            public final ImageView imgSlika;
            public ProblemViewModel.Problem mItem;

            public ProblemViewHolder(View view) {
                super(view);
                mView = view;

                txtVrsta = (TextView) view.findViewById(R.id.list_problem_vrsta);
                txtOpis = (TextView) view.findViewById(R.id.list_problem_opis);
                txtLokacija = (TextView) view.findViewById(R.id.list_lokacija);
                txtStatus = (TextView) view.findViewById(R.id.list_status);

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

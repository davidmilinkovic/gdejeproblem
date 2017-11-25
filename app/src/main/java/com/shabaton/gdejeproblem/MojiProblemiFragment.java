package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MojiProblemiFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView lista;
    public String email = "";
    public boolean ucitano = false;


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


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

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fabDodaj);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent iii = new Intent(getActivity(), PrijaviProblemActivity.class);
                getActivity().startActivity(iii);
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

        ProblemViewModel model = ViewModelProviders.of((AppCompatActivity) getActivity()).get(ProblemViewModel.class);

        model.dajProbleme(!ucitano).observe((AppCompatActivity) getActivity(), new Observer<List<ProblemViewModel.Problem>>() {
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
            holder.txtOpis.setText(holder.mItem.opis.replace("<br>", "\n"));


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

            model.dajStatuse(!ucitano).observe((AppCompatActivity)getActivity(), new Observer<List<StatusViewModel.Status>>() {
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
            if(holder.mItem.slika.length() > 0) {
                try {
                    Glide.with(getActivity())
                            .load(Uri.parse(holder.mItem.slika))
                            .apply(RequestOptions.fitCenterTransform())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    holder.progressBar.setVisibility(View.GONE);
                                    Glide.with(getActivity()).load(R.drawable.nemaslike).apply(RequestOptions.fitCenterTransform()).into(holder.imgSlika);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    holder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(holder.imgSlika);
                } catch (Exception e) {
                    holder.progressBar.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(R.drawable.nemaslike).apply(RequestOptions.fitCenterTransform()).into(holder.imgSlika);
                }
            }
            else {
                holder.progressBar.setVisibility(View.GONE);
                Glide.with(getActivity()).load(R.drawable.nemaslike).apply(RequestOptions.fitCenterTransform()).into(holder.imgSlika);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Activity context = (Activity) v.getContext();
                    Intent intent = new Intent(context, PregledProblemaActivity.class);
                    intent.putExtra("vrsta", holder.mItem.vrsta.naziv);
                    intent.putExtra("lokacija", holder.txtLokacija.getText());
                    intent.putExtra("opis", holder.mItem.opis);
                    intent.putExtra("slika", holder.mItem.slika);
                    intent.putExtra("latitude", holder.mItem.latitude);
                    intent.putExtra("longitude", holder.mItem.longitude);

                    context.startActivity(intent);

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

            public final ProgressBar progressBar;

            public ProblemViewHolder(View view) {
                super(view);
                mView = view;

                txtVrsta = (TextView) view.findViewById(R.id.list_problem_vrsta);
                txtOpis = (TextView) view.findViewById(R.id.list_problem_opis);
                txtLokacija = (TextView) view.findViewById(R.id.list_lokacija);
                txtStatus = (TextView) view.findViewById(R.id.list_status);

                imgIkonica = (ImageView) view.findViewById(R.id.list_ikonica_sluzba);
                imgSlika = (ImageView) view.findViewById(R.id.list_slika_problem);

                progressBar = (ProgressBar) view.findViewById(R.id.listaProgress);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + txtVrsta.getText() + "'";
            }
        }
    }


}

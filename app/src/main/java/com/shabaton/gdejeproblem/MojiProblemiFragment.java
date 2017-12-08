package com.shabaton.gdejeproblem;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import javax.sql.DataSource;

public class MojiProblemiFragment extends Fragment {

    public SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView lista;
    public String email = "";
    private LinearLayout nemaProblema;
    List<ProblemViewModel.Problem> trenutna = null;


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_moji_problemi, container, false);

        nemaProblema = (LinearLayout)view.findViewById(R.id.layoutNemaProblema);

        final Activity c = getActivity();
        lista = (RecyclerView) view.findViewById(R.id.moji_problemi_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                osveziProbleme();
            }
        });

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fabDodaj);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent iii = new Intent(getActivity(), PrijaviProblemActivity.class);
                getActivity().startActivityForResult(iii, 333);
            }
        });

        if(savedInstanceState == null) setupRecyclerView();

        return view;
    }


    public void setupRecyclerView() {
        final ProblemiRecyclerAdapter recyclerViewAdapter = new ProblemiRecyclerAdapter(new ArrayList<ProblemViewModel.Problem>());
        lista.setLayoutManager(new LinearLayoutManager(getActivity()));
        lista.setAdapter(recyclerViewAdapter);
        ucitajProbleme();
    }

    public void ucitajProbleme()
    {
        try {
            mSwipeRefreshLayout.setRefreshing(true);

            final ProblemiRecyclerAdapter recyclerViewAdapter = (ProblemiRecyclerAdapter)lista.getAdapter();

            ProblemViewModel model = ViewModelProviders.of((AppCompatActivity) getActivity()).get(ProblemViewModel.class);

            final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            mUser.getToken(false)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();

                                Pair<MutableLiveData, MutableLiveData> pp = model.dajProbleme(idToken);


                                pp.second.observe((AppCompatActivity) getActivity(), new Observer<Boolean>() {
                                    @Override
                                    public void onChanged(@Nullable Boolean prazna) {
                                        if(prazna)
                                        {
                                            mSwipeRefreshLayout.setRefreshing(false);
                                            nemaProblema.setVisibility(View.VISIBLE);
                                            lista.removeAllViews();
                                            recyclerViewAdapter.addItems(new ArrayList<ProblemViewModel.Problem>());
                                        }
                                    }
                                });


                                pp.first.observe((AppCompatActivity) getActivity(), new Observer<List<ProblemViewModel.Problem>>() {
                                    @Override
                                    public void onChanged(@Nullable List<ProblemViewModel.Problem> problemi) {
                                        try {
                                            nemaProblema.setVisibility(View.GONE);
                                            mSwipeRefreshLayout.setRefreshing(false);
                                            recyclerViewAdapter.addItems(problemi);
                                            // Toast.makeText(getActivity(), "Hehe", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(), R.string.greska_token, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void osveziProbleme()
    {
        mSwipeRefreshLayout.setRefreshing(true);

        final ProblemiRecyclerAdapter recyclerViewAdapter = (ProblemiRecyclerAdapter)lista.getAdapter();

        ProblemViewModel model = ViewModelProviders.of((AppCompatActivity) getActivity()).get(ProblemViewModel.class);
        StatusViewModel modelS = ViewModelProviders.of((AppCompatActivity) getActivity()).get(StatusViewModel.class);

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            model.ucitajProbleme(idToken);
                            modelS.ucitajStatuse();

                        } else {
                            Toast.makeText(getActivity(), R.string.greska_token, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public class ProblemiRecyclerAdapter extends RecyclerView.Adapter<ProblemiRecyclerAdapter.ProblemViewHolder> {

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


            String adresa = holder.mItem.adresa;

            holder.txtLokacija.setText(adresa);

            StatusViewModel model = ViewModelProviders.of((AppCompatActivity)getActivity()).get(StatusViewModel.class);

            model.dajStatuse().observe((AppCompatActivity)getActivity(), new Observer<List<StatusViewModel.Status>>() {
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

            holder.imgIkonica.setImageResource(getResources().getIdentifier(holder.mItem.vrsta.sluzba.ikonica, "drawable", getActivity().getPackageName()));
            if(holder.mItem.slika.length() > 0) {
                try {
                    Glide.with(getActivity())
                            .load(Uri.parse(holder.mItem.slika.replace(".jpg", "_t.jpg")))
                            .fitCenter()
                            .dontAnimate()
                            .listener(new RequestListener<Uri, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, Uri uri, Target<GlideDrawable> target, boolean b) {
                                    holder.progressBar.setVisibility(View.GONE);
                                    Glide.with(getActivity()).load(R.drawable.nemaslike).fitCenter().into(holder.imgSlika);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable glideDrawable, Uri uri, Target<GlideDrawable> target, boolean b, boolean b1) {
                                    holder.progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .error(R.drawable.nemaslike)
                            .into(holder.imgSlika);
                } catch (Exception e) {
                    holder.progressBar.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(R.drawable.nemaslike).fitCenter().into(holder.imgSlika);
                }
            }
            else {
                holder.progressBar.setVisibility(View.GONE);
                Glide.with(getActivity()).load(R.drawable.nemaslike).fitCenter().into(holder.imgSlika);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), PregledProblemaActivity.class);
                    intent.putExtra("vrsta", holder.mItem.vrsta.naziv);
                    intent.putExtra("lokacija", holder.txtLokacija.getText());
                    intent.putExtra("opis", holder.mItem.opis);
                    intent.putExtra("slika", holder.mItem.slika);
                    intent.putExtra("status", holder.txtStatus.getText());
                    intent.putExtra("latitude", holder.mItem.latitude);
                    intent.putExtra("longitude", holder.mItem.longitude);
                    getActivity().startActivity(intent);
                }
            });
            setAnimation(holder.itemView, position);
        }

        private int lastPosition = -1;

        private void setAnimation(View viewToAnimate, int position)
        {
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);

                lastPosition = position;
            }
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

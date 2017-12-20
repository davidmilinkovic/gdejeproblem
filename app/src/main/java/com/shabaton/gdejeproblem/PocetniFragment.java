package com.shabaton.gdejeproblem;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.List;

public class PocetniFragment extends Fragment {


    private SwipeRefreshLayout swajp;

    public PocetniFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_pocetni, container, false);

        swajp = (SwipeRefreshLayout)v.findViewById(R.id.swipe);
        swajp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ucitajPodatke(v);
            }
        });

        if(savedInstanceState == null)
            ucitajPodatke(v);

        return v;
    }

    private void ucitajPodatke(View v) {
        swajp.setRefreshing(true);

        ((TextView)v.findViewById(R.id.txt_dobrodosli)).setText("Dobrodošli, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((TextView)v.findViewById(R.id.txt_porukica)).setText("Drago nam je što ste ovde!");
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).fitCenter().into((ImageView)v.findViewById(R.id.img_korisnik));

        TextView txtBr = v.findViewById(R.id.br_problema);
        TextView txtBrRes = v.findViewById(R.id.br_prihvacenih);
        TextView txtBrDoSledece = v.findViewById(R.id.br_dosledece);
        ProgressBar prog = v.findViewById(R.id.progressBar3);

        ((Button)v.findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PrijaviProblemActivity.class);
                getActivity().startActivityForResult(intent, 333);
            }
        });

        int sledecaTitula = 100;
        int idStatusResen = 1;

        ProblemViewModel model = ViewModelProviders.of((AppCompatActivity) getActivity()).get(ProblemViewModel.class);

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            model.problemi = null;
                            Pair<MutableLiveData<List<ProblemViewModel.Problem>>, MutableLiveData<Boolean>> pp = model.dajProbleme(idToken);

                            pp.second.observe((AppCompatActivity) getActivity(), new Observer<Boolean>() {
                                @Override
                                public void onChanged(@Nullable Boolean prazna) {
                                    if(prazna)
                                    {
                                        swajp.setRefreshing(false);
                                        txtBr.setText("0");
                                        txtBrRes.setText("0");
                                        txtBrRes.setText(Integer.toString(sledecaTitula));
                                    }
                                }
                            });


                            pp.first.observe((AppCompatActivity) getActivity(), new Observer<List<ProblemViewModel.Problem>>() {
                                @Override
                                public void onChanged(@Nullable List<ProblemViewModel.Problem> problemi) {
                                    try {
                                        swajp.setRefreshing(false);
                                        txtBr.setText(Integer.toString(problemi.size()));

                                        int resenih = 0;
                                        for(ProblemViewModel.Problem p : problemi)
                                            if(p.statusi.get(0).status.id == idStatusResen)
                                                resenih++;

                                        txtBrRes.setText(Integer.toString(resenih));
                                        txtBrDoSledece.setText(Integer.toString(sledecaTitula - resenih));

                                        ObjectAnimator anim = ObjectAnimator.ofInt(prog, "progress", 0, resenih*100/sledecaTitula);
                                        anim.setDuration(500);
                                        anim.start();


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
    }


}

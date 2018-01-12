package com.shabaton.gdejeproblem;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SpisakStatusaFragment extends DialogFragment {

    public static SpisakStatusaFragment newInstance(String param1, String param2) {
        return new SpisakStatusaFragment();
    }

    public ProblemViewModel.Problem prob;


    // neki bag je prisutan, rekli na forumu da mora ovako
    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        setRetainInstance(true);

        View v = inflater.inflate(R.layout.fragment_spisak_statusa, null);

        RecyclerView recyclerView = v.findViewById(R.id.spisak_statusa_list);
        assert recyclerView != null;
        final SpisakStatusaAdapter adapter = new SpisakStatusaAdapter(prob.statusi);
        LinearLayoutManager mng = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mng);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mng.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        SwipeRefreshLayout swajp = v.findViewById(R.id.swipe);
        swajp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ProblemViewModel model = ViewModelProviders.of(getActivity()).get(ProblemViewModel.class);
                SluzbaViewModel modelS = ViewModelProviders.of(getActivity()).get(SluzbaViewModel.class);
                final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                mUser.getToken(false)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String idToken = task.getResult().getToken();

                                    modelS.dajSluzbe(idToken).observe(getActivity(), new Observer<List<Sluzba>>() {
                                        @Override
                                        public void onChanged(@Nullable List<Sluzba> sluzbas) {
                                            model.problemi = null;
                                            model.dajProbleme(idToken, modelS.vrste.getValue()).first.observe(getActivity(), new Observer<List<ProblemViewModel.Problem>>() {
                                                @Override
                                                public void onChanged(@Nullable List<ProblemViewModel.Problem> problemi) {
                                                    try {
                                                        for(ProblemViewModel.Problem p : problemi) {
                                                            if (p.id == prob.id) {
                                                                prob = p;
                                                                swajp.setRefreshing(false);
                                                                adapter.addItems(prob.statusi);
                                                                break;
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    });


                                } else {
                                    Toast.makeText(getActivity(), R.string.greska_token, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

        builder.setView(v)
                .setTitle(R.string.istorijaStatusa)
                .setNegativeButton(R.string.str_zatvori, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SpisakStatusaFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_spisak_statusa, container, false);
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ((PregledProblemaActivity)this.getActivity()).dialogZatvoren(prob);
    }

    public class SpisakStatusaAdapter
            extends RecyclerView.Adapter<SpisakStatusaAdapter.SpisakStatusaHolder> {

        private List<StatusEntry> mValues;

        public SpisakStatusaAdapter(List<StatusEntry> items) {
            mValues = items;
        }

        @Override
        public SpisakStatusaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spisak_statusa_content, parent, false);
            return new SpisakStatusaAdapter.SpisakStatusaHolder(view);
        }

        public void addItems(List<StatusEntry> statusi) {
            this.mValues = statusi;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final SpisakStatusaHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.txtStatus.setText(holder.mItem.status.naziv);
            holder.txtStatus.setTextColor(Color.parseColor(holder.mItem.status.boja));

            SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat f2 = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
            try {
                Date date = f1.parse(holder.mItem.datum);
                holder.txtDatum.setText(f2.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.txtDatum.setText(holder.mItem.datum);
            }
            holder.txtKomentar.setText(holder.mItem.komentar);
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class SpisakStatusaHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public StatusEntry mItem;
            public final TextView txtStatus;
            public final TextView txtDatum;
            public final TextView txtKomentar;


            public SpisakStatusaHolder(View view) {
                super(view);
                mView = view;
                txtStatus = (TextView) view.findViewById(R.id.spisak_status);
                txtDatum = (TextView) view.findViewById(R.id.spisak_datum);
                txtKomentar = (TextView) view.findViewById(R.id.spisak_komentar);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + txtStatus.getText() + "'";
            }
        }
    }

}

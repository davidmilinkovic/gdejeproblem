package com.shabaton.gdejeproblem;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
    private RecyclerView recObavestenja;

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
                ucitajPodatke(v, true);
            }
        });


        Glide.with(getActivity()).load(R.drawable.priroda).centerCrop().into((ImageView)v.findViewById(R.id.imgGrad));
        recObavestenja = v.findViewById(R.id.obavestenja_list);
        LinearLayoutManager mng = new LinearLayoutManager(getActivity());
        recObavestenja.setLayoutManager(mng);


        ucitajPodatke(v, false);
        return v;
    }

    private void ucitajPodatke(View v, Boolean osvezi) {
        swajp.setRefreshing(true);

        ((TextView) v.findViewById(R.id.txt_dobrodosli)).setText(getString(R.string.home_welcome) + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        ((TextView) v.findViewById(R.id.txt_porukica)).setText(R.string.home_gegaraDobrodosao);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).fitCenter().into((ImageView) v.findViewById(R.id.img_korisnik));

        TextView txtBr = v.findViewById(R.id.br_problema);
        TextView txtBrRes = v.findViewById(R.id.br_prihvacenih);
        TextView txtBrDoSledece = v.findViewById(R.id.br_dosledece);
        ImageView img = (ImageView) v.findViewById(R.id.imgTitula);
        ProgressBar prog = v.findViewById(R.id.progressBar3);

        ((Button) v.findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PrijaviProblemActivity.class);
                getActivity().startActivityForResult(intent, 333);
            }
        });

        int idStatusResen = 1;

        ProblemViewModel model = ViewModelProviders.of((AppCompatActivity) getActivity()).get(ProblemViewModel.class);
        ObavestenjeViewModel obModel = ViewModelProviders.of((AppCompatActivity) getActivity()).get(ObavestenjeViewModel.class);
        SluzbaViewModel sluzbaViewModel = ViewModelProviders.of(getActivity()).get(SluzbaViewModel.class);

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getToken(false)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            sluzbaViewModel.sluzbe = null;
                            sluzbaViewModel.dajSluzbe(idToken).observe(getActivity(), new Observer<List<Sluzba>>() {
                                @Override
                                public void onChanged(@Nullable List<Sluzba> sluzbe) {
                                    // ucitane su sluzbe, mozemo dalje

                                    if(osvezi) model.problemi = null;
                                    Pair<MutableLiveData<List<ProblemViewModel.Problem>>, MutableLiveData<Boolean>> pp = model.dajProbleme(idToken, sluzbaViewModel.vrste.getValue());

                                    pp.second.observe((AppCompatActivity) getActivity(), new Observer<Boolean>() {
                                        @Override
                                        public void onChanged(@Nullable Boolean prazna) {
                                            if(prazna)
                                            {
                                                swajp.setRefreshing(false);
                                                txtBr.setText("0");
                                                txtBrRes.setText("0");
                                                prog.setProgress(0);
                                                if(img != null && getActivity() != null) Glide.with(getActivity()).load(R.drawable.t0).fitCenter().into(img);
                                                ((TextView) v.findViewById(R.id.txtTitula)).setText(StaticDataProvider.titule.get(0).naziv);
                                                txtBrDoSledece.setText(Integer.toString(StaticDataProvider.titule.get(1).brProblema));
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

                                                Titula trenutnaTitula = StaticDataProvider.titule.get(0), sledecaTitula = null;

                                                for(int i = 0; i < StaticDataProvider.titule.size(); i++)
                                                    if(StaticDataProvider.titule.get(i).brProblema <= resenih)
                                                    {
                                                        trenutnaTitula = StaticDataProvider.titule.get(i);
                                                        if(i != StaticDataProvider.titule.size()-1) sledecaTitula = StaticDataProvider.titule.get(i+1);
                                                    }
                                                Glide.with(getActivity()).load(getResources().getIdentifier(trenutnaTitula.slika, "drawable", getActivity().getPackageName())).fitCenter().into(img);
                                                ((TextView) v.findViewById(R.id.txtTitula)).setText(trenutnaTitula.naziv);
                                                txtBrRes.setText(Integer.toString(resenih));

                                                if(sledecaTitula != null) {
                                                    txtBrDoSledece.setText(Integer.toString(sledecaTitula.brProblema - resenih));
                                                    ObjectAnimator anim = ObjectAnimator.ofInt(prog, "progress", 0, resenih * 100 / sledecaTitula.brProblema);
                                                    anim.setDuration(500);
                                                    anim.start();
                                                }
                                                else
                                                {
                                                    txtBrDoSledece.setText(R.string.home_maxTitula);
                                                }


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    String mesto = ((GlavniActivity)getActivity()).mesto;
                                    if(mesto != "") {
                                        obModel.obavestenja = null;
                                        mesto = Alati.Lat(mesto);
                                        Log.i("Pocetni fragment", "Trazim obavestenja za "+mesto);
                                        MutableLiveData<List<Obavestenje>> ob = obModel.dajObavestenja(idToken, mesto, sluzbe);
                                        ob.observe(getActivity(), new Observer<List<Obavestenje>>() {
                                            @Override
                                            public void onChanged(@Nullable List<Obavestenje> obavestenjes) {
                                                SQLiteObavestenja mDbHelper = new SQLiteObavestenja(getContext());
                                                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                                                String[] projection = {"id", "obrisano", "notif"};
                                                Cursor cursor = db.query("obavestenja", projection, null, null, null, null, null);
                                                List<Long> idS = new ArrayList<>();
                                                List<Long> obrisanoS = new ArrayList<>();
                                                List<Long> notifS = new ArrayList<>();
                                                while(cursor.moveToNext()) {
                                                    long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                                                    long ob = cursor.getLong(cursor.getColumnIndexOrThrow("obrisano"));
                                                    long notif = cursor.getLong(cursor.getColumnIndexOrThrow("notif"));
                                                    idS.add(id);
                                                    obrisanoS.add(ob);
                                                    notifS.add(notif);
                                                }
                                                cursor.close();

                                                for(long id : idS)
                                                    Log.i("OB", Long.toString(id));

                                                List<Obavestenje> zaBrisanje = new ArrayList<>();

                                                for(Obavestenje o : obavestenjes)
                                                {
                                                    int ind = idS.indexOf((long)o.id);
                                                    if(ind == -1) {
                                                        ContentValues values = new ContentValues();
                                                        values.put("id", (long)o.id);
                                                        values.put("obrisano", 0);
                                                        values.put("notif", 1);
                                                        db.insert("obavestenja", null, values);
                                                    }
                                                    else
                                                    {
                                                        if(obrisanoS.get(ind) == 1)
                                                            zaBrisanje.add(o);
                                                        idS.remove(ind);
                                                        obrisanoS.remove(ind);
                                                        notifS.remove(ind);
                                                    }
                                                }

                                                for(Obavestenje o : zaBrisanje)
                                                    obavestenjes.remove(o);

                                                for(long id : idS) { // zaostala od pre
                                                    String[] args = { Long.toString(id) };
                                                    db.delete("obavestenja", "id = ?", args);
                                                }

                                                final ObavestenjaAdapter adapter = new ObavestenjaAdapter(obavestenjes);
                                                recObavestenja.setAdapter(adapter);

                                                db.close();
                                            }
                                        });
                                    }
                                    else
                                        Toast.makeText(getActivity(), R.string.glavniAc_nemaLokacije, Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    }
                });


    }

    public class ObavestenjaAdapter
            extends RecyclerView.Adapter<ObavestenjaAdapter.ObavestenjaHolder> {

        private List<Obavestenje> mValues;

        public ObavestenjaAdapter(List<Obavestenje> items) {
            mValues = items;
        }

        @Override
        public ObavestenjaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.obavestenje_content, parent, false);
            return new ObavestenjaHolder(view);
        }

        public void addItems(List<Obavestenje> obs) {
            this.mValues = obs;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final ObavestenjaHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.txtNaslov.setText(holder.mItem.naslov);
            holder.txtTekst.setText(holder.mItem.tekst);
            holder.txtSluzba.setText(holder.mItem.sluzba.naziv);

            holder.btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SQLiteObavestenja mDbHelper = new SQLiteObavestenja(getContext());
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();

                    String[] args = { Integer.toString(holder.mItem.id) };
                    db.delete("obavestenja", "id = ?", args);

                    ContentValues values = new ContentValues();
                    values.put("id", holder.mItem.id);
                    values.put("obrisano", 1);
                    values.put("notif", 1);
                    db.insert("obavestenja", null, values);

                    db.close();

                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ObavestenjaHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public Obavestenje mItem;
            public final TextView txtNaslov;
            public final TextView txtTekst;
            public final TextView txtSluzba;
            public final ImageButton btnDismiss;


            public ObavestenjaHolder(View view) {
                super(view);
                mView = view;
                txtNaslov = (TextView) view.findViewById(R.id.obNaslov);
                txtTekst = (TextView) view.findViewById(R.id.obTekst);
                txtSluzba = (TextView) view.findViewById(R.id.obSluzba);
                btnDismiss = (ImageButton) view.findViewById(R.id.btnDismiss);
            }

        }
    }


}

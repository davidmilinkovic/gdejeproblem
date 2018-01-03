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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class HoumtaunFragment extends DialogFragment {







    public static HoumtaunFragment newInstance(String param1, String param2) {
        return new HoumtaunFragment();
    }


    // neki bag je prisutan, rekli da mora ovako
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

        View v = inflater.inflate(R.layout.houmtaun_dialog, null);

        RecyclerView recyclerView = v.findViewById(R.id.opstine_risajkl);

        assert recyclerView != null;
        final OpstineAdapter adapter = new OpstineAdapter(StaticDataProvider.opstine);
        LinearLayoutManager mng = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mng);
        recyclerView.setAdapter(adapter);
        EditText search = (EditText)v.findViewById(R.id.txtOpstinePretraga);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String tekst = search.getText().toString();
                if(tekst.isEmpty())
                {
                    OpstineAdapter adapterr = new OpstineAdapter(StaticDataProvider.opstine);
                    recyclerView.setAdapter(adapterr);
                }
                else {
                    List<String> opstine = StaticDataProvider.opstine;
                    List<String> nova = new ArrayList<String>();
                    for(int j = 0; j < opstine.size(); j++)
                    {
                        String s = opstine.get(j);
                        if(Pattern.compile(Pattern.quote(tekst), Pattern.CASE_INSENSITIVE).matcher(s).find() || tekst.isEmpty())
                            nova.add(s);
                    }
                    OpstineAdapter adapterr = new OpstineAdapter(nova);
                    recyclerView.setAdapter(adapterr);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mng.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        builder.setView(v)
                .setTitle("Rodna gruda")
                .setNegativeButton("Zatvori", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HoumtaunFragment.this.getDialog().cancel();
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
    }

    public class OpstineAdapter
            extends RecyclerView.Adapter<OpstineAdapter.OpstinaHolder> {

        private List<String> mValues;

        public OpstineAdapter(List<String> items) {
            mValues = items;
        }



        @Override
        public OpstineAdapter.OpstinaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opstina_list_content, parent, false);
            return new OpstineAdapter.OpstinaHolder(view);
        }

        public void addItems(List<String> opstine) {
            this.mValues = opstine;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final OpstineAdapter.OpstinaHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.chk.setText(holder.mItem);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HoumtaunFragment.this.getDialog().cancel();
                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class OpstinaHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public String mItem;
            public final TextView chk;


            public OpstinaHolder(View view) {
                super(view);
                mView = view;
                chk = (TextView) view.findViewById(R.id.txtBoxOpstina);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + chk.getText() + "'";
            }
        }
    }
}

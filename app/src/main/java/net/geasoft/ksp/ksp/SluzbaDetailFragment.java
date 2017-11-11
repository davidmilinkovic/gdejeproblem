package net.geasoft.ksp.ksp;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.net.io.ToNetASCIIInputStream;

import java.util.List;

/**
 * A fragment representing a single Sluzba detail screen.
 * This fragment is either contained in a {@link SluzbaListActivity}
 * in two-pane mode (on tablets) or a {@link SluzbaDetailActivity}
 * on handsets.
 */
public class SluzbaDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private SluzbaViewModel.Sluzba mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SluzbaDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            final int id = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            SluzbaViewModel model = ViewModelProviders.of(this).get(SluzbaViewModel.class);
            model.dajSluzbe().observe(getActivity(), new Observer<List<SluzbaViewModel.Sluzba>>() {
                @Override
                public void onChanged(@Nullable List<SluzbaViewModel.Sluzba> sluzbe) {
                    for (SluzbaViewModel.Sluzba s : sluzbe) {
                        if(s.id == id) {
                            mItem = s;
                            ((TextView) getActivity().findViewById(R.id.sluzba_detail)).setText(mItem.naziv);
                        }
                    }
                }
            });
        }
        /*
                OVAKO SE VRACA ODGOVOR
                Intent intent=new Intent();
                intent.putExtra("poruka", "hehe");
                getActivity().setResult(69, intent);
                getActivity().finish();
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sluzba_detail, container, false);

        return rootView;
    }
}

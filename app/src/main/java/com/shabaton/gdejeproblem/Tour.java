package com.shabaton.gdejeproblem;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class Tour extends AppCompatActivity {

    int str = 0; // br. trenutne stranice

    private ViewPager mViewPager;

    private ImageButton btnSledeci;
    private Button btnPreskoci;
    private Button btnKraj;


    private ImageView[] indikatori;

    public static String[] naslovi = {"Ovo je prvi naslov", "A ovo je drugi", "Poslednji, treci"};
    public static String[] opisi = {"Hehe", "Sus", "Haaaaaaaaaaaaj"};
    public static String[] boje = {"#283593", "#c62828", "#2e7d32"};
    public static int[] slike = {R.drawable.common_google_signin_btn_icon_dark_pressed, R.drawable.nemaslike, R.drawable.gloglo};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        btnSledeci = (ImageButton) findViewById(R.id.intro_btn_next);
        btnPreskoci = (Button) findViewById(R.id.intro_btn_skip);
        btnKraj = (Button) findViewById(R.id.intro_btn_finish);

        // indikatori
        ImageView ind0 = (ImageView) findViewById(R.id.intro_indicator_0);
        ImageView ind1 = (ImageView) findViewById(R.id.intro_indicator_1);
        ImageView ind2 = (ImageView) findViewById(R.id.intro_indicator_2);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        CoordinatorLayout mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);

        indikatori = new ImageView[]{ind0, ind1, ind2};

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(str);
        updateIndicators(str);

        final ArgbEvaluator evaluator = new ArgbEvaluator();


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, Color.parseColor(boje[position]), Color.parseColor(boje[position == 2 ? position : position + 1]));
                mViewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {

                str = position;

                updateIndicators(str);

                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(Color.parseColor(boje[0]));
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(Color.parseColor(boje[0]));
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(Color.parseColor(boje[0]));
                        break;
                }
                btnSledeci.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                btnKraj.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnSledeci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str += 1;
                mViewPager.setCurrentItem(str, true);
            }
        });

        btnPreskoci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        btnKraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Alati.promeniPref(Tour.this, GlavniActivity.PREF_USER_FIRST_TIME, "false");
            }
        });



    }

    void updateIndicators(int position) {
        for (int i = 0; i < indikatori.length; i++) {
            indikatori[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";


        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tour, container, false);
            int index = getArguments().getInt(ARG_SECTION_NUMBER)-1;
            TextView txtNaslov = (TextView) rootView.findViewById(R.id.naslov_tour);
            TextView txtOpis = (TextView) rootView.findViewById(R.id.opis_tour);
            ImageView img = (ImageView) rootView.findViewById(R.id.slika_tour);
            txtNaslov.setText(naslovi[index]);
            txtOpis.setText(opisi[index]);
            Glide.with(this).load(slike[index]).fitCenter().into(img);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return opisi.length;
        }
    }
}

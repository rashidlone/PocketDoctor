package com.rashedlone.pocketdoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
  Created by Raashidlone on 31-10-2018.
 */

public class StartActivity extends AppCompatActivity {


    private DatabaseReference banner, interstitial,fbbanner;
    private SharedPreferences sp;

    @Override protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.start_main);

        ProgressBar pb = findViewById(R.id.progress);
        TextView tv = findViewById(R.id.main_name);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        banner = database.getReference("banner");
        interstitial = database.getReference("interstitial");
        fbbanner = database.getReference("fbbanner");

        View adContainer = findViewById(R.id.adMobView);

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(sp.getString("banner",""));
        ((RelativeLayout)adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        String text = "<font color='#AC0B1F'>Pocket </font><font color='#6B83C1'>Doctor</font>";
        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/rashed.ttf");
        tv.setTypeface(face);
        tv.setText(Html.fromHtml(text));

        pb.bringToFront();




    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            final Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
            mainIntent.putExtra("ads", "yes");
            startActivity(mainIntent);
            finish();

        }
    }, 5000);

        banner();
        fbbanner();
        interstitial();

    }


    public void fbbanner() {

        fbbanner.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    SharedPreferences.Editor ed = sp.edit();

                    ed.putString("fbbanner", ""+val);
                    ed.apply();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void banner() {

        banner.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    SharedPreferences.Editor ed = sp.edit();

                    ed.putString("banner", ""+val);
                    ed.apply();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void interstitial() {

        interstitial.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String val = dataSnapshot.getValue(String.class);

                    SharedPreferences.Editor ed = sp.edit();

                    ed.putString("interstitial", ""+val);
                    ed.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





}

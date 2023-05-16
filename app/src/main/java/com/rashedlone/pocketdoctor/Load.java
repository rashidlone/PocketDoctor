package com.rashedlone.pocketdoctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Raashidlone on 19-05-2018.
 */

class Load {


    private InterstitialAd mInterstitialAd;
    private Context context;
    private SharedPreferences sp;



    Load(Context curr)
    {

        this.context = curr;

    }

    void showAds() {

        sp = PreferenceManager.getDefaultSharedPreferences(context);


        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(sp.getString("interstitial",""));
        // Toast.makeText(context,sp.getString("interstitial",""),Toast.LENGTH_LONG).show();
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
             Log.i("Ads", "onAdLoaded");
            mInterstitialAd.show();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Code to be executed when an ad request fails.
            Log.i("Ads", "onFailedToLoad");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

        }

        @Override
        public void onAdOpened() {
            // Code to be executed when the ad is displayed.
            Log.i("Ads", "onAdOpened");
        }

        @Override
        public void onAdLeftApplication() {
            // Code to be executed when the user has left the app.
            Log.i("Ads", "onAdLeftApplication");

        }

        @Override
        public void onAdClosed() {
            // Code to be executed when when the interstitial ad is closed.
            Log.i("Ads", "onAdClosed");


        }
    });

}
}

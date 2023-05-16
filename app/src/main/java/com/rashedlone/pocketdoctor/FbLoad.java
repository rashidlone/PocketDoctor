package com.rashedlone.pocketdoctor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import static com.facebook.ads.AudienceNetworkAds.TAG;

/**
 * Created by Raashidlone on 19-05-2018.
 */

class FbLoad {


    private InterstitialAd interstitialAd;
    private Context context;
    private SharedPreferences sp;


    FbLoad(Context curr)
    {

        this.context = curr;
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        interstitialAd = new InterstitialAd(context, sp.getString("fbbanner",""));
        interstitialAd.loadAd();

    }

    void showAds() {


        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                 // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown








/*
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(sp.getString("full_banner",""));
       // Toast.makeText(context,sp.getString("full_banner",""),Toast.LENGTH_LONG).show();
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
        });*/

    }

  void showt(String s){

    Toast.makeText(context,s,Toast.LENGTH_SHORT).show();


}

        }


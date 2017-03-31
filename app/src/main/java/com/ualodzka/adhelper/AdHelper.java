package com.ualodzka.adhelper;

import android.content.Context;
import android.util.Log;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by Sam on 30.03.2017.
 */

public class AdHelper {

    private InterstitialAd mInterstitialAd;
    private Context context;
    private boolean mAlreadyShowed = false;
    private long firstTimeInterval, secondTimeInterval,  startTime, lastShowTime;
    private String testDevice;

    public AdHelper(Context context) {
        this.context = context;
        mInterstitialAd = new InterstitialAd(context);
        startTime = System.currentTimeMillis();
    }

    AdHelper setAdUnitId(String id) {
        mInterstitialAd.setAdUnitId(id);
        return this;
    }

    AdHelper setAdListener() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
//                Log.i(LOG_TAG, "AdLoaded ");
            }
        });
        return this;
    }

    AdHelper setFirstTimeInterval(long millis) {
        firstTimeInterval = millis;
        return this;
    }

    AdHelper setSecondTimeInterval(long millis) {
        secondTimeInterval = millis;
        return this;
    }

    AdHelper addTestDevice(String testDeviceString) {
        testDevice = testDeviceString;
        return this;
    }

    private void requestNewInterstitial() {
        if (!mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(testDevice)
                    .build();

            mInterstitialAd.loadAd(adRequest);
        }
    }

    public void tryToShowAd() {
        long  currentTime = System.currentTimeMillis();
        if (!mAlreadyShowed) {
            if (currentTime - startTime >= firstTimeInterval) {
                mInterstitialAd.show();
                mAlreadyShowed = true;
                lastShowTime = System.currentTimeMillis();
            }
        } else {
            if (currentTime - lastShowTime >= secondTimeInterval) {
                mInterstitialAd.show();
                lastShowTime = System.currentTimeMillis();
            }
        }
        requestNewInterstitial();
    }

}

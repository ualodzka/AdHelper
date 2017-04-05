package com.ualodzka.adhelper;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by Vladimir Lezhnevich on 30.03.2017.
 */

public class AdHelper {

    private InterstitialAd interstitialAd;
    private Context context;
    private boolean isAlreadyShowed;
    private long firstTimeInterval, secondTimeInterval, startTime, lastShowTime;
    private String testDevice, adUnitId;
    private Builder builder;

    public AdHelper(final Context context, Builder builder) {
        this.context = context;
        this.builder = builder;
        startTime = System.currentTimeMillis();
        interstitialAd = builder.interstitialAd;
        testDevice = builder.testDevice;

        //if other onAdClosed implementation were not provided use default one
        if (builder.adListener != null) {
            interstitialAd.setAdListener(builder.adListener);
        } else {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });
        }

        //if time intervals were not provided set default ones
        if (builder.firstTimeInterval != 0) {
            firstTimeInterval = builder.firstTimeInterval;
        } else {
            firstTimeInterval = 25000;
        }

        if (builder.secondTimeInterval != 0) {
            secondTimeInterval = builder.secondTimeInterval;
        } else {
            secondTimeInterval = 60000;
        }
    }

    public static class Builder {

        private InterstitialAd interstitialAd;
        private final Context context;
        private long firstTimeInterval, secondTimeInterval;
        private String testDevice;
        private AdListener adListener;

        public Builder(Context context) {
            this.context = context;
            this.interstitialAd = new InterstitialAd(this.context);
        }

        public AdHelper build() {
            return new AdHelper(context, this);
        }

        Builder setAdUnitId(String id) {
            this.interstitialAd.setAdUnitId(id);
            return this;
        }

        Builder setAdListener(AdListener adListener) {
            this.adListener = adListener;
            return this;
        }

        Builder setFirstTimeInterval(long millis) {
            this.firstTimeInterval = millis;
            return this;
        }

        Builder setSecondTimeInterval(long millis) {
            this.secondTimeInterval = millis;
            return this;
        }

        Builder addTestDevice(String testDeviceString) {
            this.testDevice = testDeviceString;
            return this;
        }

    }


    private void requestNewInterstitial() {
        if (!interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(testDevice)
                    .build();

            interstitialAd.loadAd(adRequest);
        }
    }

    public void tryToShowAd() {
        long currentTime = System.currentTimeMillis();
        if (!isAlreadyShowed) {
            if (currentTime - startTime >= firstTimeInterval) {
                interstitialAd.show();
                isAlreadyShowed = true;
                lastShowTime = System.currentTimeMillis();
            }
        } else {
            if (currentTime - lastShowTime >= secondTimeInterval) {
                interstitialAd.show();
                lastShowTime = System.currentTimeMillis();
            }
        }
        requestNewInterstitial();
    }

}
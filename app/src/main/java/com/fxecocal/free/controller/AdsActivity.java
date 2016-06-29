package com.fxecocal.free.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.fxecocal.free.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.tapjoy.TJConnectListener;
import com.tapjoy.Tapjoy;

import java.util.Hashtable;

public class AdsActivity extends AppCompatActivity implements TJConnectListener {

    String TAG = "ADS Activity";
    Hashtable connectFlags = new Hashtable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        Tapjoy.connect(this.getApplicationContext(), "eUXHtSfCTcytkykgWIijxQECOjJNDkXoQo1y9xHIsRdQOEUywFl39uVgBrbX", connectFlags, this);

    }

    //session start
    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    //session end
    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }
    // called when Tapjoy connect call succeed
    @Override
    public void onConnectSuccess() {
        Log.d(TAG, "Tapjoy connect Succeeded");
    }
    // called when Tapjoy connect call failed
    @Override
    public void onConnectFailure() {
        Log.d(TAG, "Tapjoy connect Failed");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

package com.fxecocal.free.controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.fxecocal.free.R;
import com.fxecocal.free.Utility.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectCore;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyLog;

import java.util.Hashtable;

public class AdsActivity extends AppCompatActivity {

    String TAG = "ADS Activity";
    String app_Id = "2616c6fb-5f82-4fa7-a3a2-431f6c644027";
    String secret_Key =  "9tM10ACpFIfPdA7a60DC";
    Hashtable connectFlags = new Hashtable();
    private TJPlacement offerwallPlacement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        callShowOffers();

    }

//    /**
//     * Attempts to connect to Tapjoy
//     */
//    private void connectToTapjoy() {
//        // OPTIONAL: For custom startup flags.
//        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
//        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
//
//        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
//        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");
//
//        // Connect with the Tapjoy server.  Call this when the application first starts.
//        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
//        String tapjoySDKKey = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaD";
//
//        Tapjoy.setGcmSender("34027022155");
//
//        // NOTE: This is the only step required if you're an advertiser.
//        Tapjoy.connect(this, tapjoySDKKey, connectFlags, new TJConnectListener() {
//            @Override
//            public void onConnectSuccess() {
//                AdsActivity.this.onConnectSuccess();
//            }
//
//            @Override
//            public void onConnectFailure() {
//                AdsActivity.this.onConnectFailure();
//            }
//        });
//    }
//
//    //session start
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Tapjoy.onActivityStart(this);
//    }
//
//    //session end
//    @Override
//    protected void onStop() {
//        Tapjoy.onActivityStop(this);
//        super.onStop();
//    }
//    // called when Tapjoy connect call succeed
//    @Override
//    public void onConnectSuccess() {
//        Log.d(TAG, "Tapjoy connect Succeeded");
//    }
//    // called when Tapjoy connect call failed
//    @Override
//    public void onConnectFailure() {
//        Log.d(TAG, "Tapjoy connect Failed");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        callShowOffers();
//    }
    private void callShowOffers() {
        // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
        offerwallPlacement = new TJPlacement(this, "offerwall_unit", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                showToast("onRequestSuccess for placement " + placement.getName());

                if (!placement.isContentAvailable()) {
                    showToast("No Offerwall content available");
                }
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                showToast( "Offerwall error: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

                showToast( "Offerwall request success");
                placement.showContent();
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
            }
        });
        offerwallPlacement.requestContent();
    }
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showToast(AdsActivity.this, text);
            }
        });
    }
}

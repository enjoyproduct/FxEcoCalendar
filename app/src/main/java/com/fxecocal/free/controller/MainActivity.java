package com.fxecocal.free.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.CustomRequest;
import com.android.volley.toolbox.Volley;
import com.fxecocal.free.R;
import com.fxecocal.free.Utility.LocaleHelper;
import com.fxecocal.free.Utility.Utils;
import com.fxecocal.free.controller.fragment.MainCalendarFragment;
import com.fxecocal.free.controller.fragment.MenuFragment;
import com.fxecocal.free.controller.push.GetNotificationRegID;
import com.fxecocal.free.model.API;
import com.fxecocal.free.model.Const;
import com.fxecocal.free.model.SelectionModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements TJConnectListener, AdapterView.OnItemSelectedListener {

    String TAG = "MainActivity";

    private Toolbar toolbar;
    public static DrawerLayout mDrawerLayout;
    private static FragmentManager fragmentManager;
    private static MenuFragment menuFragment;
    private static TextView tvTitle;
    private static Activity mActivity;
    private static int currentFragmentNum;

    private Timer timer;
    private TimerTask timerTask;

    public String language;
    public int notificationTime, adsPeriod;
    public Set<String> symbols, impacts;
    public boolean localTime, showAds;


    private PublisherInterstitialAd mPublisherInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.fxecocal.free.R.layout.activity_main);


        String language = Utils.getFromPreference(this, Const.LANGUAGE_CODE);
       if (language.length() == 0 ) {
           language = "en";
       }
        LocaleHelper.onCreate(this, language);

        if (Utils.getFromPreference(this, Const.DEVICE_TOKEN).length() == 0) {
            GetNotificationRegID getNotificationRegID = new GetNotificationRegID(this);
            getNotificationRegID.registerInBackground();
        }

        if (Utils.getFromPreference(this, Const.DEVICE_ID).length() == 0) {
            String deviceID = ((TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            Utils.saveToPreference(this, Const.DEVICE_ID, deviceID);
        }

        initVariables();

        initUI();
        //////////////////
        int count = Utils.getIntFromPreference(this, "loadNum");
        if (count == 0) {
            setShowAds();
        } else {
            count = 1;
            navigateToMainCalendar();
            initTimer();
        }
        count++;
        Utils.saveIntToPreference(this, "loadNum", count);
        /////////////////////////////////////
//        signin();
        connectToTapjoy();
    }
    private void initVariables() {
        fragmentManager = getSupportFragmentManager();
        mActivity = this;
        adsPeriod = 60;
        //interstitial ads
        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mPublisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();

        initSettings();
    }
    private void requestNewInterstitial() {

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
//                .addTestDevice("6A9AC51F285D17A969017B112BA62D0E")
                .build();

        mPublisherInterstitialAd.loadAd(adRequest);
    }
    public void initSettings() {
        ///
        language = Utils.getFromPreference(this, Const.LANGUAGE);
        if (language.length() == 0) {
            Utils.saveToPreference(this, Const.LANGUAGE, getResources().getString(R.string.English));
            language = getResources().getString(R.string.English);
        }
        ////
        notificationTime = Utils.getIntFromPreference(this, Const.NOTIFICATION);
        if (notificationTime == 0) {
            Utils.saveIntToPreference(this, Const.NOTIFICATION, 5);
            notificationTime = 5;
        }
        ////
        localTime = Utils.getBooleanFromPreference(this, Const.LOCAL_TIME);
        ///
        showAds = Utils.getBooleanFromPreference(this, Const.SHOW_ADS);
        /////
        symbols = Utils.getArrayFromPreference(this, Const.SYMBOLS);
        if (symbols == null) {
            Set<String> set = new HashSet<String>();
            String[] symbol = getResources().getStringArray(R.array.symbols);
            for (int i = 0; i < symbol.length; i ++) {
                set.addAll(Arrays.asList(symbol));
            }
            Utils.saveArrayToPreference(this, Const.SYMBOLS, set);
            symbols = set;
        }
        ////
        impacts = Utils.getArrayFromPreference(this, Const.IMPACTS);
        if (impacts == null) {
            Set<String> set = new HashSet<String>();
            String[] impact = getResources().getStringArray(R.array.impacts);
            for (int i = 0; i < impact.length; i ++) {
                set.addAll(Arrays.asList(impact));
            }
            Utils.saveArrayToPreference(this, Const.IMPACTS, set);
            impacts = set;
        }
    }
    int adsTime = 0;
    private void initTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                adsTime ++;
                if (adsTime == adsPeriod) {

                    Random r = new Random();
                    adsPeriod = r.nextInt(300 - 60) + 60; // 60s ~ 300s

                    adsTime = 0;

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPublisherInterstitialAd.isLoaded()) {
                                if (Utils.getBooleanFromPreference(mActivity, Const.SHOW_ADS)) {
                                    mPublisherInterstitialAd.show();
                                }
                            }

                        }
                    });
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainCalendarFragment.updateCalendar();
                    }
                });


            }
        };
        timer.schedule(timerTask, 10000, 1000);
    }
    private void initUI() {
        toolbar = (Toolbar) findViewById(com.fxecocal.free.R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton ibMenu = (ImageButton)toolbar.findViewById(R.id.ib_menu);
        ibMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        ImageButton ibSearch = (ImageButton)toolbar.findViewById(R.id.ib_search);
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        tvTitle = (TextView) findViewById(R.id.tv_page_title);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        menuFragment = new MenuFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_menu_container, menuFragment)
                .commit();
    }
    private void navigateToMainCalendar() {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new MainCalendarFragment())
                .commit();
    }
    private void setShowAds() {

        View view = getLayoutInflater().inflate(R.layout.ads_dialog, null);
        final Switch aSwitch = (Switch)view.findViewById(R.id.sw_dlg);

        if (Utils.getBooleanFromPreference(mActivity, Const.SHOW_ADS)) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }
        Button btnSet = (Button)view.findViewById(R.id.btn_dlg_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aSwitch.isChecked()) {
                    Utils.saveBooleanToPreference(mActivity, Const.SHOW_ADS, true);
                } else {
                    Utils.saveBooleanToPreference(mActivity, Const.SHOW_ADS, false);
                }
                navigateToMainCalendar();
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button)view.findViewById(R.id.btn_dlg_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigateToMainCalendar();
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(view)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                initTimer();
            }
        });

    }
    public void navigate(int num) {
        currentFragmentNum = num;
        switch (num) {

            case 0:
               inviteFriends();
                break;
            case 1:
                showFeedbackDlg();
                break;
            case 2:
                rateUs();
                break;
            case 3:
                more();
                break;
            case 4:
                setNotificationTime();
                break;
            case 5:
                setSymbol();
                break;
            case 6:
                setImpact();
                break;
            case 7:
                setLanguage();
                break;
        }
        mDrawerLayout.closeDrawers();
    }
    private void inviteFriends() {
        String shareBody = "Forex and Economic Calendar is free app." +
                "Please download from Google Play Store and enjoy!" +
                " https://play.google.com/store/apps/details?id=************************";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Forex and Economic Calendar");

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void rateUs() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }
    private void more() {
//        startActivity(new Intent(this, AdsActivity.class));
        callShowOffers();
    }
    Dialog dialog;
    ArrayList<SelectionModel> arrSelectionModel;
    private void setNotificationTime() {
        arrSelectionModel = new ArrayList<>();
        final String[] strTimes = getResources().getStringArray(R.array.notification_times);
        for (int i = 0; i < strTimes.length; i ++) {
            SelectionModel selectionModel = new SelectionModel();
            selectionModel.setTitle(strTimes[i] + " min");
            selectionModel.setIsSelected(false);
            if (notificationTime == Integer.parseInt(strTimes[i])) selectionModel.setIsSelected(true);
            selectionModel.setType(getResources().getString(R.string.Notificatoin));
            arrSelectionModel.add(selectionModel);
        }
        View view = getLayoutInflater().inflate(R.layout.select_dialog, null);

        ListView listView = (ListView)view.findViewById(R.id.lv_dlg);
        final SingleChoiceAdapter singleChoiceAdapter = new SingleChoiceAdapter(arrSelectionModel);
        listView.setAdapter(singleChoiceAdapter);

        TextView tvTitle = (TextView)view.findViewById(R.id.tv_dlg_title);
        tvTitle.setText(getResources().getString(R.string.Notificatoin));

        TextView tvDescription = (TextView)view.findViewById(R.id.tv_dlg_descrption);
        tvDescription.setText(getResources().getString(R.string.notification_select_description));

        Button btnSet = (Button)view.findViewById(R.id.btn_dlg_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrSelectionModel.size(); i++) {
                    if (arrSelectionModel.get(i).isSelected()) {
                        Utils.saveIntToPreference(mActivity, Const.NOTIFICATION, Integer.parseInt(strTimes[i]));
                        initSettings();
                        break;
                    }
                }
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button)view.findViewById(R.id.btn_dlg_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(view)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void setLanguage() {

        String[] languages = getResources().getStringArray(R.array.languages);
        arrSelectionModel = new ArrayList<>();
        for (int i = 0; i < languages.length; i ++) {
            SelectionModel selectionModel = new SelectionModel();
            selectionModel.setTitle(languages[i]);
            selectionModel.setIsSelected(false);
            if (language.equals(languages[i]) ) selectionModel.setIsSelected(true);
            selectionModel.setType(getResources().getString(R.string.Language));
            arrSelectionModel.add(selectionModel);
        }
        View view = getLayoutInflater().inflate(R.layout.select_dialog, null);

        ListView listView = (ListView)view.findViewById(R.id.lv_dlg);
        final SingleChoiceAdapter singleChoiceAdapter = new SingleChoiceAdapter(arrSelectionModel);
        listView.setAdapter(singleChoiceAdapter);

        TextView textView = (TextView)view.findViewById(R.id.tv_dlg_title);
        textView.setText(getResources().getString(R.string.Language));



        Button btnSet = (Button)view.findViewById(R.id.btn_dlg_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < arrSelectionModel.size(); i++) {
                    if (arrSelectionModel.get(i).isSelected()) {
                        Utils.saveToPreference(mActivity, Const.LANGUAGE, arrSelectionModel.get(i).getTitle());
                        initSettings();
                        if (arrSelectionModel.get(i).getTitle().equals("English")) {
                            LocaleHelper.setLocale(mActivity, "en");
                            Utils.saveToPreference(mActivity, Const.LANGUAGE_CODE, "en");

                        } else if (arrSelectionModel.get(i).getTitle().equals("Chinese")) {
                            LocaleHelper.setLocale(mActivity, "cn");
                            Utils.saveToPreference(mActivity, Const.LANGUAGE_CODE, "cn");
                        } else if (arrSelectionModel.get(i).getTitle().equals("Japanese")) {
                            LocaleHelper.setLocale(mActivity, "jp");
                            Utils.saveToPreference(mActivity, Const.LANGUAGE_CODE, "jp");
                        } else if (arrSelectionModel.get(i).getTitle().equals("Persian")) {
                            LocaleHelper.setLocale(mActivity, "ar");
                            Utils.saveToPreference(mActivity, Const.LANGUAGE_CODE, "ar");
                        }
                        startActivity(new Intent(mActivity, MainActivity.class));
                        finish();
                        break;
                    }
                }
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button)view.findViewById(R.id.btn_dlg_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(view)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private void setSymbol() {
        String[] symbols = getResources().getStringArray(R.array.symbols);
        arrSelectionModel = new ArrayList<>();
        for (int i = 0; i < symbols.length; i ++) {
            SelectionModel selectionModel = new SelectionModel();
            selectionModel.setTitle(symbols[i]);
            selectionModel.setIsSelected(false);
            for (String strSymbol : this.symbols) {
                if (strSymbol.equals(symbols[i])) {
                    selectionModel.setIsSelected(true);
                }
            }

            selectionModel.setType(getResources().getString(R.string.Symbols));
            arrSelectionModel.add(selectionModel);
        }
        View view = getLayoutInflater().inflate(R.layout.select_dialog, null);

        ListView listView = (ListView)view.findViewById(R.id.lv_dlg);
        final MultiChoiceAdapter multiChoiceAdapter = new MultiChoiceAdapter(arrSelectionModel);
        listView.setAdapter(multiChoiceAdapter);

        TextView textView = (TextView)view.findViewById(R.id.tv_dlg_title);
        textView.setText(getResources().getString(R.string.Impact));

        TextView tvDescription = (TextView)view.findViewById(R.id.tv_dlg_descrption);
        tvDescription.setText(getResources().getString(R.string.symbol_select_discription));

        Button btnSet = (Button)view.findViewById(R.id.btn_dlg_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList = new ArrayList<String>();
                for (int i = 0; i < arrSelectionModel.size(); i ++) {
                    if (arrSelectionModel.get(i).isSelected()) {
                        arrayList.add(arrSelectionModel.get(i).getTitle());
                    }
                }
                Set<String> set = new HashSet<String>();
                set.addAll(arrayList);
                Utils.saveArrayToPreference(mActivity, Const.SYMBOLS, set);
                initSettings();
                MainCalendarFragment.loadData();
                dialog.dismiss();
            }
        });
        Button btnCancel = (Button)view.findViewById(R.id.btn_dlg_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.cb_all);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (SelectionModel selectionModel : arrSelectionModel) {
                        selectionModel.setIsSelected(true);
                    }
                } else {
                    for (SelectionModel selectionModel : arrSelectionModel) {
                        selectionModel.setIsSelected(false);
                    }
                }
                multiChoiceAdapter.notifyDataSetChanged();
            }
        });
        dialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(view)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private void setImpact() {
        String[] impacts = getResources().getStringArray(R.array.impacts);
        arrSelectionModel = new ArrayList<>();
        for (int i = 0; i < impacts.length; i ++) {
            SelectionModel selectionModel = new SelectionModel();
            selectionModel.setTitle(impacts[i]);
            selectionModel.setIsSelected(false);
            for (String strImpact : this.impacts) {
                if (strImpact.equals(impacts[i])) {
                    selectionModel.setIsSelected(true);
                }
            }
            selectionModel.setType(getResources().getString(R.string.Impact));
            arrSelectionModel.add(selectionModel);
        }
        View view = getLayoutInflater().inflate(R.layout.select_dialog, null);

        ListView listView = (ListView)view.findViewById(R.id.lv_dlg);
        final MultiChoiceAdapter multiChoiceAdapter = new MultiChoiceAdapter(arrSelectionModel);
        listView.setAdapter(multiChoiceAdapter);

        TextView textView = (TextView)view.findViewById(R.id.tv_dlg_title);
        textView.setText(getResources().getString(R.string.Impact));

        TextView tvDescription = (TextView)view.findViewById(R.id.tv_dlg_descrption);
        tvDescription.setText(getResources().getString(R.string.impact_select_discription));

        Button btnSet = (Button)view.findViewById(R.id.btn_dlg_set);
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arrayList = new ArrayList<String>();
                for (int i = 0; i < arrSelectionModel.size(); i ++) {
                    if (arrSelectionModel.get(i).isSelected()) {
                        arrayList.add(arrSelectionModel.get(i).getTitle());
                    }
                }
                Set<String> set = new HashSet<String>();
                set.addAll(arrayList);
                Utils.saveArrayToPreference(mActivity, Const.IMPACTS, set);
                initSettings();
                MainCalendarFragment.loadData();

                dialog.dismiss();
            }
        });
        Button btnCancel = (Button)view.findViewById(R.id.btn_dlg_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CheckBox checkBox = (CheckBox)view.findViewById(R.id.cb_all);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (SelectionModel selectionModel : arrSelectionModel) {
                        selectionModel.setIsSelected(true);
                    }
                } else {
                    for (SelectionModel selectionModel : arrSelectionModel) {
                        selectionModel.setIsSelected(false);
                    }
                }
                multiChoiceAdapter.notifyDataSetChanged();
            }
        });

        dialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(view)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }



    class SingleChoiceAdapter extends BaseAdapter {

        List<SelectionModel> arrayList;
        public SingleChoiceAdapter(List<SelectionModel> arrayList) {
            this.arrayList = arrayList;
        }
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_single_choice, null);
            }
            RadioButton radioButton = (RadioButton)view.findViewById(R.id.radio);
            ImageView ivFlag = (ImageView)view.findViewById(R.id.iv_flag);

            radioButton.setText(arrayList.get(position).getTitle());
            if (arrayList.get(position).getType().equals(getResources().getString(R.string.Notificatoin))) {
                ivFlag.setVisibility(View.GONE);
            } else {
                ivFlag.setVisibility(View.VISIBLE);
                if (arrayList.get(position).getTitle().equals(getResources().getString(R.string.English))) {
                    ivFlag.setBackgroundDrawable(getResources().getDrawable(R.drawable.usd));
                } else if (arrayList.get(position).getTitle().equals(getResources().getString(R.string.Chinese))) {
                    ivFlag.setBackgroundDrawable(getResources().getDrawable(R.drawable.cny));
                } else if (arrayList.get(position).getTitle().equals(getResources().getString(R.string.Japanese))) {
                    ivFlag.setBackgroundDrawable(getResources().getDrawable(R.drawable.jpy));
                }
            }
            if (arrayList.get(position).isSelected()) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < arrSelectionModel.size(); i ++) {
                        boolean flag = false;
                        if (i == position) {
                            if (!arrSelectionModel.get(position).isSelected()) {
                                flag = true;
                            }
                        }
                        arrSelectionModel.get(i).setIsSelected(flag);
                    }
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }
    class MultiChoiceAdapter extends BaseAdapter {

        List<SelectionModel> arrayList;
        public MultiChoiceAdapter(List<SelectionModel> arrayList) {
            this.arrayList = arrayList;
        }
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_mulit_choice, null);
            }
            CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkbox);
            TextView textView = (TextView)view.findViewById(R.id.tv_title);

            textView.setText(arrayList.get(position).getTitle());
            if (arrayList.get(position).isSelected()) {
                checkBox.setChecked(true);
            }else {
                checkBox.setChecked(false);
            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (arrayList.get(position).isSelected()) {
                        arrayList.get(position).setIsSelected(false);
                    } else {
                        arrayList.get(position).setIsSelected(true);
                    }
                }
            });
            return view;
        }
    }












    private void signin() {
        Utils.showProgress(mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put(Const.DEVICE_TYPE, Const.ANDROID);
        params.put(Const.DEVICE_TOKEN, Utils.getFromPreference(mActivity, Const.DEVICE_TOKEN));
        params.put(Const.DEVICE_ID, Utils.getFromPreference(mActivity, Const.DEVICE_ID));

        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.SIGN_IN, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                JSONObject jsonObject = response.getJSONObject("data");

                                String user_id = jsonObject.getString("user_id");


                            } else  if (status.equals("401")) {
//                                Utils.showOKDialog(mActivity, getResources().getString(R.string.email_unregistered));
                            } else if (status.equals("402")) {
//                                Utils.showOKDialog(mActivity, getResources().getString(R.string.incorrect_password));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(signinRequest);

    }







    public void showFeedbackDlg() {
        View view = getLayoutInflater().inflate(R.layout.feedback_dialog, null);

        final EditText etName = (EditText)view.findViewById(R.id.et_name);
        final EditText etEmail = (EditText)view.findViewById(R.id.et_email);
        final EditText etFeedback = (EditText)view.findViewById(R.id.et_feedback);


        Button btnSend = (Button)view.findViewById(R.id.btn_dlg_send);

        Button btnCancel = (Button)view.findViewById(R.id.btn_dlg_cancel);


        // Spinner element
        final Spinner spinner = (Spinner) view.findViewById(R.id.sp_type);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        // Spinner Drop down elements
        String[] types = getResources().getStringArray(R.array.type);
        List<String> categories = Arrays.asList(types);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String feedback = etFeedback.getText().toString();
                String email = etEmail.getText().toString();
                String type = spinner.getSelectedItem().toString();
                if (!Utils.isEmailValid(email)) {
                    Utils.showToast(mActivity, getResources().getString(R.string.Invalid_Email));
                    return;
                }
                if (name.length() > 0 && feedback.length() > 0 && email.length() > 0 && type.length() > 0) {
//                    send_feedback(name, email, type, feedback);
                } else {
                    return;
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = new AlertDialog.Builder(mActivity)
                .setCancelable(false)
                .setView(view)
                .show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private void send_feedback(String name,String email,String type, String feedback) {
        Utils.showProgress(mActivity);

        Map<String, String> params = new HashMap<String, String>();
        params.put(Const.USER_ID, Utils.getFromPreference(mActivity, Const.USER_ID));
        params.put("display_name", name);
        params.put("feedback", feedback);
        params.put("email", email);
        params.put("type", type);

        CustomRequest signinRequest = new CustomRequest(Request.Method.POST, API.SEND_FEEDBACK, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.hideProgress();
                        try {
                            String status = response.getString("status");
                            if (status.equals("200")) {
                                Utils.showToast(mActivity, getResources().getString(R.string.Feedback_sent_successfully));

                            } else  if (status.equals("401")) {
//                                Utils.showOKDialog(mActivity, getResources().getString(R.string.email_unregistered));
                            } else if (status.equals("402")) {
//                                Utils.showOKDialog(mActivity, getResources().getString(R.string.incorrect_password));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.hideProgress();
                        Toast.makeText(mActivity, error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(signinRequest);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        switch (position) {
            case 0:

                break;
            case 1:

                break;
        }
        // Showing selected spinner item
//        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.fxecocal.free.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == com.fxecocal.pro.R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }











    /**
     * Attempts to connect to Tapjoy
     */
    private void connectToTapjoy() {
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");

        // Connect with the Tapjoy server.  Call this when the application first starts.
        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
        String tapjoySDKKey = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaD";

        Tapjoy.setGcmSender("34027022155");

        // NOTE: This is the only step required if you're an advertiser.
        Tapjoy.connect(this, tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                MainActivity.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                MainActivity.this.onConnectFailure();
            }
        });
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
    private TJPlacement offerwallPlacement;
    private void callShowOffers() {
        // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
        offerwallPlacement = new TJPlacement(this, "offerwall_unit", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
//                showToast("onRequestSuccess for placement " + placement.getName());

                if (!placement.isContentAvailable()) {
//                    showToast("No Offerwall content available");
                }
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {
                showToast( "Offerwall error: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

//                showToast( "Offerwall request success");
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
                Utils.showToast(MainActivity.this, text);
            }
        });
    }




}

package com.fxecocal.free.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.fxecocal.free.R;
import com.fxecocal.free.Utility.ExceptionHandler;
import com.fxecocal.free.Utility.LocaleHelper;
import com.fxecocal.free.Utility.Utils;
import com.fxecocal.free.controller.fragment.MainCalendarFragment;
import com.fxecocal.free.controller.fragment.MenuFragment;
import com.fxecocal.free.model.Const;
import com.fxecocal.free.model.SelectionModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

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
    public int notificationTime;
    public Set<String> symbols, impacts;
    public boolean localTime, showAds;


    private PublisherInterstitialAd mPublisherInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.fxecocal.free.R.layout.activity_main);

        ///set exception handler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        LocaleHelper.onCreate(this, "en");

        initVariables();

        initUI();
        setShowAds();
    }
    private void initVariables() {
        fragmentManager = getSupportFragmentManager();
        mActivity = this;

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
                .addTestDevice("6A9AC51F285D17A969017B112BA62D0E")
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
                if (adsTime == 300) {
                    adsTime = 0;
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mPublisherInterstitialAd.isLoaded()) {
//                                mPublisherInterstitialAd.show();
                            }
                            MainCalendarFragment.updateCalendar();
                        }
                    });
                }

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
               sendFeedback();
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
    private void sendFeedback() {

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
        startActivity(new Intent(this, AdsActivity.class));

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

        TextView textView = (TextView)view.findViewById(R.id.tv_dlg_title);
        textView.setText(getResources().getString(R.string.Notificatoin));

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
                        } else if (arrSelectionModel.get(i).getTitle().equals("Chinese")) {
                            LocaleHelper.setLocale(mActivity, "cn");
                        } else if (arrSelectionModel.get(i).getTitle().equals("Japanese")) {
                            LocaleHelper.setLocale(mActivity, "jp");
//                        } else if (arrSelectionModel.get(i).getTitle().equals("")) {

                        }
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
}

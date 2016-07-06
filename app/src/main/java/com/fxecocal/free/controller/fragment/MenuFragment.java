package com.fxecocal.free.controller.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fxecocal.free.R;
import com.fxecocal.free.Utility.Utils;
import com.fxecocal.free.controller.MainActivity;
import com.fxecocal.free.model.Const;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements View.OnClickListener{



    private LinearLayout llInviteFriend, llSendFeedback, llRateUs, llMore;
    private RelativeLayout rlNotification, rlSymbol, rlImpact, rlLanguage;
    private Switch swLocalTime,swAds;
    private TextView tvLanguage;
    private ImageView ivLanguage;

    private Activity mActivity;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_fragment, container, false);
        initVriables();
        initUI(view);

        return view;
    }

    private void initVriables() {
        mActivity = getActivity();
    }
    private void initUI(View view) {
        llInviteFriend  = (LinearLayout)view.findViewById(R.id.ll_menu_invite);
        llSendFeedback  = (LinearLayout)view.findViewById(R.id.ll_menu_send_feedback);
        llRateUs        = (LinearLayout)view.findViewById(R.id.ll_menu_rate_us);
        llMore        = (LinearLayout)view.findViewById(R.id.ll_menu_more);
        rlNotification  = (RelativeLayout)view.findViewById(R.id.rl_menu_notification);
        rlSymbol        = (RelativeLayout)view.findViewById(R.id.rl_menu_symbols);
        rlImpact        = (RelativeLayout)view.findViewById(R.id.rl_menu_impact);
        rlLanguage      = (RelativeLayout)view.findViewById(R.id.rl_menu_language);

        swLocalTime = (Switch)view.findViewById(R.id.sw_menu);
        swAds = (Switch)view.findViewById(R.id.sw_ads);

        tvLanguage = (TextView)view.findViewById(R.id.tv_menu_language);
        ivLanguage = (ImageView)view.findViewById(R.id.iv_menu_language);

        llInviteFriend.setOnClickListener(this);
        llSendFeedback.setOnClickListener(this);
        llRateUs      .setOnClickListener(this);
        llMore        .setOnClickListener(this);
        rlNotification.setOnClickListener(this);
        rlSymbol      .setOnClickListener(this);
        rlImpact      .setOnClickListener(this);
        rlLanguage    .setOnClickListener(this);

        if (Utils.getBooleanFromPreference(mActivity, Const.LOCAL_TIME)) {
            swLocalTime.setChecked(true);
        } else {
            swLocalTime.setChecked(false);
        }


        swLocalTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.saveBooleanToPreference(mActivity, Const.LOCAL_TIME, true);
                } else {
                    Utils.saveBooleanToPreference(mActivity, Const.LOCAL_TIME, false);
                }
                ((MainActivity) mActivity).initSettings();
//                ((MainActivity) mActivity).navigate(10);
                MainCalendarFragment.loadData();
            }
        });
        if (Utils.getBooleanFromPreference(mActivity, Const.SHOW_ADS)) {
            swAds.setChecked(true);
        } else {
            swAds.setChecked(false);
        }
        swAds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.saveBooleanToPreference(mActivity, Const.SHOW_ADS, true);
                } else {
                    Utils.saveBooleanToPreference(mActivity, Const.SHOW_ADS, false);
                }
            }
        });

        Random r = new Random();
        int rndInt = r.nextInt(6 - 1) + 1;
        ImageView imageView = (ImageView)view.findViewById(R.id.iv_menu_banner);
        int imageId = Utils.getImageIdentifier(mActivity, "menu" + rndInt);
        imageView.setBackgroundDrawable(getResources().getDrawable(imageId));

        String language = ((MainActivity)mActivity).language;
        tvLanguage.setText(language);
        if (language.equals(getResources().getString(R.string.English))) {
            ivLanguage.setBackgroundDrawable(getResources().getDrawable(R.drawable.usd));
        } else if (language.equals(getResources().getString(R.string.Chinese))) {
            ivLanguage.setBackgroundDrawable(getResources().getDrawable(R.drawable.cny));
        } else if (language.equals(getResources().getString(R.string.Japanese))) {
            ivLanguage.setBackgroundDrawable(getResources().getDrawable(R.drawable.jpy));
        }

    }

    @Override
    public void onClick(View v) {
        if (v == llInviteFriend) {
            ((MainActivity)mActivity).navigate(0);
        }
        if (v == llSendFeedback) {
            ((MainActivity)mActivity).navigate(1);
        }
        if (v == llRateUs) {
            ((MainActivity)mActivity).navigate(2);
        }
        if (v == llMore) {
            ((MainActivity)mActivity).navigate(3);
        }
        if (v == rlNotification) {
            ((MainActivity)mActivity).navigate(4);
        }
        if (v == rlSymbol) {
            ((MainActivity)mActivity).navigate(5);
        }
        if (v == rlImpact) {
            ((MainActivity)mActivity).navigate(6);
        }
        if (v == rlLanguage) {
            ((MainActivity)mActivity).navigate(7);
        }

    }
}

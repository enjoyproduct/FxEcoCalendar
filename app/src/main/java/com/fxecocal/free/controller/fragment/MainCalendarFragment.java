package com.fxecocal.free.controller.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fxecocal.free.R;
import com.fxecocal.free.Utility.TimeUtility;
import com.fxecocal.free.Utility.Utils;
import com.fxecocal.free.controller.MainActivity;
import com.fxecocal.free.model.FactModel;
import com.fxecocal.free.widget.SelectDateFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainCalendarFragment extends Fragment implements View.OnClickListener{

    // URL Address
    static String url = "http://www.forexfactory.com/calendar.php";
    String datePattern = "MMM dd,YYYY";
    static String strDate;
    static ProgressDialog mProgressDialog;

    private Button btnToday, btnTomorrow, btnThisWeek, btnByDate;
    private ImageButton ibBefore, ibNext;
    private static Button btnDate;
    private static ListView listView;
    private static FactAdapter factAdapter;

    private static ArrayList<FactModel> arrFacts;
    private static ArrayList<FactModel> arrBuffer;
    private static Activity mActivity;

    private static int currentTabNumber;
    public MainCalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_calendar, container, false);
        initVariables();
        initUI(view);

        chooseDate(0);
        loadData();

        return view;
    }

    private void initVariables() {
        mActivity  = getActivity();
        strDate = "";
        arrBuffer = new ArrayList<>();
        arrFacts = new ArrayList<>();
        currentTabNumber = 0;
    }
    private void initUI(View view){
        btnToday    = (Button)view.findViewById(R.id.btn_today);
        btnTomorrow = (Button)view.findViewById(R.id.btn_tomorrow);
        btnThisWeek = (Button)view.findViewById(R.id.btn_thisweek);
        btnByDate   = (Button)view.findViewById(R.id.btn_bydate);
        btnDate     = (Button)view.findViewById(R.id.btn_date);
        ibBefore    = (ImageButton)view.findViewById(R.id.btn_before);
        ibNext      = (ImageButton)view.findViewById(R.id.btn_next);

        btnToday    .setOnClickListener(this);
        btnTomorrow .setOnClickListener(this);
        btnThisWeek .setOnClickListener(this);
        btnByDate   .setOnClickListener(this);
        btnDate     .setOnClickListener(this);
        ibBefore    .setOnClickListener(this);
        ibNext      .setOnClickListener(this);

        listView = (ListView)view.findViewById(R.id.listview);
        factAdapter = new FactAdapter();
    }
    public static void loadData() {
        arrBuffer = new ArrayList<>();
        arrFacts = new ArrayList<>();
        if (((MainActivity)mActivity).localTime) {
            DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            try {
                Date date = (Date)formatter.parse(btnDate.getText().toString());
                long timestamp = date.getTime() / 1000;
                int offset = TimeUtility.getOffset();
                timestamp = timestamp - (offset + 4) * 3600;
                if (currentTabNumber != 2) {
                    strDate = TimeUtility.getDateStringFromTimeStampSecond(timestamp, "MMMdd.yyyy");
                    strDate = strDate.substring(0,1).toLowerCase() + strDate.substring(1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {

        }
        new Description().execute();
    }
    private void chooseDate(int num) {
        switch (num) {
            case 0:
                convertDateAndLoadData((TimeUtility.getCurrentTimeStamp()));
                break;
            case 1:
                convertDateAndLoadData((TimeUtility.getCurrentTimeStamp())+ 24 * 3600);
                break;
            case 2:
                strDate = "this";
                btnDate.setText("This week");
                break;
            case 3:
                break;
        }
    }
    @Override
    public void onClick(View v) {
        if (v == btnToday) {
            currentTabNumber = 0;

            btnToday   .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnTomorrow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnThisWeek.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnByDate  .setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            btnToday   .setTextColor(getResources().getColor(R.color.white));
            btnTomorrow.setTextColor(getResources().getColor(R.color.grey));
            btnThisWeek.setTextColor(getResources().getColor(R.color.grey ));
            btnByDate  .setTextColor(getResources().getColor(R.color.grey));

            ibBefore.setVisibility(View.GONE);
            ibNext.setVisibility(View.GONE);

            chooseDate(0);
            loadData();
        }
        if (v == btnTomorrow) {
            currentTabNumber = 1;

            btnToday   .setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnTomorrow.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnThisWeek.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnByDate  .setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            btnToday   .setTextColor(getResources().getColor(R.color.grey));
            btnTomorrow.setTextColor(getResources().getColor(R.color.white));
            btnThisWeek.setTextColor(getResources().getColor(R.color.grey));
            btnByDate  .setTextColor(getResources().getColor(R.color.grey));

            ibBefore.setVisibility(View.GONE);
            ibNext.setVisibility(View.GONE);

            chooseDate(1);
            loadData();
        }
        if (v == btnThisWeek) {
            currentTabNumber = 2;

            btnToday   .setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnTomorrow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btnThisWeek.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btnByDate.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            btnToday.setTextColor(getResources().getColor(R.color.grey));
            btnTomorrow.setTextColor(getResources().getColor(R.color.grey));
            btnThisWeek.setTextColor(getResources().getColor(R.color.white));
            btnByDate.setTextColor(getResources().getColor(R.color.grey));

            ibBefore.setVisibility(View.GONE);
            ibNext.setVisibility(View.GONE);

            chooseDate(2);
            loadData();
        }
        if (v == btnByDate) {
            loadByDate();
        }
        if (v == btnDate) {
            loadByDate();
        }
        if (v == ibBefore) {
            long lgDate = getTimeStampFromDate() - 24 * 3600;

            convertDateAndLoadData(lgDate);

            loadData();
        }
        if (v == ibNext) {
            long lgDate = getTimeStampFromDate() + 24 * 3600;

            convertDateAndLoadData(lgDate);

            loadData();
        }

    }
    private long getTimeStampFromDate() {
        String str = btnDate.getText().toString().trim().replace(",", "");
        str = str.replace(" ", "/");
        long lgDate = TimeUtility.getTimeStampFromString(str);
        return lgDate;
    }
    private void convertDateAndLoadData(long timestamp) {
        String str0 = TimeUtility.getDateStringFromTimeStampSecond(timestamp, "MMMdd.yyyy");
        strDate = str0.substring(0, 1).toLowerCase() + str0.substring(1);

        String string0 = TimeUtility.getDateStringFromTimeStampSecond(timestamp, "MMM dd, yyyy");
        btnDate.setText(string0);
    }

    private void loadByDate() {

        DialogFragment newFragment = new SelectDateFragment(btnDate, new DateSelectResult() {
            @Override
            public void finish(String date) {
                strDate = date;

                currentTabNumber = 3;

                btnToday   .setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnTomorrow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnThisWeek.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnByDate  .setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                btnToday   .setTextColor(getResources().getColor(R.color.grey));
                btnTomorrow.setTextColor(getResources().getColor(R.color.grey));
                btnThisWeek.setTextColor(getResources().getColor(R.color.grey));
                btnByDate  .setTextColor(getResources().getColor(R.color.white));

                ibBefore.setVisibility(View.VISIBLE);
                ibNext.setVisibility(View.VISIBLE);
                ///

                loadData();
            }
        });
        newFragment.show(getChildFragmentManager(), "Date");
    }
    public static void updateCalendar() {
        for (int i = 0; i < arrBuffer.size(); i ++) {
            long timestamp = arrBuffer.get(i).getRemaining_timestamp();
            if (timestamp > 0) {
                timestamp --;
                arrBuffer.get(i).setRemaining_timestamp(timestamp);
            }
        }
        factAdapter.notifyDataSetChanged();
    }
    static class FactAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return arrBuffer.size();
        }

        @Override
        public Object getItem(int position) {
            return arrBuffer.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mActivity.getLayoutInflater().inflate(R.layout.item_fact, null);
            }
            LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.ll_fact);
            if (position % 2 == 0) {
                linearLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
            } else {
                linearLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.grey_light));
            }
            TextView tvDate         = (TextView)view.findViewById(R.id.tv_if_date);
            TextView tvTime         = (TextView)view.findViewById(R.id.tv_if_time);
            TextView tvDescription  = (TextView)view.findViewById(R.id.tv_if_description);
            TextView tvCurrency     = (TextView)view.findViewById(R.id.tv_if_currency);
            TextView tvImpact       = (TextView)view.findViewById(R.id.tv_if_impact);
            TextView tvActual       = (TextView)view.findViewById(R.id.tv_if_actual);
            TextView tvForcast      = (TextView)view.findViewById(R.id.tv_if_forcast);
            TextView tvPrevious     = (TextView)view.findViewById(R.id.tv_if_previous);
            ImageView ivAlarm       = (ImageView)view.findViewById(R.id.iv_if_alarm);
            ImageView ivFlag        = (ImageView)view.findViewById(R.id.iv_if_flag);
            RelativeLayout rlImpact = (RelativeLayout)view.findViewById(R.id.rl_if_impact);

            FactModel factModel = arrBuffer.get(position);

            tvDate.setText(factModel.getDate());
//            tvTime.setText(factModel.getTime());
            if (factModel.getRemaining_timestamp() < 0) {
                tvTime.setText("");
                ivAlarm.setVisibility(View.INVISIBLE);
            } else {
                tvTime.setText(TimeUtility.countTime(factModel.getRemaining_timestamp()));
            }

            tvDescription.setText(factModel.getDescription());
            tvCurrency.setText(factModel.getCurrency());
            tvImpact.setText(factModel.getImpact());
            if (factModel.getImpact().equals("high")) {
                rlImpact.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_red));
            } else if (factModel.getImpact().equals("medium")) {
                rlImpact.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_orange));
            } else if (factModel.getImpact().equals("low")) {
                rlImpact.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_yellow));
            } else {
                rlImpact.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.bg_grey));
            }

            tvActual.setText(factModel.getActual());
            if (factModel.getActual_status().equals("better")) {
                tvActual.setTextColor(mActivity.getResources().getColor(R.color.random_color_9));
            } else if (factModel.getActual_status().equals("worse")) {
                tvActual.setTextColor(mActivity.getResources().getColor(R.color.red));

            } else {
                tvActual.setTextColor(mActivity.getResources().getColor(R.color.black));

            }
            tvForcast.setText(factModel.getForcast());
            tvPrevious.setText(factModel.getPrevious());
            int flagId = Utils.getImageIdentifier(mActivity, factModel.getCurrency().toLowerCase());
            if (flagId != 0) {
                ivFlag.setBackgroundDrawable(mActivity.getResources().getDrawable(flagId));
            }

            if (factModel.getTime().equals("All Day") || factModel.getTime().length() == 0) {
                ivAlarm.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }
    // Description AsyncTask
    private static class Description extends AsyncTask<Void, Void, Void> {
        String desc = "";
        String date = "";
        Dialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = mActivity.getLayoutInflater().inflate(R.layout.loading_dialog, null);
            dialog = new AlertDialog.Builder(mActivity)
                    .setCancelable(false)
                    .setView(view)
                    .show();

//            mProgressDialog = new ProgressDialog(mActivity);
//            mProgressDialog.setTitle("Forex and Economic Calendar");
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String full_url = url + "?week=" + strDate;
                if (currentTabNumber == 2) {
                    full_url = url + "?week="  + strDate;

                } else {
                    full_url = url + "?day=" + strDate;

                }
                // Connect to the web site
                Document document = Jsoup.connect(full_url).get();

                // Using Elements to get the Meta data
                Elements tbody = document.select("tbody");
                Elements trs = tbody.select("tr");
                for (Element tr : trs) {
                    FactModel factModel = new FactModel();

                    String className = tr.attr("class");
                    if (className.contains("calendar__row--day-breaker")) {
                        String dd = tr.text();
                        String w = dd.substring(0, 3);
                        String m = dd.substring(3, 6);
                        String d = dd.substring(6);

                        date = w + " " + m + " " + d;

                        desc = desc + w + " " + m + " " + d + "\n";
                    }
                    if (className.contains("calendar_row")) {
                        Elements tds = tr.select("td");
                        String time = "";
                        String currency = "";
                        String actual = "";
                        String actual_status = "";
                        String forecast = "";
                        String previous = "";
                        String impact = "";
                        String title = "";
                        for (Element td : tds) {
                            className = td.attr("class");
                            if (className.contains("calendar__time")) {
                                time = td.text();
                            } else if (className.contains("calendar__currency")) {
                                currency = td.text();
                            } else if (className.contains("calendar__actual")) {
                                actual = td.text();

                                Elements spans = td.select("span");
                                for (Element span : spans) {
                                    className = span.attr("class");
                                    if (className.contains("better")) {
                                        actual_status = "better";
                                    } else if (className.contains("worse")) {
                                        actual_status = "worse";
                                    }
                                }
                            } else if (className.contains("calendar__forecast")) {
                                forecast = td.text();
                            } else if (className.contains("calendar__previous")) {
                                previous = td.text();
                            } else if (className.contains("calendar__impact")) {
                                Elements spans = td.select("span");
                                for (Element span : spans) {
                                    className = span.attr("class");
                                    if (className.contains("low")) {
                                        impact = "low";
                                    } else if (className.contains("medium")) {
                                        impact = "medium";
                                    } else if (className.contains("high")) {
                                        impact = "high";
                                    }
                                }
                            }else if (className.contains("calendar__event")) {
                                Elements spans = td.select("span");
                                for (Element span : spans) {
                                    className = span.attr("class");
                                    if (className.contains("calendar__event")) {
                                        title = span.text();
                                    }
                                }
                            }
                        }
                        desc = desc + "\t" + time + ",\t" + currency + ",\t" + impact + ",\t" + actual + ",\t" + forecast + ",\t" + previous + "\n";

                        factModel.setDate(date);
                        factModel.setTime(time);
                        factModel.setCurrency(currency);
                        factModel.setImpact(impact);
                        factModel.setActual(actual);
                        factModel.setActual_status(actual_status);
                        factModel.setForcast(forecast);
                        factModel.setPrevious(previous);
                        factModel.setDescription(title);

                        arrFacts.add(factModel);
                    }
                }
                // Locate the content attribute
//				desc = description.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set description into TextView
            if (arrFacts.size() > 0) {
                arrFacts.remove(arrFacts.size() - 1);
            }


//            arrBuffer = (ArrayList) arrFacts.clone();
            filter();
            factAdapter = new FactAdapter();
            listView.setAdapter(factAdapter);

            dialog.dismiss();
//            mProgressDialog.dismiss();
        }
    }
    private static void filter(){
        arrBuffer = new ArrayList<>();
        for (FactModel factModel : arrFacts) {
            String strDate = factModel.getDate();
            String strTime = factModel.getTime();
            if (!strTime.equals("All Day") &&  strDate.length() > 0) {
                DateFormat formatter = null;
                Date date = null;
                String time = "";
                if (strTime.length() > 0) {
                    formatter = new SimpleDateFormat("EEE MMM dd yyyy hh:mma");
                    time = strDate + " " + TimeUtility.getCurrentYear()+ " " + strTime;
                } else {
                    formatter = new SimpleDateFormat("EEE MMM dd yyyy");
                    time = strDate + " " + TimeUtility.getCurrentYear();
                }
                try {
                    date = (Date)formatter.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date == null) {
                    factModel.setDate("");
                    factModel.setTime("");
                    factModel.setRemaining_timestamp(-1);
                } else {
                    long timestamp = date.getTime() / 1000;
                    int offset = TimeUtility.getOffset();
                    if (((MainActivity)mActivity).localTime) {
                        timestamp = timestamp + (offset + 4) * 3600;
                    }
                    long remainingTimestamp = timestamp - TimeUtility.getCurrentTimeStamp();
                    strDate = TimeUtility.getDateStringFromTimeStampSecond(timestamp, "EEE MMM dd");
                    strTime = TimeUtility.getDateStringFromTimeStampSecond(timestamp, "hh:mma");
                    factModel.setDate(strDate);
                    factModel.setTime(strTime);
                    factModel.setRemaining_timestamp(remainingTimestamp);
                }

            } else {
                factModel.setRemaining_timestamp(-1);
            }

            if (((MainActivity)mActivity).symbols.contains(factModel.getCurrency())) {
                if (factModel.getImpact().length() == 0 && ((MainActivity)mActivity).impacts.contains("No Impact")) {
                    arrBuffer.add(factModel);

                } else if (factModel.getImpact().length() > 0) {
                    if (((MainActivity)mActivity).impacts.contains(factModel.getImpact().substring(0, 1).toUpperCase() + factModel.getImpact().substring(1)))
                    arrBuffer.add(factModel);
                }
            }
        }
    }
}

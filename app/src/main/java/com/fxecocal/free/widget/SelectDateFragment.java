package com.fxecocal.free.widget;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;


import com.fxecocal.free.Utility.TimeUtility;
import com.fxecocal.free.controller.fragment.DateSelectResult;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextView textview;
    private DateSelectResult dateSelectResult;
    public SelectDateFragment(TextView textview, DateSelectResult dateSelectResult) {
        this.textview = textview;
        this.dateSelectResult = dateSelectResult;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    final Calendar calendar = Calendar.getInstance();
    int yy = calendar.get(Calendar.YEAR);
    int mm = calendar.get(Calendar.MONTH);
    int dd = calendar.get(Calendar.DAY_OF_MONTH);
    return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
    public void populateSetDate(int year, int month, int day) {
        // Show selected date
        String date = String.valueOf(day) + "/"  + String.valueOf(month + 1) + "/" + String.valueOf(year);
        String strMonth = TimeUtility.getDatewithFormat(String.valueOf(month), "MM", "MMM");

        textview.setText(strMonth + " " + day + ", " + year);

        strMonth = strMonth.toLowerCase();
        dateSelectResult.finish(strMonth + day + "." + year);

    }
}
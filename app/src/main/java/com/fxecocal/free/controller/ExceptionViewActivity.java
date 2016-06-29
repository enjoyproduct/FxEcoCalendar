package com.fxecocal.free.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fxecocal.free.R;

public class ExceptionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_view);

        TextView textView = (TextView)findViewById(R.id.tv_exception_view);
        String exception = getIntent().getStringExtra("error");
        textView.setText(exception);
    }

}

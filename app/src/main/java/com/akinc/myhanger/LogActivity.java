package com.akinc.myhanger;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        try {
            populateLogs();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method populates the log table for past flights
     * 1 -> FROM LOCATION [0]
     * 2 -> TO LOCATION [1]
     * 3 -> FLIGHT # [2]
     * 4 -> REG # [3]
     * 5 -> DATE [4]
     * 6 -> INCIDENT? [5]
     */
    public void populateLogs() throws FileNotFoundException {
        LinearLayout lv = findViewById(R.id.ilist);
        lv.removeAllViews();
        LinearLayout ll = new LinearLayout(lv.getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView v1 = new TextView(lv.getContext());
        v1.setText("Date");
        v1.setTextSize(18);
        v1.setPadding(45,45,45,45);
        TextView v2 = new TextView(lv.getContext());
        v2.setText("Reg #");
        v2.setTextSize(18);
        v2.setPadding(45,45,45,45);
        TextView v3 = new TextView(lv.getContext());
        v3.setText("Flight #");
        v3.setTextSize(18);
        v3.setPadding(45,45,45,45);
        TextView v4 = new TextView(lv.getContext());
        v4.setText("Origin");
        v4.setTextSize(18);
        v4.setPadding(45,45,45,45);
        TextView v5 = new TextView(lv.getContext());
        v5.setText("Dest");
        v5.setTextSize(18);
        v5.setPadding(45,45,45,45);
        ll.addView(v1);
        ll.addView(v2);
        ll.addView(v3);
        ll.addView(v4);
        ll.addView(v5);
        lv.addView(ll);

        //Parse the files
        File[] files = getFilesDir().listFiles();
        //For each trip
        for(File file : files) {
            Scanner sc = new Scanner(file);
            //For each line
            int i = 0;
            String[] prop = new String[6];
            while(sc.hasNext()) {
                prop[i] = sc.next();
                i++;
            }
            //Fill a data log
            LinearLayout ll2 = new LinearLayout(this);
            ll2.setOrientation(LinearLayout.HORIZONTAL);
            TextView v01 = new TextView(lv.getContext());
            v01.setText(prop[0]);
            v01.setPadding(55,55,55,55);
            TextView v02 = new TextView(lv.getContext());
            v02.setText(prop[1]);
            v02.setPadding(55,55,65,55);
            TextView v03 = new TextView(lv.getContext());
            v03.setText(prop[2]);
            v03.setPadding(55,55,60,55);
            TextView v04 = new TextView(lv.getContext());
            v04.setText(prop[3]);
            v04.setPadding(55,55,55,55);
            TextView v05 = new TextView(lv.getContext());
            v05.setText(prop[4]);
            v05.setPadding(55,55,0,55);
            ll2.addView(v01);
            ll2.addView(v02);
            ll2.addView(v03);
            ll2.addView(v04);
            ll2.addView(v05);
            lv.addView(ll2);
        }
    }
}

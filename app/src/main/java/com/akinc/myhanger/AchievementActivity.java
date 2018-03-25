package com.akinc.myhanger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class AchievementActivity extends AppCompatActivity {
    int[] achievements = {0,0,0,0,0,0,0,0};
    String[] aTitle = {"Deja Vu","Home Sweet Home","Sightseer","'Whoops'","Welcome!","Cinque",
    "10/10","100!"};
    String[] aDesc = {"Same flight number has been seen twice!","Rode on the same aircraft!",
            "Went on more than 5 flights in a day!","Boarded a plane with a 'history'",
            "Scanned your first ticket!","Scanned 5 tickets!", "Scanned 10 tickets!!",
            "Scanned 100 tickets!!!"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        try {
            checkAchievements();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        populateAchievements();
    }

    /**
     * Updates the achievements integer array
     */
    public void checkAchievements() throws FileNotFoundException {
        ArrayList<String[]> flights = new ArrayList<>();
        //Parse the files
        File[] files = getFilesDir().listFiles();
        //For each trip
        for (File file : files) {
            Scanner sc = new Scanner(file);
            //For each line
            int i = 0;
            String[] prop = new String[6];
            while (sc.hasNext()) {
                prop[i] = sc.next();
                i++;
            }
            flights.add(prop);
        }
        //1 -> Same Flight No
        for(String[] f : flights) {
            String curNum = f[2];
            for(String[] g : flights) {
                String tempNum = g[2];
                if(!Arrays.equals(f,g) && curNum.equals(tempNum)) {
                    achievements[0] = 1;
                }
            }
        }
        //2 -> Same Aircraft
        for(String[] f : flights) {
            String curAir = f[3];
            for(String[] g : flights) {
                String tempAir = g[3];
                if(!Arrays.equals(f,g) && curAir.equals(tempAir)) {
                    achievements[1] = 1;
                }
            }
        }
        //3 -> More than 5 Flights in one day
        int counter = 0;
        for(String[] f : flights) {
            String curDate = f[4];
            for(String[] g : flights) {
                String tempDate = g[4];
                if(!Arrays.equals(f,g) && curDate.equals(tempDate)) {
                    counter++;
                }
            }
        }
        if(counter>=5) {
            achievements[2] = 1;
        }
        //4 -> Plane w/ incidents
        for(String[] f : flights) {
            String incident = f[5];
            if(incident.equals("1")) {
                achievements[3] = 1;
            }
        }
        //5 -> Ticket Scanner [1,5,10,100]
        if(flights.size()>=1) {
            achievements[4] = 1;
        }
        if(flights.size()>=5) {
            achievements[5] = 1;
        }
        if(flights.size()>=10) {
            achievements[6] = 1;
        }
        if(flights.size()>=100) {
            achievements[7] = 1;
        }
    }

    /**
     * Populates the achievement views
     */
    public void populateAchievements() {
        LinearLayout llp = findViewById(R.id.iachieve);
        llp.removeAllViews();
        for(int i = 0; i < achievements.length; i++) {
            if(achievements[i]==0) { //Locked
                LinearLayout llc = new LinearLayout(llp.getContext());
                llc.setOrientation(LinearLayout.HORIZONTAL);

                TextView tv = new TextView(llc.getContext());
                tv.setText("???");
                tv.setTextSize(25);
                tv.setPadding(20,20,80,20);

                TextView tv2 = new TextView(llc.getContext());
                tv2.setText("????????????????");
                tv2.setTextSize(15);

                llc.addView(tv);
                llc.addView(tv2);
                llp.addView(llc);
            } else { //Unlocked
                LinearLayout llc = new LinearLayout(llp.getContext());
                llc.setOrientation(LinearLayout.HORIZONTAL);

                TextView tv = new TextView(llc.getContext());
                tv.setText(aTitle[i]);
                tv.setTextSize(25);
                tv.setPadding(20,20,20,20);

                TextView tv2 = new TextView(llc.getContext());
                tv2.setText(aDesc[i]);
                tv2.setTextSize(15);

                llc.addView(tv);
                llc.addView(tv2);
                llp.addView(llc);
            }
        }
    }

}

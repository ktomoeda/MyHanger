package com.akinc.myhanger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) throws IOException {
        switch(v.getId()) {
            case R.id.achieve:
                Intent i = new Intent(this, AchievementActivity.class);
                startActivity(i);
                break;
            case R.id.camera:
                Intent j = new Intent(this, ScanActivity.class);
                startActivity(j);
                break;
            case R.id.map:
                Intent k = new Intent(this, MapActivity.class);
                startActivity(k);
                break;
            case R.id.log:
                Intent l = new Intent(this, LogActivity.class);
                startActivity(l);
                break;
            case R.id.home:
                Intent m = new Intent(this, MainActivity.class);
                startActivity(m);
                break;
        }
    }
}

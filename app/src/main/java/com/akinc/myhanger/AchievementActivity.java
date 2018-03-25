package com.akinc.myhanger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AchievementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.homebutton:
                Intent m = new Intent(this, MainActivity.class);
                startActivity(m);
                break;
        }
    }
}

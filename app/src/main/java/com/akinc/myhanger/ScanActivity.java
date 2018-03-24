package com.akinc.myhanger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    }

    /**
     * This is the main Async Task which handles the barcode information, parses it, and
     * retrieves relevant information for the user about the aircraft. They will then have the
     * option to save this plane into their personal "hanger".
     */

    class TicketScanner extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            //TODO
            //Will show something for the user while it's loading...
        }

        @Override
        protected String[] doInBackground(String... args) {
            try {
                //TODO
                //  Main function for what occurs in the background. Scans ticket, retrieves needed
                //information, then parses the web/database for needed information.
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null; //***DELETE LATER
        }
        @Override
        protected void onPostExecute(String[] result)
        {
            //TODO
            //  This will format the strings retrieved in the doInBackground method for the
            // users to see on the next activity screen
        }

    }
    public void onClick(View v) {
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
        }
    }

}

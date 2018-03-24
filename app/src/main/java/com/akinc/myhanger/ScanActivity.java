package com.akinc.myhanger;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.Integer.parseInt;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        new TicketScanner().execute();
    }

    /**
     * This is the main Async Task which handles the barcode information, parses it, and
     * retrieves relevant information for the user about the aircraft. They will then have the
     * option to save this plane into their personal "hanger".
     */

    class TicketScanner extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //TODO
            //Will show something for the user while it's loading...
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                //  Main function for what occurs in the background. Scans ticket, retrieves needed
                //information, then parses the web/database for needed information.
                String tempFlightNo = "wn1909"; //***DELETE WHEN BARCODE WORKING
                int[] tempDate = {2018,03,22}; //***DELETE WHEN BARCODE WORKING
                //  First, find the tail #
                return fetchTailNum(tempFlightNo, tempDate);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                // If the web scrapper failed to find something...
                e.printStackTrace();
            }
            return null; //***DELETE LATER
        }

        /**
         * Returns the tail number of the aircraft using the flight No & date
         * @param flightNo Flight Number for the trip
         * @param date Date for which the trip begins
         * @return Tail Number of the aircraft
         */
        protected String fetchTailNum(String flightNo, int[] date) {
            LocalDate inputDate = null;
            int inputTime = 0;
            String apc = null;
            Document doc = null;
            //Connect to flightradar24 for the correct flight no.
            try {
                doc = Jsoup.connect("https://www.flightradar24.com/data/flights/"+flightNo).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //For each table entry, compare dates to see if there is a match
            for (Element table : doc.select("table#tbl-datatable").select("tbody")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    //Departure date in UTC
                    inputDate = LocalDate.parse(tds.get(1).text());
                    //Departure time in UTC
                    inputTime = parseInt(tds.get(6).text().replace(":",""));
                    //Airport code
                    apc = tds.get(2).select("span").select("a").text();
                    //Before comparing, convert UTC date to local date
                    LocalDate tempDate = fetchLocalDate(inputDate, inputTime, apc);
                    if (tempDate.equals(inputDate)) { //Change to input from barcode
                        String[] tempString = tds.get(4).text().split(" ");
                        //return tempString[1];
                    }
                }
            }
            return null;
        }

        /**
         * Returns the local date to be used for flight no. lookup (Using UTC)
         * @param dateUTC Date in UTC
         * @param timeUTC Time in UTC
         * @param apc Airport Code
         * @return Date in local time
         */
        protected LocalDate fetchLocalDate(LocalDate dateUTC, int timeUTC, String apc) {
            LocalDate localDate = null;
            JSONParser parser = new JSONParser();

            try {
                URL oracle = new URL("https://airports-api.s3-us-west-2.amazonaws.com/icao/k"+apc.toLowerCase()+".json"); // URL to Parse
                URLConnection yc = oracle.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    JSONArray a = (JSONArray) parser.parse(inputLine);
                    JSONObject o = a.getJSONObject(0);
                    Log.d("Test",(String) o.get("utc_offset"));
                }
                in.close();
            } catch (org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return localDate;
        }

        @Override
        protected void onPostExecute(String result)
        {
            TextView v = findViewById(R.id.aircraftReg);
            v.setText(result);
            //TODO
            //  This will format the strings retrieved in the doInBackground method for the
            // users to see on the next activity screen
        }

    }
}
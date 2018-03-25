package com.akinc.myhanger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import static java.lang.Integer.parseInt;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new TicketScanner().execute();
    }

    /**
     * This is the main Async Task which handles the barcode information, parses it, and
     * retrieves relevant information for the user about the aircraft. They will then have the
     * option to save this plane into their personal "hanger".
     */

    class TicketScanner extends AsyncTask<String, String, String[]> {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView progressText = findViewById(R.id.progressText);

        @Override
        protected void onPreExecute() {
            //Sets up the progress bar and text
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(100);
            progressText.setVisibility(View.VISIBLE);
            progressText.setText("Searching...");
        }

        @Override
        protected String[] doInBackground(String... args) {
            try {
                //  Main function for what occurs in the background. Scans ticket, retrieves needed
                //information, then parses the web/database for needed information.
                String tempFlightNo = "ke623"; //***DELETE WHEN BARCODE WORKING
                int[] tempDate = {2018,3,24}; //***DELETE WHEN BARCODE WORKING
                //  First, find the tail #
                publishProgress("Fetching plane tail number...");
                String tailno = fetchTailNum(tempFlightNo, tempDate);
                progressBar.setProgress(30);
                // Second, find the plane model #
                publishProgress("Fetching plane model...");
                String modelno = fetchModelNum(tailno);
                progressBar.setProgress(50);
                // Third, find a picture of the plane
                publishProgress("Fetching image of plane...");
                String planeImg = fetchPlanePicture(tailno);
                progressBar.setProgress(70);
                //Finally, find if the plane was in an accident
                publishProgress("Fetching any accident reports...");
                String accidentDesc = fetchAccidentRepot(tailno);
                progressBar.setProgress(90);
                //Put into an array and send to post
                publishProgress("Finishing...");
                String[] planeData = {tailno, modelno, planeImg, accidentDesc};
                progressBar.setProgress(100);
                return planeData;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... args) {
            progressText.setText(args[0]);
        }

        /**
         * Returns the tail number of the aircraft using the flight No & date
         * @param flightNo Flight Number for the trip
         * @param date Date for which the trip begins
         * @return Tail Number of the aircraft
         */
        protected String fetchTailNum(String flightNo, int[] date) throws IOException {
            LocalDate inputDate;
            if(date[1] < 10) {
                inputDate = LocalDate.parse(Integer.toString(date[0])+"-0"+Integer.toString(date[1])+"-"+Integer.toString(date[2]));
            } else {
                inputDate = LocalDate.parse(Integer.toString(date[0])+"-"+Integer.toString(date[1])+"-"+Integer.toString(date[2]));
            }
            LocalDate fetchedDate = null;
            int fetchedTime = 0;
            String apc = null;
            Document doc = null;
            //Connect to flightradar24 for the correct flight no.
            doc = Jsoup.connect("https://www.flightradar24.com/data/flights/"+flightNo).get();
            //For each table entry, compare dates to see if there is a match
            for (Element table : doc.select("table#tbl-datatable").select("tbody")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    //Departure date in UTC
                    fetchedDate = LocalDate.parse(tds.get(1).text());
                    //Departure time in UTC
                    fetchedTime = parseInt(tds.get(6).text().replace(":",""));
                    //Airport code
                    apc = tds.get(2).select("span").select("a").text();
                    //Before comparing, convert UTC date to local date
                    LocalDate tempDate = fetchLocalDate(fetchedDate, fetchedTime, apc);
                    if (tempDate.equals(inputDate)) { //Change to input from barcode
                        String[] tempString = tds.get(4).text().split(" ");
                        return tempString[1];
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
        protected LocalDate fetchLocalDate(LocalDate dateUTC, int timeUTC, String apc) throws IOException {
            LocalDate localDate = dateUTC;
            String sURL = "https://airports-api.s3-us-west-2.amazonaws.com/iata/"+apc.toLowerCase()+".json"; //just a string

            // Connect to the URL using java's native library
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            // Convert to a JSON object
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            int utcDiff = parseInt(rootobj.get("utc_offset").getAsString())*100;

            //Scale back the time and see if the date should stay the same, be set back or move forward
            int localTime = timeUTC + utcDiff;
            if(localTime<0) { //Go back a day
                localDate = localDate.minusDays(1);
            } else if(localTime>2400) { //Go forward a day
                localDate.plusDays(1);
            } else { //Stay in the say day
            }
            return localDate;
        }

        /**
         * Returns the model number of the airplane
         * @param tailno Tail number of the airplane
         * @return Model number
         */
        protected String fetchModelNum(String tailno) throws IOException {
            Document planeModelSearch = Jsoup.connect("https://www.flightradar24.com/data/aircraft/"+tailno).get();
            return planeModelSearch.select("div#cnt-aircraft-info").select("span.details").get(0).text();
        }

        /**
         * Fetch the URL for a picture of the given plane
         * @param tailNo Tail # of the plane
         * @return image URL in string format
         */
        protected String fetchPlanePicture(String tailNo) throws IOException {
            Document planeSpotSearch = Jsoup.connect("https://www.flightradar24.com/data/aircraft/"+tailNo).get();
            return planeSpotSearch.select("section#cnt-data-subpage").select("div.col-md-6.n-p").select("a").get(3).select("img").attr("src");
        }

        /**
         * Fetchs an accident report for the plane (if there is one)
         * @param tailno Tail # of the plane
         * @return Description of the accident (if there is one)
         */
        protected String fetchAccidentRepot(String tailno) throws IOException {
            Document doc3 = Jsoup.connect("https://en.wikipedia.org/w/index.php?search="+tailno+"&title=Special%3ASearch&fulltext=1").get();
            String firstResult = doc3.select("ul.mw-search-results").select("li").get(0).select("div.mw-search-result-heading").select("a").get(0).attr("abs:href");
            Document doc4 = Jsoup.connect(firstResult).get();
            if(doc4.select("table.infobox.vcard.vevent").size()==0) {
                return "No previous accidents to report";
            } else {
                if(doc4.select("table.infobox.vcard.vevent").select("tr").size()>13) {
                    String registration;
                    String accidentType = doc4.select("table.infobox.vcard.vevent").select("tr").get(1).select("th").text();
                    if(accidentType.equals("Hijacking summary")) {
                        registration = doc4.select("table.infobox.vcard.vevent").select("tr").get(11).select("td").get(0).text().split("\\[")[0];
                    } else {
                        registration = doc4.select("table.infobox.vcard.vevent").select("tr").get(12).select("td").get(0).text().split("\\[")[0];
                    }
                    if(registration.equals(tailno)) {
                        Elements accidentDesc = doc4.select("div.mw-parser-output");
                        StringBuilder sb = new StringBuilder();
                        for (Element para : accidentDesc.select("p")) {
                            sb.append(para.text()+"\n\n");
                            if(para.text().length()==0) {
                                return sb.toString();
                            }
                        }
                    } else {
                        return "No previous accidents to report";
                    }
                } else {
                    return "No previous accidents to report";
                }
            }
            return "No previous accidents to report";
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            progressBar.setVisibility(View.INVISIBLE);
            progressText.setVisibility(View.INVISIBLE);
            //Insert plane registration #
            TextView v = findViewById(R.id.aircraftReg);
            v.setText(result[0]);
            //Insert plane model #
            TextView v2 = findViewById(R.id.planeModel);
            v2.setText(result[1]);
            //Insert plane picture
            ImageView iv = findViewById(R.id.planeImg);
            InputStream is = null;
            try {
                is = new URL(result[2]).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap planeImg = BitmapFactory.decodeStream(is);
            iv.setImageBitmap(planeImg);
            //Insert plane accident report (if there is one)
            TextView v3 = findViewById(R.id.planeAccident);
            v3.setText(result[3]);
        }

    }
}
//
// Name                 Michele Moretti
// Student ID           S2222142
// Programme of Study   Computing
//

package org.me.gcu.moretti_michele_s2222142;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//import gcu.mpd.bgsdatastarter.R;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private ListView quakeListView;
    private TextView rawDataDisplay;
    private Button startButton;
    private String result;

    private String url1 = "";
    private String urlSource = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private Dialog earthquakeDialog;
    private Button filterButton;
    private Button searchButton;
    private Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        // Find the filter button and set an OnClickListener to it
        filterButton = findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateRangeDialog();
            }
        });

        // Find the search button and set an OnClickListener to it
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateLocationDialog();
            }
        });

        // Find the map button and set an OnClickListener to it
        mapButton = findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the MapsActivity to display the map
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("earthquakeList", earthquakeList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        quakeListView = findViewById(R.id.quakeListView);
        quakeListView.setOnItemClickListener((parent, view, position, id) -> {

            // Get the earthquake object
            Earthquake earthquake = (Earthquake) parent.getItemAtPosition(position);

            // Reuse the dialog if it already exists
            if (earthquakeDialog == null) {
                earthquakeDialog = new Dialog(MainActivity.this);
                earthquakeDialog.setContentView(R.layout.dialog_earthquake);
                earthquakeDialog.setTitle("Earthquake Information");
            }

            // Populate the custom layout with the earthquake information
            String description = earthquake.getDescription();
            // Extract the location and magnitude from the description
            String[] parts = description.split(";");
            String location = parts[1].substring(10); // Extract the location string after "Location: "
            String magnitude = parts[4].substring(12); // Extract the magnitude string after "Magnitude: "
            // Construct the new description string
            String newDescription = "Location:" + "\n" + location + "\n" + "\n" + "Magnitude:" + "\n" + magnitude;
            // Set the new description string to the descriptionTextView
            TextView descriptionTextView = earthquakeDialog.findViewById(R.id.descriptionTextView);
            descriptionTextView.setText(newDescription);

            TextView linkTextView = earthquakeDialog.findViewById(R.id.linkTextView);
            linkTextView.setText(earthquake.getLink());

            // Format the date using SimpleDateFormat
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            Date date = new Date();
            String dateString = formatter.format(date);

            String formattedDate = formatter.format(earthquake.getPubDate());
            TextView dateTextView = earthquakeDialog.findViewById(R.id.dateTextView);
            dateTextView.setText(formattedDate);

            TextView latitudeTextView = earthquakeDialog.findViewById(R.id.latitudeTextView);
            latitudeTextView.setText(earthquake.getLatitude());

            TextView longitudeTextView = earthquakeDialog.findViewById(R.id.longitudeTextView);
            longitudeTextView.setText(earthquake.getLongitude());

            Button exitButton = earthquakeDialog.findViewById(R.id.exitButton);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    earthquakeDialog.dismiss(); // dismiss the dialog
                }
            });

            // Show the dialog
            earthquakeDialog.show();


        });



    }

    // Method allowing users to search for Earthquakes within a specific Date Range
    private void showDateRangeDialog() {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filter, null);

        // Get references to the DatePicker widgets and the Confirm button
        DatePicker startDatePicker = dialogView.findViewById(R.id.startDatePicker);
        DatePicker endDatePicker = dialogView.findViewById(R.id.endDatePicker);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Create a new AlertDialog and set its view to the custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Set an OnClickListener for the Confirm button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected start and end dates from the DatePicker widgets
                int startYear = startDatePicker.getYear();
                int startMonth = startDatePicker.getMonth();
                int startDay = startDatePicker.getDayOfMonth();
                Calendar startDate = Calendar.getInstance();
                startDate.set(startYear, startMonth, startDay);
                long startTimestamp = startDate.getTimeInMillis();

                int endYear = endDatePicker.getYear();
                int endMonth = endDatePicker.getMonth();
                int endDay = endDatePicker.getDayOfMonth();
                Calendar endDate = Calendar.getInstance();
                endDate.set(endYear, endMonth, endDay);
                long endTimestamp = endDate.getTimeInMillis();

                // Define the start and end dates for the date range filter
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(startTimestamp);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date firstDate = cal.getTime();

                cal.setTimeInMillis(endTimestamp);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                Date secondDate = cal.getTime();

                //Iterate through the earthquakeList and filter by date range


                // Create a new list to hold the filtered earthquake
                LinkedList<Earthquake> filteredEarthquakes = new LinkedList<>();

                // Loop through each earthquake in the list
                for (Earthquake earthquake : earthquakeList) {

                    // Check if the earthquake's pubDate falls within the specified date range
                    if ((earthquake.getPubDate().equals(firstDate) || earthquake.getPubDate().after(firstDate))
                            && (earthquake.getPubDate().equals(secondDate) || earthquake.getPubDate().before(secondDate))) {

                        // Add the earthquake to the filtered list
                        filteredEarthquakes.add(earthquake);
                    }
                }
                // Set the filtered earthquakes to the adapter for display in the list view
                EarthquakeAdapter adapter = new EarthquakeAdapter(MainActivity.this, filteredEarthquakes);
                quakeListView.setAdapter(adapter);


                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }


    // Method allowing users to search for Earthquake for Date & Location
    private void showDateLocationDialog() {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search, null);

        // Get references to the EditText widgets, the DatePicker widgets, and the Confirm button
        EditText locationEditText = dialogView.findViewById(R.id.locationEditText);
        DatePicker searchdatePicker = dialogView.findViewById(R.id.searchdatePicker);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Create a new AlertDialog and set its view to the custom dialog layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected location and date from the EditText and DatePicker widgets
                String location = locationEditText.getText().toString().trim();

                // Get the selected start and end dates from the DatePicker widgets
                int searchYear = searchdatePicker.getYear();
                int searchMonth = searchdatePicker.getMonth();
                int searchDay = searchdatePicker.getDayOfMonth();
                Calendar searchDate = Calendar.getInstance();
                searchDate.set(searchYear, searchMonth, searchDay);
                long searchTimestamp = searchDate.getTimeInMillis();


                // Create a calendar instance with the selected search date and time set to midnight
                Calendar searchCalendar = Calendar.getInstance();
                searchCalendar.setTimeInMillis(searchTimestamp);
                searchCalendar.set(Calendar.HOUR_OF_DAY, 0);
                searchCalendar.set(Calendar.MINUTE, 0);
                searchCalendar.set(Calendar.SECOND, 0);
                searchCalendar.set(Calendar.MILLISECOND, 0);
                Date searchedDate = searchCalendar.getTime();


                LinkedList<Earthquake> filteredQuakes = new LinkedList<>();
                for (Earthquake earthquake : earthquakeList) {
                    boolean matchLocation = false;
                    boolean matchDate = false;

                    // Check if the location matches
                    if (!location.isEmpty() && earthquake.getLocation().equalsIgnoreCase(location)) {
                        matchLocation = true;
                    }

                    // Check if the date matches
                    Calendar quakeCalendar = Calendar.getInstance();
                    quakeCalendar.setTimeInMillis(earthquake.getPubDate().getTime());
                    quakeCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    quakeCalendar.set(Calendar.MINUTE, 0);
                    quakeCalendar.set(Calendar.SECOND, 0);
                    quakeCalendar.set(Calendar.MILLISECOND, 0);
                    Date quakeDate = quakeCalendar.getTime();

                    if (quakeDate.equals(searchedDate)) {
                        matchDate = true;
                    }

                    if (matchLocation && matchDate) {
                        filteredQuakes.add(earthquake);
                    }

                }

                // Set the filtered earthquakes to the adapter for display in the list view
                EarthquakeAdapter adapter = new EarthquakeAdapter(MainActivity.this, filteredQuakes);
                quakeListView.setAdapter(adapter);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });


        // Show the dialog
        dialog.show();
    }

    // Custom Executor class to run tasks on the main thread
    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable runnable) {
            handler.post(runnable);
        }
    }


    // Creates a LinkedList of Earthquake Data
    public static LinkedList<Earthquake> earthquakeList = new LinkedList<>();


    public void onClick(View aview) {

        startProgress();

    }

    public void startProgress() {
        // Create a new Executor instance
        Executor executor = Executors.newSingleThreadExecutor();

        // Execute the network access on the executor
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Network access code goes here
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";

                Log.e("MyTag", "in run");

                try {
                    Log.e("MyTag", "in try");
                    aurl = new URL(urlSource);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                    // Throw away the first 2 header lines before parsing
                    int i = 0;
                    while ((inputLine = in.readLine()) != null) {
                        if (i > 1) {
                            result = result + inputLine;
                        }
                        i = i + 1;
                    }
                    in.close();
                } catch (IOException ae) {
                    Log.e("MyTag", "ioexception");
                }
                result = result.replaceAll("null", "");
                result = result.replaceAll("geo:", "");
                result = result.replaceAll("</channel>", "");
                result = result.replaceAll("</rss>", "");

                //
                // Now that you have the xml data you can parse it
                //

                // Creates an object of type Earthquake
                Earthquake earthquake = new Earthquake();


                try {
                    // Set-up of XMLPullParser Feature
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result));
                    int eventType = xpp.getEventType();


                    while (eventType != XmlPullParser.END_DOCUMENT) { // Found a tag

                        if (eventType == XmlPullParser.START_TAG) {
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("item")) {
                                xpp.next();
                                // Creates new earthquake instance
                                earthquake = new Earthquake();
                            }
                            if (xpp.getName().equalsIgnoreCase("title")) {
                                xpp.next();
                                // sets title
                                earthquake.setTitle(xpp.getText());
                                String title = xpp.getText();
                                // Extract location from the title
                                String[] titleSegments = title.split(":");
                                if (titleSegments.length >= 3) {
                                    String[] locationSegments = titleSegments[2].split(",");
                                    if (locationSegments.length >= 1) {
                                        String location = locationSegments[0].trim();
                                        // Set location to the earthquake object
                                        earthquake.setLocation(location);

                                    }
                                }
                            } else if (xpp.getName().equalsIgnoreCase("description")) {
                                xpp.next();
                                // sets description
                                earthquake.setDescription(xpp.getText());
                                // Extract the magnitude value from the description
                                String descriptionText = earthquake.getDescription();
                                String[] segments = descriptionText.split(";");
                                for (String segment : segments) {
                                    if (segment.trim().startsWith("Magnitude:")) {
                                        String[] parts = segment.split(":");
                                        String magnitude = parts[1].trim();
                                        double magnitudeValue = Double.parseDouble(magnitude);
                                        earthquake.setMagnitude(magnitudeValue);
                                        break;
                                    }
                                }
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                xpp.next();
                                // sets link
                                earthquake.setLink(xpp.getText());
                            } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                                xpp.next();
                                String dateString = xpp.getText();
                                // parse the input date string using this format
                                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
                                // parse the date string into a Date object
                                Date date = inputFormat.parse(dateString);
                                // set the parsed date object to earthquake's pubDate attribute
                                earthquake.setPubDate(date);

                            } else if (xpp.getName().equalsIgnoreCase("lat")) {
                                xpp.next();
                                // sets latitude
                                earthquake.setLatitude(xpp.getText());

                            } else if (xpp.getName().equalsIgnoreCase("long")) {
                                xpp.next();
                                // sets longitude
                                earthquake.setLongitude(xpp.getText());
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {

                            earthquakeList.addLast(earthquake);

                        }

                        // gets the next event
                        eventType = xpp.next();
                    }


                } catch (XmlPullParserException ae1) {

                    Log.e("MyTag", "Pull Parser Error" + ae1.toString());


                } catch (IOException ae1) {

                    Log.e("MyTag", "IO Exception Error" + ae1.toString());

                } catch (ParseException e) {

                    throw new RuntimeException(e);
                }


                Log.e("MyTag", "End document");


                // Now update the TextView to display raw XML data
                // Probably not the best way to update TextView
                // but we are just getting started !


                // Update the UI on the main thread using another Executor instance
                Executor mainExecutor = new MainThreadExecutor();
                mainExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // UI update code goes here

                        // Populate the ListView with the earthquake incident titles
                        ArrayAdapter<Earthquake> adapter = new ArrayAdapter<Earthquake>(MainActivity.this, android.R.layout.simple_list_item_1, earthquakeList);
                        quakeListView.setAdapter(adapter);

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

}






//
// Name                 Michele Moretti
// Student ID           S2222142
// Programme of Study   Computing
//
package org.me.gcu.moretti_michele_s2222142;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.LinkedList;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    public EarthquakeAdapter(Context context, LinkedList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Earthquake earthquake = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.filtered_list, parent, false);
        }

        // Lookup view for data population
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);

        // Populate the data into the template view using the data object
        titleTextView.setText(earthquake.getTitle());

        // Set the background color based on the magnitude of the earthquake
        double magnitude = earthquake.getMagnitude();
        int colorResourceId;
        if (magnitude < 0.2) {
            colorResourceId = R.color.magnitude1;
        } else if (magnitude < 0.5) {
            colorResourceId = R.color.magnitude2;
        } else if (magnitude < 1.1) {
            colorResourceId = R.color.magnitude3;
        } else if (magnitude < 2.1) {
            colorResourceId = R.color.magnitude4;
        }
        else {
            colorResourceId = R.color.magnitude5;
            }
            int color = ContextCompat.getColor(getContext(), colorResourceId);
            convertView.setBackgroundColor(color);


            // Return the completed view to render on screen
        return convertView;
    }
}

package edu.uw.tgents.selftracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by tgents on 1/25/2016.
 */
public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.detail_layout, container, false);

        Bundle bundle = getArguments();

        if(bundle != null) {
            TextView titleView = (TextView) rootView.findViewById(R.id.title);
            TextView descView = (TextView) rootView.findViewById(R.id.description);

            Activity thing = (Activity) bundle.get("Activity");

            titleView.setText(thing.getQuantity());
            descView.setText(thing.getComment());
        }

        return rootView;
    }
}

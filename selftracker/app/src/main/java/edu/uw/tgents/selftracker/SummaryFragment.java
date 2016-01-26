package edu.uw.tgents.selftracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by tgents on 1/25/2016.
 */
public class SummaryFragment extends Fragment {
    private static final String TAG = "SummaryFragment";

    public SummaryFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.detail_layout, container, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        titleView.setText("Summary");

        Bundle bundle = getArguments();
        int count;
        if(bundle != null){
            count = bundle.getInt("numAct");
            TextView descView = (TextView) rootView.findViewById(R.id.description);
            descView.setText("You currently have " + count + " activities!");
        }

        return rootView;
    }
}

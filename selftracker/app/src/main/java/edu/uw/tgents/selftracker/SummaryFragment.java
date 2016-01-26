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
        final TextView descView = (TextView) rootView.findViewById(R.id.description);
        titleView.setText("Summary");

        Firebase fireDB = new Firebase("https://infoselftracker.firebaseio.com/activities");
        fireDB.addValueEventListener(new ValueEventListener() {
            ArrayList<Activity> tempList;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int temp = (int) snapshot.getChildrenCount();
                descView.setText("You have recorded " + temp + " activities!");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("MainActivity", "The read failed: " + firebaseError.getMessage());
            }
        });

        return rootView;
    }
}

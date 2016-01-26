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
    private int numAct = 0;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public void setNumAct(int temp) {
        numAct = temp;
    }

    public int getNumAct() {
        Firebase fireDB = new Firebase("https://infoselftracker.firebaseio.com/activities");
        fireDB.addValueEventListener(new ValueEventListener() {
            ArrayList<Activity> tempList;

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int temp = 0;
                for (DataSnapshot thing : snapshot.getChildren()) {
                    temp++;
                }
                setNumAct(temp);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("MainActivity", "The read failed: " + firebaseError.getMessage());
            }
        });
        Log.v("temp", numAct+"");
        return numAct;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.detail_layout, container, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        titleView.setText("Summary");

        TextView descView = (TextView) rootView.findViewById(R.id.description);
        descView.setText("You currently have " + getNumAct() + " activities!");


        return rootView;
    }
}

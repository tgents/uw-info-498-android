package edu.uw.tgents.selftracker;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by tgents on 1/25/2016.
 */
public class RecordFragment extends DialogFragment {
    private static final String TAG = "RecordFragment";
    private int currentOrientation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.record_layout, container, false);

        Button submit = (Button) rootView.findViewById(R.id.postBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText q = (EditText) rootView.findViewById(R.id.quantity);
                EditText com = (EditText) rootView.findViewById(R.id.comment);
                post(Integer.parseInt(q.getText().toString()),com.getText().toString());
                getDialog().cancel();
            }
        });

        return rootView;
    }

    private Activity post(int quantity, String comment) {
        Activity activity = new Activity(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis(), quantity, comment);
        Firebase fireDB = new Firebase("https://infoselftracker.firebaseio.com/activities");
        fireDB.push().setValue(activity);
        Toast.makeText(getActivity(), "Successfully added!", Toast.LENGTH_SHORT).show();
        return activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
}

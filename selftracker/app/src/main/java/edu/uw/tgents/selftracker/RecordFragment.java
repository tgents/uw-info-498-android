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

    private OnPostListener callback;

    public interface OnPostListener {
        public void onPost(Activity activity);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            callback = (OnPostListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnPostListener");
        }

    }

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
                if(q.getText().toString().equals("") || com.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Please do not leave blank.", Toast.LENGTH_LONG).show();
                }else{
                    Activity activity = post(Integer.parseInt(q.getText().toString()),com.getText().toString());
                    Toast.makeText(getActivity(), "Successfully added!", Toast.LENGTH_LONG).show();
                    ((OnPostListener) getActivity()).onPost(activity);
                }
                getDialog().cancel();
            }
        });

        return rootView;
    }

    private Activity post(int quantity, String comment) {
        Activity activity = new Activity(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis(), quantity, comment);
        Firebase fireDB = new Firebase("https://infoselftracker.firebaseio.com/activities");
        fireDB.push().setValue(activity);

        return activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
}

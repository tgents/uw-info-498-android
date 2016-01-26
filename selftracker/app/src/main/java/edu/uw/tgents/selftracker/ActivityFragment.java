package edu.uw.tgents.selftracker;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tgents on 1/25/2016.
 */
public class ActivityFragment extends Fragment {
    private static final String TAG = "ActivityFragment";
    private ArrayAdapter<Activity> adapter;

    private OnActivitySelectionListener callback;

    public interface OnActivitySelectionListener {
        public void onActivitySelected(Activity activity);
    }

    public ActivityFragment() {
        // Required empty public constructor
    }

    public ArrayAdapter<Activity> getAdapter() {
        return adapter;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            callback = (OnActivitySelectionListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnActivitySelectionListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_layout, container, false);

        ArrayList<Activity> list = new ArrayList<Activity>();

        //controller
        adapter = new ArrayAdapter<Activity>(
                getActivity(), R.layout.list_item, R.id.txtItem, list);

        AdapterView listView = (AdapterView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = (Activity) parent.getItemAtPosition(position);
                Log.i(TAG, "selected: " + activity.toString());

                //swap the fragments to show the detail
                ((OnActivitySelectionListener) getActivity()).onActivitySelected(activity);
            }
        });

        final Firebase fireDB = new Firebase("https://infoselftracker.firebaseio.com/activities");
        fireDB.addValueEventListener(new ValueEventListener() {
            ArrayList<Activity> tempList;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tempList = new ArrayList<Activity>();
                for(DataSnapshot thing : snapshot.getChildren()){
                    Map<String, Object> data = (HashMap<String,Object>) snapshot.child(thing.getKey()).getValue();

                    long time = (Long) data.get("time");
                    int quantity = (int)(long) data.get("quantity");
                    String comment = (String) data.get("comment");

                    tempList.add(new Activity(time, quantity, comment));
                }
                Collections.sort(tempList);
                adapter.clear();
                adapter.addAll(tempList);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("MainActivity","The read failed: " + firebaseError.getMessage());
            }
        });

        return rootView;
    }
}

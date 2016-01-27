package edu.uw.tgents.selftracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ActivityFragment.OnActivitySelectionListener {
    private int currentOrientation;
    private ActivityFragment home;
    private int numAct = 0;
    Firebase fireDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Firebase.setAndroidContext(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fireDB = new Firebase("https://infoselftracker.firebaseio.com/activities");
        getNumAct();
        home = new ActivityFragment();

        LinearLayout rightPanel = (LinearLayout) findViewById(R.id.container_right);
        if (rightPanel != null && rightPanel.getVisibility() == View.VISIBLE) {
            currentOrientation = 2;
        } else {
            currentOrientation = 1;
        }
        showActivityFragment();
    }

    public void setNumAct(int temp) {
        numAct = temp;
        Log.v("main", numAct+"");
    }

    public int getNumAct() {
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
        return numAct;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.sumBtn);
        if (currentOrientation == 2) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager().popBackStack();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                return true;
            case R.id.addBtn:
                showRecordDialog();
                return true;
            case R.id.sumBtn:
                showSummaryFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRecordDialog() {
        final RecordFragment newFragment = new RecordFragment();
        newFragment.show(getSupportFragmentManager(), "dialog");

    }

    private void showActivityFragment() {
        if (currentOrientation == 2) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_right, home)
                    .commit();
            showSummaryFragment();
        } else if (currentOrientation == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, home)
                    .commit();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void showSummaryFragment() {
        SummaryFragment sum = new SummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("numAct", numAct);
        Log.v("putin", bundle.get("numAct")+"");
        sum.setArguments(bundle);
        if (currentOrientation == 2) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_left, sum)
                    .commit();
        } else if (currentOrientation == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, sum)
                    .addToBackStack(null)
                    .commit();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivitySelected(Activity activity) {
        DetailFragment detail = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("time", activity.getTimeString());
        bundle.putInt("quantity", activity.getQuantity());
        bundle.putString("comment", activity.getComment());


        detail.setArguments(bundle);

        if (currentOrientation == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, detail)
                    .addToBackStack(null)
                    .commit();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (currentOrientation == 2) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_left, new ActivityFragment())
                    .replace(R.id.container_right, detail)
                    .addToBackStack(null)
                    .commit();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


}

package edu.uw.tgents.motion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "main";

    private DrawingSurfaceView view;
    private SensorManager mSensorManager;
    private Sensor mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        view = (DrawingSurfaceView) findViewById(R.id.drawingView);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tap to destroy red. (Only every 10 seconds)\nTilt to move blue.\nNew Game to restart.")
                .setTitle("Instructions")
                .setNegativeButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startGame(null);
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getRawX();
        int touchY = (int) event.getRawY();
        if (view.tapCooldown <= 0) {
            view.playerTap.x = touchX;
            view.playerTap.y = touchY;
        }

        return super.onTouchEvent(event);
    }

    public void startGame(View v) {
        view.newGame();
    }

    @Override
    protected void onResume() {
        //register sensor
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        //unregister sensor
        mSensorManager.unregisterListener(this, mOrientation);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v(TAG, "z: " + event.values[0]);
        Log.v(TAG, "x: " + event.values[1]);
        Log.v(TAG, "y: " + event.values[2]);
        if (event.values[2] < -15.0) {
            if (Math.abs(event.values[1]) < 45) {
                view.player.x = view.col3;
            } else {
                view.player.x = view.col1;
            }
        } else if (event.values[2] > 15.0) {
            if (Math.abs(event.values[1]) < 45) {
                view.player.x = view.col1;
            } else {
                view.player.x = view.col3;
            }
        } else {
            view.player.x = view.col2;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

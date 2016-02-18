package edu.uw.tgents.motion;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "main";

    private DrawingSurfaceView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        view = (DrawingSurfaceView)findViewById(R.id.drawingView);
    }

    public void startGame(View v){
        v.setVisibility(View.INVISIBLE);
        view.setPause(false);
    }


    private boolean checkCollision(DrawObject thing1, DrawObject thing2) {
        int dx = thing1.x - thing2.x;
        int dy = thing1.y - thing2.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < thing1.radius + thing2.radius) {
            return true;
        }
        return false;
    }

}

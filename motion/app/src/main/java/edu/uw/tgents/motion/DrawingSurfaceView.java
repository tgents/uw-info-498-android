package edu.uw.tgents.motion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * An example SurfaceView for generating graphics on
 *
 * @author Joel Ross
 * @version Winter 2016
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceView";
    public DrawObject player;

    //    private Bitmap bmp; //image to draw on
    private int viewWidth, viewHeight; //size of the view
    private SurfaceHolder mHolder; //the holder we're going to post updates to
    private DrawingRunnable mRunnable; //the code htat we'll want to run on a background thread
    private Thread mThread; //the background thread
    private Paint redPaint; //drawing variables (pre-defined for speed)
    private boolean pause = false;
    private boolean firstLoad = true;
    private int col1;
    private int col2;
    private int col3;
    private int obstacleRadius;
    private ArrayList<DrawObject> obstacles;

    private int fallSpeed = 5;


    /**
     * We need to override all the constructors, since we don't know which will be called
     */
    public DrawingSurfaceView(Context context) {
        this(context, null);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawingSurfaceView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);

        viewWidth = 1;
        viewHeight = 1; //positive defaults; will be replaced when #surfaceChanged() is called

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        //set up drawing variables ahead of timme
        redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(Color.RED);

        player = new DrawObject();
        obstacles = new ArrayList<DrawObject>();
    }


    /**
     * Helper method for the "game loop"
     */
    public void update() {
        //update the "game state" here (move things around, etc.
        //TODO: fill in your own logic here!
        if (firstLoad) {
            firstLoad = !firstLoad;
            setPause(true);
        }

        if (!pause) {
            if (obstacles.size() == 0 || obstacles.get(obstacles.size()-1).y > viewHeight/2) {
                obstacles.add(new DrawObject(chooseCol(), 0, obstacleRadius));
            }

            for (DrawObject obs : obstacles) {
                obs.y += fallSpeed;
            }
        }

    }


    /**
     * Helper method for the "render loop"
     *
     * @param canvas The canvas to draw on
     */
    public void render(Canvas canvas) {
        if (canvas == null) return; //if we didn't get a valid canvas for whatever reason

        //TODO: replace the below example with your own rendering

        canvas.drawColor(Color.BLACK); //black out the background
        canvas.drawCircle(player.x, player.y, player.radius, redPaint); //draw player
        for (int i = 0; i < obstacles.size(); i++) {
            DrawObject obj = obstacles.get(i);
            canvas.drawCircle(obj.x, obj.y, obj.radius, redPaint);
            if(obj.y > viewHeight+obj.radius){
                obstacles.remove(i);
            }
        }

        //see http://developer.android.com/reference/android/graphics/Canvas.html for a list of options

//        for(int x=50; x<viewWidth-50; x++) { //most of the width
//            for(int y=100; y<110; y++) { //10 pixels high
//                bmp.setPixel(x, y, Color.BLUE); //we can also set individual pixels in a Bitmap (like a BufferedImage)
//            }
//        }

        //Canvas bmc = new Canvas(bmp); //we can also make a canvas out of a Bitmap to draw on that (like fetching g2d from a BufferedImage)
        //canvas.drawBitmap(bmp,0,0,null); //and then draw the BitMap onto the canvas.
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // create thread only; it's started in surfaceCreated()
        Log.v(TAG, "making new thread");
        mThread = new Thread(mRunnable);
        mRunnable.setRunning(true); //turn on the runner
        mThread.start(); //start up the thread when surface is created

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        synchronized (mHolder) { //synchronized to keep this stuff atomic
            viewWidth = width;
            viewHeight = height;
//            bmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); //new buffer to draw on.

            // set up obstacles
            col1 = viewWidth / 6;
            col2 = 3 * viewWidth / 6;
            col3 = 5 * viewWidth / 6;
            obstacleRadius = viewWidth / 6;

            // set up player
            player.x = col2;
            player.radius = obstacleRadius;
            player.y = viewHeight - player.radius;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        mRunnable.setRunning(false); //turn off
        boolean retry = true;
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
                //will try again...
            }
        }
        Log.d(TAG, "Drawing thread shut down.");
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    private int chooseCol(){
        int newX = col1;
        double temp = Math.random();
        if (temp < (1 / 3.0)) {
            newX = col2;
        } else if (temp > (2 / 3.0)) {
            newX = col3;
        }
        return newX;
    }

    /**
     * An inner class representing a runnable that does the drawing. Animation timing could go in here.
     * http://obviam.net/index.php/the-android-game-loop/ has some nice details about using timers to specify animation
     */
    public class DrawingRunnable implements Runnable {

        private boolean isRunning; //whether we're running or not (so we can "stop" the thread)

        public void setRunning(boolean running) {
            this.isRunning = running;
        }

        public void run() {
            Canvas canvas;
            while (isRunning) {
                canvas = null;
                try {
                    canvas = mHolder.lockCanvas(); //grab the current canvas
                    synchronized (mHolder) {
                        update(); //update the game
                        render(canvas); //redraw the screen
                    }
                } finally { //no matter what (even if something goes wrong), make sure to push the drawing so isn't inconsistent
                    if (canvas != null) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
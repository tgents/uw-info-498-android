package edu.uw.tgents.motion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An example SurfaceView for generating graphics on
 *
 * @author Joel Ross
 * @version Winter 2016
 */
public class DrawingSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "SurfaceView";
    private static final int FALL_SPEED_SLOW = 5;
    private static final int FALL_SPEED_NORMAL = 7;
    private static final int FALL_SPEED_FAST = 10;
    private static final int DEFAULT_COOLDOWN = 10000;
    // interactive variables
    public DrawObject player;
    public DrawObject playerTap;
    public int col1;
    public int col2;
    public int col3;
    public int tapCooldown;
    //view dimensions
    private int viewWidth, viewHeight;
    // background thread stuff
    private SurfaceHolder mHolder;
    private DrawingRunnable mRunnable;
    private Thread mThread;
    // game attributes
    private Paint playerColor;
    private Paint obstacleColor;
    private int obstacleRadius;
    private ArrayList<DrawObject> obstacles;
    // game status
    private boolean pause = false;
    private boolean firstLoad = true;
    //sounds
    private SoundPool mSoundPool;
    private int[] soundIds;
    private boolean[] loadedSound;


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

        initializeSoundPool();

        viewWidth = 1;
        viewHeight = 1;

        // register our interest in hearing about changes to our surface
        mHolder = getHolder();
        mHolder.addCallback(this);

        mRunnable = new DrawingRunnable();

        // set draw object colors
        playerColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        playerColor.setColor(Color.BLUE);
        obstacleColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        obstacleColor.setColor(Color.RED);
    }

    public void newGame() {
        player = new DrawObject();
        playerTap = new DrawObject(-100, -100, 10);
        obstacles = new ArrayList<DrawObject>();

        // set up obstacles and columns
        col1 = viewWidth / 6;
        col2 = 3 * viewWidth / 6;
        col3 = 5 * viewWidth / 6;
        obstacleRadius = viewWidth / 6;

        // set up player
        player.x = col2;
        player.radius = obstacleRadius;
        player.y = viewHeight - player.radius;
        tapCooldown = 0;
        setPause(false);
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

        if (!pause && isSoundLoaded()) {
            if (obstacles.size() == 0 || obstacles.get(obstacles.size() - 1).y > viewHeight / 2) {
                obstacles.add(new DrawObject(chooseCol(), 0, obstacleRadius));
                if (Math.random() < 0.1) {
                    obstacles.add(new DrawObject(chooseCol(), 0, obstacleRadius));
                }
                playSound(1);
            }

            for (int i = 0; i < obstacles.size(); i++) {
                DrawObject obs = obstacles.get(i);
                obs.y += FALL_SPEED_NORMAL;
                if (checkCollision(player, obs)) {
                    playSound(2);
                    //reset game
                    newGame();
                }
                if (checkCollision(obs, playerTap)) {
                    //reset to default
                    tapCooldown = DEFAULT_COOLDOWN;
                    obstacles.remove(i);
                    playerTap.x = -100;
                    playerTap.y = -100;
                    playSound(0);
                }
            }

            tapCooldown--;
        }
    }

    private boolean isSoundLoaded(){
        for(boolean b : loadedSound) {
            if(!b) {
                return false;
            }
        }
        return true;
    }

    private void initializeSoundPool() {

        final int MAX_STREAMS = 4;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attribs = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(attribs)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        soundIds = new int[3];
        loadedSound = new boolean[3];

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) {
                    if (sampleId == soundIds[0]) {
                        loadedSound[0] = true;
                    } else if (sampleId == soundIds[1]) {
                        loadedSound[1] = true;
                    } else if (sampleId == soundIds[2]) {
                        loadedSound[2] = true;
                    }
                }
            }
        });

        // sound sources:
        // http://soundbible.com/2067-Blop.html
        // http://soundbible.com/1320-Short-Circuit.html
        // http://soundbible.com/1682-Robot-Blip.html
        soundIds[0] = mSoundPool.load(getContext(), R.raw.start, 0);
        soundIds[1] = mSoundPool.load(getContext(), R.raw.spawn, 0);
        soundIds[2] = mSoundPool.load(getContext(), R.raw.end, 0);
    }

    public void playSound(int index) {
        if (loadedSound[index]) {
            mSoundPool.play(soundIds[index], 1, 1, 1, 0, 1);
        }
    }


    /**
     * Helper method for the "render loop"
     *
     * @param canvas The canvas to draw on
     */
    public void render(Canvas canvas) {
        if (canvas == null) return; //if we didn't get a valid canvas for whatever reason

        canvas.drawColor(Color.BLACK); //black out the background

        for (int i = 0; i < obstacles.size(); i++) {
            DrawObject obj = obstacles.get(i);
            canvas.drawCircle(obj.x, obj.y, obj.radius, obstacleColor);
            if (obj.y > viewHeight + obj.radius / 2) {
                obstacles.remove(i);
            }
        }

        canvas.drawCircle(player.x, player.y, player.radius, playerColor); //draw player

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
            newGame();

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

    private int chooseCol() {
        int lastX = obstacles.size() > 0 ? obstacles.get(obstacles.size() - 1).x : col2;
        int newX;
        double temp = Math.random();
        if (lastX == col2) {
            if (temp < .5) {
                newX = col1;
            } else {
                newX = col3;
            }
        } else if (lastX == col1) {
            if (temp < .5) {
                newX = col2;
            } else {
                newX = col3;
            }
        } else {
            if (temp < .5) {
                newX = col1;
            } else {
                newX = col2;
            }
        }
        return newX;
    }

    private boolean checkCollision(DrawObject thing1, DrawObject thing2) {
        int dx = thing1.x - thing2.x;
        int dy = thing1.y - thing2.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < thing1.radius + thing2.radius - thing1.radius / 10) {
            return true;
        }
        return false;
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
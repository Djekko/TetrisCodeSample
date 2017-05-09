/**
 * Created by Karl Spurgin on 18/12/2016.
 */

package com.example.tetris;

import android.app.Activity;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

public class GameActivity extends Activity implements GestureFilter.SimpleGestureListener{

    private GameView gameView;
    private GestureFilter detector;

    public static int MOVE_LEFT = 0;
    public static int ROTATE = 1;
    public static int MOVE_DOWN = 2;
    public static int MOVE_RIGHT = 3;

    public static Activity Game;

    MediaPlayer GameSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Game = this;

        // Detect touched area
        detector = new GestureFilter(this,this);

        GameSound = MediaPlayer.create(this,R.raw.tetris);
        GameSound.setLooping(true);

        setupGameActivity();
    }

    public void setupGameActivity()
    {
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();


        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Passing in the screen resolution to the constructor
        gameView = new GameView(this, size.x, size.y);

        // Make our gameView the view for the Activity
        setContentView(gameView);

        //set the maximum swipe distance to the width of the screen
        detector.setSwipeMaxDistance(size.x);

    }   //   public void setupGameActivity()


    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause()
    {
        super.onPause();

        GameSound.pause();

        gameView.pause();

    }   //  protected void onPause()

    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume()
    {
        super.onResume();

        GameSound.start();
        gameView.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                gameView.movePiece(ROTATE);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                gameView.movePiece(MOVE_RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                gameView.movePiece(MOVE_DOWN);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                gameView.movePiece(MOVE_LEFT);
                break;
        }

        return super.onKeyDown(keyCode, msg);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of GestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        switch (direction) {
            case GestureFilter.SWIPE_UP :
                gameView.movePiece(ROTATE);
                break;
            case GestureFilter.SWIPE_RIGHT :
                gameView.movePiece(MOVE_RIGHT);
                break;
            case GestureFilter.SWIPE_DOWN :
                gameView.movePiece(MOVE_DOWN);
                break;
            case GestureFilter.SWIPE_LEFT :
                gameView.movePiece(MOVE_LEFT);
                break;
        }
    }

    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }
}

package com.example.spaceinvaders;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;


public class SpaceGameView extends SurfaceView implements Runnable{
    private SoundPool soundPool;

    // alternate which menace sound should play next
    private boolean uhOrOh;
    private long lastMenaceTime = System.currentTimeMillis();

    private Context context;

    // This is our thread
    private Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    private int screenX;
    private int screenY;

    // The score
    private int score = 0;

    // Lives
    private int lives = 5;

    private Spaceship spaceShip;
    private Bullet bullet;
    private Bitmap bitmapback;

    // SM Invaders
    private Invader[] invaders = new Invader[24];
    private int numInvaders = 0;

    // how menacing should the sound be
    private long menaceInterval = 1000;

    //    sound
    private int uhID = -1;
    private int ohID = -1;




    // This special constructor method runs
    public SpaceGameView(Context context, int x, int y) {

        // The next line of code asks the
        // SurfaceView class to set up our object.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        initLevel();
    }
    //data declarations -
    //create a new instance of the spaceship pass the sizes by invoking the constructor method of the spaceship class
    private void initLevel(){

        // make a new  spaceship, player's bullets, invader's bullets, invaders, shelters
        spaceShip = new Spaceship(context, screenX, screenY);
        bullet = new Bullet(screenY,screenX);

        // Create an array of invaders
        numInvaders = 0;
        for (int column = 0; column < 6; column++)
            for (int row = 0; row < 3; row++) {
                invaders[numInvaders++] = new Invader(context, row, column, screenX, screenY);
//                numInvaders++;
            }

    }


    @Override
    public void run() {
        while (playing) {
            score = 0;
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
            // play a sound based on the menace level
            if (!paused) {
                if ((startFrameTime - lastMenaceTime) > menaceInterval) {
                    if (uhOrOh) soundPool.play(uhID, 1, 1, 0, 0, 1);
                    else soundPool.play(ohID, 1, 1, 0, 0, 1);
                    // reset the last menace time
                    lastMenaceTime = System.currentTimeMillis();
                    // flip value of uhOrOh
                    uhOrOh = !uhOrOh;
                }
            }

        }

    }



    private void update(){

        //defender.update(fps);
        //checkCollisions
        spaceShip.update(fps);

        if(bullet.getStatus())
            bullet.update(fps);

        checkCollisions();
    }



    private void checkCollisions(){
        //  if (spaceShip.getX() > screenX - spaceShip.getLength())
        //     spaceShip.setX(0);
        //  if (spaceShip.getX() < 0 + spaceShip.getLength())
        //      spaceShip.setX(screenX);

        //   if (spaceShip.getY() > screenY - spaceShip.getLength())
        //       spaceShip.setY(0);
        //   if (spaceShip.getY() < 0 + spaceShip.getLength())
        //       spaceShip.setY(screenY);

        if(bullet.getImpactPointY() < 0)
            bullet.setInactive();
        if(bullet.getImpactPointY() > screenY)
            bullet.setInactive();

        if(bullet.getImpactPointX() < 0)
            bullet.setInactive();
        if(bullet.getImpactPointX() > screenX)
            bullet.setInactive();

    }


    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));


            bitmapback = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
            bitmapback = Bitmap.createScaledBitmap(bitmapback, (int) (screenX), (int) (screenY),false);

            //  SM draw the defender spaceShip.getY()/2
            canvas.drawBitmap(bitmapback, 0, 0, paint);
            // SM Now draw the player spaceship
            canvas.drawBitmap(spaceShip.getBitmap(), spaceShip.getX(), spaceShip.getY(), paint);

            // SM draw the invaders
            for (int i = 0; i < numInvaders; i++)
                if (invaders[i].getVisibility())
                    if (uhOrOh)
                        canvas.drawBitmap(invaders[i].getBitmap(), invaders[i].getX(), invaders[i].getY(), paint);
                    else
                        canvas.drawBitmap(invaders[i].getBitmap2(), invaders[i].getX(), invaders[i].getY(), paint);

            if(bullet.getStatus())
                canvas.drawRect(bullet.getRect(), paint);
            // Draw the score and remaining lives and Change the brush color to organge
            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(40);
            // Co-ordinates x10 y50
            canvas.drawText("Score: " + score + "   Lives: " + lives, 1600,50, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // If SpaceGameActivity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If Activity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            //direction of the bllet up=0, down=1,right=2, left= 3
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getY() > screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.RIGHT);
                        //shooting
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength(),spaceShip.getY()+ spaceShip.getHeight()/2,2);
                    } else {
                        spaceShip.setMovementState(spaceShip.LEFT);
                        bullet.shoot(spaceShip.getX(),spaceShip.getY()+ spaceShip.getHeight()/2,3);
                    }

                }

                if(motionEvent.getY() < screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.UP);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY(),0);
                    } else {
                        spaceShip.setMovementState(spaceShip.DOWN);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY()+ spaceShip.getHeight(),1);
                    }


                }

//                    if(motionEvent.getY() < screenY - screenY / 8) {
//                 //Shots fired
//                       if(bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,screenY)){
//                        soundPool.play(shootID, 1, 1, 0, 0, 1);
//                       }
//                   }
               break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                //   if(motionEvent.getY() > screenY - screenY / 10) {
                spaceShip.setMovementState(spaceShip.STOPPED);
                //   }
                break;
        }
        return true;
    }  //end onTouchEvent



}  // end class SpaceGameView

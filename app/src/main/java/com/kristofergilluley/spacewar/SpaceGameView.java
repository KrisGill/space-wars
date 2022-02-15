package com.kristofergilluley.spacewar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
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
    public int score = 0;

    // Lives
    private int lives = 4;


    private HeroShip spaceShip;
    private Missile bullet;
    private Bitmap bitmapback;
    private SpaceBug [][] bug = new SpaceBug[6][4];
    private ShooterShip shooterShip,shooterShip2;
    private BigBadBoss boss;


    // This special constructor method runs
    public SpaceGameView(Context context, int x, int y) {

        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;



        initLevel();
    }

    private void initLevel(){

        spaceShip = new HeroShip(context, screenX, screenY);
        bullet = new Missile(context,screenX,screenY);
        for(int i=0; i<bug.length;i++)
            for(int j=0;j<bug[1].length;j++)
        {
            bug[i][j]= new SpaceBug(context,screenX,screenY,i,j+2);
        }
        shooterShip = new ShooterShip(context, screenX, screenY,2,0,6);
        shooterShip2 = new ShooterShip(context,screenX,screenY,0,2,6);
        boss=new BigBadBoss(context,screenX,screenY);

    }

    @Override
    public void run() {
        while (playing) {
            score = 10;
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

        }

    }

    private void update()
    {
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
            //  canvas.drawBitmap(background.getBitmap(), spaceShip.getX(), spaceShip.getY() , paint);
            //  draw the defender
            canvas.drawBitmap(bitmapback, 0, 0, paint);
            canvas.drawBitmap(spaceShip.getBitmap(), spaceShip.getX(), spaceShip.getY() , paint);
//            canvas.drawBitmap(bullet.getBitmap(),bullet.getX(),bullet.getY(),paint);
//         if(bullet.getStatus())
       //         canvas.drawRect(bullet.getRect(), paint);

            for(int sp =0;sp<bug.length;sp++)
                for(int ss=0;ss<bug[1].length;ss++)
            {
                canvas.drawBitmap(bug[sp][ss].getBitmap(),bug[sp][ss].getX(),bug[sp][ss].getY(),paint);
            }
            canvas.drawBitmap(shooterShip.getBitmap(),shooterShip.getX(),shooterShip.getY(),paint);
            canvas.drawBitmap(shooterShip2.getBitmap(),shooterShip2.getX(),shooterShip2.getY(),paint);
            canvas.drawBitmap(boss.getBitmap(),boss.getX(),boss.getY(),paint);
            // Draw the score and remaining lives
            // Change the brush color
            paint.setColor(Color.argb(255,  20, 255, 0));
            paint.setTextSize(50);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);
            paint.setTextSize(screenX/8);
            paint.setColor(Color.argb(255,255,50,43));
           // paint.setTypeface(Typeface.create("Biome", Typeface.BOLD));
            paint.setTypeface(Typeface.create("biorhyme_bold",Typeface.BOLD));//font not working but bold is
            canvas.drawText("SPACE WAR",screenX/5,(screenY/4)+50,paint);

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
    // If SpaceInvadersActivity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
  //  @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen

            case MotionEvent.ACTION_DOWN:
                Log.e("Touch","The screen has been touched 4");
                paused = false;

                if(motionEvent.getY() > screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.RIGHT);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength(),spaceShip.getY()+ spaceShip.getHeight()/2,2);
                        Log.e("Touch","The screen has been touched 4");
                    } else {
                        spaceShip.setMovementState(spaceShip.LEFT);
                        bullet.shoot(spaceShip.getX(),spaceShip.getY()+ spaceShip.getHeight()/2,3);
                        Log.e("Touch","The screen has been touched 4");
                    }
                }

                if(motionEvent.getY() < screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.UP);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY(),0);
                        Log.e("Touch","The screen has been touched 4");
                    } else {
                        spaceShip.setMovementState(spaceShip.DOWN);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY()+ spaceShip.getHeight(),1);
                        Log.e("Touch","The screen has been touched 4");
                    }
                    Log.e("Touch","The screen has been touched 4");
                }

                //    if(motionEvent.getY() < screenY - screenY / 8) {
                // Shots fired
                //       if(bullet.shoot(playerShip.getX()+ playerShip.getLength()/2,screenY)){
                //        soundPool.play(shootID, 1, 1, 0, 0, 1);
                //       }
                //   }

                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

                //   if(motionEvent.getY() > screenY - screenY / 10) {
                spaceShip.setMovementState(spaceShip.STOPPED);
                Log.e("Touch","The screen has been touched 4");
                //   }
                break;
        }
        return true;
    }
}  // end class
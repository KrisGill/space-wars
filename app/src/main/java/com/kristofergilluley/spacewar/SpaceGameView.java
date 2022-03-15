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
import java.util.Random;

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
    private Missile bullet, badBullet;
    private Bitmap bitmapback;
    private SpaceBug [][] bug = new SpaceBug[15][3];
    private ShooterShip shooterShip,shooterShip2;
    private BigBadBoss boss;
    private Boolean bumped;
    private Random random = new Random();
    private int shooterShipLaunch=0,shooterShipShoot=0;


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
        badBullet=new Missile(context,screenX,screenY);
        for(int i=0; i<bug.length;i++)
            for(int j=0;j<bug[1].length;j++)
        {
            bug[i][j]= new SpaceBug(context,screenX,screenY,i,j);
            bug[i][j].setMovementState(2);
        }
        shooterShip = new ShooterShip(context, screenX, screenY,2,0,6);
        shooterShip2 = new ShooterShip(context,screenX,screenY,0,2,6);
        boss=new BigBadBoss(context,screenX,screenY);
        bumped=false;
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
        for(int i=0; i<bug.length;i++)
            for(int j=0;j<bug[1].length;j++)
            {
                bug[i][j].update(fps);
                if(bug[i][j].changeDirection())
                    bumped=true;
            }
        if(bumped)
        {
            for(int x=0;x< bug.length;x++)
                for(int y=0;y<bug[1].length;y++)
                    bug[x][y].dropDownAndReverse();
                bumped=false;
        }
        shooterShipLaunch=random.nextInt(100);
        shooterShipShoot=random.nextInt(10);
        Log.e("Random",String.valueOf(shooterShipLaunch));
        if(shooterShipLaunch==50  && !shooterShip.getVisible())
            shooterShip.setVisibility(true);
       if(shooterShip.getVisible()) {
            shooterShip.update(fps);
            if(shooterShipShoot==5) {
              //  badBullet.shoot(shooterShip.getX(), shooterShip.getY(), 1);
               // badBullet.update(fps);
               // shooterShip.shoot();
            }
        }
        if(shooterShip.getVisible() && shooterShipShoot==5) {
            badBullet.shoot(shooterShip.getX() + (shooterShip.getLength()/2), shooterShip.getY(), 1);
            badBullet.update(fps);
        }

        if(shooterShip.getX()>screenX) {//resets shootership location
            shooterShip.setVisibility(false);
            shooterShip.setX(0);
            shooterShip.setY(screenY/8);
        }
        if(badBullet.getStatus())
            badBullet.update(fps);

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

        if(badBullet.getImpactPointY()>screenY)
            badBullet.setInactive();
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
//           canvas.drawBitmap(bullet.getBitmap(),bullet.getX(),bullet.getY(),paint);
            if(bullet.getStatus())
                canvas.drawRect(bullet.getRect(), paint);
            if(badBullet.getStatus())
                canvas.drawRect(badBullet.getRect(),paint);

            for(int i =0;i<bug.length;i++)
                for(int j=0;j<bug[1].length;j++)
            {
                if(bug[i][j].getVisibility())
                canvas.drawBitmap(bug[i][j].getBitmap(),bug[i][j].getX(),bug[i][j].getY(),paint);
            }
            if(shooterShip.getVisible())
            canvas.drawBitmap(shooterShip.getBitmap(),shooterShip.getX(),shooterShip.getY(),paint);

         //   canvas.drawBitmap(boss.getBitmap(),boss.getX(),boss.getY(),paint);
            // Draw the score and remaining lives
            // Change the brush color
            paint.setColor(Color.argb(255,  20, 255, 0));
            paint.setTextSize(50);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);
            paint.setTextSize(screenX/8);
            paint.setColor(Color.argb(255,255,50,43));
           // paint.setTypeface(Typeface.create("Biome", Typeface.BOLD));
           // paint.setTypeface(Typeface.create("biorhyme_bold",Typeface.BOLD));//font not working but bold is
            //canvas.drawText("SPACE WAR",screenX/5,(screenY/4)+50,paint);

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
               // Log.e("Touch", "The screen has been touched 4");
                paused = false;

                if(motionEvent.getY() > screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.RIGHT);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength(),spaceShip.getY()+ spaceShip.getHeight()/2,2);
                        Log.e("Touch","The screen has been touched 1");
                    } else {
                        spaceShip.setMovementState(spaceShip.LEFT);
                        bullet.shoot(spaceShip.getX(),spaceShip.getY()+ spaceShip.getHeight()/2,3);
                       // badBullet.shoot(screenX/2,0,1); //testing bad bullet
                        Log.e("Touch","The screen has been touched 2");
                    }
                }
                if(motionEvent.getY() < screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.UP);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY(),0);
                        Log.e("Touch","The screen has been touched 3");
                    } else {
                        spaceShip.setMovementState(spaceShip.DOWN);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY()+ spaceShip.getHeight(),1);
                        Log.e("Touch","The screen has been touched 4");
                    }
                    Log.e("Touch","The screen has been touched 4");
        }
        break;
            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                spaceShip.setMovementState(spaceShip.STOPPED);
                Log.e("Touch","The screen has been touched 4");
                break;
        }
        return true;
    }
}  // end class
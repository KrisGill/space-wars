package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    public int highScore, scoreDiff;


    private HeroShip spaceShip;
    private Missile bullet, badBullet;
    private Bitmap bitmapback;
    private SpaceBug [][] bug = new SpaceBug[15][3];
    private ShooterShip shooterShip,shooterShip2;
    private BigBadBoss boss;
    private Planet planet;
    private Boolean bumped;
    private Random random = new Random();
    private int shooterShipLaunch=0,shooterShipShoot=0;
    private int bulletDirection=1;
    private int bugGone;
    public boolean gameContinue=true, newHighScore=false;
    private PlayAgain playAgain;


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

     //   playAgain = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_again_button);
      //  playAgain = Bitmap.createScaledBitmap(playAgain, (int) (screenX/4), (int) (screenY/2),false);

        screenX = x;
        screenY = y;

        initLevel();
    }

    private void initLevel(){
        paused=true;
        spaceShip = new HeroShip(context, screenX, screenY);
        bullet = new Missile(context,screenX,screenY);
        bullet.setSpeed(1200);
        badBullet=new Missile(context,screenX,screenY);
        planet = new Planet(context,screenX,screenY,screenX/2,(screenY/5)*3);
        for(int i=0; i<bug.length;i++)
            for(int j=0;j<bug[1].length;j++)
        {
            bug[i][j]= new SpaceBug(context,screenX,screenY,i,j+1);
            bug[i][j].setMovementState(2);
        }
        bugGone=bug.length*bug[1].length;
        shooterShip = new ShooterShip(context, screenX, screenY,2,0,6);
        boss=new BigBadBoss(context,screenX,screenY);
        bumped=false;
        playAgain = new PlayAgain(context, screenX,screenY);
        playAgain.setVisibility(false);
    }

    private void reloadGame()
    {
        for (SpaceBug[] spaceBugs : bug)
            for (int j = 0; j < bug[1].length; j++) {
                spaceBugs[j].setVisibility(true);
                spaceBugs[j].setActive();
            }
        //shooterShip.setVisibility(true);
        spaceShip.setVisibility(true);
        planet.setExists(true);
        placeSpaceship();
        replaceAlien();
        score=0;
        spaceShip.setLives(3);
        newHighScore=false;
        planet.setHealth(3);
    }

    @Override
    public void run() {
        while (playing) {
          //  score = 10;
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
        if(fps>0) {//prevents random crashes
            //Log.e("FPS", String.valueOf(fps));
            if(spaceShip.getVisible())
            spaceShip.update(fps);
            planet.update();
            for (int i = 0; i < bug.length; i++)
                for (int j = 0; j < bug[1].length; j++) {
                    if (bug[i][j].isVisible)
                        bug[i][j].update(fps);
                    if (bug[i][j].changeDirection())
                        bumped = true;
                }
            if (bumped) {
                for (int x = 0; x < bug.length; x++)
                    for (int y = 0; y < bug[1].length; y++)
                        bug[x][y].moveDownChangeDirection();
                bumped = false;
            }
            shooterShipLaunch = random.nextInt(100);
            shooterShipShoot = random.nextInt(10);
            Log.e("Random", String.valueOf(shooterShipLaunch));
            if (shooterShipLaunch == 50 && !shooterShip.getVisible()) {
                shooterShip.setLives(2);
                shooterShip.setX(0);
                shooterShip.setY(screenY / 8);
                shooterShip.setVisibility(true);
            }
            if (shooterShip.getVisible()) {
                shooterShip.update(fps);
            }
            if (shooterShip.getVisible() && shooterShipShoot == 5) {//shoots enemy bullet
                badBullet.shoot(shooterShip.getX() + (shooterShip.getLength() / 2), shooterShip.getY(), 1);
                badBullet.update(fps);
            }
            if (badBullet.getStatus())
                badBullet.update(fps);
            if (bullet.getStatus())
                bullet.update(fps);
            checkCollisions();

            if(spaceShip.getLives()<1 || planet.getHealth()<1)
                gameContinue=false;

            if(!gameContinue)
            {
                for (SpaceBug[] spaceBugs : bug)
                    for (int j = 0; j < bug[1].length; j++) {
                        spaceBugs[j].setVisibility(false);
                        spaceBugs[j].setInactive();
                    }
                shooterShip.setVisibility(false);
                spaceShip.setVisibility(false);
                planet.setExists(false);

                if(score>highScore) {
                    newHighScore = true;
                    scoreDiff=score-highScore;
                    highScore=score;
                }
            }

            if(bugGone<1)
            {
                for (SpaceBug[] spaceBugs : bug)
                    for (int j = 0; j < bug[1].length; j++) {
                        spaceBugs[j].setVisibility(true);
                        spaceBugs[j].setActive();
                    }
                //shooterShip.setVisibility(true);
                spaceShip.setVisibility(true);
                planet.setExists(true);
                placeSpaceship();
                replaceAlien();
                bugGone=bug.length*bug[1].length;
                Log.e("Bug Gone",String.valueOf(bugGone));
            }
        }
    }

    private void checkCollisions(){
        //Bullet collisons
        if(bullet.getImpactPointY() < 0)
            bullet.setInactive();
        if(bullet.getImpactPointY() > screenY)
            bullet.setInactive();
        if(bullet.getImpactPointX() < 0)
            bullet.setInactive();
        if(bullet.getImpactPointX() > screenX)
            bullet.setInactive();

        //Shooter ship goes off screen
        if(shooterShip.getY() < 0)
            shooterShip.setVisibility(false);
        if(shooterShip.getY() > screenY)
            shooterShip.setVisibility(false);
        if(shooterShip.getX() < 0)
            shooterShip.setVisibility(false);
        if(shooterShip.getX() > screenX)
            shooterShip.setVisibility(false);

        //Shooter ship bullet goes off screen, it only goes 1 way
        if(badBullet.getImpactPointY()>screenY)
            badBullet.setInactive();

        //If players bullet hits shoter ship
        if(RectF.intersects(bullet.getRect(),shooterShip.getRect()))
        {
            if(bullet.getStatus())
            {
                bullet.setInactive();
                shooterShip.lives--;
                if (shooterShip.getLives() == 0) {
                    shooterShip.setVisibility(false);
                    score +=50;
                }
                Log.e("ShooterShip", "Shootership lives: " + String.valueOf(shooterShip.lives));
            }
        }

        //if the bullets collide
        if(RectF.intersects(badBullet.getRect(),bullet.getRect()))
        {
            badBullet.setInactive();
            bullet.setInactive();
        }

        //Shooter ship bullet hits user
        if(RectF.intersects(badBullet.getRect(),spaceShip.getRect()))
        {
            if(badBullet.getStatus()) {
                spaceShip.setVisibility(false);
                spaceShip.lives--;
                badBullet.setInactive();
                placeSpaceship();
            }
        }

        if(RectF.intersects(badBullet.getRect(),planet.getRect()))
        {
            if(badBullet.getStatus())
            {
                badBullet.setInactive();
                planet.health--;
            }
        }

        //Space bug interacts with players bullet, or crashes into user
        for(int i=0; i<bug.length;i++)
            for(int j=0;j<bug[1].length;j++)
            {
                if(RectF.intersects(bullet.getRect(),bug[i][j].getRect())) {
                    if(bullet.getStatus()) {//only effect space bugs is bullet is active
                        if (bug[i][j].getStatus()) {
                            bullet.setInactive();
                            score +=10;
                            bugGone--;
                            // bullet.setRecAfterCollision();
                        }
                        bug[i][j].setVisibility(false);
                        bug[i][j].setInactive();
                        Log.e("Intersect", "Bullet intersect with Spacebug");
                    }
                }

                if(RectF.intersects(bug[i][j].getRect(),spaceShip.getRect())){
                    if(bug[i][j].isActive)
                    {
                        bug[i][j].setInactive();
                        bugGone--;
                        spaceShip.lives --;
                        placeSpaceship();
                        replaceAlien();
                        Log.e("Intersect", "Spaceship intesect with Bug");
                    }
                }
                if(bug[i][j].isActive&& bug[i][j].getY() > screenY) {
                    replaceAlien();
                    placeSpaceship();
                }

                if(RectF.intersects(bug[i][j].getRect(),planet.getRect()))
                {
                    if(bug[i][j].isActive)
                    {
                        bug[i][j].setInactive();
                        planet.health --;
                        replaceAlien();;
                    }
                }
            }
    }
    private void replaceAlien()
    {
        for(int i=0; i<bug.length;i++)
            for(int j=0;j<bug[1].length;j++)
            {
                bug[i][j].resetShips();
                bug[i][j].setMovementState(2);
            }

    }

    private void placeSpaceship()
    {
        spaceShip.setX(screenX/2);
        spaceShip.setY((screenY/6)*5);
        spaceShip.setVisibility(true);
    }

    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            //canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));
            bitmapback = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
            bitmapback = Bitmap.createScaledBitmap(bitmapback, (int) (screenX), (int) (screenY),false);
            //  canvas.drawBitmap(background.getBitmap(), spaceShip.getX(), spaceShip.getY() , paint);
            //  draw the defender
            canvas.drawBitmap(bitmapback, 0, 0, paint);
            if(spaceShip.getVisible())
            canvas.drawBitmap(spaceShip.getBitmap(), spaceShip.getX(), spaceShip.getY() , paint);
//           canvas.drawBitmap(bullet.getBitmap(),bullet.getX(),bullet.getY(),paint);
            if(bullet.getStatus())
                canvas.drawRect(bullet.getRect(), paint);
            if(badBullet.getStatus())
                canvas.drawRect(badBullet.getRect(),paint);

            for(int i =0;i<bug.length;i++)
                for(int j=0;j<bug[1].length;j++)
            {
                if(bug[i][j].getStatus())
                    canvas.drawBitmap(bug[i][j].getBitmap(),bug[i][j].getX(),bug[i][j].getY(),paint);
            }
            if(shooterShip.getVisible())
            canvas.drawBitmap(shooterShip.getBitmap(),shooterShip.getX(),shooterShip.getY(),paint);

            if(planet.getExists())
            canvas.drawBitmap(planet.getBitmap(),planet.getX(),planet.getY(),paint);

            // Draw the score and remaining lives
            // Change the brush color
            if(gameContinue) {
                paint.setColor(Color.argb(255, 20, 255, 0));
                paint.setTextSize(75);
                canvas.drawText("Score: " + score + "   Lives: " + spaceShip.getLives(), 10, 95, paint);
                paint.setTextSize(screenX / 8);
                paint.setColor(Color.argb(255, 255, 50, 43));
            }

            if(!gameContinue)
            {
                paint.setColor(Color.argb(255,  255, 50, 10));
                paint.setTextSize(250);
                canvas.drawText("GAME OVER",screenX/4,screenY/20+150, paint);
               // bitmapback = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
               // bitmapback = Bitmap.createScaledBitmap(bitmapback, (int) (screenX), (int) (screenY),false);

                //paint.setColor(Color.argb(255,  20, 255, 0));
                paint.setTextSize(100);
                paint.setColor(Color.argb(255,  100, 255, 100));
                canvas.drawText(" The Final Score: " + score, (screenX/4)+200,(screenY/20)+260, paint);
                if(newHighScore)
                {
                    paint.setTextSize(75);
                    canvas.drawText(" A New High Score! You beat the old High Score by " + scoreDiff + " Points!", screenX/15,(screenY/20)+370, paint);
                }
                paint.setTextSize(screenX/8);
                paint.setColor(Color.argb(255,255,50,43));
                canvas.drawBitmap(playAgain.getBitmap(),screenX/4,screenY/2-100,paint);
            }
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

                if(!gameContinue)
            {
                if(motionEvent.getY()>screenY/2 && motionEvent.getX()>screenX/4 && motionEvent.getX()<(screenX/4)*3)
                {
                    gameContinue=true;
                    reloadGame();
                    Log.e("New Game", "New game should begin");
                }
            }
                if(motionEvent.getY()>screenY/3 && motionEvent.getY()<(screenY/3)*2){
                    if(motionEvent.getX()<screenX/3) {
                        spaceShip.setMovementState(spaceShip.LEFT);
                        bulletDirection=3;
                    }
                    if(motionEvent.getX()>((screenX/3)*2)) {
                        spaceShip.setMovementState(spaceShip.RIGHT);
                        bulletDirection=2;
                    }
                }
                if(motionEvent.getX()>screenX/3 && motionEvent.getX()<(screenX/3)*2)
                {
                    if(motionEvent.getY()<screenY/3) {
                        spaceShip.setMovementState(spaceShip.UP);
                        bulletDirection=0;
                    }
                    if(motionEvent.getY()>(screenY/3)*2) {
                        spaceShip.setMovementState(spaceShip.DOWN);
                        bulletDirection=1;
                    }
                }

                if(motionEvent.getY()>screenY/3 && motionEvent.getY()<(screenY/3*2)
                     && motionEvent.getX()> screenX/3 && motionEvent.getX()<(screenX/3*2))//touched middle of the screen
                {
                    if(spaceShip.getVisible())
                    bullet.shoot(spaceShip.getX()+spaceShip.getLength()/2,spaceShip.getY()+ spaceShip.getHeight()/4,bulletDirection);
                }


              /*  if(motionEvent.getY() > screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.RIGHT);
                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength(),spaceShip.getY()+ spaceShip.getHeight()/2,2);
                        Log.e("Touch","The screen has been touched 1");
                    } else {
                      //  spaceShip.setMovementState(spaceShip.LEFT);
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
        }*/
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
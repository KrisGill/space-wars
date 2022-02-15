package com.kristofergilluley.tankhell;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.io.Serializable;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Random;

public class TankWarView extends SurfaceView implements Runnable {

    //data declarations
    private Context context;
    private Thread gameThread = null;
    private SurfaceHolder ourHolder;
    private volatile boolean playing;
    private boolean paused = true, gameOver=false;
    private boolean gamestart=false;
    private Canvas canvas;
    private Canvas canvas2;
    private Paint paint;
    private long fps;
    private long timeThisFrame;
    private int screenX;
    private int screenY;
    private int score = 0;
    private int lives = 5;
    private int tankRandomDir;
    private Bitmap bit = BitmapFactory.decodeResource(getResources(),R.drawable.tankdown);
    float float1=0,float2=0;
    Random random = new Random();
    private Tank tank,tank2,tank3;
    private Bullet bullet,bullet2,bullet3,badBullet;
    private int bulletDirection, bulletsFired=0;
    private String bullet1status="Ready",bullet2status="Ready",bullet3status="Ready";


    public TankWarView(Context context, int x, int y)
    {
        super(context);
        this.context = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        initLevel();
        run();
    }

    private void initLevel()
    {
        tank = new Tank(context,screenX,screenY,screenX,screenY,350);
        tank2 = new Tank(context,screenX,screenY,screenX/2,screenY/2,175);
        bullet=new Bullet(screenX/15,screenX/15);
        bullet2=new Bullet(screenX/15,screenX/15);
        bullet3=new Bullet(screenX/15,screenX/15);
        badBullet=new Bullet(screenX/15,screenX/15);
    }

    @Override
    public void run() {
        while (playing)
        {
            if(lives>0) {
                long startFrameTime = System.currentTimeMillis();
                if (!paused) {
                    update(fps);
                }
                badTankUpdate(fps);
                movingBullet(fps);
                draw();
                moveEnemyTank(tank2);

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
            else
            {
                gameOver=true;
                drawGameOver();
            }
        }
    }
    private void update(long fps)
    {
        tank.update(fps);
        Log.d("Tag",String.valueOf(tank.getState()));

        checkCollisions(tank,tank2);
    }
    private void badTankUpdate(long fps)
    {
        if(gamestart==true){
            tank2.update(fps);
        }
        bulletShoot(badBullet,tank2);

        if(checkCollisions(tank2,tank)==true)
        {
            tank2 = new Tank(context,screenX,screenY,screenX,screenY,175);
            //look for random number
            tankRandomDir=random.nextInt(6)+1;
        };
    }
    private boolean checkCollisions(Tank tank, Tank tank2)//check if tanks are hitting or bullets hitting or whatver
    {
        //code if for tanks crashing into the sides of the screen
        if(tank.getX()<=0)
        {
            tank.setX(tank.getX()+20);
            return true;
        }

        if ((tank.getX()+tank.getLength())>screenX)
        {
            tank.setX(tank.getX()-20);
            return true;
        }
        if(tank.getY()<=0-(tank.getHeight()/2))
        {
            tank.setY(screenY);
            return true;
        }
        if(tank.getY()>=screenY-(tank.getHeight()-20))
        {
            // tank.setY(tank.getY()-50);
            tank.setY(0);
            return true;
        }

        if(RectF.intersects(tank.getRect(),tank2.getRect()))
        {
            if(tank.getState()==tank.UP)
            {
                tank.setY(tank.getY()+30);
                tank2.setY(tank2.getY()-30);
            }
            if(tank.getState()==tank.DOWN)
            {
                tank.setY(tank.getY()-30);
                tank2.setY(tank2.getY()+30);
            }
            if(tank.getState()==tank.RIGHT)
            {
                tank.setX(tank.getX()-30);
                tank2.setX(tank2.getX()+30);
            }
            if(tank.getState()==tank.LEFT)
            {
                tank.setX(tank.getX()+30);
                tank2.setX(tank2.getX()-30);
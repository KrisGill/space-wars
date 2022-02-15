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
        tank2 = new Tank(cont
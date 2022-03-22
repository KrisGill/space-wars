package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Spaceship {

    RectF rect;
    private Bitmap bitmapup;
    private Bitmap bitmapleft;
    private Bitmap bitmapright;
    private Bitmap bitmapdown;
    public Bitmap currentBitmap;
    private float height;
    private float length;
    private float x;
    private float y;
    private int screenX;
    private int screenY;
    private float SpaceShipSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    public final int UP = 3;
    public final int DOWN = 4;

    ///maybe more movement than this
    private int SpaceShipMoving = STOPPED;
    private int spaceShipSpeed;

    public Spaceship(Context context, int screenX, int screenY){

        rect = new RectF();

        length = screenX/10;
        height = screenY/10;

        x = screenX / 2;
        y = screenY / 2;

        spaceShipSpeed = 350;

        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceshipup);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmapup = Bitmap.createScaledBitmap(bitmapup,
                (int) (length),
                (int) (height),
                false);

        bitmapright = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceshipright);
        bitmapright = Bitmap.createScaledBitmap(bitmapright, (int) (length), (int) (height),false);

        bitmapleft = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceshipleft);
        bitmapleft = Bitmap.createScaledBitmap(bitmapleft, (int) (length), (int) (height),false);

        bitmapdown = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceshipdown);
        bitmapdown = Bitmap.createScaledBitmap(bitmapdown, (int) (length), (int) (height),false);

        currentBitmap = bitmapleft;
        this.screenX = screenX;
        this.screenY = screenY;
    }
    //method setMovementState in state eg 0 1 2 3 4 including stop state
    public void setMovementState(int state){
        SpaceShipMoving = state;
    }

//uupdate method
    public void update(long fps){
        if(SpaceShipMoving == LEFT){
            x = x - spaceShipSpeed / fps;
            currentBitmap = bitmapleft;
            if ((x+length)<=0)
                x = screenX;
        }
        if(SpaceShipMoving == RIGHT){
            x = x + spaceShipSpeed / fps;
            currentBitmap = bitmapright;
            if (x>=screenX)
                x = 0 - length;
        }
        if(SpaceShipMoving == UP){
            y = y - spaceShipSpeed / fps;
            currentBitmap = bitmapup;
            if (y+height <=0)
                y = screenY;
        }

        if(SpaceShipMoving == DOWN){
            y = y + spaceShipSpeed / fps;
            currentBitmap = bitmapdown;
            if (y>=screenY)
                y = 0 - height;
        }

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public RectF getRect(){

        return rect;
    }

    public Bitmap getBitmap(){

        return currentBitmap;
    }

    public float getX(){

        return x;
    }
    public void setX(int x) {

        this.x = x;
    }
    public float getY(){

        return y;
    }
    public void setY(int y){

        this.y = y;
    }
    public float getLength(){

        return length;
    }
    public float getHeight(){

        return height;
    }

} //end the class spaceship


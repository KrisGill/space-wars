package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

public class Planet extends Spaceship
{
    public int health;
    private Bitmap bitmapDis, bitmapFull, bitmapMed, bitmapLow;
    private int screenX;
    private int screenY;
    private float height;
    private float width;
    private float x;
    private float y;
  //  public RectF rect;
    private boolean exists;

    public Planet(Context context, int screenX, int screenY, int locX, int locY)
    {
        super(context,screenX,screenY);
        width=screenX/7;
        height=screenY/5;
        rect = new RectF();

        this.x=locX;
        this.y=locY;

        exists=true;

        health=3;

        bitmapFull = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_health_full);
        bitmapFull = Bitmap.createScaledBitmap(bitmapFull, (int) width, (int) height,false);

        bitmapMed = BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_health_med);
        bitmapMed = Bitmap.createScaledBitmap(bitmapMed, (int) width, (int) height,false);

        bitmapLow= BitmapFactory.decodeResource(context.getResources(), R.drawable.planet_health_low);
        bitmapLow = Bitmap.createScaledBitmap(bitmapLow, (int) width, (int) height,false);

        bitmapDis=bitmapFull;

        this.screenX=screenX;
        this.screenY=screenY;
    }

    public void update()
    {
        rect.top =y;
        rect.bottom =y + height;
        rect.left =x;
        rect.right =x + width;

        if(health==3)
            bitmapDis=bitmapFull;
        if(health==2)
            bitmapDis=bitmapMed;
        if(health==1)
            bitmapDis=bitmapLow;
    }

    public Bitmap getBitmap()
    {
        return bitmapDis;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public RectF getRect(){
        return rect;
    }

    public boolean getExists() {
        return exists;
    }

    public void setExists(Boolean exists) {
        this.exists = exists;
    }

    public int getHealth()
    {
        return health;
    }

    public void setHealth(int num)
    {
        health=num;
    }
}

package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;


public class ShooterShip extends Spaceship
{
    private Missile bullet;

    public ShooterShip(Context context, int screenX, int screenY, int live, int locX, int locY) {
        super(context, screenX, screenY);

        rect = new RectF();
        lives=live;
        length = screenX / 8;
        height = screenY / 8;

      //  x = (screenX / 10) + (length*locX);
      //  y = screenY / 10 + (height*locY);

        x=0-length;
        y=screenY/8;

        spaceShipSpeed = 200;
        visibility=false;
        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.alienspaceship);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmapup = Bitmap.createScaledBitmap(bitmapup,
                (int) (length),
                (int) (height),
                false);

        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.alienspaceship);
        bitmapup = Bitmap.createScaledBitmap(bitmapup, (int) (length), (int) (height), false);

        bitmapdown = BitmapFactory.decodeResource(context.getResources(), R.drawable.alienspaceshipcracked);
        bitmapdown = Bitmap.createScaledBitmap(bitmapdown, (int) (length), (int) (height), false);

        if(lives==2) {
            currentBitmap = bitmapup;
        }
        if(lives<2) {
            currentBitmap = bitmapdown;
        }
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public String getLives()
    {
        String str = String.valueOf(lives);
        return str;
    }

    public void update(long fps)
    {
            x = x + spaceShipSpeed / fps;
            currentBitmap = getBitmap();
            if ((x + length) <= 0)
                x = screenX;
    }

    public void shoot()
    {
        Log.e("Random","bang");
    }
}

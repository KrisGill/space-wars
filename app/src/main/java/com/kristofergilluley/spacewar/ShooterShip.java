package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

public class ShooterShip extends Spaceship
{
    public ShooterShip(Context context, int screenX, int screenY, int live, int locX, int locY) {
        super(context, screenX, screenY);

        rect = new RectF();
        lives=live;
        length = screenX / 4;
        height = screenY / 10;

        x = (screenX / 10) + (length*locX);
        y = screenY / 10 + (height*locY);

        spaceShipSpeed = 350;
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
}

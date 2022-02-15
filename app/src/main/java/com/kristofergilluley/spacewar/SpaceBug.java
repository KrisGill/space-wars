package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class SpaceBug extends Spaceship
{


    public SpaceBug(Context context, int screenX, int screenY, int lives, int locationX, int locationY)
    {
        super(context,screenX,screenY);
        this.lives=lives;

        rect = new RectF();

        length = screenX/20;
        height = screenY/20;

        x = (screenX / 10) + (locationX*length);
        y = (screenY / 10) + (locationY*height);

        spaceShipSpeed = 350;
        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spacemonster);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmapup = Bitmap.createScaledBitmap(bitmapup,
                (int) (length),
                (int) (height),
                false);

        currentBitmap = bitmapup;
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public SpaceBug(Context context, int screenX, int screenY,int locX, int locY)
    {
        super(context, screenX, screenY);

        rect = new RectF();

        length = screenX/7;
        height = screenY/10;

        x = screenX / 10 + (length*locX);
        y = screenY /10 + (height*locY);

        spaceShipSpeed = 350;
        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spacemonster);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmapup = Bitmap.createScaledBitmap(bitmapup,
                (int) (length),
                (int) (height),
                false);

        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spacemonster);
        bitmapup = Bitmap.createScaledBitmap(bitmapup, (int) (length), (int) (height),false);

        currentBitmap = bitmapup;
        this.screenX = screenX;
        this.screenY = screenY;
    }

}

package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class SpaceBug extends Spaceship {
    boolean isVisible,bumped;
    int id;

    public SpaceBug(Context context, int screenX, int screenY, int lives, int locationX, int locationY) {
        super(context, screenX, screenY);
        this.lives = lives;

        rect = new RectF();

        isVisible = true;

        length = screenX / 50;
        height = screenY / 50;

        x = (screenX / 10) + (locationX * length);
        y = (screenY / 10) + (locationY * height);

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

    public SpaceBug(Context context, int screenX, int screenY, int locX, int locY) {
        super(context, screenX, screenY);

        rect = new RectF();

        isVisible = true;
        bumped=false;

        length = screenX / 20;
        height = screenY / 10;

        // x = screenX/2  + (length*locX);
        //y = screenY /10 + (height*locY);
        x = length * locX;
        y = height * locY;

        spaceShipSpeed = 150;
        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spacemonster);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmapup = Bitmap.createScaledBitmap(bitmapup,
                (int) (length),
                (int) (height),
                false);

        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spacemonster);
        bitmapup = Bitmap.createScaledBitmap(bitmapup, (int) (length), (int) (height), false);

        currentBitmap = bitmapup;
        this.screenX = screenX;
        this.screenY = screenY;

    }

    public void update(long fps) {
        if (spaceShipMoving == LEFT) {
            x = x - spaceShipSpeed / fps;
            currentBitmap = bitmapup;
            if ((x + length) <= 0)
                x = screenX;
        }
        if (spaceShipMoving == RIGHT) {
            x = x + spaceShipSpeed / fps;
            currentBitmap = bitmapup;
            if (x >= screenX)
                x = 0 - length;
        }
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public void dropDownAndReverse() {
        if (spaceShipMoving == LEFT)
            spaceShipMoving = RIGHT;
        else
            spaceShipMoving = LEFT;
        y = y + (height/2);
    }

    public boolean changeDirection()
    {
        if( rect.right>screenX || rect.left<0)
            return true;
        else
            return false;
    }

        public boolean getVisibility ()
        {
            return isVisible;
        }

        public void setVisible ( boolean bool)
        {
            isVisible = bool;
        }
    }

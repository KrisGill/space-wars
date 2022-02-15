package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class EnemyShip extends Spaceship {
    public EnemyShip(Context context, int screenX, int screenY)
    {
        super(context, screenX, screenY);

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

        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceshipup);
        bitmapup = Bitmap.createScaledBitmap(bitmapup, (int) (length), (int) (height),false);

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
}

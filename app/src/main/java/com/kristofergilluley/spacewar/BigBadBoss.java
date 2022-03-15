package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class BigBadBoss extends Spaceship{
    BigBadBoss(Context context,int screenX, int screenY){
        super(context, screenX, screenY);
        rect = new RectF();

        length = screenX/4;
        height = screenY/4;

        x = screenX/5;
        y = 0;

        spaceShipSpeed = 350;
        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.bigboss);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmapup = Bitmap.createScaledBitmap(bitmapup, (int) (length), (int) (height), false);

        currentBitmap = bitmapup;
        this.screenX = screenX;
        this.screenY = screenY;
    }

}

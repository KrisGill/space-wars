package com.kristofergilluley.spacewar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class PlayAgain extends Spaceship{
    boolean startAgain=false;
    public PlayAgain(Context context, int screenX, int screenY) {
        super(context, screenX, screenY);

        rect = new RectF();

        length = screenX/2;
        height = screenY/2;
        visibility=true;
        lives=3;

        x = screenX / 2;
        y = screenY / 2;

        spaceShipSpeed = 350;
        currentBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_again_button);

        // stretch the bitmap to a size appropriate for the screen resolution
        currentBitmap = Bitmap.createScaledBitmap(currentBitmap,
                (int) (length),
                (int) (height),
                false);
    }
}

package com.example.revgame2.Entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.revgame2.Game;

public class Unit extends Entity{
    public Hex hex;

    public Unit(Hex hex, Bitmap selectedTexture, Bitmap noSelectedTexture) {
        super(selectedTexture,noSelectedTexture);
        this.hex = hex;
        //hex.setSquad(this);
        paint = new Paint();
        paint.setTextSize(24);//48

    }

    public void draw(Canvas canvas){
        canvas.drawBitmap(texture,null, hex.rect,paint);
    }
}

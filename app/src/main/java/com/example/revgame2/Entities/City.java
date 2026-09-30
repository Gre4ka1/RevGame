package com.example.revgame2.Entities;

import android.graphics.Canvas;

import com.example.revgame2.Game;
import com.example.revgame2.TextureManager;

public class City extends Unit{
    public int level;
    public float[] screenCoords;

    public City(Hex hex, int level) {
        super(hex, TextureManager.getInstance().getCity1STexture(), TextureManager.getInstance().getCity1Texture());
        this.level=level;
    }

    @Override
    public void draw(Canvas canvas) {
        //super.draw(canvas);
        screenCoords= Game.getInstance().getScreenCoordinates(hex.getX(),hex.getY());
        if (Entity.selectedEntity==hex) {
            if (level == 1) {
                canvas.drawBitmap(TextureManager.getInstance().getCity1STexture(), null, hex.rect, paint);
            } else if (level == 2) {
                canvas.drawBitmap(TextureManager.getInstance().getCity2STexture(), null, hex.rect, paint);
            } else if (level == 3) {
                canvas.drawBitmap(TextureManager.getInstance().getCity3STexture(), null, hex.rect, paint);
            }
        } else {
            if (level == 1) {
                canvas.drawBitmap(TextureManager.getInstance().getCity1Texture(), null, hex.rect, paint);
            } else if (level == 2) {
                canvas.drawBitmap(TextureManager.getInstance().getCity2Texture(), null, hex.rect, paint);
            } else if (level == 3) {
                canvas.drawBitmap(TextureManager.getInstance().getCity3Texture(), null, hex.rect, paint);
            }
        }
    }

}

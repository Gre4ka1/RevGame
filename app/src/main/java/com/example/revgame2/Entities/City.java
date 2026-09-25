package com.example.revgame2.Entities;

import android.graphics.Canvas;

import com.example.revgame2.TextureManager;

public class City extends Unit{
    public int level;


    public City(Hex hex, int level) {
        super(hex, TextureManager.getInstance().getCity1STexture(), TextureManager.getInstance().getCity1Texture());
        this.level=level;
    }

    @Override
    public void draw(Canvas canvas) {
        //super.draw(canvas);
        if (Entity.selectedEntity==hex) {
            if (level == 1) {
                canvas.drawBitmap(TextureManager.getInstance().getCity1STexture(), hex.getX()+hex.globalX, hex.getY()+hex.globalY, paint);
            } else if (level == 2) {
                canvas.drawBitmap(TextureManager.getInstance().getCity2STexture(), hex.getX()+hex.globalX, hex.getY()+hex.globalY, paint);
            } else if (level == 3) {
                canvas.drawBitmap(TextureManager.getInstance().getCity3STexture(), hex.getX()+hex.globalX, hex.getY()+hex.globalY, paint);
            }
        } else {
            if (level == 1) {
                canvas.drawBitmap(TextureManager.getInstance().getCity1Texture(), hex.getX()+hex.globalX, hex.getY()+hex.globalY, paint);
            } else if (level == 2) {
                canvas.drawBitmap(TextureManager.getInstance().getCity2Texture(), hex.getX()+hex.globalX, hex.getY()+hex.globalY, paint);
            } else if (level == 3) {
                canvas.drawBitmap(TextureManager.getInstance().getCity3Texture(), hex.getX()+hex.globalX, hex.getY()+hex.globalY, paint);
            }
        }
    }

}

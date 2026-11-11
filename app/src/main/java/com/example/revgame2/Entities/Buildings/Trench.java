package com.example.revgame2.Entities.Buildings;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Trench extends Building{

    public Trench(Hex hex) {
        super(hex, Relief.FIELD, null, ConstantManager.TRENCH_COST, TextureManager.getInstance().getFortTexture(), TextureManager.getInstance().getFortTexture());
    }

    @Override
    public void draw(Canvas canvas) {
        for (Bitmap b:hex.trenchTextureList){
            canvas.drawBitmap(b,null,hex.rect, paint);
        }
        if (hex.trenchTextureList.size()==0){
            canvas.drawBitmap(TextureManager.getInstance().trenchTextureMap.get(-1),null,hex.rect,paint);
        }
    }
}

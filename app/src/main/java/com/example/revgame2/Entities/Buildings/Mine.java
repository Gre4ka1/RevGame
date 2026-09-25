package com.example.revgame2.Entities.Buildings;

import android.graphics.Bitmap;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Mine extends Building{

    public Mine(Hex hex) {
        super(hex, Relief.MOUNTAIN, null, ConstantManager.MINE_COST, TextureManager.getInstance().getMineTexture(), TextureManager.getInstance().getMineTexture());
    }
}

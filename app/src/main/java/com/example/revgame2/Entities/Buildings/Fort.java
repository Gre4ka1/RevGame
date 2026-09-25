package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Fort extends Building{
    public Fort(Hex hex) {
        super(hex, Relief.FIELD, Relief.MOUNTAIN, ConstantManager.FORT_COST,TextureManager.getInstance().getFortTexture(), TextureManager.getInstance().getFortTexture());
    }
}

package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Factory extends Building{
    public Factory(Hex hex) {
        super(hex, Relief.FIELD,null, ConstantManager.FACTORY_COST,TextureManager.getInstance().getFactoryTexture(), TextureManager.getInstance().getFactoryTexture());
    }
}

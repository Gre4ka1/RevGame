package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Road extends Building{
    public Road(Hex hex) {
        super(hex, Relief.MOUNTAIN, Relief.SWAMP, ConstantManager.ROAD_COST, TextureManager.getInstance().getRoadTexture(), TextureManager.getInstance().getRoadTexture());
    }
}

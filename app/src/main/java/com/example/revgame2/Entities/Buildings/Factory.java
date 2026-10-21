package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Factory extends Building{
    public Factory(Hex hex) {
        super(hex, Relief.FIELD,null, ConstantManager.FACTORY_COST,TextureManager.getInstance().getFactoryTexture(), TextureManager.getInstance().getFactoryTexture());
    }
    @Override
    public void work(Player player) {
        super.work(player);
        player.addMoney(1);
        hex.addReputation(player.fraction,-ConstantManager.INFLUENCE_BAD_BUILDINGS_LOST_DIST0);
        for (Hex h:hex.getNearestHexes()){
            h.addReputation(player.fraction,-ConstantManager.INFLUENCE_BAD_BUILDINGS_LOST_DIST1);
        }
    }
}

package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Farm extends Building{
    public Farm(Hex hex) {
        super(hex, Relief.FIELD, null, ConstantManager.FARM_COST,TextureManager.getInstance().getFarmTexture(), TextureManager.getInstance().getFarmTexture());
    }

    @Override
    public void work(Player player) {
        super.work(player);
        hex.addReputation(player.fraction,ConstantManager.INFLUENCE_FARM_BONUS_DIST0);
        for (Hex h:hex.getNearestHexes()) {
            h.addReputation(player.fraction,ConstantManager.INFLUENCE_FARM_BONUS_DIST1);
        }
    }
}

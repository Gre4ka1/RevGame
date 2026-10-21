package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Sawmill extends Building {
    public Sawmill(Hex hex) {
        super(hex, Relief.FOREST, null, ConstantManager.SAWMILL_COST, TextureManager.getInstance().getSawmillTexture(), TextureManager.getInstance().getSawmillTexture());
    }
    @Override
    public void work(Player player) {
        super.work(player);
        player.addResurces(1);
        hex.addReputation(player.fraction,-ConstantManager.INFLUENCE_BAD_BUILDINGS_LOST_DIST0);
        for (Hex h:hex.getNearestHexes()){
            h.addReputation(player.fraction,-ConstantManager.INFLUENCE_BAD_BUILDINGS_LOST_DIST1);
        }
    }
}

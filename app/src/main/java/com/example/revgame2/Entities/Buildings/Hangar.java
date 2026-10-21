package com.example.revgame2.Entities.Buildings;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

public class Hangar extends Building{
    public Hangar(Hex hex) {
        super(hex, Relief.FIELD,null, ConstantManager.HANGAR_COST, TextureManager.getInstance().getHangarTexture(), TextureManager.getInstance().getHangarTexture());
    }
    @Override
    public void work(Player player) {
        super.work(player);
        hex.addReputation(player.fraction,-ConstantManager.INFLUENCE_BAD_BUILDINGS_LOST_DIST0);
        for (Hex h:hex.getNearestHexes()){
            h.addReputation(player.fraction,-ConstantManager.INFLUENCE_BAD_BUILDINGS_LOST_DIST1);
        }
    }
}

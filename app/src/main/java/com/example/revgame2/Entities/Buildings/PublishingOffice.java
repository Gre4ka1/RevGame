package com.example.revgame2.Entities.Buildings;


import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Fraction;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;

public class PublishingOffice extends Building{
    IdentityHashMap<Player, Fraction> targetFractionByPlayer = new IdentityHashMap<>();
    public PublishingOffice(Hex hex) {
        super(hex, Relief.FIELD, null, ConstantManager.PUBLISHING_OFFICE_COST, TextureManager.getInstance().getPublishingOfficeTexture(), TextureManager.getInstance().getPublishingOfficeTexture());
    }

    @Override
    public void work(Player player) {
        super.work(player);
        if (targetFractionByPlayer.containsKey(player)) {
            Fraction targetFraction = targetFractionByPlayer.get(player);
            hex.addReputation(targetFraction, -ConstantManager.INFLUENCE_PUBLISHING_OFFICE_LOST_DIST0);
            ArrayList<Hex> nearestHexes = hex.getNearestHexes();
            for (Hex h : hex.get36NearestHexes()) {
                if (nearestHexes.contains(h))
                    h.addReputation(targetFraction, -ConstantManager.INFLUENCE_PUBLISHING_OFFICE_LOST_DIST1);
                else
                    h.addReputation(targetFraction, -ConstantManager.INFLUENCE_PUBLISHING_OFFICE_LOST_DIST2);
            }
        }
    }
    public void setTargetFractionByPlayer(Player player, Fraction fraction){
        targetFractionByPlayer.put(player,fraction);
    }
}

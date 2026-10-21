package com.example.revgame2.Entities.Buildings;

import android.graphics.Bitmap;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Unit;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Building extends Unit {
    public static ArrayList<Building> buildings = new ArrayList<>();
    public Relief providedRelief;
    public Relief secondProvidedRelief;
    public final int cost;
    public boolean isWorking = false;
    public Player ownerPlayer = null;
    public Building(Hex hex, Relief providedRelief, Relief secondProvidedRelief, int cost, Bitmap selectedTexture, Bitmap noSelectedTexture) {
        super(hex, selectedTexture, noSelectedTexture);
        this.providedRelief=providedRelief;
        this.secondProvidedRelief=secondProvidedRelief;
        this.cost=cost;
    }
    public void tryWork(){
        if (hex.getSquad()!=null) {
            work(hex.getSquad().player);
            return;
        }
        HashMap<Player,Double> playerInfluenceMap = new HashMap<>();
        for (Hex h:hex.getNearestHexes()) {
            if (h.getSquad()!=null){
                playerInfluenceMap.put(h.getSquad().player,
                        playerInfluenceMap.getOrDefault(h.getSquad().player,new Double(0))+
                                h.getSquad().getHp()*Math.log1p(hex.getReputation(h.getSquad().player.fraction)));
            }
        }
        Player highestInfluencePlayer = null;
        for (Player pl:playerInfluenceMap.keySet()) {
            if (playerInfluenceMap.get(pl) < ConstantManager.PLAYER_INFLUENCE_TO_WORK_LIMIT) continue;
            if (highestInfluencePlayer == null) {
                highestInfluencePlayer = pl;
                continue;
            }
            if (playerInfluenceMap.get(pl)>playerInfluenceMap.get(highestInfluencePlayer)){
                highestInfluencePlayer=pl;
            }

        }
        if (highestInfluencePlayer!=null){
            work(highestInfluencePlayer);
        }

    }
    public void work(Player player){
        isWorking=true;
        ownerPlayer=player;
    }
}

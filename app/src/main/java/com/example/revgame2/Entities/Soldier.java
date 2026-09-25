package com.example.revgame2.Entities;

import android.graphics.Bitmap;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Soldier extends Squad{
    private static ArrayList<Bitmap> selectedSoldierTextureList = new ArrayList<>();
    private static ArrayList<Bitmap> noSelectedSoldierTextureList = new ArrayList<>();
    private static HashMap<Player,Integer> playerSoldierTextureMap = new HashMap<>();
    public Soldier(Hex hex,Player player) {
        super(hex,player, ConstantManager.SPAWN_SOLDIER_COST, true,
                selectedSoldierTextureList.get(playerSoldierTextureMap.get(player)),
                noSelectedSoldierTextureList.get(playerSoldierTextureMap.get(player)));
        maxHp=10;
        defence=2;
        speed=2;
        speedLost=speed;
        distance=1;
        damage=2;
        hp=maxHp;
        moveAfterDefeatSquadFlag=true;
    }
    public static void addTexturesForPlayer(Player player, Bitmap soldierTexture){
        Bitmap selectedSoldierTexture= Bitmap.createScaledBitmap(soldierTexture, (int) (Hex.SIZE_X*0.7), (int) (Hex.SIZE_Y*0.7),true);
        Bitmap noSelectedSoldierTexture= Bitmap.createScaledBitmap(soldierTexture, (int) (Hex.SIZE_X*0.6), (int) (Hex.SIZE_Y*0.6),true);
        if (!playerSoldierTextureMap.containsKey(player)){
            playerSoldierTextureMap.put(player,playerSoldierTextureMap.size());
            selectedSoldierTextureList.add(selectedSoldierTexture);
            noSelectedSoldierTextureList.add(noSelectedSoldierTexture);
        }
    }

}

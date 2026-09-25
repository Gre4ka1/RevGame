package com.example.revgame2;

import com.example.revgame2.Entities.Buildings.Factory;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Buildings.Sawmill;
import com.example.revgame2.Entities.Squad;

import java.util.ArrayList;
import java.util.Random;

public class AI {
    public Player player;
    private Random random = new Random();


    public AI(Player player) {
        this.player = player;

    }
    public void step(){
        for (Hex h:Hex.hexes) {
            h.hexScore=getHexScore(h);
        }
        for (int i = player.squads.size()-1;i > -1; i--) {
            Squad s = player.squads.get(i);


            if (!s.canMove()) continue;
            
            while (s.speedLost > 0) {

                Hex target = null;
                boolean enemyIsNearFlag = false;
                if (s.getHp()>s.getMaxHp()/2){
                    double maxHexDanger = -100;
                    ArrayList<Hex> hs = s.hex.getNearestHexes();
                    Hex maxDangerHex = hs.get(0);
                    for (Hex hex:hs) {
                        double d = getHexDanger1(hex,s.player) + 0.75*s.getHp()*ConstantManager.DANGER1_SCORE_KOEF;

                        if (d>maxHexDanger && hex.relief!=Relief.WATER){
                            maxHexDanger=d;
                            maxDangerHex=hex;

                        }
                    }
                    if (maxHexDanger<=0){
                        if (player.getMoney()<3){
                            if (s.hex.relief==Relief.FIELD && s.hex.getBuilding()==null && s.player.getResources() >= ConstantManager.FACTORY_COST){
                                s.hex.setBuilding(new Factory(s.hex));
                                s.player.spendResurces(ConstantManager.FACTORY_COST);
                                continue;
                            } else {
                                Hex forestHex = null;
                                boolean f = true;
                                for (Hex h : s.hex.getNearestHexes()) {
                                    if (s.hex.getBuilding()==null) {
                                        if (h.relief == Relief.FIELD) {
                                            target = h;
                                            f = false;
                                            break;
                                        }
                                        if (h.relief == Relief.FOREST) {
                                            forestHex = h;
                                        }
                                    }
                                }
                                if (f) target = forestHex;
                            }
                        } else if (player.getResources()<3){
                            if (s.hex.relief==Relief.FOREST && s.hex.getBuilding()==null){
                                if (player.getResources()==0) {
                                    s.hex.removeForest(player);
                                    continue;
                                }
                                else if (s.player.getResources()>=ConstantManager.SAWMILL_COST){
                                    s.hex.setBuilding(new Sawmill(s.hex));
                                    s.player.spendResurces(ConstantManager.SAWMILL_COST);
                                    continue;
                                }
                            } else {
                                for (Hex h : s.hex.getNearestHexes()) {
                                    if (h.relief == Relief.FOREST) {
                                        target = h;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    target = maxDangerHex;
                } else {
                    double minHexDanger = 100;
                    ArrayList<Hex> hs = s.hex.getNearestHexes();
                    Hex minDangerHex = hs.get(0);
                    for (Hex hex:s.hex.getNearestHexes()) {
                        double d = getHexDanger1(hex,s.player);
                        if (d<minHexDanger && hex.relief!=Relief.WATER){
                            minHexDanger=d;
                            minDangerHex=hex;
                        }
                    }
                    target = minDangerHex;
                }

                if (target.getSquad()!=null) s.attack(target.getSquad());
                else s.move(target);

//                ArrayList<Hex> hexes = s.hex.getNearestHexes();
//                double maxHexScore=-100;
//                double secondMaxHexScore=-100;
//                Hex maxScoreHex=hexes.get(0);
//                Hex secondMaxScoreHex=hexes.get(0);
//                for (Hex h : hexes) {
//                    double hexScore=0;
//                    double danger = 0;
//                    int braveKoef = (s.getHp()-s.getMaxHp()/2) /s.getMaxHp();
//                    if (h.getSquad()!=null) {
//                        if (h.getSquad().player==player) hexScore-=100;
//                        else danger+=h.getSquad().getHp();
//                    }
//
//                    hexScore+=2*danger*braveKoef;
//                    if (h.relief==Relief.SWAMP) hexScore+=ConstantManager.SWAMP_SCORE_KOEF;
//                    if (h.relief==Relief.MOUNTAIN) hexScore+=ConstantManager.MOUNTAIN_SCORE_KOEF;
//                    if (h.relief==Relief.FOREST) hexScore+=ConstantManager.FOREST_SCORE_KOEF;
//                    if (h.relief==Relief.WATER) hexScore-=100;
//                    if (h.getCity()!=null) hexScore+=ConstantManager.CITY_SCORE_KOEF;
//                    ArrayList<Hex> nearestHexes = h.get36NearestHexes();
//                    danger = 0;
//                    for (Hex nH: nearestHexes) {
//                        double distKoef=Math.hypot(nH.getX()-h.getX(),nH.getY()-h.getY())>Hex.SIZE_Y/2?ConstantManager.DANGER2_SCORE_KOEF:ConstantManager.DANGER1_SCORE_KOEF;
//                        if (nH.relief==Relief.SWAMP) hexScore-=0.5*ConstantManager.SWAMP_SCORE_KOEF*distKoef;
//                        if (nH.getCity()!=null) hexScore+=0.5*ConstantManager.CITY_SCORE_KOEF*distKoef;
//                        if (nH.getSquad()!=null) {
//                            if (nH.getSquad().player==player) danger-=nH.getSquad().getHp()*0.75*distKoef;
//                            else danger+=nH.getSquad().getHp();
//                        }
//
//                    }
//
//                    hexScore+=danger*braveKoef;
//
//                    if (hexScore>maxHexScore){
//                        secondMaxHexScore=maxHexScore;
//                        maxHexScore=hexScore;
//                        secondMaxScoreHex=maxScoreHex;
//                        maxScoreHex=h;
//                    } else if (hexScore>secondMaxHexScore){
//                        secondMaxHexScore=hexScore;
//                        secondMaxScoreHex=h;
//                    }
//                }
//                Hex target = maxScoreHex;
//                if (random.nextInt(4)==1) target=secondMaxScoreHex;
//                if (target.getSquad()!=null) s.attack(target.getSquad());
//                else s.move(target);

            }
        }
    }
    public static double getHexScore(Hex h){
        double hexScore = 0;
        if (h.relief==Relief.SWAMP) hexScore+=ConstantManager.SWAMP_SCORE_KOEF;
        if (h.relief==Relief.MOUNTAIN) hexScore+=ConstantManager.MOUNTAIN_SCORE_KOEF;
        if (h.relief==Relief.FOREST) hexScore+=ConstantManager.FOREST_SCORE_KOEF;
        if (h.relief==Relief.WATER) hexScore-=100;
        if (h.getCity()!=null) hexScore+=ConstantManager.CITY_SCORE_KOEF;
        ArrayList<Hex> nearestHexes = h.get36NearestHexes();

        for (Hex nH: nearestHexes) {
            double distKoef=Math.hypot(nH.getX()-h.getX(),nH.getY()-h.getY())>Hex.SIZE_Y/2?ConstantManager.DANGER2_SCORE_KOEF:ConstantManager.DANGER1_SCORE_KOEF;
            if (nH.relief==Relief.SWAMP) hexScore+=0.5*ConstantManager.SWAMP_SCORE_KOEF*distKoef;
            if (nH.getCity()!=null) hexScore+=0.5*ConstantManager.CITY_SCORE_KOEF*distKoef;
        }

        return hexScore;
    }
    public static double getHexDanger1(Hex hex, Player player){
        double danger=0;
        if (hex.getSquad()!=null){
            if (hex.getSquad().player==player) danger-=0.75*hex.getSquad().getHp();
            else danger+=hex.getSquad().getHp();
        }

        ArrayList<Hex> hexes = hex.getNearestHexes();
        for (Hex h:hexes){
            if (h.getSquad()!=null){
                if (h.getSquad().player==player) danger-=0.75*h.getSquad().getHp()*ConstantManager.DANGER1_SCORE_KOEF;
                else danger+=h.getSquad().getHp()*ConstantManager.DANGER1_SCORE_KOEF;
            }
        }
        return danger;
    }
}

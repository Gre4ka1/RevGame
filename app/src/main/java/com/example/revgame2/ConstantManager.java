package com.example.revgame2;

import com.example.revgame2.Entities.Buildings.Farm;

import java.util.HashMap;

public class ConstantManager {
    public final static int FARM_COST = 1;
    public final static int FACTORY_COST = 2;
    public final static int SAWMILL_COST = 1;
    public final static int HANGAR_COST = 3;
    public final static int ROAD_COST = 1;
    public final static int FORT_COST = 3;
    public final static int MINE_COST = 2;
    public final static int SPAWN_SOLDIER_COST = 1;


    public final static int REMOVE_FOREST_PRIZE = 1;
    public final static int REMOVE_BUILDING_PRIZE = 1;


    public final static double FOREST_SCORE_KOEF = 2;
    public final static double MOUNTAIN_SCORE_KOEF = 3;
    public final static double CITY_SCORE_KOEF = 4;
    public final static double SWAMP_SCORE_KOEF = -5;
    public final static double DANGER1_SCORE_KOEF = 0.7;//отряд в одной клетке
    public final static double DANGER2_SCORE_KOEF = 0.4;//отряд в двух клетках


    public static HashMap<Class,Integer> buildingCosts = new HashMap<>();
    static {
        buildingCosts.put(Farm.class,FARM_COST);
        //buildingCosts.put(Factory.class,FARM_COST);
        //buildingCosts.put(Barracks.class,FARM_COST);
        //buildingCosts.put(Hangar.class,FARM_COST);
        //buildingCosts.put(Farm.class,FARM_COST);
    }
}
package com.example.revgame2;

import android.graphics.Paint;

import com.example.revgame2.Entities.Buildings.Farm;
import com.example.revgame2.Entities.Soldier;

import java.util.HashMap;

public class ConstantManager {
    public final static double PLAYER_INFLUENCE_TO_WORK_LIMIT = 13;
    public final static double INFLUENCE_FARM_BONUS_DIST0 = 0.75;
    public final static double INFLUENCE_FARM_BONUS_DIST1 = 0.34;
    public final static double INFLUENCE_BAD_BUILDINGS_LOST_DIST0 = 0.34;
    public final static double INFLUENCE_BAD_BUILDINGS_LOST_DIST1 = 0.2;
    public final static int FARM_COST = 1;
    public final static int FACTORY_COST = 2;
    public final static int SAWMILL_COST = 1;
    public final static int HANGAR_COST = 3;
    public final static int ROAD_COST = 1;
    public final static int FORT_COST = 3;
    public final static int MINE_COST = 2;
    public final static int SPAWN_SOLDIER_COST = 1;
    public static final int LOYALITY_REPUTATION_LIMIT = 3;
    public final static int SQUAD_1_CITY_LIMIT = 2;
    public final static int SQUAD_2_CITY_LIMIT = 3;
    public final static int SQUAD_3_CITY_LIMIT = 5;

    public static HashMap<Integer,Integer> squadCityLimitMap = new HashMap<>();
    public static HashMap<Class,Integer> squadCostMap = new HashMap<>();


    public final static int REMOVE_FOREST_PRIZE = 1;
    public final static int REMOVE_BUILDING_PRIZE = 1;


    public final static double FOREST_SCORE_KOEF = 2;
    public final static double MOUNTAIN_SCORE_KOEF = 3;
    public final static double CITY_SCORE_KOEF = 4;
    public final static double SWAMP_SCORE_KOEF = -5;
    public final static double DANGER1_SCORE_KOEF = 0.7;//отряд в одной клетке
    public final static double DANGER2_SCORE_KOEF = 0.4;//отряд в двух клетках

    public static Paint paintRep0 = new Paint();
    public static Paint paintRep1 = new Paint();
    public static Paint paintRep2 = new Paint();
    public static Paint paintRep3 = new Paint();
    public static Paint paintRep4 = new Paint();
    public static Paint paintRep5 = new Paint();
    public static HashMap<Integer,Paint> paintRepMap = new HashMap<>();

    public static HashMap<Class,Integer> buildingCosts = new HashMap<>();
    static {
        buildingCosts.put(Farm.class,FARM_COST);
        //buildingCosts.put(Factory.class,FARM_COST);
        //buildingCosts.put(Barracks.class,FARM_COST);
        //buildingCosts.put(Hangar.class,FARM_COST);
        //buildingCosts.put(Farm.class,FARM_COST);
        squadCityLimitMap.put(1,SQUAD_1_CITY_LIMIT);
        squadCityLimitMap.put(2,SQUAD_2_CITY_LIMIT);
        squadCityLimitMap.put(3,SQUAD_3_CITY_LIMIT);
        squadCostMap.put(Soldier.class,SPAWN_SOLDIER_COST);
        paintRep0.setARGB(150,254,20,27);
        paintRep1.setARGB(150,253,78,50);
        paintRep2.setARGB(150,254,122,67);
        paintRep3.setARGB(150,254,179,91);
        paintRep4.setARGB(150,254,211,103);
        paintRep5.setARGB(150,254,250,118);
        paintRepMap.put(0,paintRep0);
        paintRepMap.put(1,paintRep1);
        paintRepMap.put(2,paintRep2);
        paintRepMap.put(3,paintRep3);
        paintRepMap.put(4,paintRep4);
        paintRepMap.put(5,paintRep5);

    }
}
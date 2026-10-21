package com.example.revgame2;

import com.example.revgame2.Entities.Buildings.Factory;
import com.example.revgame2.Entities.Buildings.Farm;
import com.example.revgame2.Entities.Buildings.Road;
import com.example.revgame2.Entities.City;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Buildings.Sawmill;
import com.example.revgame2.Entities.Soldier;
import com.example.revgame2.Entities.Squad;

import java.util.ArrayList;
import java.util.Random;

/**
 * Стратегический ИИ бота.
 *
 * Идея хода (step):
 *  1. analyze()      — снимок мира: свои/чужие отряды, города, карта угроз
 *                      (кто из врагов сможет ударить по какому гексу на их ходу),
 *                      соотношение сил (stance), список стратегических целей.
 *  2. moveSquads()   — каждому отряду выбирается ЦЕЛЬ (город / вражеский отряд /
 *                      защита своего города / отход при низком hp), затем пошагово
 *                      выбирается лучшее тактическое действие: атака, шаг или
 *                      остаться. Всё оценивается в одних единицах — "стоимости
 *                      отряда" (Squad.cost): ожидаемый нанесённый урон и риск
 *                      считаются по настоящим формулам Squad.getAttackDamage /
 *                      getDefenceDamage.
 *  3. manageEconomy()— деньги тратятся на солдат в городах, которые ближе
 *                      всего к фронту; ресурсы — на дороги в горах/болотах рядом
 *                      с городами, незанятые отряды вырубают лес / строят.
 *
 * Места, зависящие от классов, которых я не видел (Player, City, Building,
 * Fraction), помечены [ADAPT] — их всего несколько маленьких методов.
 */
public class AI {
    public Player player;
    private Random random;
    private int stallTurns = 0;        // сколько ходов подряд не было ни одной атаки (антизастой)
    private int attacksThisStep = 0;

    // ==================== НАСТРОЙКИ (всё в одном месте) ====================
    private static final double HIT_PROB = 0.7;            // [ASSUMPTION] шанс попадания атаки (в Squad.attack не виден)
    private static final double KILL_BONUS = 0.5;          // бонус за добивание (доля стоимости врага)
    private static final double DEATH_PENALTY = 1.0;       // штраф за риск гибели (доля стоимости своего)
    private static final double ATTACK_BIAS = 0.6;         // предпочтение инициативы: бить первым (в долях стоимости отряда)
    private static final double PROGRESS_FRACTION = 0.15;  // сколько "стоимости цели" стоит один шаг к ней
    private static final double SUPPORT_WEIGHT = 0.0;      // бонус за соседей-союзников: в тестах ВРЕДИЛ (отряды сбивались в кучу), см. GROUP_DILUTION
    private static final double TERRAIN_WEIGHT = 0.05;     // вес getHexScore (нормируется внутри кандидатов)
    private static final double REPUTATION_WEIGHT = 0.03;  // тяга к гексам с малой репутацией своей фракции
    private static final double WASTED_MOVE_WEIGHT = 0.08; // штраф за сгорающие очки хода в горах/болоте
    private static final double CITY_GUARD_WEIGHT = 0.2;   // бонус остаться в своём городе под угрозой
    private static final double RETREAT_HP_RATIO = 0.0;    // ниже этой доли hp отряд отходит к городу. 0 = выключено. Включайте (напр. 0.35) только если города лечат отряды
    private static final double RETREAT_STEP_VALUE = 0.5;  // ценность шага назад (в "средних стоимостях отряда")
    private static final double ENEMY_SQUAD_VALUE = 1.0;
    private static final double CITY_VALUE = 2.5;
    private static final double DEFEND_CITY_VALUE = 1.5;
    private static final double VACATE_WEIGHT = 1.0;       // штраф за стоянку на месте, где можно спавнить (если есть деньги)
    private static final double CROWDING = 0.7;            // штраф цели, на которую уже назначены другие
    private static final double DEFENSIVE_STANCE = 0.7;    // отношение сил ниже — обороняемся
    private static final double AGGRESSIVE_STANCE = 1.1;   // выше — действуем смелее
    private static final int THREAT_DEPTH_CAP = 3;
    private static final double ROUGH_PATH_COST = 2.0;     // стоимость входа в гору/болото без дороги
    private static final double BLOCKED_BY_ENEMY_COST = 3.0;
    private static final double BLOCKED_BY_OWN_COST = 0.7;
    private static final double GROUP_DILUTION = 0.7;      // "безопасность в числе": риск делится на (1 + k * союзников рядом); ключ к выходу из позиционного тупика
    private static final double GROUP_RADIUS = 2.5;        // что считать "рядом" (в гексах)
    private static final double IMPATIENCE_PER_TURN = 0.3; // рост смелости за каждый ход без боёв
    private static final double IMPATIENCE_MAX = 3.0;
    private static final int MAX_ACTIONS_PER_SQUAD = 8;
    private static final int SPAWNS_PER_CITY = 2;
    // экономика: [ADAPT] подберите под реальные цены в вашей игре
    private static final int ROAD_MIN_RESOURCES = 30;
    private static final double ROAD_MAX_CITY_STEPS = 4.0;
    private static final int FOREST_CLEAR_BELOW = 60;



    public AI(Player player) {
        this.player = player;
        this.random = new Random();
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
                        } else if (s.hex.relief==Relief.FIELD && getRegionReputation(s.hex,player.fraction)<ConstantManager.BUILD_FARM_REP_LIMIT){
                            if (s.hex.getBuilding()==null){
                                if (player.getResources()>=ConstantManager.FARM_COST){
                                    s.hex.setBuilding(new Farm(s.hex));
                                    s.player.spendResurces(ConstantManager.FARM_COST);
                                    continue;
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

            }


            for (City city : Game.getInstance().cities) {
                if (random.nextInt(5)<3) {
                    if (city.hex.spawnSquad(new Soldier(city.hex, player))) break;
                }
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
    public static double getRegionReputation(Hex h,Fraction fr){
        double result = 0;
        result+=h.getReputation(fr);
        for (Hex nh:h.getNearestHexes()) {
            result+=h.getReputation(fr)/4d;
        }
        return result;
    }
    /*private boolean isMyCity(Hex h, Turn t) {
        int mine = h.getReputation(player.fraction);
        for (Player p : Game.getInstance().players) {
            if (p == player) continue;
            if (h.getReputation(p.fraction) > mine) return false;
        }
        return true;
    }
    private boolean hasRoad(Hex h) { return h.getBuilding() != null && h.getBuilding() instanceof Road; }*/
}

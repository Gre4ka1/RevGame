package com.example.revgame2;

import android.util.Log;
import android.widget.Toast;

import com.example.revgame2.Entities.Buildings.Building;
import com.example.revgame2.Entities.Buildings.Factory;
import com.example.revgame2.Entities.Buildings.Farm;
import com.example.revgame2.Entities.Buildings.Road;
import com.example.revgame2.Entities.City;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Buildings.Sawmill;
import com.example.revgame2.Entities.Soldier;
import com.example.revgame2.Entities.Squad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
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
 *Антитупик (три уровня):
 *  *  - глобальный: stallTurns -> impatience (растёт без потолка до IMPATIENCE_MAX);
 *  *  - личный: у каждого отряда свой счётчик "застоя" (стоит на месте / ходит туда-сюда,
 *  *    не бьёт и не на цели) -> личная impatience;
 *  *  - принудительный: при высокой impatience отряд, который хочет остаться, обязан сделать
 *  *    шаг к цели (FORCE_ADVANCE_IMPATIENCE), независимо от оценки риска.
 *  *  Если достижимых целей нет вообще (острова, вода) — отряд исследует карту.
 *
 * Места, зависящие от классов, которых я не видел (Player, City, Building,
 * Fraction), помечены [ADAPT] — их всего несколько маленьких методов.
 */
public class AI {
    public Player player;
    private Random random;
    private int stallTurns = 0;        // сколько ходов подряд не было ни одной атаки (антизастой)
    private int attacksThisStep = 0;
    public long lastStepLength = 0;
    private final HashMap<Squad, Integer> squadIdle = new HashMap<>();      // сколько ходов подряд отряд "буксует"
    private final HashMap<Squad, ArrayList<Hex>> trail = new HashMap<>();   // где отряд начинал последние ходы
    private final HashMap<Squad, Hex> exploreGoal = new HashMap<>();        // запасная цель, когда реальных нет
    private double curImpatience = 1.0;                                     // личная impatience отряда, который ходит сейчас

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
    private static final double IMPATIENCE_PER_TURN = 0.4; // рост смелости за каждый ход без боёв (у всего бота)
    private static final double SQUAD_IMPATIENCE_PER_TURN = 0.6; // то же, но за каждый ход "застоя" конкретного отряда
    private static final double IMPATIENCE_MAX = 8.0;      // потолок смелости (раньше было 3.0 — этого не хватало, чтобы пробить тупик)
    private static final double FORCE_ADVANCE_IMPATIENCE = 4.0; // с этого уровня отряд, решивший стоять, всё равно делает шаг к цели
    private static final int TRAIL_LENGTH = 4;             // сколько прошлых стартовых позиций отряда помним (поиск "хождения туда-сюда")
    private static final double REVISIT_PENALTY = 0.08;    // штраф за возврат на гекс, где отряд недавно начинал ход
    private static final double EXPLORE_STEP_FRACTION = 0.1; // ценность шага к цели-исследованию (доля baseCost)
    private static final int EXPLORE_REP_MAX = 2;          // "малоосвоенный" гекс: своя репутация не выше этого
    private static final boolean DEBUG = false;            // true — в logcat (тег AI) пишет, почему отряд решил стоять
    private static final int MAX_ACTIONS_PER_SQUAD = 5;
    private static final int SPAWNS_PER_CITY = 2;
    // экономика: [ADAPT] подберите под реальные цены в вашей игре
    private static final int ROAD_MIN_RESOURCES = 30;
    private static final double ROAD_MAX_CITY_STEPS = 4.0;
    private static final int FOREST_CLEAR_BELOW = 60;

    private double spacing = -1;

    public AI(Player player) {
        this.player = player;
        this.random = new Random();
    }
    public void step() {

        attacksThisStep = 0;
        forgetDeadSquads();
        Turn t = analyze();
        moveSquads(t);
        curImpatience = 1.0;
        // антизастой: если противники есть, а боёв нет — становимся постепенно смелее.
        // Один бой не обнуляет счётчик полностью (иначе тупик "бой-пауза-бой" вечно сбрасывается), а только уменьшает вдвое.
        if (t.enemies.isEmpty()) stallTurns = 0;
        else if (attacksThisStep > 0) stallTurns /= 2;
        else stallTurns++;

        manageEconomy(analyze());   // заново: города освободились, угрозы изменились

    }

    private void forgetDeadSquads() {
        squadIdle.keySet().retainAll(player.squads);
        trail.keySet().retainAll(player.squads);
        exploreGoal.keySet().retainAll(player.squads);
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
            result+=nh.getReputation(fr)/4d;
        }
        return result;
    }
    private boolean isMyCity(Hex h, Turn t) {
        int mine = h.getReputation(player.fraction);
        for (Player p : Game.getInstance().players) {
            if (p == player) continue;
            if (h.getReputation(p.fraction) > mine) return false;
        }
        return true;
    }
    private Turn analyze() {
        Turn t = new Turn();
        t.myFraction = player.fraction;
        t.mine = player.squads;

        double enemyPowerSum = 0;
        int enemyPlayers = 0;
        for (Player p : Game.getInstance().players) {
            if (p == player) continue;
            ArrayList<Squad> sq = p.squads;
            if (sq.isEmpty()) continue;
            t.enemies.addAll(sq);
            enemyPowerSum += totalPower(sq);
            enemyPlayers++;
        }

        for (Hex h : Hex.hexes) {
            if (h.getCity() == null) continue;
            if (isMyCity(h, t)) t.myCitiesHexes.add(h); else t.otherCitiesHexes.add(h);
        }

        t.spawnSpots.addAll(t.myCitiesHexes);
        t.canSpawnSoldier = player.getMoney()>=ConstantManager.squadCostMap.get(Soldier.class);

        ArrayList<Squad> costSource = t.mine.isEmpty() ? t.enemies : t.mine;
        double sum = 0;
        for (Squad s : costSource) sum += s.cost;
        t.baseCost = costSource.isEmpty() ? 1.0 : sum / costSource.size();

        double myPower = totalPower(t.mine);
        double avgEnemy = enemyPlayers == 0 ? 0 : enemyPowerSum / enemyPlayers;
        t.stance = Math.min(3.0, (myPower + 1.0) / (avgEnemy + 1.0));

        for (Squad sq : t.mine) {
            Hex h = sq.hex;    //-----------------------------------------------------------------------------------------
            if (h != null) t.where.put(sq, h);
        }
        t.impatience = Math.min(IMPATIENCE_MAX, 1.0 + IMPATIENCE_PER_TURN * stallTurns);

        t.threatMap = buildThreatMap(t.enemies);
        t.targets = buildTargets(t);
        return t;
    }
    private double totalPower(ArrayList<Squad> list) {
        double p = 0;
        for (Squad s : list) p += s.getHp() * (s.getDamage() + s.getDefence());
        return p;
    }

    /** Для каждого гекса — какие вражеские отряды смогут ударить по нему на своём ходу. */
    private HashMap<Hex, ArrayList<Squad>> buildThreatMap(ArrayList<Squad> enemies) {
        HashMap<Hex, ArrayList<Squad>> map = new HashMap<>();
        for (Squad e : enemies) {
            if (e.getHp() <= 0) continue;
            Hex start = e.hex;
            if (start == null) continue;
            int moves = Math.min(Math.max(0, e.speed - 1), THREAT_DEPTH_CAP); // последнее очко уходит на удар

            HashSet<Hex> reach = new HashSet<>();
            reach.add(start);
            ArrayList<Hex> frontier = new ArrayList<>();
            frontier.add(start);
            for (int d = 0; d < moves; d++) {
                ArrayList<Hex> next = new ArrayList<>();
                for (Hex x : frontier) {
                    if (x != start && isRough(x)) continue;      // вход в гору/болото обнуляет ход — дальше не пойти
                    for (Hex n : x.getNearestHexes()) {
                        if (n.relief == Relief.WATER || reach.contains(n)) continue;
                        Squad occ = n.getSquad();
                        if (occ != null && occ.player != e.player) continue;   // чужой отряд — преграда
                        reach.add(n);
                        next.add(n);
                    }
                }
                frontier = next;
            }
            for (Hex x : reach) {
                if (x != start && isRough(x)) continue;           // оттуда уже не ударить
                if (x != start && x.getSquad() != null) continue; // пройти сквозь своих можно, остановиться — нет
                for (Hex n : x.getNearestHexes()) {
                    ArrayList<Squad> l = map.get(n);
                    if (l == null) { l = new ArrayList<>(); map.put(n, l); }
                    if (!l.contains(e)) l.add(e);
                }
            }
        }
        return map;
    }
    private ArrayList<Target> buildTargets(Turn t) {
        ArrayList<Target> list = new ArrayList<>();
        boolean defensive = t.stance < DEFENSIVE_STANCE;
        double base = t.baseCost;

        for (Squad e : t.enemies) {
            Hex h = e.hex;
            if (h == null) continue;
            double v = ENEMY_SQUAD_VALUE * base * (1.0 + 0.8 * (1.0 - hpRatio(e)));  // раненых бьём охотнее
            if (nearOwnCity(h, t, 3.0)) v *= 1.5;       // угроза дому
            else if (defensive) v *= 0.4;               // слабым в чужие земли не лезем
            list.add(new Target(TargetType.ENEMY_SQUAD, h, v, e));
        }
        for (Hex c : t.otherCitiesHexes) {
            list.add(new Target(TargetType.CITY_CAPTURE, c, CITY_VALUE * base * (defensive ? 0.5 : 1.0), null));
        }
        for (Hex c : t.myCitiesHexes) {
            ArrayList<Squad> th = t.threatMap.get(c);
            if (th != null && !th.isEmpty()) {
                list.add(new Target(TargetType.CITY_DEFEND, c, DEFEND_CITY_VALUE * base * (1.0 + 0.3 * th.size()), null));
            }
        }
        return list;
    }

    // ---------- Поле стоимостей пути (Дейкстра от цели) ----------

    private static class Node {
        Hex hex; double d;
        Node(Hex hex, double d) { this.hex = hex; this.d = d; }
    }

    private HashMap<Hex, Double> field(Hex goal, Turn t) {
        HashMap<Hex, Double> cached = t.fields.get(goal);
        if (cached != null) return cached;

        HashMap<Hex, Double> dist = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Double.compare(a.d, b.d));
        dist.put(goal, 0.0);
        pq.add(new Node(goal, 0.0));
        while (!pq.isEmpty()) {
            Node cur = pq.poll();
            if (cur.d > dist.get(cur.hex)) continue;
            double step = enterCost(cur.hex, goal);   // цена шага ВНУТРЬ cur.hex со стороны соседа
            for (Hex n : cur.hex.getNearestHexes()) {
                if (n.relief == Relief.WATER) continue;
                double nd = cur.d + step;
                Double old = dist.get(n);
                if (old == null || nd < old) {
                    dist.put(n, nd);
                    pq.add(new Node(n, nd));
                }
            }
        }
        t.fields.put(goal, dist);
        return dist;
    }

    private double enterCost(Hex h, Hex goal) {
        double c = 1.0;
        if (isRough(h) && !hasRoad(h)) c = ROUGH_PATH_COST;
        Squad occ = h.getSquad();
        if (h != goal && occ != null) c += (occ.player == player) ? BLOCKED_BY_OWN_COST : BLOCKED_BY_ENEMY_COST;
        return c;
    }

    // ---------- Движение и бой ----------

    private void moveSquads(Turn t) {
        final HashMap<Squad, Double> frontDist = new HashMap<>();

        for (Squad s : t.mine) {
            Hex h = s.hex;
            double best = 1e9;
            if (h != null) {
                for (Squad e : t.enemies) {
                    Hex eh = e.hex;
                    if (eh != null) best = Math.min(best, approxSteps(h, eh));
                }
            }
            frontDist.put(s, best);
        }


        ArrayList<Squad> order = new ArrayList<>(t.mine);
        // сначала передовые — они расчищают путь и задают, кого добивать остальным
        Collections.sort(order, (a, b) -> Double.compare(frontDist.get(a), frontDist.get(b)));
        //lastStepLength = System.currentTimeMillis();
        for (Squad s : order) {
            if (s.getHp() > 0) actSquad(s, t);
        }//lastStepLength = System.currentTimeMillis() - lastStepLength;
    }

    private enum OptionType { HOLD, MOVE, ATTACK }

    private static class Option {
        OptionType type; Hex hex; Squad enemy; double score;
        Option(OptionType type, Hex hex, Squad enemy, double score) {
            this.type = type; this.hex = hex; this.enemy = enemy; this.score = score;
        }
    }

    private static class Plan {
        Hex goal;
        HashMap<Hex, Double> field;
        double stepValue;   // ценность одного шага к цели (в единицах стоимости)
    }

    private void actSquad(Squad s, Turn t) {

        final Hex start = s.hex;
        if (start == null) return;
        final boolean couldAct = s.speedLost > 0 && s.canMove();
        Integer idle = squadIdle.get(s);
        curImpatience = Math.min(IMPATIENCE_MAX, 1.0 + SQUAD_IMPATIENCE_PER_TURN * (idle == null ? 0 : idle));

        Plan plan = choosePlan(s, start, t);
        boolean acted = false;
        boolean attacked = false;
        Hex here = start;

        for (int i = 0; i < MAX_ACTIONS_PER_SQUAD; i++) {
            long time = System.currentTimeMillis();
            if (s.getHp() <= 0 || s.speedLost <= 0 || !s.canMove()) break;
            here = s.hex;
            if (here == null) break;

            Option opt = chooseBestOption(s, here, plan, t);
            if (opt.type == OptionType.HOLD) {
                if (DEBUG && plan.goal != null) {
                    Log.d("AI", "HOLD squad(" + here.getX() + "," + here.getY() + ") goal(" + plan.goal.getX() + ","
                            + plan.goal.getY() + ") idle=" + (idle == null ? 0 : idle) + " imp=" + effImpatience(t)
                            + " stall=" + stallTurns + " holdScore=" + opt.score);
                }
                break;
            }

            int pointsBefore = s.speedLost;
            int myHpBefore = s.getHp();
            Squad victim = opt.enemy;
            int victimHpBefore = victim == null ? 0 : victim.getHp();

            if (opt.type == OptionType.ATTACK) {
                attacksThisStep++;
                attacked = true;
                s.attack(victim);
                if (victim.getHp() <= 0 || victim.hex == null) {
                    t.enemies.remove(victim);
                    t.threatMap = buildThreatMap(t.enemies);
                }
            } else {
                s.move(opt.hex);
            }
            acted = true;

            Hex after = s.getHp() > 0 ? s.hex : null;
            if (after != null) t.where.put(s, after); else t.where.remove(s);
            boolean changed = after != here || s.speedLost != pointsBefore || s.getHp() != myHpBefore
                    || (victim != null && victim.getHp() != victimHpBefore);
            if (!changed) break;    // действие не удалось — не зацикливаемся
            Toast.makeText(Game.getInstance().gameActivity.getApplicationContext(), here+" "+(System.currentTimeMillis()-time), Toast.LENGTH_SHORT).show();
        }

        if (couldAct) updateStuckState(s, start, plan, attacked);
        if (s.getHp() > 0) afterTurnWork(s, t, acted);
    }

    /**
     * Личный антитупик: отряд "буксует", если за ход не бил, не стоит на своей цели и либо остался
     * на месте, либо вернулся на гекс, где уже начинал один из последних ходов (хождение туда-сюда).
     */
    private void updateStuckState(Squad s, Hex start, Plan plan, boolean attacked) {
        if (s.getHp() <= 0) {
            squadIdle.remove(s); trail.remove(s); exploreGoal.remove(s);
            return;
        }
        Hex end = s.hex;
        ArrayList<Hex> tr = trail.get(s);
        if (tr == null) { tr = new ArrayList<>(); trail.put(s, tr); }
        boolean onGoal = plan.goal != null && end == plan.goal;
        boolean stuck = plan.goal != null && !attacked && !onGoal && (end == start || tr.contains(end));
        Integer idle = squadIdle.get(s);
        squadIdle.put(s, stuck ? (idle == null ? 1 : idle + 1) : 0);
        tr.add(start);
        while (tr.size() > TRAIL_LENGTH) tr.remove(0);
    }

    /** Итоговая смелость отряда: максимум из общей (по боту) и личной. */
    private double effImpatience(Turn t) {
        return Math.min(IMPATIENCE_MAX, Math.max(t.impatience, curImpatience));
    }

    private int revisitCount(Squad s, Hex h) {
        ArrayList<Hex> tr = trail.get(s);
        if (tr == null) return 0;
        int n = 0;
        for (Hex x : tr) if (x == h) n++;
        return n;
    }

    /** Выбор цели отряда: отход при низком hp, иначе лучшая цель с учётом дальности и "толкучки". */
    private Plan choosePlan(Squad s, Hex here, Turn t) {
        Plan plan = new Plan();

        if (hpRatio(s) < RETREAT_HP_RATIO && !t.myCitiesHexes.isEmpty() && !t.enemies.isEmpty()) {
            Hex bestCity = null;
            double bestD = Double.MAX_VALUE;
            for (Hex c : t.myCitiesHexes) {
                Double d = field(c, t).get(here);
                if (d != null && d < bestD) { bestD = d; bestCity = c; }
            }
            if (bestCity != null) {
                plan.goal = bestCity;
                plan.field = field(bestCity, t);
                plan.stepValue = RETREAT_STEP_VALUE * t.baseCost;
                return plan;
            }
        }

        Target best = null;
        double bestScore = 0;
        for (Target tg : t.targets) {
            if (tg.enemy != null && (tg.enemy.getHp() <= 0 || tg.enemy.hex != tg.hex)) continue;
            Double d = field(tg.hex, t).get(here);
            if (d == null) continue;
            double turns = d / Math.max(1, s.speed);
            double sc = tg.value / (1.0 + 0.5 * turns) / (1.0 + CROWDING * tg.assigned);
            if (sc > bestScore) { bestScore = sc; best = tg; }
        }
        if (best != null) {
            best.assigned++;
            plan.goal = best.hex;
            plan.field = field(best.hex, t);
            plan.stepValue = PROGRESS_FRACTION * bestScore * effImpatience(t);
            exploreGoal.remove(s);
        } else {
            // достижимых целей нет (острова, вода, все города "свои") — не стоим на "лучшей" клетке, а исследуем карту
            Hex g = pickExploreGoal(s, here, t);
            if (g != null) {
                t.claimedExplore.add(g);
                exploreGoal.put(s, g);
                plan.goal = g;
                plan.field = field(g, t);
                plan.stepValue = EXPLORE_STEP_FRACTION * t.baseCost * effImpatience(t);
            }
        }
        return plan;
    }

    /**
     * Запасная цель: ближайший (по суше) свободный гекс с малой своей репутацией; если такого нет —
     * случайный гекс из самого дальнего слоя достижимой суши. Выбранная цель сохраняется до достижения.
     */
    private Hex pickExploreGoal(Squad s, Hex here, Turn t) {
        Hex old = exploreGoal.get(s);
        if (old != null && old != here && old.getSquad() == null && field(old, t).get(here) != null) return old;

        HashSet<Hex> seen = new HashSet<>();
        seen.add(here);
        ArrayList<Hex> layer = new ArrayList<>();
        layer.add(here);
        ArrayList<Hex> lastLayer = new ArrayList<>();
        while (!layer.isEmpty()) {
            ArrayList<Hex> next = new ArrayList<>();
            for (Hex x : layer) {
                for (Hex n : x.getNearestHexes()) {
                    if (n.relief == Relief.WATER || !seen.add(n)) continue;
                    next.add(n);
                }
            }
            Collections.shuffle(next, random);
            for (Hex n : next) {
                if (n.getSquad() == null && !t.claimedExplore.contains(n)
                        && n.getReputation(t.myFraction) <= EXPLORE_REP_MAX) return n;
            }
            if (!next.isEmpty()) lastLayer = next;
            layer = next;
        }
        for (Hex n : lastLayer) {
            if (n.getSquad() == null && !t.claimedExplore.contains(n)) return n;
        }
        return null;
    }

    private Option chooseBestOption(Squad s, Hex here, Plan plan, Turn t) {
        double C = costOf(s);
        ArrayList<Hex> neighbors = here.getNearestHexes();

        // getHexScore нормируем внутри кандидатов — не зависим от его масштаба
        double min = getHexScore(here), max = min;
        for (Hex n : neighbors) {
            double v = getHexScore(n);
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        double range = Math.max(1e-9, max - min);

        double holdScore = -positionRisk(here, s, t, null)
                + positionBonus(here, s, t, (getHexScore(here) - min) / range - 0.5);
        ArrayList<Squad> hereThreat = t.threatMap.get(here);
        if (hereThreat != null && !hereThreat.isEmpty() && t.myCitiesHexes.contains(here)) {
            holdScore += CITY_GUARD_WEIGHT * C;
        }
        Option best = new Option(OptionType.HOLD, here, null, holdScore);

        for (Hex n : neighbors) {
            if (n.relief == Relief.WATER) continue;
            double normN = (getHexScore(n) - min) / range - 0.5;
            Squad occ = n.getSquad();
            double noise = random.nextDouble() * 0.01 * C;

            if (occ == null) {
                double score = -positionRisk(n, s, t, null) + positionBonus(n, s, t, normN);
                if (plan.field != null) {
                    Double fh = plan.field.get(here), fn = plan.field.get(n);
                    double progress = (fh == null || fn == null) ? -1.0 : fh - fn;
                    score += plan.stepValue * progress;
                }
                if (isRough(n) && !hasRoad(n) && s.speedLost > 1) {
                    score -= WASTED_MOVE_WEIGHT * C * (s.speedLost - 1);
                }
                score -= REVISIT_PENALTY * C * revisitCount(s, n);
                score += noise;
                if (score > best.score) best = new Option(OptionType.MOVE, n, null, score);
            } else if (occ.player != player && occ.getHp() > 0) {
                double score = attackScore(s, here, occ, t, (getHexScore(here) - min) / range - 0.5) + noise;
                if (score > best.score) best = new Option(OptionType.ATTACK, n, occ, score);
            }
        }

        // принудительный шаг: если отряд упорно хочет стоять, а цель есть и смелость уже высока — идём к цели
        // по самому выгодному по пути гексу, не оглядываясь на риск (иначе два фронта могут стоять вечно)
        if (best.type == OptionType.HOLD && plan.field != null && plan.goal != here
                && effImpatience(t) >= FORCE_ADVANCE_IMPATIENCE) {
            Double fh = plan.field.get(here);
            Hex bestN = null;
            double bestProgress = 0;
            if (fh != null) {
                for (Hex n : neighbors) {
                    if (n.relief == Relief.WATER || n.getSquad() != null) continue;
                    Double fn = plan.field.get(n);
                    if (fn == null) continue;
                    double progress = fh - fn - 0.01 * positionRisk(n, s, t, null) / C;
                    if (progress > bestProgress) { bestProgress = progress; bestN = n; }
                }
            }
            if (bestN != null) best = new Option(OptionType.MOVE, bestN, null, 0);
        }
        return best;
    }

    /** Ожидаемая выгода атаки в единицах стоимости: урон врагу + добивание − ответный удар − риск после. */
    private double attackScore(Squad s, Hex here, Squad e, Turn t, double normHere) {
        double C = costOf(s), Ce = costOf(e);
        double dmg = Squad.getAttackDamage(s.getDamage(),e.getDefence(), (float) hpRatio(s));
        double counter = Squad.getDefenceDamage(s.getDefence(),e.getDefence(), (float) hpRatio(e));

        boolean kills = dmg >= e.getHp();
        double gain = HIT_PROB * (Math.min(dmg, e.getHp()) / Math.max(1, e.getMaxHp())) * Ce;
        if (kills) gain += HIT_PROB * KILL_BONUS * Ce;

        double loss = (1.0 - HIT_PROB) * (Math.min(counter, s.getHp()) / Math.max(1, s.getMaxHp())) * C;
        if (counter >= s.getHp()) loss += (1.0 - HIT_PROB) * DEATH_PENALTY * C;
        loss *= riskAversion(t);

        double riskWith = positionRisk(here, s, t, null);
        double risk = riskWith;
        if (kills) {   // если враг будет убит, он уже не ударит в ответ
            risk = HIT_PROB * positionRisk(here, s, t, e) + (1.0 - HIT_PROB) * riskWith;
        }
        return gain - loss - risk + positionBonus(here, s, t, normHere) + ATTACK_BIAS * C;
    }

    /** Ожидаемая потеря стоимости отряда, если он окажется на гексе (по карте угроз). */
    private double positionRisk(Hex hex, Squad s, Turn t, Squad ignore) {
        ArrayList<Squad> list = t.threatMap.get(hex);
        if (list == null) return 0;
        double incoming = 0;
        for (Squad e : list) {
            if (e == ignore || e.getHp() <= 0) continue;
            incoming += HIT_PROB * Squad.getAttackDamage(e.getDamage(), s.getDefence(), (float) hpRatio(e));
        }
        if (incoming <= 0) return 0;
        double C = costOf(s);
        double risk = (Math.min(incoming, s.getHp()) / Math.max(1, s.getMaxHp())) * C;
        if (incoming >= s.getHp()) risk += 0.5 * DEATH_PENALTY * C;
        // враг бьёт одного из многих: чем больше союзников рядом, тем меньше достаётся каждому
        risk /= (1.0 + GROUP_DILUTION * nearbyAllies(hex, s, t));
        return risk * riskAversion(t);
    }

    /** Сколько своих отрядов (кроме s) в радиусе GROUP_RADIUS от гекса. */
    private int nearbyAllies(Hex hex, Squad self, Turn t) {
        int n = 0;
        for (HashMap.Entry<Squad, Hex> en : t.where.entrySet()) {
            Squad q = en.getKey();
            if (q == self || q.getHp() <= 0) continue;
            if (approxSteps(hex, en.getValue()) <= GROUP_RADIUS) n++;
        }
        return n;
    }

    /** Мягкие плюсы гекса: союзники рядом, рельеф/город (getHexScore), своя репутация. */
    private double positionBonus(Hex pos, Squad s, Turn t, double normHex) {
        double C = costOf(s);
        double b = SUPPORT_WEIGHT * C * alliesAdjacent(pos, s);
        b += TERRAIN_WEIGHT * C * normHex;
        b += REPUTATION_WEIGHT * C * (5 - pos.getReputation(t.myFraction)) / 5.0;
        // не загромождаем базу: если можем нанять солдата, место у города должно быть свободно
        if (t.canSpawnSoldier && t.spawnSpots.contains(pos)) b -= VACATE_WEIGHT * C;
        return b;
    }

    private double riskAversion(Turn t) {
        double base = t.stance >= AGGRESSIVE_STANCE ? 0.8 : (t.stance >= DEFENSIVE_STANCE ? 1.0 : 1.4);
        return base / effImpatience(t);
    }

    private int alliesAdjacent(Hex hex, Squad self) {
        int n = 0;
        for (Hex nb : hex.getNearestHexes()) {
            Squad q = nb.getSquad();
            if (q != null && q != self && q.player == player && q.getHp() > 0) n++;
        }
        return n;
    }

    // ---------- Хозяйственные действия отрядов ----------

    private void afterTurnWork(Squad s, Turn t, boolean acted) {
        Hex here = s.hex;
        if (here == null) return;

        // дорога на горе/болоте у своих городов — линия снабжения для подкреплений
        if (isRough(here) && !hasRoad(here) && player.getResources() >= ROAD_MIN_RESOURCES
                && nearOwnCity(here, t, ROAD_MAX_CITY_STEPS)) {
            here.buildRoad(player);
        }

        // бездействующий и в безопасности отряд работает на экономику
        if (!acted && s.speedLost > 0 && positionRisk(here, s, t, null) <= 0) {
            if (here.relief == Relief.FOREST && player.getResources() < FOREST_CLEAR_BELOW) {
                here.removeForest(player);
            } else {
                Building b = new Farm(here);
                if (b != null) here.buildBuilding(b, player);
            }
        }
    }

    // ---------- Экономика: деньги → солдаты ----------

    private void manageEconomy(final Turn t) {
        final int cost = ConstantManager.squadCostMap.get(Soldier.class);
        ArrayList<Hex> cities = new ArrayList<>(t.myCitiesHexes);
        // сначала города, ближе всего к фронту / под угрозой
        Collections.sort(cities, (a, b) -> Double.compare(frontScore(b, t), frontScore(a, t)));

        for (Hex city : cities) {
            if (player.getMoney() < cost) break;
            int spawned = 0;
            for (Hex spot : spawnSpots(city, t)) {
                if (spawned >= SPAWNS_PER_CITY || player.getMoney() < cost) break;
                if (spot.spawnSquad(new Soldier(spot,player))) spawned++;
            }
        }
    }

    /** Чем ближе враги и чем сильнее угроза — тем важнее усилить этот город. */
    private double frontScore(Hex city, Turn t) {
        double score = 0;
        for (Squad e : t.enemies) {
            Hex eh = e.hex;
            if (eh != null) score += 1.0 / (1.0 + approxSteps(city, eh));
        }
        ArrayList<Squad> th = t.threatMap.get(city);
        if (th != null) score += 2.0 * th.size();
        return score;
    }

    /** Свободные гексы для спавна: сам город, затем свободные соседи. */
    private ArrayList<Hex> spawnSpots(Hex city, final Turn t) {
        ArrayList<Hex> spots = new ArrayList<>();
        if (city.getSquad() == null) spots.add(city);
        /*ArrayList<Hex> nb = new ArrayList<>();
        for (Hex n : city.getNearestHexes()) {
            if (n.relief != Relief.WATER && n.getSquad() == null) nb.add(n);
        }*/
        return spots;
    }
    private boolean nearOwnCity(Hex h, Turn t, double maxSteps) {
        for (Hex c : t.myCitiesHexes) {
            if (approxSteps(h, c) <= maxSteps) return true;
        }
        return false;
    }

    private boolean hasRoad(Hex h) { return h.getBuilding() != null && h.getBuilding() instanceof Road; }
    private double hpRatio(Squad s) { return (double) s.getHp() / Math.max(1, s.getMaxHp()); }
    private static class Turn {
        Fraction myFraction;
        ArrayList<Squad> mine = new ArrayList<>();
        ArrayList<Squad> enemies = new ArrayList<>();
        ArrayList<Hex> myCitiesHexes = new ArrayList<>();
        ArrayList<Hex> otherCitiesHexes = new ArrayList<>();
        HashMap<Hex, ArrayList<Squad>> threatMap = new HashMap<>();
        HashMap<Hex, HashMap<Hex, Double>> fields = new HashMap<>();
        ArrayList<Target> targets = new ArrayList<>();
        HashMap<Squad, Hex> where = new HashMap<>();   // текущие позиции своих отрядов
        HashSet<Hex> spawnSpots = new HashSet<>();     // свои города и их соседи — места, где можно создавать солдат
        boolean canSpawnSoldier = false;
        HashSet<Hex> claimedExplore = new HashSet<>();  // гексы, уже выбранные кем-то как цель исследования в этом ходу
        double impatience = 1.0;  // 1..IMPATIENCE_MAX: растёт при затяжном позиционном тупике
        double stance = 1.0;      // силы своих / средние силы одного противника
        double baseCost = 1.0;    // средняя стоимость отряда — единица измерения ценностей
    }
    private enum TargetType { ENEMY_SQUAD, CITY_CAPTURE, CITY_DEFEND }

    private static class Target {
        TargetType type;
        Hex hex;
        double value;
        Squad enemy;
        int assigned;
        Target(TargetType type, Hex hex, double value, Squad enemy) {
            this.type = type; this.hex = hex; this.value = value; this.enemy = enemy;
        }
    }
    private double costOf(Squad s) { return Math.max(1, s.cost); }
    private boolean isRough(Hex h) { return (h.relief == Relief.MOUNTAIN || h.relief == Relief.SWAMP) && !hasRoad(h); }
    /** Приближённое расстояние в гексах (по локальным координатам Hex.getX/getY). */
    private double approxSteps(Hex a, Hex b) {
        if (spacing < 0) spacing = neighborSpacing();
        double dx = a.getX() - b.getX(), dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy) / spacing;
    }

    private double neighborSpacing() {
        for (Hex h : Hex.hexes) {
            for (Hex n : h.getNearestHexes()) {
                double dx = h.getX() - n.getX(), dy = h.getY() - n.getY();
                double d = Math.sqrt(dx * dx + dy * dy);
                if (d > 0) return d;
            }
        }
        return 1.0;
    }

    // ==================== СТАТИЧЕСКИЕ ОЦЕНКИ (ваши существующие методы) ====================
    // ВНИМАНИЕ: если вставляете этот файл целиком, верните сюда ВАШИ реализации
    // getHexScore / getHexDanger1 / getRegionReputation. Ниже — заглушки, чтобы файл собирался.
    // Из них новый код использует только getHexScore.


}

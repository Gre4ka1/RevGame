package com.example.revgame2.Entities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.revgame2.ConstantManager;
import com.example.revgame2.Entities.Buildings.Building;
import com.example.revgame2.Entities.Buildings.Road;
import com.example.revgame2.Game;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;
import com.example.revgame2.TextureManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Hex extends Entity{
    public static ArrayList<Hex> hexes = new ArrayList<>();
    private float x,y;
    public float globalX,globalY;
    public HashMap<Player,Integer> reputation = new HashMap<>();
    public static HashMap<Player,Paint> reputationPaints = new HashMap<>();
    public static int SIZE_X;
    public static int SIZE_Y;

    public static ArrayList<Hex> moveDotHexList=new ArrayList<>();
    public static ArrayList<Hex> attackDotHexList=new ArrayList<>();
    //private static Hex targetHex;
    public Relief relief;

    private Squad squad;
    private City city;
    private Building building;
    public double hexScore=0;
    private static Random random = new Random();


    public Hex(float x, float y, Relief relief) {
        super(TextureManager.getInstance().getHexSTexture(relief), TextureManager.getInstance().getHexTexture(relief));
        this.x = x;
        this.y = y;
        this.relief=relief;
        hexes.add(this);
        if (paint==null) paint = new Paint();
    }


    public void draw(Canvas canvas){
        globalX=Game.getInstance().globalX;
        globalY=Game.getInstance().globalY;
        canvas.drawBitmap(texture, (float) x+globalX, (float) y+globalY,paint);
        //canvas.drawText(hexScore+"",x+SIZE_X/2,y+SIZE_Y/2,paint);
        /*switch (relief){
            case FIELD:
                canvas.drawBitmap(TextureManager.getInstance().getFieldHexTexture(), (float) x, (float) y,paint);
                break;
            case MOUNTAIN:
                canvas.drawBitmap(TextureManager.getInstance().getMountainHexTexture(), (float) x, (float) y,paint);
                break;
            case WATER:
                canvas.drawBitmap(TextureManager.getInstance().getWaterHexTexture(), (float) x, (float) y,paint);
                break;
            case FOREST:
                canvas.drawBitmap(TextureManager.getInstance().getForestHexTexture(), (float) x, (float) y,paint);
                break;
            case SWAMP:
                canvas.drawBitmap(TextureManager.getInstance().getSwampHexTexture(), (float) x, (float) y,paint);
                break;
            default:
                canvas.drawBitmap(TextureManager.getInstance().getEmptyHexTexture(), (float) x, (float) y,paint);
        }*/
        /*if (moveDotHexList.contains(this))
            drawMoveDot(canvas);
        if (attackDotHexList.contains(this))
            drawAttackDot(canvas);*/
    }

    public void drawMoveDot(Canvas canvas){
        canvas.drawBitmap(TextureManager.getInstance().getMoveDotTexture(),x+SIZE_X*0.4f+globalX,y+SIZE_Y*0.4f+globalY,paint);
    }
    public void drawAttackDot(Canvas canvas){
        canvas.drawBitmap(TextureManager.getInstance().getAttackDotTexture(),x+SIZE_X*0.3f+globalX,y+SIZE_Y*0.3f+globalY,paint);
    }
    public void drawReputation(Canvas canvas) {
        if (relief==Relief.WATER)
            return;
        for (Player p: Game.getInstance().players){
            switch (p.fraction){
                case RED:
                    canvas.drawLine(x+15+globalX,
                            (float) (y+SIZE_Y*(0.5-0.042*reputation.get(p))),
                            x+15+globalX,
                            (float) (y+SIZE_Y*(0.5+0.042*reputation.get(p))),
                            reputationPaints.get(p));
                    break;
                case BLUE:
                    canvas.drawLine(
                            x+SIZE_X-15+globalX,
                            (float) (y+SIZE_Y*(0.5-0.042*reputation.get(p))),
                            x+SIZE_X-15+globalX,
                            (float) (y+SIZE_Y*(0.5+0.042*reputation.get(p))),
                            reputationPaints.get(p));
                    break;
                case YELLOW:
                    canvas.drawLine(
                            (float) (x+SIZE_X*0.75 - 0.866*SIZE_Y*0.042*reputation.get(p) -7.5)+globalX,
                            (float) (y+SIZE_Y*0.875 + 0.5*SIZE_Y*0.042*reputation.get(p) - 13)+globalY,
                            (float) (x+SIZE_X*0.75 + 0.866*SIZE_Y*0.042*reputation.get(p)-7.5)+globalX,
                            (float) (y+SIZE_Y*0.875 - 0.5*SIZE_Y*0.042*reputation.get(p) -13)+globalY,
                            reputationPaints.get(p));     //a=0.5SizeY *0.05     a*cos(30)     a*sin(30)
                    break;
                case GREEN:
                    canvas.drawLine(
                            (float) (x+SIZE_X*0.25 - 0.866*SIZE_Y*0.042*reputation.get(p) + 7.5)+globalX,
                            (float) (y+SIZE_Y*0.125 + 0.5*SIZE_Y*0.042*reputation.get(p) + 13)+globalY,
                            (float) (x+SIZE_X*0.25 + 0.866*SIZE_Y*0.042*reputation.get(p)+ 7.5)+globalX,
                            (float) (y+SIZE_Y*0.125 - 0.5*SIZE_Y*0.042*reputation.get(p) + 13)+globalY,
                            reputationPaints.get(p));     //a=0.5SizeY *0.05     a*cos(30)     a*sin(30)
                    break;
                case BLACK:
                    canvas.drawLine(
                            (float) (x+SIZE_X*0.25 - 0.866*SIZE_Y*0.042*reputation.get(p) + 7.5)+globalX,
                            (float) (y+SIZE_Y*0.875 - 0.5*SIZE_Y*0.042*reputation.get(p) - 13)+globalY,
                            (float) (x+SIZE_X*0.25 + 0.866*SIZE_Y*0.042*reputation.get(p) + 7.5)+globalX,
                            (float) (y+SIZE_Y*0.875 + 0.5*SIZE_Y*0.042*reputation.get(p) - 13)+globalY,
                            reputationPaints.get(p));     //a=0.5SizeY *0.05     a*cos(30)     a*sin(30)
                    break;
                case WHITE:
                    canvas.drawLine(
                            (float) (x+SIZE_X*0.75 - 0.866*SIZE_Y*0.042*reputation.get(p) - 7.5)+globalX,
                            (float) (y+SIZE_Y*0.125 - 0.5*SIZE_Y*0.042*reputation.get(p) + 13)+globalY,
                            (float) (x+SIZE_X*0.75 + 0.866*SIZE_Y*0.042*reputation.get(p)- 7.5)+globalX,
                            (float) (y+SIZE_Y*0.125 + 0.5*SIZE_Y*0.042*reputation.get(p) + 13)+globalY,
                            reputationPaints.get(p));     //a=0.5SizeY *0.05     a*cos(30)     a*sin(30)
                    break;
            }
        }
    }
    public void drawBuilding(Canvas canvas){
        if (building!=null)
            canvas.drawBitmap(building.texture,x+globalX,y+globalY,paint);
    }

    public static Hex getClickedHex(float cx, float cy){
        float minDist=1000;
        Hex minDistHex = hexes.get(0);
        for (Hex h:hexes) {
            if (Math.hypot(h.x+SIZE_X/2 + h.globalX-cx,h.y+SIZE_Y/2 + h.globalY-cy)<minDist){
                minDist= (float) Math.hypot(h.x+SIZE_X/2 + h.globalX-cx,h.y+SIZE_Y/2 + h.globalY-cy);
                minDistHex=h;
            }
        }
        if (minDist<=SIZE_Y*0.5) {
            //if (minDistHex.relief==Relief.WATER) return null;
            return minDistHex;
        }
        return null;
    }
    public void onTouch(){
        if (Hex.moveDotHexList.contains(this)){
            if (Entity.selectedEntity instanceof Squad){
                ((Squad) Entity.selectedEntity).move(this);
                if (((Squad) Entity.selectedEntity).speedLost==0){
                    unselect();
                }
            } else
                System.err.println("Hex.onTouch() Try to move to this hex. Selected entity is not squad.");
            return;
        }
        if (Hex.attackDotHexList.contains(this)){
            if (Entity.selectedEntity instanceof Squad){
                ((Squad) Entity.selectedEntity).attack(squad);
                if (((Squad) Entity.selectedEntity).speedLost==0){
                    unselect();
                }
            } else
                System.err.println("Hex.onTouch() Try to attack to this hex. Selected entity is not squad.");
            return;
        }

        if (Entity.selectedEntity == null){
            if (squad != null)
                squad.select();
            else
                select();
            return;
        }
        if (Entity.selectedEntity instanceof Squad){
            if (Entity.selectedEntity == squad)
                select();
            else{
                if (squad != null)
                    squad.select();
                else
                    select();
            }
            return;
        }
        if (Entity.selectedEntity instanceof Hex){
            if (Entity.selectedEntity==this)
                unselect();
            else {
                if (squad != null)
                    squad.select();
                else
                    select();
            }
            return;
        }
        /*if (Entity.selectedEntity instanceof City){
            if (Entity.selectedEntity==city)
                unselect();
            else {
                if (squad != null)
                    squad.select();
                else
                    select();
            }
            return;
        }*/

    }

    @Override
    public void select() {
        /*if (city!=null){
            city.select();
            return;
        }*/
        super.select();

    }

    public ArrayList<Hex> getNearestHexes(){
        ArrayList<Hex> temp = new ArrayList<>();
        temp.add(getClickedHex(x+globalX,y+globalY));
        temp.add(getClickedHex(x+globalX+SIZE_X,y+globalY));
        temp.add(getClickedHex(x+globalX+SIZE_X*1.5f,y+globalY+SIZE_Y*0.5f));
        temp.add(getClickedHex(x+globalX+SIZE_X,y+globalY+SIZE_Y));
        temp.add(getClickedHex(x+globalX,y+globalY+SIZE_Y));
        temp.add(getClickedHex(x+globalX-SIZE_X*0.5f,y+globalY+SIZE_Y*0.5f));
        while (temp.contains(null))
            temp.remove(null);
        return temp;
    }
    public ArrayList<Hex> get36NearestHexes(){
        HashSet<Hex> hexSet = new HashSet<>();
        ArrayList<Hex> list = getNearestHexes();
        for (Hex h:list) {
            hexSet.addAll(h.getNearestHexes());
        }
        hexSet.remove(this);
        return new ArrayList<>(hexSet);
    }
    public static boolean isPlayersSquadNear(Hex hex, Player player){
        for (Hex h:hex.getNearestHexes()) {
            if (h.getSquad() != null && h.getSquad().player == player)
                return true;
        }
        if (hex.getSquad() != null && hex.getSquad().player == player)
            return true;
        return false;
    }
    public static void initReputation(){
        for (Hex h:hexes) {
            if (h.relief!=Relief.WATER) {
                for (Player player : Game.getInstance().players) {
                    h.reputation.put(player, random.nextInt(6));
                }
            }
        }
        for (Player player:Game.getInstance().players) {
            Paint p = new Paint();
            p.setStrokeWidth(10);
            switch (player.fraction){
                case RED:
                    p.setColor(Color.RED);
                    break;
                case BLUE:
                    p.setColor(Color.BLUE);
                    break;
                case YELLOW:
                    p.setColor(Color.YELLOW);
                    break;
                case GREEN:
                    p.setColor(Color.GREEN);
                    break;
                case BLACK:
                    p.setColor(Color.BLACK);
                    break;
                case WHITE:
                    p.setColor(Color.WHITE);
                    break;

            }
            reputationPaints.put(player,p);
        }
    }
    public void spawnSquad(Squad newSquad){
        if (newSquad.player.getMoney() >= newSquad.cost && squad==null){
            squad=newSquad;
            newSquad.player.spendMoney(ConstantManager.SPAWN_SOLDIER_COST);
        }

    }
    public void buildRoad(Player player){
        if ((relief==Relief.MOUNTAIN || relief==Relief.SWAMP) && building==null){
            if (player.getResources()>= ConstantManager.ROAD_COST) {
                if (squad != null && squad.player == player) {
                    setBuilding(new Road(this));
                }
                player.spendResurces(ConstantManager.ROAD_COST);
            }
        }
    }
    public void buildBuilding(Building building, Player player){
        if ((relief==building.providedRelief || relief==building.secondProvidedRelief) && this.building==null){
            if (player.getResources()>= building.cost) {
                if (squad != null && squad.player == player && squad.speedLost>0) {
                    setBuilding(building);
                    player.spendResurces(building.cost);
                    squad.speedLost--;
                }
            }
        }
    }
    public void removeForest(Player player){
        if (relief==Relief.FOREST){
            if (squad != null && squad.player==player && squad.speedLost>0){
                relief=Relief.FIELD;
                selectedTexture=TextureManager.getInstance().getHexSTexture(Relief.FIELD);
                noSelectedTexture=TextureManager.getInstance().getHexTexture(Relief.FIELD);
                texture=noSelectedTexture;
                player.addResurces(ConstantManager.REMOVE_FOREST_PRIZE);
                squad.speedLost--;
            }
        }
    }
    public void removeBuilding(Player player){
        if (building!=null && squad!=null && squad.player==player){
            building=null;
            player.addResurces(ConstantManager.REMOVE_BUILDING_PRIZE);
            squad.speedLost--;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Squad getSquad() {
        return squad;
    }

    public void setSquad(Squad squad) {
        this.squad = squad;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public void setRelief(Relief relief) {
        this.relief = relief;
        selectedTexture=TextureManager.getInstance().getHexSTexture(Relief.FIELD);
        noSelectedTexture=TextureManager.getInstance().getHexTexture(Relief.FIELD);
        texture=noSelectedTexture;
    }
    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {return city;}
}

package com.example.revgame2.Entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.revgame2.Entities.Buildings.Road;
import com.example.revgame2.Game;
import com.example.revgame2.Player;
import com.example.revgame2.Relief;

import java.util.ArrayList;

public class Squad extends Unit{
    protected int maxHp;
    protected int hp;
    public int speed;
    protected int damage;
    protected int defence;
    protected int distance;
    public int speedLost=speed;
    public Player player;
    public int cost;
    public float[] screenCoords;
    public float scaleKoef;
    public boolean moveAfterDefeatSquadFlag;

    public Squad(Hex hex, Player player, int cost, boolean moveAfterDefeatSquadFlag, Bitmap selectedTexture, Bitmap noSelectedTexture) {
        super(hex,selectedTexture,noSelectedTexture);
        this.player=player;
        player.addSquad(this);
        //hex.setSquad(this);
        this.cost=cost;
        this.moveAfterDefeatSquadFlag=moveAfterDefeatSquadFlag;
    }

    public void move(Hex targetHex){
        Hex.moveDotHexList.clear();
        Hex.attackDotHexList.clear();
        if (targetHex.getSquad()!=null || targetHex.relief== Relief.WATER) return;
        if (speedLost>0){
            hex.setSquad(null);
            targetHex.setSquad(this);
            hex=targetHex;
            speedLost-=1;
            if (targetHex.relief==Relief.MOUNTAIN || targetHex.relief==Relief.SWAMP)
                if (!(targetHex.getBuilding() instanceof Road))
                    speedLost = 0;
        }
        if (speedLost>0 && player.getAI()==null){
            select();
        }
    }
    public boolean canMove(){
        ArrayList<Hex> hexes = hex.getNearestHexes();

        boolean flag = false;
        for (Hex h:hexes) {
            if (h.relief!=Relief.WATER){
                if (h.getSquad() == null || h.getSquad().player!=player) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    @Override
    public void select() {
        super.select();
        ArrayList<Hex> nearestHexes = hex.getNearestHexes();
        if (speedLost>0) {
            for (Hex h : nearestHexes) {
                if (h.getSquad()!=null){
                    if (h.getSquad().player!=player)
                        Hex.attackDotHexList.add(h);
                    continue;
                }
                if (h == null || h.relief==Relief.WATER)
                    continue;

                //h.drawMoveDot(canvas);
                Hex.moveDotHexList.add(h);

            }
        }

    }


    @Override
    public void draw(Canvas canvas) {
        screenCoords= Game.getInstance().getScreenCoordinates(hex.getX(),hex.getY());
        scaleKoef = Game.getInstance().scaleKoef;
        if (Entity.selectedEntity == this) {
            canvas.drawBitmap(texture, null, new Rect((int) (screenCoords[0]+hex.SIZE_X*scaleKoef*0.15), (int) (screenCoords[1]+hex.SIZE_Y*scaleKoef*0.15), (int) (screenCoords[0]+hex.SIZE_X*scaleKoef*0.85), (int) (screenCoords[1]+hex.SIZE_Y*scaleKoef*0.85)), paint);

        }
        else
            canvas.drawBitmap(texture, null, new Rect((int) (screenCoords[0]+hex.SIZE_X*scaleKoef*0.2), (int) (screenCoords[1]+hex.SIZE_Y*scaleKoef*0.2), (int) (screenCoords[0]+hex.SIZE_X*scaleKoef*0.8), (int) (screenCoords[1]+hex.SIZE_Y*scaleKoef*0.8)),paint);
        drawHP(canvas);
    }
    private void drawHP(Canvas canvas){
        canvas.drawText(hp+"",(float) (screenCoords[0] + Hex.SIZE_X*scaleKoef * 0.05), (float) (screenCoords[1] + Hex.SIZE_Y*scaleKoef * 0.4),paint);
    }

    public void attack(Squad squad) {
        Hex.moveDotHexList.clear();
        Hex.attackDotHexList.clear();

        if (speedLost>0){
            if (!squad.takeDamage(getAttackDamage(damage,squad.defence,(float)(hp)/maxHp))){
                takeDamage(getDefenceDamage(defence, squad.defence, (float)(squad.hp)/squad.maxHp));
                speedLost=0;
            } else {
                speedLost-=1;
                if (moveAfterDefeatSquadFlag){
                    move(squad.hex);
                }
            }

        }
    }
    public boolean takeDamage(int damage){
        hp-=damage;
        if (hp<=0){
            die();
            return true;
        }
        return false;
    }
    public void die(){
        hex.setSquad(null);
        player.squads.remove(this);
        if (Entity.selectedEntity==this)
            Entity.selectedEntity=null;
    }
    public static int getAttackDamage(int damage,int defenceDefender, float hpRatioAttacker){
        return Math.round((float)(5*(damage-0.5*defenceDefender)*(2*hpRatioAttacker-hpRatioAttacker*hpRatioAttacker)));
    }
    public static int getDefenceDamage(int defenceAttacker,int defenceDefender, float hpRatioDefender){
        return Math.round((float)(1.2*(defenceDefender*defenceDefender*defenceDefender/(defenceAttacker+1))*(2*hpRatioDefender-hpRatioDefender*hpRatioDefender)));
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }
}

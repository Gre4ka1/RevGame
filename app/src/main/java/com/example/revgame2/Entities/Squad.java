package com.example.revgame2.Entities;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.revgame2.Entities.Buildings.Road;
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
        if (Entity.selectedEntity == this) {
            canvas.drawBitmap(texture, (float) (hex.getX() + Hex.SIZE_X * 0.15) + hex.globalX, (float) (hex.getY() + Hex.SIZE_Y * 0.15)+hex.globalY, paint);

        }
        else
            canvas.drawBitmap(texture, (float) (hex.getX()+Hex.SIZE_X*0.2)+hex.globalX, (float) (hex.getY()+Hex.SIZE_Y*0.2)+hex.globalY,paint);
        drawHP(canvas);
    }
    private void drawHP(Canvas canvas){
        canvas.drawText(hp+"",(float) (hex.getX() + Hex.SIZE_X * 0.05)+hex.globalX, (float) (hex.getY() + Hex.SIZE_Y * 0.4)+hex.globalY,paint);
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

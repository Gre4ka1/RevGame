package com.example.revgame2;

import com.example.revgame2.Entities.Squad;

import java.util.ArrayList;

public class Player {
    public ArrayList<Squad> squads = new ArrayList<>();
    public Fraction fraction;
    protected AI ai;
    private int money, resources;

    public Player(Fraction fraction) {
        this.fraction = fraction;
        initResMon(fraction);
    }
    public void addSquad(Squad squad){
        squads.add(squad);
    }

    public AI getAI() {
        return ai;
    }

    public void initAI() {
        ai = new AI(this);
    }
    private void initResMon(Fraction f){
        switch (f){
            case RED:
                resources=3;
                money=3;
                break;
            case BLUE:
                resources=2;
                money=4;
                break;
            case YELLOW:
                resources=1;
                money=5;
                break;
            case GREEN:
                resources=5;
                money=1;
                break;
            case BLACK:
                resources=4;
                money=2;
                break;
            case WHITE:
                resources=3;
                money=3;
                break;
        }
    }

    public void addResurces(int res) {resources+=res;}
    public void addMoney(int mon) {money += mon;}
    public void spendResurces(int res) {resources-=res;}
    public void spendMoney(int mon) {money -= mon;}
    public int getResources() {return resources;}
    public int getMoney() {return money;}
}

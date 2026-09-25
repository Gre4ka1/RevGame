package com.example.revgame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.revgame2.Entities.City;
import com.example.revgame2.Entities.Entity;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Soldier;
import com.example.revgame2.Entities.Squad;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    public GameActivity gameActivity;
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<City> cities = new ArrayList<>();
    public Player humanPlayer;
    public static int step=0;
    private static Game instance;
    private Random random = new Random();
    public double scaleKoef = 1;
    public boolean showReputationFlag = false;
    private ArrayList<ConstraintLayout> stateLayoutList = new ArrayList<>();
    public float globalX=0,globalY=0;
    private Game() {
    }
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }
    public void setGameActivity(GameActivity gameActivity){
        this.gameActivity = gameActivity;
    }
    public void init(int width,int height, Context context) {

        Hex.SIZE_X=width/6;
        Hex.SIZE_Y= (int) (Hex.SIZE_X/1.732*2);
        TextureManager.getInstance().init(context);
        //Hex.setTextures(hex4,hex2,moveDot2);

        double stepX = Hex.SIZE_X/2;
        double stepY = Math.sqrt((Hex.SIZE_Y/2)*(Hex.SIZE_Y/2)-(Hex.SIZE_X/2)*(Hex.SIZE_X/2))+Hex.SIZE_Y/2;

        for (int i = 0; i < 200; i++) {
            if ((i/12)%2==0){
                new Hex((float) (i%12*Hex.SIZE_X), (float) (i/12*stepY), Relief.values()[random.nextInt(Relief.values().length)]);
            }
            else {
                if (i%12==11)
                    continue;
                new Hex((float) (i%12*Hex.SIZE_X+stepX), (float) (i/12*stepY), Relief.values()[random.nextInt(Relief.values().length)]);
            }
            Hex h = Hex.hexes.get(Hex.hexes.size()-1);
            if (random.nextInt(10)==0 && h.relief!=Relief.WATER){
                City c = new City(h, random.nextInt(3)+1);
                cities.add(c);
                h.setCity(c);
                h.relief = Relief.FIELD;

            }
        }

        Player playerBlue = new Player(Fraction.BLUE);
        Player playerRed = new Player(Fraction.RED);
        Player playerYellow = new Player(Fraction.YELLOW);
        Player playerGreen = new Player(Fraction.GREEN);
        Player playerBlack = new Player(Fraction.BLACK);
        Player playerWhite = new Player(Fraction.WHITE);
        players.add(playerRed);
        players.add(playerBlue);
        players.add(playerYellow);
        players.add(playerGreen);
        players.add(playerBlack);
        players.add(playerWhite);
        humanPlayer=playerRed;
        playerBlue.initAI();
        playerYellow.initAI();


        Hex.initReputation();


        Bitmap soldierRedTexture = BitmapFactory.decodeResource(context.getResources(),R.drawable.soldier_red);
        Bitmap soldierBlueTexture = BitmapFactory.decodeResource(context.getResources(),R.drawable.soldier_blue);
        Bitmap soldierYellowTexture = BitmapFactory.decodeResource(context.getResources(),R.drawable.soldier_yellow);
        Soldier.addTexturesForPlayer(players.get(0),soldierRedTexture);
        Soldier.addTexturesForPlayer(players.get(1),soldierBlueTexture);
        Soldier.addTexturesForPlayer(players.get(2),soldierYellowTexture);

        Hex tempHex=Hex.hexes.get(random.nextInt(Hex.hexes.size()));
        tempHex.setSquad(new Soldier(tempHex,players.get(0)));
        tempHex=Hex.hexes.get(random.nextInt(Hex.hexes.size()));
        tempHex.setSquad(new Soldier(tempHex,players.get(1)));
        tempHex=Hex.hexes.get(random.nextInt(Hex.hexes.size()));
        tempHex.setSquad(new Soldier(tempHex,players.get(2)));
        tempHex=Hex.hexes.get(random.nextInt(Hex.hexes.size()));
        tempHex.setSquad(new Soldier(tempHex,players.get(2)));




        gameActivity.binding.getRoot();
        stateLayoutList.add(gameActivity.binding.mainState);
        stateLayoutList.add(gameActivity.binding.squadSelectedState);
        stateLayoutList.add(gameActivity.binding.citySelectedState);
        stateLayoutList.add(gameActivity.binding.forestSelectedState);
        stateLayoutList.add(gameActivity.binding.swampSelectedState);
        stateLayoutList.add(gameActivity.binding.waterSelectedState);
        stateLayoutList.add(gameActivity.binding.fieldSelectedState);
        stateLayoutList.add(gameActivity.binding.mountainSelectedState);
        stateLayoutList.add(gameActivity.binding.buildingHexSelectedState);

        updateUI();
    }
    public void nextStep(){
        step+=1;

        botsSteps();

        for (Player player:players) {
            for (Squad squad:player.squads) {
                squad.speedLost=squad.speed;
            }
        }


        Hex.moveDotHexList.clear();
        Hex.attackDotHexList.clear();
        if (Entity.selectedEntity!=null)
            Entity.selectedEntity.texture=Entity.selectedEntity.noSelectedTexture;
        Entity.selectedEntity=null;
        updateUI();
    }

    private void botsSteps(){
        for (Player p:players) {
            if (p.ai!=null){
                p.ai.step();
            }
        }
        updateUI();
    }
    public void selectState(Entity entity){
        setGoneVisible();
        if (entity == null){
            gameActivity.binding.mainState.setVisibility(View.VISIBLE);
            return;
        } if (entity instanceof Squad) {
            gameActivity.binding.squadSelectedState.setVisibility(View.VISIBLE);
            return;
        } if (entity instanceof Hex){
            Hex h = (Hex) entity;

            if (h.getCity()!=null){
                gameActivity.binding.citySelectedState.setVisibility(View.VISIBLE);
                return;
            }
            if (h.relief == Relief.FIELD){

                if (h.getBuilding()==null){
                    gameActivity.binding.fieldSelectedState.setVisibility(View.VISIBLE);
//                    gameActivity.binding.removeInFieldButton.setVisibility(View.GONE);
//                    gameActivity.binding.buildFarmButton.setVisibility(View.VISIBLE);
//                    gameActivity.binding.buildFactoryButton.setVisibility(View.VISIBLE);
//                    //gameActivity.binding.buildBarracksButton.setVisibility(View.VISIBLE);
//                    gameActivity.binding.buildHangarButton.setVisibility(View.VISIBLE);
                } else {
                    gameActivity.binding.buildingHexSelectedState.setVisibility(View.VISIBLE);
//                    gameActivity.binding.removeInFieldButton.setVisibility(View.VISIBLE);
//                    gameActivity.binding.buildFarmButton.setVisibility(View.GONE);
//                    gameActivity.binding.buildFactoryButton.setVisibility(View.GONE);
//                    //gameActivity.binding.buildBarracksButton.setVisibility(View.GONE);
//                    gameActivity.binding.buildHangarButton.setVisibility(View.GONE);
                }
                return;
            }
            if (h.relief == Relief.FOREST){
                if (h.getBuilding()==null){
                    gameActivity.binding.forestSelectedState.setVisibility(View.VISIBLE);
                } else {
                    gameActivity.binding.buildingHexSelectedState.setVisibility(View.VISIBLE);
                }
                return;
            }
            if (h.relief == Relief.MOUNTAIN) {
                if (h.getBuilding()==null){
                    gameActivity.binding.mountainSelectedState.setVisibility(View.VISIBLE);
                } else {
                    gameActivity.binding.buildingHexSelectedState.setVisibility(View.VISIBLE);
                }
                return;
            }
            if (h.relief == Relief.SWAMP) {
                if (h.getBuilding()==null){
                    gameActivity.binding.swampSelectedState.setVisibility(View.VISIBLE);
                } else {
                    gameActivity.binding.buildingHexSelectedState.setVisibility(View.VISIBLE);
                }
                return;
            }
            if (h.relief == Relief.WATER) {
                if (h.getBuilding()==null){
                    gameActivity.binding.waterSelectedState.setVisibility(View.VISIBLE);
                } else {
                    gameActivity.binding.buildingHexSelectedState.setVisibility(View.VISIBLE);
                }
                return;
            }

            return;
        } /*if (entity instanceof City) {
            gameActivity.binding.citySelectedState.setVisibility(View.VISIBLE);
            return;
        }*/
    }
    private void setGoneVisible(){
        for (ConstraintLayout layout:stateLayoutList) {
            layout.setVisibility(View.GONE);
        }
    }


    public void updateUI(){
        updateTopTable();
    }
    private void updateTopTable(){
        gameActivity.binding.moneyCounter.setText(humanPlayer.getMoney()+"");
        gameActivity.binding.resourceCounter.setText(humanPlayer.getResources()+"");
    }


}

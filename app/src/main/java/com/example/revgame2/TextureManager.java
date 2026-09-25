package com.example.revgame2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.revgame2.Entities.Hex;

import java.util.HashMap;

public class TextureManager {
    private static TextureManager instance;

    private Bitmap selectedHexTexture;
    private Bitmap emptyHexTexture;
    private Bitmap fieldHexTexture;
    private Bitmap fieldHexSTexture;
    private Bitmap forestHexTexture;
    private Bitmap forestHexSTexture;
    private Bitmap mountainHexTexture;
    private Bitmap mountainHexSTexture;
    private Bitmap swampHexTexture;
    private Bitmap swampHexSTexture;
    private Bitmap waterHexTexture;
    private Bitmap waterHexSTexture;
    private Bitmap moveDotTexture;
    private Bitmap attackDotTexture;
    private Bitmap city1Texture;
    private Bitmap city2Texture;
    private Bitmap city3Texture;
    private Bitmap city1STexture;
    private Bitmap city2STexture;
    private Bitmap city3STexture;
    private Bitmap roadTexture;
    private Bitmap farmTexture;
    private Bitmap factoryTexture;
    private Bitmap hangarTexture;
    private Bitmap sawmillTexture;
    private Bitmap fortTexture;
    private Bitmap mineTexture;

    public HashMap<Relief,Bitmap> hexTextureMap = new HashMap<>();
    public HashMap<Relief,Bitmap> hexSTextureMap = new HashMap<>();

    private TextureManager() {}

    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }

    public void init(Context context) {
        Bitmap emptyHex = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex);
        Bitmap emptyHexScaled= Bitmap.createScaledBitmap(emptyHex, Hex.SIZE_X,Hex.SIZE_Y,true);
        emptyHexTexture=emptyHexScaled;
        Bitmap fieldHex = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_field2);
        Bitmap fieldHexScaled= Bitmap.createScaledBitmap(fieldHex,Hex.SIZE_X,Hex.SIZE_Y,true);
        fieldHexTexture=fieldHexScaled;
        Bitmap forestHex = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_forest2);
        Bitmap forestHexScaled= Bitmap.createScaledBitmap(forestHex,Hex.SIZE_X,Hex.SIZE_Y,true);
        forestHexTexture=forestHexScaled;
        Bitmap waterHex = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_water);
        Bitmap waterHexScaled= Bitmap.createScaledBitmap(waterHex,Hex.SIZE_X,Hex.SIZE_Y,true);
        waterHexTexture=waterHexScaled;
        Bitmap mountaintHex = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_mountain2);
        Bitmap mountainHexScaled= Bitmap.createScaledBitmap(mountaintHex,Hex.SIZE_X,Hex.SIZE_Y,true);
        mountainHexTexture=mountainHexScaled;
        Bitmap swamptHex = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_swamp2);
        Bitmap swampHexScaled= Bitmap.createScaledBitmap(swamptHex,Hex.SIZE_X,Hex.SIZE_Y,true);
        swampHexTexture=swampHexScaled;
        Bitmap fieldHexS = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_field2s);
        Bitmap fieldHexSScaled= Bitmap.createScaledBitmap(fieldHexS, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        fieldHexSTexture=fieldHexSScaled;
        Bitmap forestHexS = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_forest2s);
        Bitmap forestHexSScaled= Bitmap.createScaledBitmap(forestHexS,Hex.SIZE_X,Hex.SIZE_Y,true);
        forestHexSTexture=forestHexSScaled;
        Bitmap waterHexS = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_waters);
        Bitmap waterHexSScaled= Bitmap.createScaledBitmap(waterHexS,Hex.SIZE_X,Hex.SIZE_Y,true);
        waterHexSTexture=waterHexSScaled;
        Bitmap mountaintHexS = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_mountain2s);
        Bitmap mountainHexSScaled= Bitmap.createScaledBitmap(mountaintHexS,Hex.SIZE_X,Hex.SIZE_Y,true);
        mountainHexSTexture=mountainHexSScaled;
        Bitmap swamptHexS = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_swamp2s);
        Bitmap swampHexSScaled= Bitmap.createScaledBitmap(swamptHexS,Hex.SIZE_X,Hex.SIZE_Y,true);
        swampHexSTexture=swampHexSScaled;
        Bitmap moveDot = BitmapFactory.decodeResource(context.getResources(),R.drawable.move_dot);
        Bitmap moveDotScaled= Bitmap.createScaledBitmap(moveDot, (int) (Hex.SIZE_X*0.2), (int) (Hex.SIZE_Y*0.2),true);
        moveDotTexture=moveDotScaled;
        Bitmap attackDot = BitmapFactory.decodeResource(context.getResources(),R.drawable.attack_dot);
        Bitmap attackDotScaled= Bitmap.createScaledBitmap(attackDot, (int) (Hex.SIZE_X*0.4), (int) (Hex.SIZE_Y*0.4),true);
        attackDotTexture=attackDotScaled;
        Bitmap city1 = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_city1);
        Bitmap city1Scaled= Bitmap.createScaledBitmap(city1, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        city1Texture=city1Scaled;
        Bitmap city2 = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_city2);
        Bitmap city2Scaled= Bitmap.createScaledBitmap(city2, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        city2Texture=city2Scaled;
        Bitmap city3 = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_city3);
        Bitmap city3Scaled= Bitmap.createScaledBitmap(city3, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        city3Texture=city3Scaled;
        Bitmap city1S = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_city1s);
        Bitmap city1SScaled= Bitmap.createScaledBitmap(city1S, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        city1STexture=city1SScaled;
        Bitmap city2S = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_city2s);
        Bitmap city2SScaled= Bitmap.createScaledBitmap(city2S, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        city2STexture=city2SScaled;
        Bitmap city3S = BitmapFactory.decodeResource(context.getResources(),R.drawable.hex_city3s);
        Bitmap city3SScaled= Bitmap.createScaledBitmap(city3S, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        city3STexture=city3SScaled;
        Bitmap road = BitmapFactory.decodeResource(context.getResources(),R.drawable.road_hex);
        Bitmap roadScaled= Bitmap.createScaledBitmap(road, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        roadTexture=roadScaled;
        Bitmap farm = BitmapFactory.decodeResource(context.getResources(),R.drawable.farm_hex);
        Bitmap farmScaled= Bitmap.createScaledBitmap(farm, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        farmTexture=farmScaled;
        Bitmap factory = BitmapFactory.decodeResource(context.getResources(),R.drawable.factory_hex);
        Bitmap factoryScaled= Bitmap.createScaledBitmap(factory, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        factoryTexture=factoryScaled;
        Bitmap hangar = BitmapFactory.decodeResource(context.getResources(),R.drawable.hangar_hex);
        Bitmap hangarScaled= Bitmap.createScaledBitmap(hangar, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        hangarTexture=hangarScaled;
        Bitmap sawmill = BitmapFactory.decodeResource(context.getResources(),R.drawable.sawmill_hex);
        Bitmap sawmillScaled= Bitmap.createScaledBitmap(sawmill, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        sawmillTexture=sawmillScaled;
        Bitmap fort = BitmapFactory.decodeResource(context.getResources(),R.drawable.fort_hex);
        Bitmap fortScaled= Bitmap.createScaledBitmap(fort, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        fortTexture=fortScaled;
        Bitmap mine = BitmapFactory.decodeResource(context.getResources(),R.drawable.mine_hex);
        Bitmap mineScaled= Bitmap.createScaledBitmap(mine, (int) (Hex.SIZE_X), (int) (Hex.SIZE_Y),true);
        mineTexture=mineScaled;




        hexTextureMap.put(Relief.FIELD,fieldHexTexture);
        hexTextureMap.put(Relief.FOREST,forestHexTexture);
        hexTextureMap.put(Relief.MOUNTAIN,mountainHexTexture);
        hexTextureMap.put(Relief.SWAMP,swampHexTexture);
        hexTextureMap.put(Relief.WATER,waterHexTexture);

        hexSTextureMap.put(Relief.FIELD,fieldHexSTexture);
        hexSTextureMap.put(Relief.FOREST,forestHexSTexture);
        hexSTextureMap.put(Relief.MOUNTAIN,mountainHexSTexture);
        hexSTextureMap.put(Relief.SWAMP,swampHexSTexture);
        hexSTextureMap.put(Relief.WATER,waterHexSTexture);
    }
    public Bitmap getHexTexture(Relief relief){
        return hexTextureMap.get(relief);
    }
    public Bitmap getHexSTexture(Relief relief){
        return hexSTextureMap.get(relief);
    }
    public Bitmap getEmptyHexTexture() {
        return emptyHexTexture;
    }
    public Bitmap getFieldHexTexture() {
        return fieldHexTexture;
    }
    public Bitmap getForestHexTexture() {
        return forestHexTexture;
    }
    public Bitmap getMoveDotTexture() {
        return moveDotTexture;
    }
    public Bitmap getSelectedHexTexture() {
        return selectedHexTexture;
    }
    public Bitmap getWaterHexTexture() {
        return waterHexTexture;
    }
    public Bitmap getMountainHexTexture() {
        return mountainHexTexture;
    }
    public Bitmap getSwampHexTexture() {
        return swampHexTexture;
    }
    public Bitmap getAttackDotTexture() {
        return attackDotTexture;
    }
    public Bitmap getCity1Texture() {
        return city1Texture;
    }
    public Bitmap getCity2Texture() {
        return city2Texture;
    }
    public Bitmap getCity3Texture() {
        return city3Texture;
    }
    public Bitmap getCity1STexture() {
        return city1STexture;
    }
    public Bitmap getCity2STexture() {
        return city2STexture;
    }
    public Bitmap getCity3STexture() {
        return city3STexture;
    }
    public Bitmap getFieldHexSTexture() {
        return fieldHexSTexture;
    }
    public Bitmap getWaterHexSTexture() {
        return waterHexSTexture;
    }
    public Bitmap getSwampHexSTexture() {
        return swampHexSTexture;
    }
    public Bitmap getMountainHexSTexture() {
        return mountainHexSTexture;
    }
    public Bitmap getForestHexSTexture() {
        return forestHexSTexture;
    }
    public Bitmap getSawmillTexture() {return sawmillTexture;}
    public Bitmap getFactoryTexture() {
        return factoryTexture;
    }
    public Bitmap getFarmTexture() {
        return farmTexture;
    }
    public Bitmap getHangarTexture() {
        return hangarTexture;
    }
    public Bitmap getRoadTexture() {
        return roadTexture;
    }
    public Bitmap getFortTexture() {return fortTexture;}
    public Bitmap getMineTexture() {return mineTexture;}
}
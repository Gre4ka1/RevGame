package com.example.revgame2.Entities;

import android.graphics.Bitmap;
import android.graphics.Paint;

import com.example.revgame2.Game;

public class Entity {
    public Bitmap texture;
    public Bitmap selectedTexture;
    public Bitmap noSelectedTexture;
    public static Paint paint;

    public static Entity selectedEntity;

    public Entity(Bitmap selectedTexture, Bitmap noSelectedTexture) {
        this.texture = noSelectedTexture;
        this.selectedTexture=selectedTexture;
        this.noSelectedTexture=noSelectedTexture;
        paint=new Paint();
    }


    public void select(){

        if (selectedEntity!=null)
            selectedEntity.texture=selectedEntity.noSelectedTexture;
        texture=selectedTexture;
        selectedEntity=this;

        Hex.moveDotHexList.clear();
        Hex.attackDotHexList.clear();
        Game.getInstance().selectState(this);
    }
    public static void unselect(){
        if (selectedEntity!=null)
            selectedEntity.texture=selectedEntity.noSelectedTexture;
        selectedEntity=null;
        Game.getInstance().selectState(null);
    }

}

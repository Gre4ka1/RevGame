package com.example.revgame2.Entities.Buildings;

import android.graphics.Bitmap;

import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Unit;
import com.example.revgame2.Relief;

public class Building extends Unit {
    public Relief providedRelief;
    public Relief secondProvidedRelief;
    public final int cost;
    public Building(Hex hex, Relief providedRelief, Relief secondProvidedRelief, int cost, Bitmap selectedTexture, Bitmap noSelectedTexture) {
        super(hex, selectedTexture, noSelectedTexture);
        this.providedRelief=providedRelief;
        this.secondProvidedRelief=secondProvidedRelief;
        this.cost=cost;
    }
}

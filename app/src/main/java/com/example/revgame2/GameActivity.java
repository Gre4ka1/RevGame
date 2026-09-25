package com.example.revgame2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.revgame2.Entities.Buildings.Fort;
import com.example.revgame2.Entities.Buildings.Mine;
import com.example.revgame2.Entities.Entity;
import com.example.revgame2.Entities.Buildings.Factory;
import com.example.revgame2.Entities.Buildings.Farm;
import com.example.revgame2.Entities.Buildings.Hangar;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Buildings.Sawmill;
import com.example.revgame2.Entities.Soldier;
import com.example.revgame2.databinding.ActivityGameBinding;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    public ActivityGameBinding binding;
    public Random random = new Random();
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGameBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        //System.out.println(intent.getStringExtra("xol"));
        int screenWidth = getIntent().getIntExtra("width",1);
        int screenHeight = getIntent().getIntExtra("height",1);

        game=Game.getInstance();
        game.setGameActivity(this);
        Game.getInstance().init(screenWidth,screenHeight,this);


        binding.nextStepButton.setOnClickListener(v -> {
            game.nextStep();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.showReputationButton.setOnClickListener(v -> {
            game.showReputationFlag=!game.showReputationFlag;
            binding.gameView.invalidate();
        });
        binding.spawnSoldierButton.setOnClickListener(v -> {
            if (Entity.selectedEntity != null && Entity.selectedEntity instanceof Hex){
                if (((Hex) Entity.selectedEntity).getSquad()==null){
                    ((Hex) Entity.selectedEntity).spawnSquad(new Soldier((Hex) Entity.selectedEntity,game.humanPlayer));
                }
            }
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildMountainRoadButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex) {
                ((Hex) Entity.selectedEntity).buildRoad(game.humanPlayer);
                Entity.unselect();
                game.updateUI();
                binding.gameView.invalidate();
            }
        });
        binding.buildSwampRoadButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex) {
                ((Hex) Entity.selectedEntity).buildRoad(game.humanPlayer);
                Entity.unselect();
                game.updateUI();
                binding.gameView.invalidate();
            }
        });
        binding.buildFarmButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Farm((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.removeForestButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).removeForest(game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildFactoryButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Factory((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildHangarButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Hangar((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildSawmillButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Sawmill((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildFortButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Fort((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildMountainFortButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Fort((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.buildMineButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).buildBuilding(new Mine((Hex) Entity.selectedEntity),game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();
        });
        binding.removeBuildingButton.setOnClickListener(v -> {
            if (Entity.selectedEntity instanceof Hex)
                ((Hex) Entity.selectedEntity).removeBuilding(game.humanPlayer);
            Entity.unselect();
            game.updateUI();
            binding.gameView.invalidate();

        });

    }



}
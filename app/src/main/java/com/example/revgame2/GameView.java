package com.example.revgame2;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.revgame2.Entities.City;
import com.example.revgame2.Entities.Hex;
import com.example.revgame2.Entities.Squad;

import java.util.Random;

public class GameView extends View{
    public Random random = new Random();
    boolean clickFlag = true;
    private final float SWAP_LIMIT=3;



    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }



    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#666666"));
        paint.setTextSize(10);

        canvas.drawRect(0,0, getWidth(),getHeight(),paint);
        for (Hex h:Hex.hexes) {
            h.draw(canvas);
            h.drawBuilding(canvas);
        }

        for (City c:Game.getInstance().cities) {
            c.draw(canvas);
        }
        for (Player player:Game.getInstance().players) {
            for (Squad squad:player.squads) {
                squad.draw(canvas);
            }
        }
        drawUI(canvas);

    }
    private void drawUI(Canvas canvas){
        if (Game.getInstance().showReputationFlag) {
            for (Hex h : Hex.hexes) {
                h.drawReputation(canvas);
            }
        }
        for (Hex h:Hex.attackDotHexList){
            h.drawAttackDot(canvas);
        }
        for (Hex h:Hex.moveDotHexList){
            h.drawMoveDot(canvas);
        }
    }
    /*@Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            performClick(); // Вызов обязателен!
        }
        Hex.getClickedHex(motionEvent.getX(),motionEvent.getY()).onClick();
        return false;
    }*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //System.out.println(event.getAction());
        if (event.getPointerCount()==2 && event.getHistorySize()>0){
            double dist = Math.hypot(event.getX(0)-event.getX(1),
                    event.getY(0)-event.getY(1));
            double historyDist = Math.hypot(event.getHistoricalX(0)-event.getHistoricalX(1),
                    event.getHistoricalY(0)-event.getHistoricalY(1));
            Game.getInstance().scaleKoef *= dist/historyDist;
            return false;

            //todo swap mid 2 points
        }
        if (event.getAction()== MotionEvent.ACTION_DOWN) {
            clickFlag=true;
            performClick();
        }
        if (event.getAction() == MotionEvent.ACTION_UP && clickFlag) {
            performClick();
            Hex touchedHex = Hex.getClickedHex(event.getX(),event.getY());
            if (touchedHex!=null)
                touchedHex.onTouch();
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            performClick();
            if (event.getHistorySize()>0) {
                float dx = event.getX() - event.getHistoricalX(0);
                float dy = event.getY() - event.getHistoricalY(0);
                //System.out.println(dx+" "+dy);
                if (clickFlag) {
                    if (Math.hypot(dx, dy) >= SWAP_LIMIT) {
                        clickFlag = false;
                    } else return false;
                }
                Game.getInstance().globalX += dx;
                Game.getInstance().globalY += dy;
                invalidate();
                //System.out.println(Game.getInstance().globalX);
            }
        }
        //return false;
        return super.onTouchEvent(event);
    }

    public static GameActivity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (GameActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


}

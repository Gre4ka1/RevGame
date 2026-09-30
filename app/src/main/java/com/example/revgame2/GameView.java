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
    private float[] startTouchRealCoord;



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
        if (event.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN){
            startTouchRealCoord=Game.getInstance().getRealCoordinates((event.getX(0)+event.getX(1))/2,(event.getY(0)+event.getY(1))/2);
        }
        if (event.getActionMasked()==MotionEvent.ACTION_POINTER_UP){
            int index = event.getActionIndex()==0?1:0;
            startTouchRealCoord=Game.getInstance().getRealCoordinates(event.getX(index),event.getY(index));
        }
        if (event.getPointerCount()==2 && event.getHistorySize()>0){
            clickFlag=false;
            performClick();
            double dist = Math.hypot(event.getX(0)-event.getX(1),
                    event.getY(0)-event.getY(1));
            double historyDist = Math.hypot(event.getHistoricalX(0,0)-event.getHistoricalX(1,0),
                    event.getHistoricalY(0,0)-event.getHistoricalY(1,0));
            float midX = (event.getX(0)+event.getX(1))/2;
            float midY = (event.getY(0)+event.getY(1))/2;

            float[] clickRealCoord = Game.getInstance().getRealCoordinates(midX,midY);
            float dx = clickRealCoord[0]-startTouchRealCoord[0];
            float dy = clickRealCoord[1]-startTouchRealCoord[1];
            Game.getInstance().globalX+=dx;
            Game.getInstance().globalY+=dy;


            double preScale=Game.getInstance().scaleKoef;
            Game.getInstance().scaleKoef *= dist/historyDist;
            Game.getInstance().globalX+=getWidth()*(1/Game.getInstance().scaleKoef-1/preScale) * (midX/getWidth());
            Game.getInstance().globalY+=getHeight()*(1/Game.getInstance().scaleKoef-1/preScale) * (midY/getHeight());

            invalidate();
            return false;
        }
        if (event.getAction()== MotionEvent.ACTION_DOWN) {
            clickFlag=true;
            startTouchRealCoord = Game.getInstance().getRealCoordinates(event.getX(),event.getY());
            performClick();
        }
        if (event.getAction() == MotionEvent.ACTION_UP && clickFlag) {
            performClick();
            float[] realCoord = Game.getInstance().getRealCoordinates(event.getX(),event.getY());
            Hex touchedHex = Hex.getClickedHex(realCoord[0],realCoord[1]);
            if (touchedHex!=null)
                touchedHex.onTouch();
            invalidate();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            performClick();
            if (event.getHistorySize()>0) {
                //float[] clickPreRealCoord = Game.getInstance().getRealCoordinates(event.getHistoricalX(0),event.getHistoricalY(0));
                //float clickRealX = clickPreRealCoord[0];
                //float clickRealY = clickPreRealCoord[1];
                //float clickScreenX = event.getHistoricalX(0);
                //float clickScreenY = event.getHistoricalY(0);

                float[] clickRealCoord = Game.getInstance().getRealCoordinates(event.getX(),event.getY());
                float dx = clickRealCoord[0]-startTouchRealCoord[0];
                float dy = clickRealCoord[1]-startTouchRealCoord[1];

                if (clickFlag) {
                    if (Math.hypot(dx, dy) >= SWAP_LIMIT) {
                        clickFlag = false;
                    } else return false;
                }

                Game.getInstance().globalX+=dx;
                Game.getInstance().globalY+=dy;
                invalidate();

                /*float dx = (float) ((event.getX() - event.getHistoricalX(0))*1.4);
                float dy = (float) ((event.getY() - event.getHistoricalY(0))*1.4);
                //System.out.println(dx+" "+dy);
                if (clickFlag) {
                    if (Math.hypot(dx, dy) >= SWAP_LIMIT) {
                        clickFlag = false;
                    } else return false;
                }
                Game.getInstance().globalX += dx/Game.getInstance().scaleKoef;
                Game.getInstance().globalY += dy/Game.getInstance().scaleKoef;*/

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

package com.shop171217.hoang.caroonline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    final static int maxNum = 15;
    private Context context;

    private ImageView[][] ivCell = new ImageView[maxNum][maxNum];
    private Drawable[] drawables = new Drawable[4];// 0 is empty, 1 is player, 2 is bot, 3 is background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        loadResource();
        designBoarGame();

    }
    @SuppressLint("NewApi")
    private void loadResource() {
        drawables[3] = context.getResources().getDrawable(R.drawable.cell_bg);
        //copy 2 image for drawable player and bot
        drawables[0] = null;
        drawables[1] = context.getResources().getDrawable(R.drawable.cat);
        drawables[2] = context.getResources().getDrawable(R.drawable.dog);
    }

    private void designBoarGame() {

        int sizeofCell = Math.round(ScreenWidth()/maxNum);
        LinearLayout.LayoutParams ipRow = new LinearLayout.LayoutParams(sizeofCell*maxNum,sizeofCell);
        LinearLayout.LayoutParams ipCell = new LinearLayout.LayoutParams(sizeofCell,sizeofCell);

        LinearLayout linBoardGame = findViewById(R.id.linBoardGame);

        for (int i = 0; i < maxNum; i++) {
            LinearLayout linRow = new LinearLayout(context);

            for (int j = 0; j < maxNum; j++) {
                ivCell[i][j] = new ImageView(context);

                ivCell[i][j].setBackground(drawables[3]);
                linRow.addView(ivCell[i][j],ipCell);
            }
            linBoardGame.addView(linRow,ipRow);
        }
    }

    private float ScreenWidth() {
        Resources  resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

}

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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button btnPlay;
    TextView tvTurn;

    private  int[][] valueCell = new int[maxNum][maxNum];
    private int winner_play;
    private int turnPlay;
    private boolean firstMove;
    private int xMove,yMove;

    final static int maxNum = 15;
    private Context context;

    private ImageView[][] ivCell = new ImageView[maxNum][maxNum];
    private Drawable[] drawables = new Drawable[4];// 0 is empty, 1 is player, 2 is bot, 3 is background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setListener();
        loadResource();
        designBoarGame();


    }

    private void setListener() {
        btnPlay = findViewById(R.id.btnPlay);
        tvTurn = findViewById(R.id.tvTurn);

        btnPlay.setText("Play Game");
        tvTurn.setText("Press button to play game");
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGame();
                playGame();
            }
        });
    }

    private void playGame() {
        //who play first;
        Random random = new Random();
        turnPlay = random.nextInt(2)+1;

        if(turnPlay == 1){
            Toast.makeText(context, "Player play first", Toast.LENGTH_SHORT).show();
            playerTurn();
        }else {
            Toast.makeText(context, "Bot play first", Toast.LENGTH_SHORT).show();
            botTurn();
        }
    }

    private void botTurn() {
        tvTurn.setText("Bot");
        //choose the center cell
        if(firstMove){
            firstMove = false;
            xMove = 7;
            yMove = 7;
            make_a_move();
        }else {

        }

    }

    private void make_a_move() {
        ivCell[xMove][yMove].setImageDrawable(drawables[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;
        
        if(noEmptyCell()){
            Toast.makeText(context, "Draw!", Toast.LENGTH_SHORT).show();
            return;
        }else if (checkWinner()) {
            if(winner_play == 1)
            {
                tvTurn.setText("Winner is Player");
                Toast.makeText(context, "Winner is player" , Toast.LENGTH_SHORT).show();
            }else {
                tvTurn.setText("Winner is Bot");
                Toast.makeText(context, "Winner is Bot" , Toast.LENGTH_SHORT).show();
            }

            return;
        }
        if(turnPlay == 1){
            turnPlay = (1+2)-turnPlay;
            botTurn();
        }else {
            turnPlay = 3- turnPlay;
            playerTurn();
        }
    }

    private boolean checkWinner() {

        if(winner_play != 0) return true;

        //check in row
        VectorEnd(xMove,0,0,1,xMove,yMove);
        return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {

        if(winner_play != 0){
            final int range = 4;
            int i = 0;
            int j = 0;
            int xbelow = rx - range*vx;
            int ybelow = ry - range*vy;
            int xabove = rx + range*vx;
            int yabove = ry + range*vy;
            String str = "";
            while (!inside(i,xbelow,xabove)|| !inside(j,ybelow,yabove)){
                i += vx;
                j += vy;
            }
        }

    }

    private boolean inside(int i, int xbelow, int xabove) {

        return (i - xbelow)* (i - xabove) <= 0;
    }

    private boolean noEmptyCell() {
        for (int i = 0; i < maxNum; i++) {
            for (int j = 0; j < maxNum; j++) {
                if(valueCell[i][j] == 0) return false;
            }
        }
        return false;
    }

    private void playerTurn() {
        tvTurn.setText("Player");
        isClicked = false;

    }

    private void initGame() {
        firstMove = true;
        winner_play = 0;
        for (int i = 0; i < maxNum; i++) {
            for (int j = 0; j < maxNum; j++) {
                ivCell[i][j].setImageDrawable(drawables[0]);
                valueCell[i][j] = 0;
            }
        }
    }

    private boolean isClicked; // track player click
    @SuppressLint("NewApi")
    private void loadResource() {
        drawables[3] = context.getResources().getDrawable(R.drawable.cell_bg);
        //copy 2 image for drawable player and bot
        drawables[0] = null;
        drawables[1] = context.getResources().getDrawable(R.drawable.x);
        drawables[2] = context.getResources().getDrawable(R.drawable.oo);
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
                final int x = i;
                final int y  = j;
                ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(valueCell[xMove][yMove] == 0){
                            if((turnPlay == 1) || (isClicked == false)){
                                Log.d("click", "onClick: "+ isClicked);
                                isClicked = true;
                                xMove = x;
                                yMove = y;
                                make_a_move();

                            }
                        }

                    }
                });
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

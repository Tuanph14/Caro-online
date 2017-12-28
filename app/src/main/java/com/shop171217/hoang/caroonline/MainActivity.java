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

import java.util.ArrayList;
import java.util.List;
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
        tvTurn.setText("Bot Turn");
//        make_a_move();
        //choose the center cell
        if(firstMove){
            firstMove = false;
            xMove = 7;
            yMove = 7;
            make_a_move();
        }else {
            findBotMove();
            make_a_move();
        }
        isClicked = false;

    }

    private final int[] iRow = {-1,-1,-1,0,1,1,1,0};
    private final int[] iCol = {-1,0,1,1,1,0,-1,-1};

    private void findBotMove() {
        List<Integer> listX = new ArrayList<>();
        List<Integer> listY = new ArrayList<>();


        final int rang = 2;
        for (int i = 0; i < maxNum; i++) {
            for (int j = 0; j < maxNum; j++) {
                if(valueCell[i][j] != 0){
                    for (int k = 0; k < rang; k++) {
                        for (int l = 0; l < 8; l++) {
                            int x = i+iRow[l]*k;
                            int y = j+iCol[l]*k;
                            if(inBoard(x,y) && valueCell[x][y] == 0){
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
            }
        }
        int Lx = listX.get(0);
        int Ly = listY.get(0);

        int res = Integer.MAX_VALUE -10;
        for (int i = 0; i < listX.size(); i++) {
            int x = listX.get(i);
            int y = listY.get(i);
            valueCell[x][y] = 2;
            int rr = getValue_position();
            if(rr<res){
                res = rr;
                Lx = x;
                Ly = y;
            }
            valueCell[x][y] = 0;
        }
        xMove = Lx;
        yMove = Ly;
    }

    private void make_a_move() {
        Log.d("tuan", "make_a_move: " + xMove+":"+yMove+":"+turnPlay);
        ivCell[xMove][yMove].setImageDrawable(drawables[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;
        
        if(noEmptyCell()){
            Toast.makeText(context, "Draw!", Toast.LENGTH_SHORT).show();
            return;
        }else if (checkWinner()) {
            if(winner_play == 1)
            {
                Log.d("checkWinner", "winner_play: " + winner_play);
                tvTurn.setText("Winner is Player");
                Toast.makeText(context, "Winner is player" , Toast.LENGTH_SHORT).show();
            }else {
                Log.d("checkWinner", "winner_play: " + winner_play);

                tvTurn.setText("Winner is Bot");
                Toast.makeText(context, "Winner is Bot" , Toast.LENGTH_SHORT).show();
            }

            return;
        }
        if(turnPlay == 1){
            turnPlay = (1+2)-turnPlay;
            botTurn();
        }else {
            turnPlay = 3 - turnPlay;
            playerTurn();
        }
    }

    private int getValue_position(){
        int rr = 0;
        int p1 = turnPlay;
        //row
        for (int i = 0; i < maxNum; i++) {
            rr += checkValue(maxNum-1,i,-1,0,p1);
        }
        //column
        for (int i = 0; i < maxNum; i++) {
            rr += checkValue(i,maxNum-1,0,-1,p1);
        }
        //cross right to left
        for (int i = maxNum-1; i >=0; i--) {
            rr += checkValue(i,maxNum-1,-1,-1,p1);
        }
        for (int i = maxNum-2; i >=0; i--) {
            rr += checkValue(maxNum-1,i,-1,-1,p1);
        }
        //cross left to right
        for (int i = maxNum-1; i >=0; i--) {
            rr += checkValue(i,0,-1,1,p1);
        }
        for (int i = maxNum-2; i >=0; i--) {
            rr += checkValue(maxNum-1,i,-1,-1,p1);
        }
        return rr;
    }

    private int checkValue(int xd, int yd, int vx, int vy, int p1) {

        int i,j;
        int rr = 0;
        i = xd; j = yd;
        String str = String.valueOf(valueCell[i][j]);
        while(true){
            i += vx;
            j += vy;
            if(inBoard(i,j)){
                str = str + String.valueOf(valueCell[i][j]);
                if(str.length() == 6){
                    rr += Eval(str,p1);
                    str = str.substring(1,6);
                }
            }else break;
        }

        return rr;
    }


    private int Eval(String str, int p1){

        int b1 = 1, b2 =1;
        if(p1 == 1){

            b1 = 2;
            b2 = 2;
        }else {
            b1 = 1;
            b2 = 1;
        }

        switch (str){
            case "111110": return b1 = 100000000;
            case "011111": return b1 = 100000000;
            case "211111": return b1 = 100000000;
            case "111112": return b1 = 100000000;
            case "011110": return b1 = 100000000;
            case "101110": return b1 = 1002;
            case "011101": return b1 = 1002;
            case "011112": return b1 = 1000;
            case "011100": return b1 = 102;
            case "001110": return b1 = 102;
            case "010111": return b1 = 100;
            case "211110": return b1 = 100;
            case "211011": return b1 = 100;
            case "211101": return b1 = 100;
            case "010100": return b1 = 10;
            case "011000": return b1 = 10;
            case "000110": return b1 = 10;
            case "211000": return b1 = 1;
            case "200110": return b1 = 1;
            case "200011": return b1 = 1;
            case "222220": return b1 = -100000000;
            case "022222": return b1 = -100000000;
            case "122222": return b1 = -100000000;
            case "222221": return b1 = -100000000;
            case "022220": return b1 = -100000000;
            case "202220": return b1 = -1002;
            case "022202": return b1 = -1002;
            case "022221": return b1 = -1000;
            case "022200": return b1 = -102;
            case "002220": return b1 = -102;
            case "120222": return b1 = -100;
            case "122220": return b1 = -100;
            case "122022": return b1 = -100;
            case "122202": return b1 = -100;
            case "020200": return b1 = -10;
            case "022000": return b1 = -10;
            case "002200": return b1 = -10;
            case "000220": return b1 = -10;
            case "122000": return b1 = -1;
            case "102200": return b1 = -1;
            case "100220": return b1 = -1;
            case "100022": return b1 = -1;
            default: break;

        }
        return 0;
    }

    private boolean checkWinner() {


        if(winner_play != 0) return true;

        //check in rowcheckValue
        VectorEnd(xMove,0,0,1,xMove,yMove);
        //column
        VectorEnd(0,yMove,1,0   ,xMove,yMove);
        //left to right
        if(xMove + yMove >= maxNum-1){
            VectorEnd(maxNum-1,xMove+yMove-maxNum+1,-1,1,xMove,yMove);
        }else {
            VectorEnd(xMove+yMove,0,-1,1,xMove,yMove);
        }
        //check right to left
        if(xMove <= yMove){
            VectorEnd(xMove-yMove+maxNum-1,maxNum-1,-1,-1,xMove,yMove);
        }else{
            VectorEnd(maxNum-1,maxNum-1-(xMove - yMove),-1,-1,xMove,yMove);
        }
        Log.d("tuan", "checkWinner: " + winner_play);
        if(winner_play != 0) return true; else return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {

        if(winner_play != 0) return;
        final int range = 4;
        int i;
        int j;
        int xbelow = rx - range*vx;
        int ybelow = ry - range*vy;
        int xabove = rx + range*vx;
        int yabove = ry + range*vy;
        String str = "";
        i = xx;
        j = yy;
        while (!inside(i,xbelow,xabove)|| !inside(j,ybelow,yabove)){
            i += vx;
            j += vy;
        }
        while(true){
            str = str + String.valueOf(valueCell[i][j]);
            if(str.length() == 5){
                Log.d("tuan", "VectorEnd: "+str);
                EvalEnd(str);
                str = str.substring(1,5);
            }
            i += vx;
            j += vy;
            if((!inBoard(i,j))||!inside(i,xbelow,xabove)||!inside(j,ybelow,yabove)){
                break;
            }
        }


    }

    private boolean inBoard(int i, int j) {
        if(i<0 || i >maxNum-1||j<0 ||j >maxNum-1) return false;

        return true;
    }

    private void EvalEnd(String str) {
        switch (str){
            case "11111":winner_play = 1; break;
            case "22222":winner_play = 2;break;
            default: break;
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
        return true;
}

    private void playerTurn() {
        tvTurn.setText("Player");
        Log.d("Player", "Player: ");
        firstMove = false;
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
                        if(valueCell[x][y] == 0){
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

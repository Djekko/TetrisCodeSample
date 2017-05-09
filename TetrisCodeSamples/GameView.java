/**
 * Created by Karl Spurgin on 18/12/2016.
 */

package com.example.tetris;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

import static android.content.ContentValues.TAG;

public class GameView extends SurfaceView implements Runnable
{
    volatile boolean playing;
    Thread gameThread = null;
    private boolean bGameOver = false;

    // For drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private Context context;

    private int screenX;
    private int screenY;

    //shape number
    private int playerShape;

    private int shapeRotation = 0;
    //cube pos for each shape, each shape is made of 4 cubes
    private int[] playerShapeX = new int[4];
    private int[] playerShapeY = new int[4];

    // 2D array of integers for the main play area
    int[][] grid;

    //play area grid size
    private int gridY = 20;
    private int gridX = 10;

    //array of 3 preview shapes
    int[] previewShapes = new int[3];

    //preview grid array
    int[][] previewGrid;

    //preview grid size
    private int previewY = 4;
    private int previewX = 14;

    //offset for spawning the grid at the bottom of the screen instead of the top
    private int PlayAreaOffset = 175;

    private long Score = 0;
    private long Lines = 0;
    private long HighScore = 0;
    private long Level = 1;
    private long MoveDelay = 800;

    private int numOfLinesBeforeNextLevel = 5;

    private long LastMove = 0;

    public GameView( Context context, int x, int y){
        super(context);

        this.context  = context;

        initArraysAndVariables(x,y);

        //Load the saved high score
        HighScore = loadHighScore();

        initGame();

    }   //    public GameView( Context context, int x, int y )

    //Initialize variables
    public void initArraysAndVariables(int x, int y){
        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;

        //define grid size
        grid = new int[gridY][gridX];

        //Initialise each value in array
        for (int rowX = 0; rowX < gridX; rowX++)
        {
            for (int colY = 0; colY < gridY; colY++)
            {
                grid[colY][rowX] = 0;
            }
        }

        //define preview grid size
        previewGrid = new int[previewY][previewX];

        //Initialise each value in array
        for (int preX = 0; preX < previewX; preX++)
        {
            for (int preY = 0; preY < previewY; preY++)
            {
                previewGrid[preY][preX] = 0;
            }
        }
    }   //initArraysAndVariables(int x, int y)

    //Initialize Game
    public void initGame(){

        //init play area
        for (int a = 0; a < gridX; a++){
            for (int b = 0; b < gridY; b++){
                grid[b][a] = 0;
            }
        }

        Score = 0;
        Lines = 0;
        Level = 1;
        MoveDelay = 800;

        Random rnd = new Random();

        for (int a = 0; a < previewShapes.length; a++){

            int nextShape = rnd.nextInt(7);    // get a random value between 0 - 6

            previewShapes[a] = nextShape;
        }

        bGameOver = false;
        spawnNextPreviewShape();
    }   //public void initGame()

    //Create new preview shape
    public void spawnNextPreviewShape(){

        Random r = new Random();
        int newShape = r.nextInt(7);    // get a random value between 0 - 6

        //reset the rotation to 0 so the shapes are drawn in teh correct rotation
        shapeRotation = 0;

        updatePlayerShape(previewShapes[0]);

        //Clear Preview grid
        for (int x = 0; x < previewX; x++){
            for (int y = 0; y < previewY; y++){
                previewGrid[y][x] = 0;
            }
        }

        previewShapes[0] = previewShapes[1];
        previewShapes[1] = previewShapes[2];
        previewShapes[2] = newShape;

        setPreviewShapesPosition();

    }   //public void spawnNextPreviewShape()

    //Make a new player shape at the top of the grid
    public void updatePlayerShape(int nextShape){
        boolean bCanSpawn = true;

        playerShape = nextShape + 1;

        switch (nextShape){
            case 0: // O Shape
                playerShapeX[0] = 4;
                playerShapeY[0] = 0;

                playerShapeX[1] = 5;
                playerShapeY[1] = 0;

                playerShapeX[2] = 4;
                playerShapeY[2] = 1;

                playerShapeX[3] = 5;
                playerShapeY[3] = 1;

                break;
            case 1: // I Shape
                playerShapeX[0] = 3;
                playerShapeY[0] = 0;

                playerShapeX[1] = 4;
                playerShapeY[1] = 0;

                playerShapeX[2] = 5;
                playerShapeY[2] = 0;

                playerShapeX[3] = 6;
                playerShapeY[3] = 0;

                break;
            case 2: // S Shape
                playerShapeX[0] = 3;
                playerShapeY[0] = 1;

                playerShapeX[1] = 4;
                playerShapeY[1] = 1;

                playerShapeX[2] = 4;
                playerShapeY[2] = 0;

                playerShapeX[3] = 5;
                playerShapeY[3] = 0;

                break;
            case 3: // Z Shape
                playerShapeX[0] = 3;
                playerShapeY[0] = 0;

                playerShapeX[1] = 4;
                playerShapeY[1] = 0;

                playerShapeX[2] = 4;
                playerShapeY[2] = 1;

                playerShapeX[3] = 5;
                playerShapeY[3] = 1;

                break;
            case 4: // L Shape
                playerShapeX[0] = 3;
                playerShapeY[0] = 1;

                playerShapeX[1] = 3;
                playerShapeY[1] = 0;

                playerShapeX[2] = 4;
                playerShapeY[2] = 0;

                playerShapeX[3] = 5;
                playerShapeY[3] = 0;

                break;
            case 5: // J Shape
                playerShapeX[0] = 3;
                playerShapeY[0] = 0;

                playerShapeX[1] = 4;
                playerShapeY[1] = 0;

                playerShapeX[2] = 5;
                playerShapeY[2] = 0;

                playerShapeX[3] = 5;
                playerShapeY[3] = 1;

                break;
            case 6: // T Shape
                playerShapeX[0] = 3;
                playerShapeY[0] = 0;

                playerShapeX[1] = 4;
                playerShapeY[1] = 0;

                playerShapeX[2] = 5;
                playerShapeY[2] = 0;

                playerShapeX[3] = 4;
                playerShapeY[3] = 1;

                break;
        }

        for (int a = 0; a < 4; a++)
        {
            if (grid[playerShapeY[a]][playerShapeX[a]] != 0){
                bCanSpawn = false;
            }
        }

        grid[playerShapeY[0]][playerShapeX[0]] = playerShape;
        grid[playerShapeY[1]][playerShapeX[1]] = playerShape;
        grid[playerShapeY[2]][playerShapeX[2]] = playerShape;
        grid[playerShapeY[3]][playerShapeX[3]] = playerShape;

        if (bCanSpawn){

        }else{
            bGameOver = true;
            Log.i(TAG,"GAME OVER!");
        }

    }   //public void updatePlayerShape(int nextShape)

    public void setPreviewShapesPosition(){

        int positionOffset;

        for (int a = 0; a < 3; a++) {

            positionOffset = a * 5;

            switch (previewShapes[a]){
                case 0: // O Shape
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][2 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][2 + positionOffset] = previewShapes[a] + 1;

                    break;
                case 1: // I Shape
                    previewGrid[1][ positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][2 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][3 + positionOffset] = previewShapes[a] + 1;

                    break;
                case 2: // S Shape
                    previewGrid[2][positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][2 + positionOffset] = previewShapes[a] + 1;

                    break;
                case 3: // Z Shape
                    previewGrid[1][positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][2 + positionOffset] = previewShapes[a] + 1;

                    break;
                case 4: // L Shape
                    previewGrid[2][positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][2 + positionOffset] = previewShapes[a] + 1;

                    break;
                case 5: // J Shape
                    previewGrid[1][0 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][2 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][2 + positionOffset] = previewShapes[a] + 1;

                    break;
                case 6: // T Shape
                    previewGrid[1][positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][1 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[1][2 + positionOffset] = previewShapes[a] + 1;
                    previewGrid[2][1 + positionOffset] = previewShapes[a] + 1;

                    break;
            }
        }

    }   //public void setPreviewShapesPosition()

    //Player Input
    public void movePiece(int direction) {

        if (direction == GameActivity.ROTATE) {

            if (bGameOver){

                if(Score > HighScore){
                    HighScore = Score;
                    saveHighScore(HighScore);
                }

                GameActivity.Game.finish();
                Log.d(TAG, "main menu!!!!!!!!!!!!");
            }

            rotateShape(playerShape);

            return;
        }

        if (direction == GameActivity.MOVE_DOWN) {

            if (bGameOver){

                if(Score > HighScore){
                    HighScore = Score;
                    saveHighScore(HighScore);
                }

                initGame();
                Log.d(TAG, "NEW GAME!!!!!!!!!!!!");
            }

            if (CanMove("Down")) {
                MoveShape("Down");
            }
            return;
        }

        if (direction == GameActivity.MOVE_LEFT) {

            if (CanMove("Left")) {
                MoveShape("Left");
            }
            return;
        }

        if (direction == GameActivity.MOVE_RIGHT) {

            if (CanMove("Right")) {
                MoveShape("Right");
            }
            return;
        }

    }   //public void movePiece(int direction)

    //Player Input
    public void rotateShape(int Shape){

        boolean bCanRotate = true;

        int [] TempShapeX = new int [4];
        int [] TempShapeY = new int [4];

        for (int a = 0; a < 4; a++){
            TempShapeX[a] = playerShapeX[a];
            TempShapeY[a] = playerShapeY[a];
        }

        switch (Shape) {
            case 1:     // O Shape Rotation
                // O shape will have no rotation as is it rotationally symmetrical
                break;
            case 2:     // I Shape Rotation

                switch (shapeRotation){
                    case 0:
                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] - 1;

                        TempShapeX[2] = TempShapeX[2] - 1;
                        TempShapeY[2] = TempShapeY[2] + 1;

                        TempShapeX[3] = TempShapeX[3] - 2;
                        TempShapeY[3] = TempShapeY[3] + 2;
                        break;
                    case 1:
                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] + 1;

                        TempShapeX[2] = TempShapeX[2] + 1;
                        TempShapeY[2] = TempShapeY[2] - 1;

                        TempShapeX[3] = TempShapeX[3] + 2;
                        TempShapeY[3] = TempShapeY[3] - 2;
                        break;
                    case 2:
                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] - 1;

                        TempShapeX[2] = TempShapeX[2] - 1;
                        TempShapeY[2] = TempShapeY[2] + 1;

                        TempShapeX[3] = TempShapeX[3] - 2;
                        TempShapeY[3] = TempShapeY[3] + 2;
                        break;
                    case 3:
                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] + 1;

                        TempShapeX[2] = TempShapeX[2] + 1;
                        TempShapeY[2] = TempShapeY[2] - 1;

                        TempShapeX[3] = TempShapeX[3] + 2;
                        TempShapeY[3] = TempShapeY[3] - 2;
                        break;
                }

                break;
            case 3:     // S Shape Rotation

                TempShapeX[3] = TempShapeX[1];
                TempShapeY[3] = TempShapeY[1];

                switch (shapeRotation){
                    case 0:

                        TempShapeX[1] = TempShapeX[1] - 1;
                        TempShapeY[1] = TempShapeY[1] - 1;

                        TempShapeY[0] = TempShapeY[0] - 2;

                        break;
                    case 1:

                        TempShapeX[1] = TempShapeX[1] + 1;
                        TempShapeY[1] = TempShapeY[1] - 1;

                        TempShapeX[0] = TempShapeX[0] +2;

                        break;
                    case 2:

                        TempShapeX[1] = TempShapeX[1] + 1;
                        TempShapeY[1] = TempShapeY[1] + 1;

                        TempShapeY[0] = TempShapeY[0] + 2;

                        break;
                    case 3:

                        TempShapeX[1] = TempShapeX[1] - 1;
                        TempShapeY[1] = TempShapeY[1] + 1;

                        TempShapeX[0] = TempShapeX[0] -2;

                        break;
                }

                break;
            case 4:     // Z Shape Rotation

                TempShapeX[2] = TempShapeX[0];
                TempShapeY[2] = TempShapeY[0];

                switch (shapeRotation){
                    case 0:

                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] - 1;

                        TempShapeX[3] = TempShapeX[3] - 2;

                        break;
                    case 1:

                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] + 1;

                        TempShapeY[3] = TempShapeY[3] - 2;

                        break;
                    case 2:

                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] + 1;

                        TempShapeX[3] = TempShapeX[3] + 2;

                        break;
                    case 3:

                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] - 1;

                        TempShapeY[3] = TempShapeY[3] + 2;

                        break;
                }

                break;
            case 5:     // L Shape Rotation

                switch (shapeRotation){
                    case 0:

                        TempShapeY[0] = TempShapeY[0]-2;

                        TempShapeX[1] = TempShapeX[1] + 1;
                        TempShapeY[1] = TempShapeY[1] - 1;

                        TempShapeX[3] = TempShapeX[3] - 1;
                        TempShapeY[3] = TempShapeY[3] + 1;

                        break;
                    case 1:

                        TempShapeX[0] = TempShapeX[0]+2;

                        TempShapeX[1] = TempShapeX[1] + 1;
                        TempShapeY[1] = TempShapeY[1] + 1;

                        TempShapeX[3] = TempShapeX[3] - 1;
                        TempShapeY[3] = TempShapeY[3] - 1;

                        break;
                    case 2:

                        TempShapeY[0] = TempShapeY[0]+2;

                        TempShapeX[1] = TempShapeX[1] - 1;
                        TempShapeY[1] = TempShapeY[1] + 1;

                        TempShapeX[3] = TempShapeX[3] + 1;
                        TempShapeY[3] = TempShapeY[3] - 1;

                        break;
                    case 3:

                        TempShapeX[0] = TempShapeX[0]-2;

                        TempShapeX[1] = TempShapeX[1] - 1;
                        TempShapeY[1] = TempShapeY[1] - 1;

                        TempShapeX[3] = TempShapeX[3] + 1;
                        TempShapeY[3] = TempShapeY[3] + 1;

                        break;
                }

                break;
            case 6:     // J Shape Rotation

                switch (shapeRotation){
                    case 0:

                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] - 1;

                        TempShapeX[2] = TempShapeX[2] - 1;
                        TempShapeY[2] = TempShapeY[2] + 1;

                        TempShapeX[3] = TempShapeX[3] - 2;

                        break;
                    case 1:

                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] + 1;

                        TempShapeX[2] = TempShapeX[2] - 1;
                        TempShapeY[2] = TempShapeY[2] - 1;

                        TempShapeY[3] = TempShapeY[3] - 2;

                        break;
                    case 2:

                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] + 1;

                        TempShapeX[2] = TempShapeX[2] + 1;
                        TempShapeY[2] = TempShapeY[2] - 1;

                        TempShapeX[3] = TempShapeX[3] + 2;

                        break;
                    case 3:

                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] - 1;

                        TempShapeX[2] = TempShapeX[2] + 1;
                        TempShapeY[2] = TempShapeY[2] + 1;

                        TempShapeY[3] = TempShapeY[3] + 2;

                        break;
                }

                break;
            case 7:     // T Shape Rotation

                TempShapeX[2] = TempShapeX[3];
                TempShapeY[2] = TempShapeY[3];

                TempShapeX[3] = TempShapeX[0];
                TempShapeY[3] = TempShapeY[0];

                switch (shapeRotation){
                    case 0:

                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] - 1;
                        break;
                    case 1:

                        TempShapeX[0] = TempShapeX[0] + 1;
                        TempShapeY[0] = TempShapeY[0] + 1;
                        break;
                    case 2:

                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] + 1;
                        break;
                    case 3:

                        TempShapeX[0] = TempShapeX[0] - 1;
                        TempShapeY[0] = TempShapeY[0] - 1;
                        break;
                }
                break;
        }

        for (int x = 0; x < 4; x++){
            // if part of the shape will be out of the play area
            if (TempShapeX[x] < 0 || TempShapeX[x] > (gridX - 1) || TempShapeY[x] < 0 || TempShapeY[x] > (gridY - 1)){
                bCanRotate = false;
            }
        }

        if (bCanRotate) {
            for (int a = 0; a < 4; a++) {
                grid[playerShapeY[a]][playerShapeX[a]] = 0;//remove old player shape
            }

            for (int x = 0; x < 4; x++) {
                if (grid[TempShapeY[x]][TempShapeX[x]] != 0) {
                    bCanRotate = false;
                }
            }

            for (int c = 0; c < 4; c++) {
                grid[playerShapeY[c]][playerShapeX[c]] = playerShape;  //draw new shape
            }
        }

        if (bCanRotate){

            if (shapeRotation == 3){
                shapeRotation = 0;
            }else{
                shapeRotation++;
            }

            for (int a = 0; a < 4; a++) {
                grid[playerShapeY[a]][playerShapeX[a]] = 0;//remove old player shape
            }

            for (int x = 0; x < 4; x++) {
                playerShapeX[x] = TempShapeX[x];
                playerShapeY[x] = TempShapeY[x];
            }

            for (int c = 0; c < 4; c++) {
                grid[playerShapeY[c]][playerShapeX[c]] = playerShape;  //draw new shape
            }

        }else{
            Log.w(TAG, "Shape Cant Rotate");
        }


    }   //public void rotateShape(int Shape)

    //Player Input
    private void MoveShape(String Direction){

        switch (Direction){
            case "Down":

                //move Down
                for (int b = 0; b < 4; b++){
                    grid[playerShapeY[b]][playerShapeX[b]] = 0;
                    playerShapeY[b] = playerShapeY[b] + 1;
                }
                for (int c = 0; c < 4; c++){
                    grid[playerShapeY[c]][playerShapeX[c]] = playerShape;
                }

                break;
            case "Left":

                //move Left
                for (int b = 0; b < 4; b++){
                    grid[playerShapeY[b]][playerShapeX[b]] = 0;
                    playerShapeX[b] = playerShapeX[b] - 1;
                }
                for (int c = 0; c < 4; c++){
                    grid[playerShapeY[c]][playerShapeX[c]] = playerShape;
                }

                break;
            case "Right":

                //move Right
                for (int b = 0; b < 4; b++){
                    grid[playerShapeY[b]][playerShapeX[b]] = 0;
                    playerShapeX[b] = playerShapeX[b] + 1;
                }
                for (int c = 0; c < 4; c++){
                    grid[playerShapeY[c]][playerShapeX[c]] = playerShape;
                }

                break;
        }
    }   //private void MoveShape(String Direction)

    //Moving the Player down
    private boolean CanMove(String Direction){
        boolean bCanMove = true;

        switch (Direction){
            case "Down":

                for (int a = 0; a < 4; a++){
                    if (playerShapeY[a] == gridY - 1){
                        bCanMove = false;
                    }
                }

                if (bCanMove) {
                    for (int a = 0; a < 4; a++) {
                        grid[playerShapeY[a]][playerShapeX[a]] = 0;//remove old player shape
                    }

                    for (int x = 0; x < 4; x++) {
                        if (grid[playerShapeY[x] + 1][playerShapeX[x]] != 0) {
                            bCanMove = false;
                        }
                    }

                    for (int c = 0; c < 4; c++) {
                        grid[playerShapeY[c]][playerShapeX[c]] = playerShape;  //draw new shape
                    }
                }

                break;
            case "Left":

                for (int a = 0; a < 4; a++){
                    if (playerShapeX[a] == 0){
                        bCanMove = false;
                    }
                }

                if (bCanMove) {
                    for (int a = 0; a < 4; a++) {
                        grid[playerShapeY[a]][playerShapeX[a]] = 0;//remove old player shape
                    }

                    for (int x = 0; x < 4; x++) {
                        if (grid[playerShapeY[x]][playerShapeX[x] - 1] != 0) {
                            bCanMove = false;
                        }
                    }

                    for (int c = 0; c < 4; c++) {
                        grid[playerShapeY[c]][playerShapeX[c]] = playerShape;  //draw new shape
                    }
                }

                break;
            case "Right":

                for (int a = 0; a < 4; a++){
                    if (playerShapeX[a] == gridX -1){
                        bCanMove = false;
                    }
                }

                if (bCanMove) {
                    for (int a = 0; a < 4; a++) {
                        grid[playerShapeY[a]][playerShapeX[a]] = 0;//remove old player shape
                    }

                    for (int x = 0; x < 4; x++) {
                        if (grid[playerShapeY[x]][playerShapeX[x] + 1] != 0) {
                            bCanMove = false;
                        }
                    }

                    for (int c = 0; c < 4; c++) {
                        grid[playerShapeY[c]][playerShapeX[c]] = playerShape;  //draw new shape
                    }
                }

                break;
        }

        return bCanMove;
    }   //private boolean CanMove(String Direction)

    public void testForCompleteRows(){

        int numInRow;
        int numOfCompletedRows = 0;
        int[] DestroyedRows = new int[4];

        //Add to player score
        Random r = new Random();
        int addToScore = r.nextInt(10) + 7;
        Score += addToScore;

        for (int a = 0; a < 4; a++){
            DestroyedRows[a] = -1;
        }

        for (int row = gridY - 1; row >= 0; row--){

            numInRow = 0;

            CheckingRow:
            for (int col = 0; col < gridX; col++){
                if (grid[row][col] == 0){
                    break CheckingRow;
                }else{
                    numInRow++;
                }
            }

            if (numInRow == 10){
                for (int a = 0; a < gridX; a++) {
                    grid[row][a] = 0;
                }

                DestroyedRows[numOfCompletedRows] = row;

                numOfCompletedRows++;
            }
        }

        // if the first value is valid
        if (DestroyedRows[0] != -1){
            /*for (int a = 0; a < 4; a++){
                Log.i(TAG, "destroying row = " + String.valueOf(DestroyedRows[a]));
            }*/

            dropIncompleteRows(DestroyedRows);
        }
    }   //public void testForCompleteRows()

    public void dropIncompleteRows(int[] DestroyingRows){

        int tempArrayLength = 0;
        int[] tempArray;

        for (int a = 0; a < 4; a++){
            if (DestroyingRows[a] != -1) {
                Log.i(TAG, "destroying row = " + String.valueOf(DestroyingRows[a]));

                tempArrayLength++;
            }
        }

        tempArray = new int [tempArrayLength];

        for (int a = 0; a < tempArrayLength; a++){
            tempArray[a] = DestroyingRows[a];
        }

        int offset = 0; //for moving rows down when a line has been complete

        //for every row from the bottom up
        for (int newRow = gridY - 1; newRow >= 0; newRow--) {

            boolean bKeepLooping = true;
            int ConsecRowsTest = 0;

            do{
                if (offset + ConsecRowsTest < tempArray.length) {
                    if (newRow - ConsecRowsTest == tempArray[offset + ConsecRowsTest]) {

                    } else {
                        offset = offset + ConsecRowsTest;
                        bKeepLooping = false;
                    }
                } else {
                    offset = offset + ConsecRowsTest;
                    bKeepLooping = false;
                }//(offset + 4 < tempArray.length)

                ConsecRowsTest++;

                if (ConsecRowsTest >= 5){
                    bKeepLooping = false;
                }

            }while(bKeepLooping);

            for (int newCol = 0; newCol < gridX; newCol++) {

                // if the dropping row is in the array
                if (newRow > offset - 1) {
                    grid[newRow][newCol] = grid[newRow - offset][newCol];
                    Log.d(TAG,"Row " + String.valueOf(newRow) + " Offset by " + String.valueOf(offset));
                } else {
                    grid[newRow][newCol] = 0;
                }
            }
        }

        testForScoreAndLevelChange(tempArrayLength);
    }   //public void dropIncompleteRows(int[] DestroyingRows)

    public void testForScoreAndLevelChange(int linesRemoved){
        //add lines removed to total lines removed
        Lines += linesRemoved;

        //Add line bonus to player score
        switch (linesRemoved){
            case 1:
                Score += 40 * Level;
                break;
            case 2:
                Score += 100 * Level;
                break;
            case 3:
                Score += 300 * Level;
                break;
            case 4:
                Score += 1200 * Level;
                break;
        }

        //If the level has increased
        if (Lines >= Level * numOfLinesBeforeNextLevel) {
            Level++;

            //change the speed based on the level
            int lvlNum = (int) (long) Level;
            switch (lvlNum) {
                case 1:
                    MoveDelay = 800;
                    break;
                case 2:
                    MoveDelay = 720;
                    break;
                case 3:
                    MoveDelay = 630;
                    break;
                case 4:
                    MoveDelay = 550;
                    break;
                case 5:
                    MoveDelay = 470;
                    break;
                case 6:
                    MoveDelay = 380;
                    break;
                case 7:
                    MoveDelay = 300;
                    break;
                case 8:
                    MoveDelay = 220;
                    break;
                case 9:
                    MoveDelay = 130;
                    break;
                case 10:
                    MoveDelay = 100;
                    break;
                case 11:
                    MoveDelay = 80;
                    break;
                case 14:
                    MoveDelay = 70;
                    break;
                case 17:
                    MoveDelay = 50;
                    break;
                case 20:
                    MoveDelay = 30;
                    break;
                case 30:
                    MoveDelay = 20;
                    break;
                default:
                    break;
            }
        }
    }   //public void testForScoreAndLevelChange(int linesRemoved)

    public void saveHighScore(long HS){


        SharedPreferences sp = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putLong("HighScore", HS);

        editor.apply();

        Log.i(TAG,"HIGH SCORE SAVED AS " + String.valueOf(HS));
    }   //public void saveHighScore(long HS)

    public long loadHighScore(){

        SharedPreferences sp = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE);

        Log.i(TAG,"HIGH SCORE LOADED");

        return sp.getLong("HighScore", 0);
    }   //public long loadHighScore()

    //Main Game Loop
    @Override
    public void run(){

        while ( playing )
        {
            if(!bGameOver){
                update();
                draw();
                control();
            }
        }   //  while (playing)
}   //public void run()

    //Game Loop
    private void update(){
        long now = System.currentTimeMillis();

        if (now - LastMove> MoveDelay){

            if (CanMove("Down")){
                MoveShape("Down");
                //Log.i(TAG, "Drop Shape");
            }else{
                testForCompleteRows();
                spawnNextPreviewShape();
                //Log.i(TAG, "Spawn Shape");
            }
            LastMove = now;
        }
    }   //private void update()

    private void draw(){
        if (ourHolder.getSurface().isValid())
        {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame
            canvas.drawColor(Color.argb(255, 39, 35, 35));

            drawPreviewArea();

            drawPlayArea();//draw the grid

            drawHud();

            //drawDebugInfo();

            if (bGameOver){
                drawGameOver();
            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }   //private void draw()

    public void drawPreviewArea(){
        int currX = gridY + 1;
        int currY = gridY + 1;

        int previewBoxSize = 40;

        for (int x = 0; x < previewX; x++)
        {
            for (int y = 0; y < previewY; y++)
            {
                // this draws a grid for the preview area
                /*paint.setStrokeWidth(3);
                paint.setColor(Color.rgb(40,40,40));
                paint.setStyle(Paint.Style.STROKE);    // Draw Border - Do NOT Fill!
                canvas.drawRect(currX, currY, currX + previewBoxSize, currY + previewBoxSize, paint);*/

                paint.setStyle(Paint.Style.FILL_AND_STROKE);    // Draw Border and FILL

                int CircleSize = 15;

                Paint p = new Paint();
                boolean bCanPaint = true;

                if (previewGrid[y][x] == 0) {
                    // this is an empty place
                    bCanPaint = false;
                }
                else if (previewGrid[y][x] == 1) {
                    p.setColor(Color.rgb(255,0,0));
                }
                else if (previewGrid[y][x] == 2) {
                    p.setColor(Color.rgb(0,255,0));
                }
                else if (previewGrid[y][x] == 3) {
                    p.setColor(Color.rgb(0,0,255));
                }
                else if (previewGrid[y][x] == 4) {
                    p.setColor(Color.rgb(255,255,0));
                }
                else if (previewGrid[y][x] == 5) {
                    p.setColor(Color.rgb(255,0,255));
                }
                else if (previewGrid[y][x] == 6) {
                    p.setColor(Color.rgb(0,255,255));
                }
                else if (previewGrid[y][x] == 7) {
                    p.setColor(Color.rgb(255,136,0));
                }

                if (bCanPaint){
                    paint.setColor(p.getColor());

                    //main Circle
                    canvas.drawCircle( currX + previewBoxSize/ 2, currY + previewBoxSize/2, CircleSize, paint );

                    //main hole
                    paint.setColor(Color.rgb(39,35,35));
                    canvas.drawCircle( currX + previewBoxSize/ 2, currY + previewBoxSize/2, CircleSize/2, paint );

                    paint.setColor(p.getColor());

                    //second Circle
                    canvas.drawCircle( currX + previewBoxSize/ 2, currY + previewBoxSize/2, CircleSize/4, paint );
                }

                currY = currY + previewBoxSize;
            }

            currY = gridY + 1;
            currX = currX + previewBoxSize;
        }
    }   //public void drawPreviewArea()

    public void drawPlayArea(){
        int currX = gridY + 1;
        int currY = PlayAreaOffset;

        for (int x = 0; x < gridX; x++)
        {
            for (int y = 0; y < gridY; y++)
            {
                // draw grid
                paint.setStrokeWidth(3);
                paint.setColor(Color.rgb(40,40,40));
                paint.setStyle(Paint.Style.STROKE);    // Draw Border - Do NOT Fill!
                canvas.drawRect(currX, currY, currX + 50, currY + 50, paint);


                paint.setStyle(Paint.Style.FILL_AND_STROKE);    // Draw Border and FILL

                int CircleSize = 20;

                Paint p = new Paint();
                boolean bCanPaint = true;

                if (grid[y][x] == 0) {
                    // this is an empty place
                    bCanPaint = false;
                }
                else if (grid[y][x] == 1) {
                    p.setColor(Color.rgb(255,0,0));
                }
                else if (grid[y][x] == 2) {
                    p.setColor(Color.rgb(0,255,0));
                }
                else if (grid[y][x] == 3) {
                    p.setColor(Color.rgb(0,0,255));
                }
                else if (grid[y][x] == 4) {
                    p.setColor(Color.rgb(255,255,0));
                }
                else if (grid[y][x] == 5) {
                    p.setColor(Color.rgb(255,0,255));
                }
                else if (grid[y][x] == 6) {
                    p.setColor(Color.rgb(0,255,255));
                }
                else if (grid[y][x] == 7) {
                    p.setColor(Color.rgb(255,136,0));
                }

                if (bCanPaint){
                    paint.setColor(p.getColor());

                    //main Circle
                    canvas.drawCircle( currX + 50/ 2, currY + 50/2, CircleSize, paint );

                    //main hole
                    paint.setColor(Color.rgb(39,35,35));
                    canvas.drawCircle( currX + 50/ 2, currY + 50/2, CircleSize/2, paint );

                    paint.setColor(p.getColor());

                    //second Circle
                    canvas.drawCircle( currX + 50/ 2, currY + 50/2, CircleSize/4, paint );
                }


                currY = currY + 50;
            }

            currY = PlayAreaOffset;
            currX = currX + 50;
        }
    }   //   public void drawPlayArea()

    private void control(){
        try
        {
            gameThread.sleep(17);
        }
        catch (InterruptedException e)
        {

        }
    }   //private void control()

    public void pause(){
        // Clean up our thread if the game is interrupted or the player quits

        playing = false;

        try
        {
            gameThread.join();
        }
        catch (InterruptedException e)
        {

        }
    }   //public void pause()

    public void resume(){
        // Make a new thread and start it

        playing = true;
        gameThread = new Thread(this);
        gameThread.start();

    }   //public void resume()

    public void drawHud(){
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.argb(255, 255, 255, 255));

        int posX = 640 ;
        int posY = PlayAreaOffset + 300;

        canvas.drawText("SCORE", posX, posY, paint);
        canvas.drawText(String.valueOf(Score), posX, posY + 30, paint);

        canvas.drawText("LEVEL", posX, posY + 250, paint);
        canvas.drawText(String.valueOf(Level), posX, posY + 280, paint);

        canvas.drawText("LINES", posX, posY + 500, paint);
        canvas.drawText(String.valueOf(Lines), posX, posY + 530, paint);

        if (Score > HighScore){
            paint.setColor(Color.argb(255, 255, 255, 0));
            canvas.drawText("HIGH SCORE", posX, posY - 250, paint);
            canvas.drawText(String.valueOf(Score), posX, posY - 220, paint);
        }else{
            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawText("HIGH SCORE", posX, posY - 250, paint);
            canvas.drawText(String.valueOf(HighScore), posX, posY - 220, paint);
        }

    }   //public void drawHud()

    private void drawGameOver(){
        canvas.drawColor(Color.argb(170, 188, 100, 100));

        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.argb(255, 0, 0, 0));

        int goX = screenX / 2;
        int goY = screenY / 2;

        canvas.drawText("GAME OVER!", goX, goY, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30);
        canvas.drawText("SCORE: " + Score, goX, goY + 50, paint);

        canvas.drawText("(UP ARROW) = MAIN MENU", goX, goY + 80, paint);
        canvas.drawText("(DOWN ARROW) = NEW GAME", goX, goY + 110, paint);
    }   //private void drawGameOver()

    public void drawDebugInfo(){
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.argb(255, 255, 0, 0));

        canvas.drawText("ScreenX = " + screenX + " ScreenY = " + screenY, 10, 20, paint);

        canvas.drawText("Cube [0] PlayerX = " + playerShapeX[0]
                + ", Cube [0] PlayerX = " + playerShapeX[0] , 10, 40, paint);

        canvas.drawText("Player Shape Rotation = " + (shapeRotation * 90), 10, 60, paint);

        canvas.drawText("Player Shape = " + playerShape, 10, 80, paint);

        canvas.drawText("Shape Moves Every = " + MoveDelay + "ms", 10, 100, paint);


        paint.setTextAlign(Paint.Align.RIGHT);
        String s = "";
        int posX = 700;
        int posY = 800;

        for (int x = 0; x < gridY; x++)
        {
            for (int y = 0; y < gridX; y++)
            {
                s = s + grid[x][y] + " ";

            }   // for (int y = 0; y < gridX; y++)

            canvas.drawText( s , posX, posY, paint);
            s = "";
            posY = posY + 20;
        } //  for (int x = 0; x < gridY; x++)

    }   //public void drawDebugInfo()

}   //  public class GameView

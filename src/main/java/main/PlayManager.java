package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayManager {

    //Basic gameplay animation

    //Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    //Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;


    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    //Effects
    boolean effectCounterOn;
    int effectCounter;
    List<Integer> effectY = new ArrayList<>();

    //Score
    int level = 1;
    int lines;
    int score;

    //Others
    public static int dropInterval = 60; //mino drops in every 60 frames


    //game over
    public boolean gameOver;

    public PlayManager() {
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2);
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        //start position
        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        //Starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {
        Mino mino = null;
        int i = new Random().nextInt(7);
        mino = switch (i) {
            case 0 -> new MinoL1();
            case 1 -> new MinoL2();
            case 2 -> new MinoSquare();
            case 3 -> new MinoBar();
            case 4 -> new MinoT();
            case 5 -> new MinoZ1();
            case 6 -> new MinoZ2();
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
        return mino;

    }


    public void update() {
        if (!currentMino.active) {
            //if not active put it in the array
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            checkGameOver();

            //nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            checkDelete();

        } else {
            currentMino.update();
        }
    }


    public void checkGameOver() {
        //check gameover, check if is moving from the starting pos.
        if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
            gameOver = true;
            GamePanel.music.stop();
            GamePanel.se.play(2, false);
        }

        currentMino.deactivating = false;
    }


    private void checkDelete() {
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {
            for (Block staticBlock : staticBlocks) {
                if (staticBlock.x == x && staticBlock.y == y) {
                    blockCount++;
                }
            }

            x += Block.SIZE;

            //checking if hit 12 blocks, if == 12 = delete;
            if (x == right_x) {
                if (blockCount == 12) {
                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size() - 1; i > -1; i--) {
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }
                    lineCount++;
                    lines++;

                    score();

                    //moving blocks down
                    for (int i = 0; i < staticBlocks.size(); i++) {
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }
                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }

            if (lineCount > 0) {
                GamePanel.se.play(1, false);
                int singleLineScore = 10 * level;
                score+= singleLineScore * lineCount;
            }
        }
    }


    private void score() {
        //Drop speed, score hits a number and increases speed, 1 is the fastest
        if (lines % 7 == 0 && dropInterval > 1) {
            level++;
            if (dropInterval > 10) {
                dropInterval -= 10;
            } else {
                dropInterval -= 1;
            }
        }
    }

    public void draw(Graphics2D g2) {

        //Playing Area
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        //Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x + 60, y + 60);

        //Score frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL: " + level, x, y); y+= 70;
        g2.drawString("LINES: " + lines, x, y); y+= 70;
        g2.drawString("SCORE: " + score, x, y);

        //Draw the currentMino
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        //Draw the nextMino
        nextMino.draw(g2);

        //Static blocks draw
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }


        //Draw effect
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.YELLOW);
            for (Integer integer : effectY) {
                g2.fillRect(left_x, integer, WIDTH, Block.SIZE);
            }

            if (effectCounter == 7) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        //pause or game over
        g2.setColor(Color.YELLOW);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (gameOver) {
            x = left_x + 25;
            y = top_y + 320;
            g2.drawString("GAME OVER", x, y);
        } else if (MovementHandler.pausePressed) {
            x = left_x + 90;
            y = top_y + 310;
            g2.drawString("PAUSE", x, y);
        }
        //Game title

        x = 60;
        y = top_y + 200;
        g2.setColor(Color.getHSBColor(130, 41, 51));
        g2.setFont(new Font("Times new Roman", Font.ITALIC, 65));
        g2.drawString("Simple Tetris", x, y);

        x = 60;
        y = top_y + 240;
        g2.setColor(Color.getHSBColor(130, 41, 51));
        g2.setFont(new Font("Times new Roman", Font.BOLD, 16));
        g2.drawString("Created by: NathansSilva, following steps of RyiSnow", x, y);


    }


}

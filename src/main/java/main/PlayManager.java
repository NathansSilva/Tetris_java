package main;

import mino.Block;
import mino.Mino;
import mino.MinoL1;

import java.awt.*;
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
    public static Color[] colors = {Color.orange, Color.blue, Color.red, Color.cyan, Color.green, Color.magenta, Color.yellow};
    public static Random random = new Random();
    public static int randomIndex = random.nextInt(colors.length);

    //Others
    public static int dropInterval = 60;

    public PlayManager() {
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
        right_x = left_x+WIDTH;
        top_y = 50;
        bottom_y = top_y+HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        //Starting Mino
        currentMino = new MinoL1();
        currentMino.setXY(MINO_START_X, MINO_START_Y);

    }

    public void update() {
        currentMino.update();
    }

    public void draw(Graphics2D g2) {

        //Playing Area
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8);

        //Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT", x+60, y+60);

        //Draw the currentMino
        if (currentMino != null) {
            currentMino.draw(g2);
        }

    }


}

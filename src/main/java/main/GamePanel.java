package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;
    private Image backgroundImage;
    public static Sound music = new Sound();
    public static Sound se = new Sound();


    public GamePanel() {
        //Panel settings
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setLayout(null);
        this.setBackground(Color.BLACK);

        backgroundImage = new ImageIcon(getClass().getResource("/bg-image.jpg")).getImage();

        //MovementHandler
        this.addKeyListener(new MovementHandler());
        this.setFocusable(true);

        pm = new PlayManager();
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
        music.play(0, true);
        music.loop();
    }


    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta+= (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }

    }

    private void update() {
        if (!MovementHandler.pausePressed && !pm.gameOver){
            pm.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(),getHeight(), this);
        }

        Graphics2D g2 = (Graphics2D)g;
        pm.draw(g2);

    }

}

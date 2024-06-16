package com.loseardes77.client;

import com.loseardes77.common.Direction;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Player extends JButton {

    private final int sleepTime = 10;
    private double instantSpeed;
    private final int maxSpeed = 5;
    private final double diagonalSpeed = 0.7071067811865476 * maxSpeed; // Math.cos(Math.PI/4) * maxSpeed;
    private final byte[] inputMap = new byte[2];
    private final Game game;
    private boolean exitThreads = false;
    private Thread inputThread;
    private KeyEventDispatcher keyEventDispatcher;
    private short health = 100;

    private boolean isLightColor(Color color) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        return hsb[2] > 0.5;
    }

    public Player(boolean inputs, Game game, Color playerColor){
        if (inputs)
            startInputDetection();

        if (isLightColor(playerColor))
            setForeground(Color.BLACK);
        else
            setForeground(Color.WHITE);

        this.game = game;
        setFocusable(false);
        setBackground(playerColor);
        setText("·_·");
    }

    public short getHealth(){
        return health;
    }

    public void setHealth(short health){
        this.health = health;
    }

    public void movePlayer(Direction dir, double speed){
        int original_x = getX();
        int original_y = getY();
        int x = original_x;
        int y = original_y;

        switch (dir) {
            case UP:
                y -= (int) Math.round(speed);
                break;
            case DOWN:
                y += (int) Math.round(speed);
                break;
            case LEFT:
                x -= (int) Math.round(speed);
                break;
            case RIGHT:
                x += (int) Math.round(speed);
                break;
        }
        if (game.checkCollision(new Rectangle(x, y, getWidth(), getHeight()), this))
            return;

        setLocation(x, y);

        if (game.checkCollision(new Rectangle(x, y, getWidth(), getHeight()), this))
            setLocation(original_x, original_y);
    }

    public void stopInputDetection(){
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
    }

    public void startInputDetection(){
        keyEventDispatcher = (e) -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_W)
                    if(inputMap[0] < 1)
                        inputMap[0] += 1;

                if (e.getKeyCode() == KeyEvent.VK_S)
                    if(inputMap[0] > -1)
                        inputMap[0] -= 1;


                if (e.getKeyCode() == KeyEvent.VK_A)
                    if(inputMap[1] < 1)
                        inputMap[1] += 1;


                if (e.getKeyCode() == KeyEvent.VK_D)
                    if(inputMap[1] > -1)
                        inputMap[1] -= 1;


            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (e.getKeyCode() == KeyEvent.VK_W)
                    if (inputMap[0] > -1)
                        inputMap[0] -= 1;

                if (e.getKeyCode() == KeyEvent.VK_S)
                    if (inputMap[0] < 1)
                        inputMap[0] += 1;

                if (e.getKeyCode() == KeyEvent.VK_A)
                    if (inputMap[1] > -1)
                        inputMap[1] -= 1;

                if (e.getKeyCode() == KeyEvent.VK_D)
                    if (inputMap[1] < 1)
                        inputMap[1] += 1;

            }
            return false;
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    public void destroy(){
        stopInputDetection();
        exitThreads = true;
        try {
            inputThread.join();
        } catch (InterruptedException e) {
            inputThread.interrupt();
        }
    }

    public void startMovingPLayer(){
       inputThread = new Thread(() -> {
            while (!exitThreads) {
                if (inputMap[0] != 0 && inputMap[1] != 0)
                    instantSpeed = diagonalSpeed;
                else
                    instantSpeed = maxSpeed;

                if (inputMap[0] == 1)
                    movePlayer(Direction.UP, instantSpeed);

                if (inputMap[0] == -1)
                    movePlayer(Direction.DOWN, instantSpeed);

                if (inputMap[1] == 1)
                    movePlayer(Direction.LEFT, instantSpeed);

                if (inputMap[1] == -1)
                    movePlayer(Direction.RIGHT, instantSpeed);
                try{
                    Thread.sleep(sleepTime);
                } catch (InterruptedException _) {

                }
            }

        });

        inputThread.start();
    }

}

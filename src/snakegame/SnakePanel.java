/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.*;

/**
 *
 * @author 11768
 */
public class SnakePanel extends JPanel {

    // <<DATA>>
    int colorOrder;
    boolean colorDecreaseFlag;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.black;
    public static Color COLOR_RAINBOW_CYCLE = new Color(0, 0, 0);
    public static final int NUM_BACK_COLORS = 3;
    public static final int MAX_COLOR_VALUE = 255;
    public static final int MIN_COLOR_VALUE = 0;
    public static final int COLOR_INCREMENT = 10;
    
    public static final int MUSIC_THRESHOLD = 10;

    public static final Color FOOD_COLOR = Color.white;
    public static final Color PLAYER_SNAKE_COLOR = Color.blue;
    public static final Color AI_SNAKE_COLOR = Color.red;
    public static final int NUMBER_OF_FOOD = 2;

    public static double WINDOW_WIDTH = 800.0;
    public static double WINDOW_HEIGHT = 800.0;

    private static int backgroundColors[];
    private static Rect2d back;

    double snakeWidth;

    public static final int NUM_PLAYERS = 1;
    public static final int NUM_AI_M1000 = 1;
    public static final int NUM_AI_M2000 = 1;
    private final int[] NUMBER_SNAKES = {NUM_PLAYERS, NUM_AI_M1000, NUM_AI_M2000};
    public Snake snakes[] = new Snake[(NUM_PLAYERS + NUM_AI_M1000 + NUM_AI_M2000)];

    public boolean music;

    public static ArrayList<Rect2d> food;

    final double moveAccel = 1000.0;
    public static KeysPressed keysPressed;
    static ImageIcon EXPLODE;

    static AudioInputStream audioIn;
    static Clip clip;

    //static Rect2d cambounds; //CAMERA WINDOW(Snake touches the edge of this to begin "scrolling")
    // <<CONSTRUCTOR>>
    public SnakePanel() {

        try {
            loadMusic();//loads the WAV file to play later, prevents lag
        } catch (Exception ex) {
            Logger.getLogger(SnakePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int j = 0; j < NUMBER_SNAKES.length; j++) {
            for (int i = 0; i < NUMBER_SNAKES[j]; i++) {
                switch (j) {
                    case 0:
                        snakes[i] = new Snake(PLAYER_SNAKE_COLOR, "Bernie");
                        break;
                    case 1:
                        snakes[NUM_PLAYERS + i] = new AISnake(AI_SNAKE_COLOR, "Berninator");
                        break;
                    case 2:
                        snakes[NUM_PLAYERS + NUM_AI_M1000 + i] = new AISnake2(AI_SNAKE_COLOR, "Robobernie");
                        break;
                }
            }
        }

        // Initialize colors for rainbow cycle
        colorDecreaseFlag = false;
        backgroundColors = new int[NUM_BACK_COLORS];
        for (int i = 0; i < NUM_BACK_COLORS; i++) {
            backgroundColors[i] = 0;
        }
        colorOrder = 0;

        music = false;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //sets window to fit screen

        WINDOW_WIDTH = screenSize.getWidth() - 10;
        WINDOW_HEIGHT = screenSize.getHeight() - 72;

        setPreferredSize(new Dimension((int) WINDOW_WIDTH, (int) WINDOW_HEIGHT));
        keysPressed = new KeysPressed();

        back = new Rect2d(-500, -500, 10000, 10000);

        food = new ArrayList<Rect2d>();

        //cambounds = new Rect2d(100, 100, screenSize.width - 200, screenSize.height - 250);
        for (int i = 0; i < snakes.length; i++) {
            buildSnake(snakes[i]);
        }
        for (int i = 0; i < NUMBER_OF_FOOD; i++) {
            food.add(new Rect2d(random_number(0, 1000), random_number(0, 500), 10, 10));
        }

        this.setFocusable(true);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keycode = e.getKeyCode();
                switch (keycode) {
                    case java.awt.event.KeyEvent.VK_W:
                        keysPressed.Up = true;
                        break;
                    case java.awt.event.KeyEvent.VK_S:
                        keysPressed.Down = true;
                        break;
                    case java.awt.event.KeyEvent.VK_A:
                        keysPressed.Left = true;
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                        keysPressed.Right = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keycode = e.getKeyCode();
                switch (keycode) {
                    case java.awt.event.KeyEvent.VK_W:  // the keycode for W (Virtual Key)
                        keysPressed.Up = false;
                        break;
                    case java.awt.event.KeyEvent.VK_S:
                        keysPressed.Down = false;
                        break;
                    case java.awt.event.KeyEvent.VK_A:
                        keysPressed.Left = false;
                        break;
                    case java.awt.event.KeyEvent.VK_D:
                        keysPressed.Right = false;
                        break;
                }
            }
        });
    }

    void buildSnake(Snake snake) {
        for (int i = 1; i < 0; i++) {
            snake.addS(new Rect2d(30.0 + (i * 30), 170.0, snake.getWidth(), snake.getWidth()));
        }

        for (int i = 0; i < snake.getSSize(); i++) {
            snake.addH(new SquareCoords((int) snake.getRect(i).getLeft(), (int) snake.getRect(i).getTop()));
        }

    }

    // <<FILLRECT>>   (a static ‘helper’ method to draw a Rect2d)
    static void fillRect(Graphics g, Rect2d rect, Color c) {
        int x = (int) rect.getLeft();
        int y = (int) rect.getTop();
        int w = (int) rect.getWidth();
        int h = (int) rect.getHeight();
        g.setColor(c);
        g.fillRect(x, y, w, h);
    }

    public boolean checkLiving(Snake snake, Color color, Graphics g) {
        if (!snake.isLiving()) {
            try {
                stopMusic();
            } catch (Exception ex) {
                Logger.getLogger(SnakePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            fillRect(g, back, DEFAULT_BACKGROUND_COLOR);

            for (int i = 1; i < snake.getSSize(); i++) {
                fillRect(g, snake.getRect(i), color);
            }

            for (int i = 0; i < food.size(); i++) {
                fillRect(g, food.get(i), FOOD_COLOR);
            }
            return true;
        }
        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        for (int i = 0; i < snakes.length; i++) {
            if (checkLiving(snakes[i], snakes[i].getColor(), g)) {
                return;
            }
        }
        fillRect(g, back, DEFAULT_BACKGROUND_COLOR);
        //RAINBOW CYCLE COLOR
        backColorFlow();

        COLOR_RAINBOW_CYCLE = new Color(this.backgroundColors[0],
                this.backgroundColors[1],
                this.backgroundColors[2]);
        //COLOR RAINBOW CYCLE END

        if(snakes[0].getScore() >= MUSIC_THRESHOLD){
        try {
            playMusic();
        } catch (Exception ex) {
            Logger.getLogger(SnakePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        //g.setColor(Color.white);
        //g.drawLine((int) berninator.getHead().getCenter().x, (int) berninator.getHead().getCenter().y, (int) berninator.targettemp.getCenter().x, (int) berninator.targettemp.getCenter().y);
        //fillRect(g, bernie1.vision, Color.yellow);
        //fillRect(g, bernie1.pathX, Color.blue);
        //fillRect(g, bernie1.pathY, Color.red);
        // Fill snake's body with colors
        fillSnake(g);

        //Draw food
        for (int i = 0; i < food.size(); i++) {
            fillRect(g, food.get(i), FOOD_COLOR);
        }
    }


    public boolean isPlayerBigger(Snake snake, int index) {
        int tempCount = 0;
        for (int i = 0; i < snakes.length; i++) {
            if (snake.getScore() > snakes[i].getScore()) {
                tempCount++;
            }
        }

        return (tempCount == snakes.length - 1);
    }

    public void fillSnake(Graphics g) {
        for (int i = 0; i < snakes.length; i++) {
            for (int j = 0; j < snakes[i].getSSize(); j++) {
                if (isPlayerBigger(snakes[i], i)) {
                    fillRect(g, snakes[i].getRect(j), COLOR_RAINBOW_CYCLE);
                } else {
                    fillRect(g, snakes[i].getRect(j), snakes[i].getColor());
                }
            }
        }
    }

    public void update() {
        for (int i = 0; i < snakes.length; i++) {
            snakes[i].update();
        }
    }

    public static int random_number(int low, int high) {
        double rand = Math.random(); //generates a random number
        int rand2 = (int) (rand * 100000); //casts the random number as int
        int interval = high - low;//interval in which to put the number ie 1-100
        rand2 = rand2 % interval;//puts the number into the interval
        rand2 = rand2 + low;//acertains that the number is above the minimum
        int randNum = rand2;//assigns the random number's value
        return randNum;//returns the random number's value
    }

    public static void playMusic() throws Exception {
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void loadMusic() throws Exception {
        File file = new File("sandstorm1.wav");
        audioIn = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioIn.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        clip = (Clip) AudioSystem.getLine(info);
        clip.open(audioIn);
    }

    public static void stopMusic() throws Exception {
        clip.stop();
    }

    void backColorFlow() {
        if (colorOrder < 2) {
            //max red
            if (this.backgroundColors[colorOrder] < MAX_COLOR_VALUE && colorDecreaseFlag == false) {
                if (this.backgroundColors[colorOrder] + COLOR_INCREMENT < MAX_COLOR_VALUE) {
                    this.backgroundColors[colorOrder] += COLOR_INCREMENT;
                } else {
                    this.backgroundColors[colorOrder] = MAX_COLOR_VALUE;
                }
            } //max green/blue
            else if (this.backgroundColors[colorOrder + 1] < MAX_COLOR_VALUE) {
                if (this.backgroundColors[colorOrder + 1] + COLOR_INCREMENT < MAX_COLOR_VALUE) {
                    this.backgroundColors[colorOrder + 1] += COLOR_INCREMENT;
                } else {
                    this.backgroundColors[colorOrder + 1] = MAX_COLOR_VALUE;
                }
            } //mins red/green
            else if (this.backgroundColors[colorOrder] > 0) {
                colorDecreaseFlag = true;
                if (this.backgroundColors[colorOrder] - COLOR_INCREMENT >= 0) {
                    this.backgroundColors[colorOrder] -= COLOR_INCREMENT;
                } else {
                    this.backgroundColors[colorOrder] = MIN_COLOR_VALUE;
                }
            } else {
                colorDecreaseFlag = false;
                colorOrder++;
            }
        } else if (colorOrder == 2) {
            //re-maxes red 
            if (this.backgroundColors[0] < MAX_COLOR_VALUE) {
                if (this.backgroundColors[0] + COLOR_INCREMENT < MAX_COLOR_VALUE) {
                    this.backgroundColors[0] += COLOR_INCREMENT;
                } else {
                    this.backgroundColors[0] = MAX_COLOR_VALUE;
                }
            }//mins blue 
            else if (this.backgroundColors[colorOrder] > MIN_COLOR_VALUE) {
                colorDecreaseFlag = true;
                if (this.backgroundColors[colorOrder] - COLOR_INCREMENT > 0) {
                    this.backgroundColors[colorOrder] -= COLOR_INCREMENT;
                } else {
                    this.backgroundColors[colorOrder] = MIN_COLOR_VALUE;
                }
            } else {
                colorDecreaseFlag = false;
                colorOrder = 0;
            }
        }
    }

    //FUNCTIONS NEEDED FOR CAMERA SCROLLING
    /*static boolean checkCamBounds() {
     if (bernie.getHead().getTop() > (cambounds.getTop()) && bernie.getHead().getTop() < (cambounds.getBottom()) && bernie.getHead().getLeft() < (cambounds.getRight()) && bernie.getHead().getLeft() > (cambounds.getLeft())) {
     return true;
     } else {
     return false;
     }
     }

     static boolean checkAtUp() {
     System.out.println("------------CHECKING IF AT TOP--------------");
     int a = ((int) (bernie.getHead().getTop()));
     int b = ((int) (cambounds.getTop()));
     System.out.println(a);
     System.out.println(b);
     if (b - 50 <= a && a <= b + 50) {
     System.out.println("TRUE");
     System.out.println("-------------CHECK DONE--------------\n");
     return true;
     } else {
     System.out.println("FALSE");
     System.out.println("-------------CHECK DONE--------------\n");
     return false;
     }
     }

     static boolean checkAtDown() {
     System.out.println("------------CHECKING IF AT BOTTOM--------------");
     int a = ((int) (bernie.getHead().getTop()));
     int b = ((int) (cambounds.getBottom()));
     System.out.println(a);
     System.out.println(b);
     if (b - 50 <= a && a <= b + 50) {
     System.out.println("TRUE");
     System.out.println("-------------CHECK DONE--------------\n");
     return true;
     } else {
     System.out.println("FALSE");
     System.out.println("-------------CHECK DONE--------------\n");
     return false;
     }
     }

     static boolean checkAtLeft() {
     System.out.println("------------CHECKING IF AT LEFT--------------");
     int a = ((int) (bernie.getHead().getLeft()));
     int b = ((int) (cambounds.getLeft()));
     System.out.println(a);
     System.out.println(b);
     if (b - 50 <= a && a <= b + 50) {
     System.out.println("TRUE");
     System.out.println("-------------CHECK DONE--------------\n");
     return true;
     } else {
     System.out.println("FALSE");
     System.out.println("-------------CHECK DONE--------------\n");
     return false;
     }
     }

     static boolean checkAtRight() {
     System.out.println("------------CHECKING IF AT RIGHT--------------");
     int a = ((int) (bernie.getHead().getLeft()));
     int b = ((int) (cambounds.getRight()));
     System.out.println(a);
     System.out.println(b);
     if (b - 50 <= a && a <= b + 50) {
     System.out.println("TRUE");
     System.out.println("-------------CHECK DONE--------------\n");
     return true;
     } else {
     System.out.println("FALSE");
     System.out.println("-------------CHECK DONE--------------\n");
     return false;
     }
     }*/
}

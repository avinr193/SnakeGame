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

    //**********************CONSTANTS*************************
    ////GAMEPLAY
    public static boolean starfield = true;
    public static final int MUSIC_THRESHOLD = 0;
    public static String[] musicArray = {"sandstorm1.wav", "remix10.wav", "MEGA_MAN.wav", "9ts.wav"};
    /////////////////////////////////////////////////////////////////////////////////////////
    ////SNAKE PROPERTIES
    public static final int WINNING_PLAYERSNAKE_WIDTH = 7;
    public static final int LOSING_PLAYERSNAKE_WIDTH = 7;
    
    public static final int WINNING_AI1SNAKE_WIDTH = 3;
    public static final int LOSING_AI1SNAKE_WIDTH = 3;

    public static final int WINNING_AI2SNAKE_WIDTH = 3;
    public static final int LOSING_AI2SNAKE_WIDTH = 3;
    
    public static final int PLAYER_SPEED_MULTIPLIER = 1;
    public static final int AI1_SPEED_MULTIPLIER = 1;
    public static final int AI2_SPEED_MULTIPLIER = 1;

    public static final boolean PLAYER_RANDOM_SPAWN_TOGGLE = true;
    public static final boolean AI1_RANDOM_SPAWN_TOGGLE = true;
    public static final boolean AI2_RANDOM_SPAWN_TOGGLE = true;

    public static final int PLAYER_NON_RANDOM_SPAWN_X = 250;
    public static final int PLAYER_NON_RANDOM_SPAWN_Y = 250;

    public static final int AI1_NON_RANDOM_SPAWN_X = 500;
    public static final int AI1_NON_RANDOM_SPAWN_Y = 500;

    public static final int AI2_NON_RANDOM_SPAWN_X = 750;
    public static final int AI2_NON_RANDOM_SPAWN_Y = 750;
    /////////////////////////////////////////////////////////////////////////////////////////
    ////# OF FOOD AND SNAKES
    public static final int NUMBER_OF_FOOD = 10;
    
    public static final int NUM_PLAYERS = 1;
    public static final int NUM_AI1 = 50;
    public static final int NUM_AI2 = 50;
    
    private final int[] NUMBER_SNAKES = {NUM_PLAYERS, NUM_AI1, NUM_AI2};
    public static Snake snakes[] = new Snake[(NUM_PLAYERS + NUM_AI1 + NUM_AI2)];
    String[] modelName = {"Berninator", "Bern-OS", "Robo-Bernie", "Bernie-Prime", "Star Bern", "Telebernie", "iBernie", "B.E.R.N.I.E", "Bern Machine", "B3RN1E"};
    /////////////////////////////////////////////////////////////////////////////////////////
    ////OBJECT COLORS
    public static final Color FOOD_COLOR = Color.white;
    public static final Color PLAYER_SNAKE_COLOR = Color.blue;
    public static final Color AI_SNAKE_COLOR = Color.red;
    public static final Color DEFAULT_BACKGROUND_COLOR = Color.black;
    /////////////////////////////////////////////////////////////////////////////////////////
    //////RANDOM COLOR GENERATOR PREFERENCES
    public static Color COLOR_RAINBOW_CYCLE = new Color(0, 0, 0);
    public static Color COLOR_RAINBOW_CYCLE2 = new Color(0, 0, 0);

    public static final int NUM_BACK_COLORS = 3;
    public static final int MAX_COLOR_VALUE = 255;
    public static final int MIN_COLOR_VALUE = 0;
    public static final int COLOR_INCREMENT = 10;
    /////////////////////////////////////////////////////////////////////////////////////////
    //*********************************CONSTANTS END*****************************************

    public static boolean updateornaw = true;
    int colorOrder;
    boolean colorDecreaseFlag;

    public static double WINDOW_WIDTH = 800.0;
    public static double WINDOW_HEIGHT = 800.0;

    private static int backgroundColors[];
    private static Rect2d back;

    double snakeWidth;

    public boolean music;
    static AudioInputStream audioIn;
    static Clip clip;

    public static ArrayList<Rect2d> food;

    public static KeysPressed keysPressed;

    static Menu menu;

    //static Rect2d cambounds; //CAMERA WINDOW(Snake touches the edge of this to begin "scrolling")
    // <<CONSTRUCTOR>>
    public SnakePanel() {

        menu = new Menu(this);

        menu.buildMain();

        constructSnake();

        // Initialize colors for rainbow cycle
        colorDecreaseFlag = false;
        backgroundColors = new int[NUM_BACK_COLORS];
        for (int i = 0; i < NUM_BACK_COLORS; i++) {
            backgroundColors[i] = 0;
        }
        colorOrder = 0;

        music = false;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //sets window to fit screen

        WINDOW_WIDTH = screenSize.getWidth();
        WINDOW_HEIGHT = screenSize.getHeight();
        setPreferredSize(new Dimension((int) WINDOW_WIDTH, (int) WINDOW_HEIGHT));
        keysPressed = new KeysPressed();

        back = new Rect2d(-500, -500, 10000, 10000);

        food = new ArrayList<>();

        //cambounds = new Rect2d(100, 100, screenSize.width - 200, screenSize.height - 250);
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
                    case java.awt.event.KeyEvent.VK_UP:
                        keysPressed.Up = true;
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        keysPressed.Down = true;
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        keysPressed.Left = true;
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        keysPressed.Right = true;
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        if (SnakeGame.state == SnakeGame.STATE.GAME) {
                            Menu.pause();
                        }
                        /*for(int i = 0;i<snakes.length;i++){
                         snakes[i].die();
                         }*/
                        break;
                    case java.awt.event.KeyEvent.VK_SPACE:
                        keysPressed.Random = !keysPressed.Random;
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
                    case java.awt.event.KeyEvent.VK_UP:  // the keycode for W (Virtual Key)
                        keysPressed.Up = false;
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        keysPressed.Down = false;
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        keysPressed.Left = false;
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        keysPressed.Right = false;
                        break;
                    case java.awt.event.KeyEvent.VK_ESCAPE:
                        break;
                }
            }
        });
    }

    void constructSnake() {
        for (int j = 0; j < NUMBER_SNAKES.length; j++) {
            for (int i = 0; i < NUMBER_SNAKES[j]; i++) {
                switch (j) {
                    case 0:
                        snakes[i] = new Snake(PLAYER_SNAKE_COLOR, "Bernie");
                        break;
                    case 1:
                        snakes[NUM_PLAYERS + i] = new AISnake1(AI_SNAKE_COLOR, (modelName[random_number(0, this.modelName.length)] + " " + random_number(0, 9000)));
                        break;
                    case 2:
                        snakes[NUM_PLAYERS + NUM_AI1 + i] = new AISnake2(AI_SNAKE_COLOR, (modelName[random_number(0, this.modelName.length)] + " " + random_number(0, 9000)));
                        break;
                }
            }
        }
        for (Snake snake : snakes) {
            buildSnake(snake);
        }
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

    //CREDIT TO CHRIS WELLONS --- http://nullprogram.com/blog/2011/06/13/
    public static final int STAR_SEED = 0x9d2c5680;
    public static int STAR_TILE_SIZE = 1000;

    public void drawStars(Graphics2D g, int xoff, int yoff, int starscale) {

        int size = STAR_TILE_SIZE / starscale;
        int w = 5000;
        int h = 3000;

        /* Top-left tile's top-left position. */
        int sx = ((xoff - w / 2) / size) * size - size;
        int sy = ((yoff - h / 2) / size) * size - size;

        /* Draw each tile currently in view. */
        for (int i = sx; i <= w + sx + size * 3; i += size) {
            for (int j = sy; j <= h + sy + size * 3; j += size) {
                int hash = mix(STAR_SEED, i, j);
                for (int n = 0; n < 3; n++) {
                    int px = (hash % size) + (i - xoff);
                    hash >>= 3;
                    int py = (hash % size) + (j - yoff);
                    hash >>= 3;
                    g.drawLine(px, py, px, py);
                }
            }
        }
    }

    /**
     * Robert Jenkins' 96 bit Mix Function.
     */
    private static int mix(int a, int b, int c) {
        a = a - b;
        a = a - c;
        a = a ^ (c >>> 13);
        b = b - c;
        b = b - a;
        b = b ^ (a << 8);
        c = c - a;
        c = c - b;
        c = c ^ (b >>> 13);
        a = a - b;
        a = a - c;
        a = a ^ (c >>> 12);
        b = b - c;
        b = b - a;
        b = b ^ (a << 16);
        c = c - a;
        c = c - b;
        c = c ^ (b >>> 5);
        a = a - b;
        a = a - c;
        a = a ^ (c >>> 3);
        b = b - c;
        b = b - a;
        b = b ^ (a << 10);
        c = c - a;
        c = c - b;
        c = c ^ (b >>> 15);
        return c;
    }
    /////////////

    public boolean checkLiving(Snake snake, Graphics g) {
        if (!snake.isLiving()) {
            //Convert snake to food
            for (Rect2d food1 : food) {
                fillRect(g, food1, FOOD_COLOR);
            }
            return true;
        }
        return false;
    }

    public boolean checkAllLiving(Snake[] snakes, Graphics g) {
        int temp = 0;
        for (Snake snake : snakes) {
            if (checkLiving(snake, g)) {
                temp++;
            }
        }
        return (temp == snakes.length);
    }

    public static int starx = 0;
    public static int stary = 0;
    public static int starscale = 10;

    public void clearGame() {
        try {
            stopMusic();
            unloadMusic();
        } catch (Exception ex) {
            Logger.getLogger(SnakePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Menu.musicIndex = Menu.DEFAULT_MUSIC_INDEX;
        this.removeAll();
        SnakeGame.frame.removeAll();
        SnakeGame.ingame = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (SnakeGame.state == SnakeGame.STATE.MENU) {
            fillRect(g, back, DEFAULT_BACKGROUND_COLOR);
            moveStars(g);
            menu.renderMenu();
        } else if (SnakeGame.state == SnakeGame.STATE.GAME) {
            if (checkLiving(snakes[0], g)) {
                Menu.gameOver();
                for (Snake snake : snakes) {
                    snake.reset();
                }
                for (int i = 0; i < NUMBER_OF_FOOD; i++) {
                    food.add(new Rect2d(random_number(0, 1000), random_number(0, 500), 10, 10));
                }
            }
            fillRect(g, back, DEFAULT_BACKGROUND_COLOR);
            //RAINBOW CYCLE COLOR
            backColorFlow();

            COLOR_RAINBOW_CYCLE = new Color(this.backgroundColors[0],
                    this.backgroundColors[1],
                    this.backgroundColors[2]);
            COLOR_RAINBOW_CYCLE2 = new Color(this.backgroundColors[1],
                    this.backgroundColors[2],
                    this.backgroundColors[0]);
            //COLOR RAINBOW CYCLE END

            if (snakes[0].getScore() >= MUSIC_THRESHOLD || this.isBigger(snakes[0], 0)) {
                try {
                    playMusic();
                } catch (Exception ex) {
                    Logger.getLogger(SnakePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    stopMusic();
                } catch (Exception ex) {
                    Logger.getLogger(SnakePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //ENABLE FOR AI TESTING
            //g.setColor(Color.white);
            //g.drawLine((int) berninator.getHead().getCenter().x, (int) berninator.getHead().getCenter().y, (int) berninator.targettemp.getCenter().x, (int) berninator.targettemp.getCenter().y);
            //fillRect(g, bernie1.vision, Color.yellow);
            //fillRect(g, bernie1.pathX, Color.blue);
            //fillRect(g, bernie1.pathY, Color.red);
            ///////////////////////
            moveStars(g);
            fillSnake(g);

            //Draw food
            for (Rect2d food1 : food) {
                fillRect(g, food1, FOOD_COLOR);
            }

        }
    }

    public void moveStars(Graphics g) {
        if (starfield) {

            starx += 1;

            STAR_TILE_SIZE = 3000;
            g.setColor(COLOR_RAINBOW_CYCLE2);
            drawStars((Graphics2D) g, starx, stary, 10);

            STAR_TILE_SIZE = 2000;
            g.setColor(Color.LIGHT_GRAY);
            drawStars((Graphics2D) g, starx / 2, stary, 5);

            STAR_TILE_SIZE = 500;
            g.setColor(Color.GRAY);
            drawStars((Graphics2D) g, starx / 3, stary, 3);

            starx++;
            STAR_TILE_SIZE = 1000;
            g.setColor(Color.DARK_GRAY);
            drawStars((Graphics2D) g, -starx / 4, stary, 15);

            starx--;
            STAR_TILE_SIZE = 10000;
            g.setColor(Color.blue);
            drawStars((Graphics2D) g, -starx / 5, stary, 8);

            STAR_TILE_SIZE = 6000;
            g.setColor(Color.red);
            drawStars((Graphics2D) g, -starx / 3, stary, 5);

            STAR_TILE_SIZE = 5000;
            g.setColor(Color.yellow);
            drawStars((Graphics2D) g, -starx / 4, stary, 7);

            STAR_TILE_SIZE = 7000;
            g.setColor(Color.MAGENTA);
            drawStars((Graphics2D) g, -starx / 6, stary, 6);

        }
    }

    public boolean isBigger(Snake snake, int index) {
        int tempCount = 0;
        for (Snake snake1 : snakes) {
            if (snake.getScore() > snake1.getScore()) {
                tempCount++;
            }
        }

        return (tempCount == snakes.length - 1);
    }

    public void fillSnake(Graphics g) {
        for (int i = 0; i < snakes.length; i++) {
            for (int j = 0; j < snakes[i].getSSize(); j++) {
                if (isBigger(snakes[i], i)) {
                    if (i < NUM_PLAYERS) {
                        snakes[i].snakeWidth = WINNING_PLAYERSNAKE_WIDTH;
                        fillRect(g, snakes[i].getRect(j), COLOR_RAINBOW_CYCLE);
                    } else if (i < NUM_PLAYERS + NUM_AI1) {
                        snakes[i].snakeWidth = WINNING_AI1SNAKE_WIDTH;
                        fillRect(g, snakes[i].getRect(j), COLOR_RAINBOW_CYCLE);
                    } else if (i < NUM_PLAYERS + NUM_AI1 + NUM_AI2) {
                        snakes[i].snakeWidth = WINNING_AI2SNAKE_WIDTH;
                        fillRect(g, snakes[i].getRect(j), COLOR_RAINBOW_CYCLE);
                    }
                } else {
                    if (i < NUM_PLAYERS) {
                        snakes[i].snakeWidth = LOSING_PLAYERSNAKE_WIDTH;
                        fillRect(g, snakes[i].getRect(j), snakes[i].getColor());
                    } else if (i < NUM_PLAYERS + NUM_AI1) {
                        snakes[i].snakeWidth = LOSING_AI1SNAKE_WIDTH;
                        fillRect(g, snakes[i].getRect(j), snakes[i].getColor());
                    } else if (i < NUM_PLAYERS + NUM_AI1 + NUM_AI2) {
                        snakes[i].snakeWidth = LOSING_AI2SNAKE_WIDTH;
                        fillRect(g, snakes[i].getRect(j), snakes[i].getColor());
                    }
                }
            }
        }
    }

    public void update() {
        if (updateornaw) {
            for (Snake snake : snakes) {
                snake.update();
            }
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

    public static void loadMusic(int index) throws Exception {
        File file = new File(musicArray[index]);
        audioIn = AudioSystem.getAudioInputStream(file);
        AudioFormat format = audioIn.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        clip = (Clip) AudioSystem.getLine(info);
        clip.open(audioIn);
    }

    public static void unloadMusic() {
        clip.close();
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

    static void defineScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //sets window to fit screen

        WINDOW_WIDTH = screenSize.getWidth();
        WINDOW_HEIGHT = screenSize.getHeight();
    }

    public static int getScreenWidth() {
        defineScreen();
        return (int) WINDOW_WIDTH;
    }

    public static int getScreenHeight() {
        defineScreen();
        return (int) WINDOW_HEIGHT;
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

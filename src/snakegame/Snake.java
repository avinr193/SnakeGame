/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snakegame;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author skinnnero5
 */
public class Snake {

    public Direction dir;
    private ArrayList<Rect2d> snake;
    private ArrayList<SquareCoords> history;
    double snakeWidth;
    boolean moving;
    boolean alive;
    boolean isPlayer;
    Rect2d head;
    Rect2d bounds;
    Color color;
    String name;
    double startX, startY;

    public static final int PLAYER_SPEED_MULTIPLIER = SnakePanel.PLAYER_SPEED_MULTIPLIER;

    public static final boolean PLAYER_RANDOM_SPAWN_TOGGLE = SnakePanel.PLAYER_RANDOM_SPAWN_TOGGLE;

    public static final int PLAYER_NON_RANDOM_SPAWN_X = SnakePanel.PLAYER_NON_RANDOM_SPAWN_X;
    public static final int PLAYER_NON_RANDOM_SPAWN_Y = SnakePanel.PLAYER_NON_RANDOM_SPAWN_Y;

    public Snake(Color color, String name) {
        this.dir = Direction.Right;
        this.color = color;
        this.name = name;
        snake = new ArrayList<Rect2d>();
        history = new ArrayList<SquareCoords>();
        bounds = new Rect2d(0, 0, SnakePanel.WINDOW_WIDTH, SnakePanel.WINDOW_HEIGHT);
        setHeadLoc();
        snakeWidth = 10;
        moving = false;
        alive = true;
        isPlayer = true;
    }

    public void setHeadLoc() {
        if (PLAYER_RANDOM_SPAWN_TOGGLE) {
            startX = (double) random_number(100, SnakePanel.getScreenWidth() - 100);
            startY = (double) random_number(100, SnakePanel.getScreenHeight() - 100);
            head = new Rect2d(startX, startY, snakeWidth, snakeWidth);
            this.addS(head);
        } else {
            head = new Rect2d(PLAYER_NON_RANDOM_SPAWN_X, PLAYER_NON_RANDOM_SPAWN_Y, snakeWidth, snakeWidth);
            this.addS(head);
        }
    }

    String getName() {
        return name;
    }

    Rect2d getRect(int i) {
        return snake.get(i);
    }

    SquareCoords getH(int i) {
        return history.get(i);
    }

    int getScore() {
        return (int) this.getSSize() - 1;
    }

    Color getColor() {
        return color;
    }

    Direction getDir() {
        return dir;
    }

    double getSSize() {
        return snake.size();
    }

    double getHSize() {
        return history.size();
    }

    double getWidth() {
        return snakeWidth;
    }

    void setWidth(double newWidth) {
        snakeWidth = newWidth;
    }

    void addH(SquareCoords a) {
        history.add(a);
    }

    void addS(Rect2d s) {
        snake.add(s);
    }

    void setS(int i, Rect2d s) {
        snake.set(i, s);
    }

    void setH(int i, SquareCoords a) {
        history.set(i, a);
    }

    void updateSize() {
        for (int i = 0; i < this.getSSize(); i++) {
            this.setS(i, new Rect2d(this.getRect(i).getLeft(), this.getRect(i).getTop(), snakeWidth, snakeWidth));
        }

    }

    boolean isMoving() {
        return moving;
    }

    void setMoving(boolean tf) {
        moving = tf;
    }

    void die() {
        alive = false;
        moving = false;
        for (int i = 0; i < history.size(); i++) {
            this.setS(i, Rect2d.EmptyRect);
            SnakePanel.food.add(new Rect2d(this.getH(i).x, this.getH(i).y, 10, 10));
        }

    }

    boolean isLiving() {
        return alive;
    }

    void update() {
        if (alive == false) {
            return;
        }

        //System.out.println("s" + this.getSSize());
        //double widthfactor = 1;
        for (int j = 0; j < SnakePanel.food.size(); j++) {
            if (Rect2d.intersect(SnakePanel.food.get(j), this.getHead()) != Rect2d.EmptyRect) {//when snake touches food
                //Rect2d.resolveOverlap(food.get(j), snake.get(0));
                this.addS(new Rect2d(1000, 1000.0, this.getWidth(), this.getWidth()));
                this.addH(new SquareCoords(0, 0));
                SnakePanel.food.remove(j);
                SnakePanel.food.add(new Rect2d(random_number(0, SnakePanel.getScreenWidth() / 10), random_number(0, SnakePanel.getScreenHeight() / 10), 10, 10));
                SnakeGame.updateScoreboard();
                //widthfactor = this.getSSize() / 10;
                //widthfactor += 1;
                //this.setWidth(10 + (widthfactor * 5));
            }
        }

        for (int j = 4; j < this.getSSize(); j++) {
            if (Rect2d.intersect(this.getRect(j), this.getHead()) != Rect2d.EmptyRect) {//when snake touches itself
                this.die();
                //break;
            }
        }

        if (Rect2d.intersect(bounds, this.getHead()) == Rect2d.EmptyRect) {
            this.die();
        }

        for (int i = 1; i < SnakePanel.snakes.length; i++) {
            for (int j = 0; j < SnakePanel.snakes[i].getSSize(); j++) {
                if (Rect2d.intersect(SnakePanel.snakes[i].getRect(j), this.getHead()) != Rect2d.EmptyRect) {
                    this.die();
                }
            }
        }

        for (int i = 0; i < this.getSSize(); i++) {
            this.setH(i, new SquareCoords((int) this.getRect(i).getLeft(), (int) this.getRect(i).getTop()));
        }

        this.setMoving(true);

        for (int i = 0; i < this.getHSize(); i++) {
            if (i == 0) {
                if (SnakePanel.keysPressed.Left && dir != Direction.Right) {
                    // bernie.getHead().translate(-bernie.getWidth(), 0.0);
                    this.setMoving(true);
                    this.dir = Direction.Left;
                }
                if (SnakePanel.keysPressed.Right && dir != Direction.Left) {
                    //  bernie.getHead().translate(bernie.getWidth(), 0.0);
                    this.setMoving(true);
                    this.dir = Direction.Right;
                }

                if (SnakePanel.keysPressed.Down && dir != Direction.Up) {
                    //    bernie.getHead().translate(0.0, bernie.getWidth());
                    this.setMoving(true);
                    this.dir = Direction.Down;
                }

                if (SnakePanel.keysPressed.Up && dir != Direction.Down) {
                    // bernie.getHead().translate(0.0, -bernie.getWidth());
                    this.setMoving(true);
                    this.dir = Direction.Up;
                }
            } else {
                if (this.isMoving()) {
                    this.getRect(i).moveTo(this.getH(i - 1));
                }

            }

        }

        //CAMERA SCROLLING SWITCH CASE (Just moves food to create illusion of scrolling)
        /*switch (this.dir) {
         case Left:
         if (RectPanel.checkCamBounds() || (RectPanel.checkAtLeft() == false)) {
         this.getHead().translate(-this.getWidth(), 0.0);
         }
         if (RectPanel.checkCamBounds() == false && RectPanel.checkAtUp() == false && RectPanel.checkAtDown() == false || RectPanel.checkAtLeft()) {
         for (int i = 0; i < RectPanel.food.size(); i++) {
         RectPanel.food.get(i).translate(transval, 0);
         }

         }

         break;

         case Right:
         if (RectPanel.checkCamBounds() || (RectPanel.checkAtRight() == false)) {
         this.getHead().translate(this.getWidth(), 0.0);
         }
         if (RectPanel.checkCamBounds() == false && RectPanel.checkAtUp() == false && RectPanel.checkAtDown() == false || RectPanel.checkAtRight()) {
         for (int i = 0; i < RectPanel.food.size(); i++) {
         RectPanel.food.get(i).translate(-transval, 0);
         }

         }
         break;

         case Down:
         if (RectPanel.checkCamBounds() || (RectPanel.checkAtDown() == false)) {
         this.getHead().translate(0.0, this.getWidth());
         }
         if (RectPanel.checkCamBounds() == false && RectPanel.checkAtRight() == false && RectPanel.checkAtLeft() == false || RectPanel.checkAtDown()) {
         for (int i = 0; i < RectPanel.food.size(); i++) {
         RectPanel.food.get(i).translate(0, -transval);
         }

         }
         break;

         case Up:
         if (RectPanel.checkCamBounds() || (RectPanel.checkAtUp() == false)) { //Snake should be able to move only when inbounds of cam-rect, should stop at line but still be able to be moved
         this.getHead().translate(0.0, -this.getWidth());
         }
         if (RectPanel.checkCamBounds() == false && RectPanel.checkAtRight() == false && RectPanel.checkAtLeft() == false || RectPanel.checkAtUp()) {
         for (int i = 0; i < RectPanel.food.size(); i++) {
         RectPanel.food.get(i).translate(0, transval);
         }

         }
         break;
         }*/
        switch (this.dir) {
            case Left:
                this.getHead().translate(-PLAYER_SPEED_MULTIPLIER * this.getWidth(), 0.0);
                break;

            case Right:
                this.getHead().translate(PLAYER_SPEED_MULTIPLIER * this.getWidth(), 0.0);
                break;

            case Down:
                this.getHead().translate(0.0, PLAYER_SPEED_MULTIPLIER * this.getWidth());
                break;

            case Up:
                this.getHead().translate(0.0, -PLAYER_SPEED_MULTIPLIER * this.getWidth());
                break;
        }

        this.updateSize();

    }

    Rect2d getHead() {
        return snake.get(0);
    }

    void reset() {
        this.alive = true;
        this.snakeWidth = 10;
        this.moving = true;
        for (int i = 1; i < snake.size(); i++) {
            this.snake.remove(i);
        }
        for (int i = 1; i < history.size(); i++) {
            this.history.remove(i);
        }
        this.setS(0, head);
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
}

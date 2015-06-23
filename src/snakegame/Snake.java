/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package snakegame;

import java.util.LinkedList;

/**
 *
 * @author cs1
 */
public class Snake {
        Direction dir;
    LinkedList<SquareCoords> body;
    boolean alive;
    int growAmount;
    int score = 0;
        int color;
        

    Snake(int x, int y, Direction dir, int color) {

    
        this.dir = dir;
        this.alive = true;
        this.growAmount = 0;
        this.color = color;
        body = new LinkedList<SquareCoords>();
        body.addLast(new SquareCoords(x+5, y+27));

    }


    
    void update(Direction dir, SnakePanel sp) {
        if(alive != true){return;}
        else{
        SquareCoords head = body.getFirst();
        int x = head.x;
        int y = head.y;
        switch (dir) {
            case Up:
                y--;
                break;

            case Down:
                y++;
                break;

            case Left:
                x--;
                break;

            case Right:
                x++;
                break;

        }
        /////WEIRD PART THAT AVIN WILL TAKE CARE OF SO DON'T WORRY
        if(sp.readSquare(x,y) != 0 && sp.readSquare(x,y) != 3 && sp.readSquare(x,y) != 1 && sp.readSquare(x,y) != 4){alive = false;}
        if (this.color == 1 && this.body.size() < sp.bernita.body.size() && sp.readSquare(x, y) == 4){alive = false; for(int i = 1; i < body.size()-1; i++){body.remove();}}
        if (this.color == 1 && this.body.size() > sp.bernita.body.size() && sp.readSquare(x, y) == 4){for(int i = 1; i < sp.bernita.body.size()-1; i++){sp.bernita.body.remove();}}
        if (this.color == 4 && this.body.size() < sp.bernie.body.size() && sp.readSquare(x, y) == 1){alive = false; for(int i = 1; i < body.size()-1; i++){body.remove();}}
        if (this.color == 4 && this.body.size() > sp.bernie.body.size() && sp.readSquare(x, y) == 1){for(int i = 1; i < sp.bernie.body.size()-1; i++){sp.bernie.body.remove();}}

        if(sp.readSquare(x,y) == 3){sp.drawMouse(); growAmount = growAmount + 10; score++;}
        body.addFirst(new SquareCoords(x,y));
        if(growAmount > 0){
        growAmount--;
        return;
        }
        body.removeLast();
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    void draw(SnakePanel sp) {
        for(int i = 0; i < body.size(); i++){
        SquareCoords pos = body.get(i);
        sp.writeSquare(pos.x, pos.y, color);
        }
    }
}

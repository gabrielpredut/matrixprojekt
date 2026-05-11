package matrixshmup;

import java.awt.*;
import java.util.Random;

/*
 Basic enemy class.
 Enemies move down and slightly sideways.
*/

public class Enemy {

    // Position of the enemy
    int x, y;

    // Speed for downward movement
    int speed;

    // Side movement speed
    int driftX;

    // Random generator
    static Random r = new Random();

    // Creates a new enemy
    public Enemy(int x, int y, int speed){

        this.x = x;
        this.y = y;
        this.speed = speed;

        // Random side movement
        // Possible values: -2 to 2
        this.driftX = r.nextInt(5) - 2;
    }

    // Creates a random enemy at the top of the screen
    public static Enemy random(){

        return new Enemy(r.nextInt(560), -20, 2 + r.nextInt(3));
    }

    // Updates enemy movement
    public void update(){

        // Move enemy downward
        y += speed;

        // Move enemy sideways
        x += driftX;

        // Bounce at left screen edge
        if(x < 0){
            x = 0;
            driftX = -driftX;
        }

        // Bounce at right screen edge
        if(x > 560){
            x = 560;
            driftX = -driftX;
        }

        // Sometimes change direction randomly
        if(r.nextDouble() < 0.02){
            driftX = r.nextInt(5) - 2;
        }
    }

    // Creates a hitbox for collisions
    public Rectangle getBounds(){

        return new Rectangle(x, y, 40, 20);
    }

    // Draws the enemy
    public void draw(Graphics g){

        // Set enemy color
        g.setColor(Color.RED);

        // Draw enemy body
        g.fillRect(x, y, 40, 20);
    }
}
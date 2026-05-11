package matrixshmup;

import java.awt.*;

public class EnemyBullet {

    // Position of the bullet
    int x, y;

    // Speed for downward movement
    int speed = 6;

    // Side movement for diagonal bullets
    int dx = 0;

    // Checks if the bullet is still active
    boolean alive = true;

    // Creates a normal enemy bullet
    public EnemyBullet(int x, int y){

        this.x = x;
        this.y = y;
    }

    // Creates a custom enemy bullet
    public EnemyBullet(int x, int y, int dx, int speed){

        this.x = x;
        this.y = y;
        this.dx = dx;
        this.speed = speed;
    }

    // Updates bullet movement
    public void update(){

        // Move bullet sideways
        x += dx;

        // Move bullet downward
        y += speed;

        // Remove bullet when it leaves the screen
        if(y > 820 || x < -20 || x > 620)
            alive = false;
    }

    // Creates a hitbox for collisions
    public Rectangle getBounds(){

        return new Rectangle(x, y, 5, 10);
    }

    // Draws the enemy bullet
    public void draw(Graphics g){

        // Set bullet color to red
        g.setColor(Color.RED);

        // Draw bullet shape
        g.fillRect(x, y, 5, 10);
    }
}
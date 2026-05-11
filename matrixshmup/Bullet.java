package matrixshmup;

import java.awt.*;

public class Bullet {

    // Position of the bullet
    int x, y;

    // Speed of the bullet
    int speed = 8;

    // Checks if the bullet is still active
    boolean alive = true;

    // Creates a new bullet
    public Bullet(int x, int y){

        this.x = x;
        this.y = y;
    }

    // Moves the bullet upward
    public void update(){

        y -= speed;

        // Remove bullet when it leaves the screen
        if(y < -20)
            alive = false;
    }

    // Creates a hitbox for collisions
    public Rectangle getBounds(){

        return new Rectangle(x, y, 5, 10);
    }

    // Draws the bullet
    public void draw(Graphics g){

        // Set bullet color
        g.setColor(new Color(180, 255, 255));

        // Draw bullet shape
        g.fillRect(x, y, 5, 10);
    }
}
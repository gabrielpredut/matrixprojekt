package matrixshmup;

import java.awt.*;

/*
 Player spaceship class.
 Handles movement, shooting and drawing.
*/

public class Player {

    // Position of the player
    int x, y;

    // Size of the player hitbox
    int width = 40;
    int height = 30;

    // Delay between shots
    int cooldown = 0;

    // Player health
    // Player dies after one hit
    int health = 1;

    // Creates the player
    public Player(int x, int y){

        this.x = x;
        this.y = y;
    }

    // Moves the player
    public void move(int dx, int dy){

        x += dx;
        y += dy;

        // Keep player inside screen borders
        if(x < 0) x = 0;
        if(x > 560) x = 560;

        if(y < 0) y = 0;
        if(y > 760) y = 760;
    }

    // Checks if the player can shoot
    public boolean canShoot(){

        // Player can shoot if cooldown is finished
        if(cooldown <= 0){

            // Reset cooldown
            cooldown = 12;

            return true;
        }

        return false;
    }

    // Creates a new bullet
    public Bullet shoot(){

        // Spawn bullet above the ship
        return new Bullet(x + width / 2 - 2, y - 10);
    }

    // Creates a hitbox for collisions
    public Rectangle getBounds(){

        return new Rectangle(x, y, width, height);
    }

    // Draws the player ship
    public void draw(Graphics g){

        // Reduce cooldown timer
        if(cooldown > 0) cooldown--;

        // Set ship color
        g.setColor(Color.GREEN);

        // Triangle shape points
        int[] xs = { x, x + width, x + width / 2 };
        int[] ys = { y + height, y + height, y };

        // Draw triangle spaceship
        g.fillPolygon(xs, ys, 3);
    }
}
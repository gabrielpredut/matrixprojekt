package matrixshmup;

import java.awt.*;

/*
 Final boss enemy of the game.
 The boss changes attack style when health gets lower.
*/

public class AgentSmithBoss {

    // Position of the boss
    int x, y;

    // Boss size
    int width = 140;
    int height = 60;

    // Speed for left and right movement
    int speedX = 4;

    // Boss health
    int maxHealth = 300;
    int health = maxHealth;

    // Time until the boss can shoot again
    int shootCooldown = 0;

    // Creates the boss at a start position
    public AgentSmithBoss(int x, int y){

        this.x = x;
        this.y = y;
    }

    // Creates a hitbox for collisions
    public Rectangle getBounds(){

        return new Rectangle(x, y, width, height);
    }

    // Updates movement and shooting
    public void update(java.util.List<EnemyBullet> bullets){

        // Move boss horizontally
        x += speedX;

        // Change direction at left border
        if(x < 40){
            x = 40;
            speedX = -speedX;
        }

        // Change direction at right border
        if(x + width > 560){
            x = 560 - width;
            speedX = -speedX;
        }

        // Reduce cooldown timer
        if(shootCooldown > 0)
            shootCooldown--;

        // Calculate remaining health in percent
        float hpPercent = health / (float)maxHealth;

        int baseDelay;

        // Phase 1 = slow attacks
        if(hpPercent > 0.66f){

            baseDelay = 55;

            // Phase 2 = faster attacks
        }else if(hpPercent > 0.33f){

            baseDelay = 35;

            // Phase 3 = aggressive attacks
        }else{

            baseDelay = 32;
        }

        // Shoot when cooldown is finished
        if(shootCooldown <= 0){

            shootPhasePattern(bullets, hpPercent);

            // Reset cooldown
            shootCooldown = baseDelay;
        }
    }

    // Creates different bullet patterns
    private void shootPhasePattern(java.util.List<EnemyBullet> bullets, float hpPercent){

        // Center position of the boss
        int centerX = x + width / 2;

        // Bottom position of the boss
        int bottomY = y + height;

        // Phase 1 = one bullet
        if(hpPercent > 0.66f){

            bullets.add(new EnemyBullet(centerX, bottomY, 0, 7));

            // Phase 2 = two bullets
        }else if(hpPercent > 0.33f){

            bullets.add(new EnemyBullet(centerX - 10, bottomY, 0, 8));
            bullets.add(new EnemyBullet(centerX + 10, bottomY, 0, 8));

            // Phase 3 = three bullets
        }else{

            bullets.add(new EnemyBullet(centerX - 15, bottomY, 0, 7));
            bullets.add(new EnemyBullet(centerX, bottomY, 0, 7));
            bullets.add(new EnemyBullet(centerX + 15, bottomY, 0, 7));
        }
    }

    // Draws the boss on the screen
    public void draw(Graphics g){

        // Draw black suit
        g.setColor(new Color(30, 30, 30));
        g.fillRect(x, y, width, height);

        // Draw white shirt
        g.setColor(Color.WHITE);
        g.fillRect(x + width/2 - 10, y + 10, 20, 25);

        // Draw green tie
        g.setColor(new Color(0, 200, 0));
        g.fillRect(x + width/2 - 3, y + 20, 6, 20);

        // Draw head
        g.setColor(new Color(220, 200, 180));
        g.fillRect(x + width/2 - 12, y - 18, 24, 18);

        // Draw sunglasses
        g.setColor(Color.BLACK);
        g.fillRect(x + width/2 - 14, y - 12, 10, 6);
        g.fillRect(x + width/2 + 4, y - 12, 10, 6);
    }
}
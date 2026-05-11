package matrixshmup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

/*
 Main game panel.
 Handles gameplay, movement, enemies, collisions and drawing.
*/

public class GamePanel extends JPanel implements KeyListener {

    // Main game window
    JFrame parent;

    // Reference to the menu
    MenuPanel menu;

    // Player object
    Player player;

    // Lists for bullets and enemies
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();

    // List for enemy bullets
    ArrayList<EnemyBullet> enemyBullets = new ArrayList<>();

    // Main game loop timer
    Timer loop;

    // Keyboard input flags
    boolean left, right, up, down, shoot;

    // Counter for enemy spawning
    int spawnCounter = 0;

    // Player score
    int score = 0;

    // Game state
    boolean gameOver = false;

    // True if the player wins
    boolean playerWon = false;

    // Reset button after losing
    JButton resetButton;

    // Time for showing controls at game start
    int instructionFrames = 60 * 4;

    // Current level
    int level = 1;

    // Number of destroyed enemies
    int enemiesDestroyed = 0;

    // Saved checkpoint values
    int checkpointLevel = 1;
    int checkpointEnemiesDestroyed = 0;
    int checkpointScore = 0;

    // Boss object
    AgentSmithBoss boss = null;

    // True while boss fight is active
    boolean bossActive = false;

    // Level transition message
    boolean levelPause = false;
    int levelMessageFrames = 0;
    String levelMessageText = "";

    // Boss intro message
    int bossIntroFrames = 0;
    String bossIntroLine1 = "";
    String bossIntroLine2 = "";

    // Creates the game panel
    public GamePanel(JFrame parent, MenuPanel menu){

        this.parent = parent;
        this.menu = menu;

        // Set black background
        setBackground(Color.BLACK);

        // Use free positioning for buttons
        setLayout(null);

        // Allow keyboard input
        setFocusable(true);

        // Activate key listener
        addKeyListener(this);

        // Create player
        player = new Player(280,700);

        // Create reset button
        resetButton = new JButton("RESET (checkpoint)");
        resetButton.setBounds(200, 460, 200, 40);
        resetButton.setVisible(false);
        resetButton.setFocusPainted(false);

        // Reset game when button is clicked
        resetButton.addActionListener(e -> resetFromCheckpoint());

        add(resetButton);
    }

    // Starts the game loop
    public void startGame(){

        loop = new Timer(16, e -> {

            // Update game logic
            update();

            // Redraw screen
            repaint();
        });

        loop.start();
    }

    // Updates the game
    void update(){

        // Stop updates if game ended
        if(gameOver) return;

        // Move player
        if(left) player.move(-6,0);
        if(right) player.move(6,0);
        if(up) player.move(0,-6);
        if(down) player.move(0,6);

        // Shoot bullets
        if(shoot && player.canShoot())
            bullets.add(player.shoot());

        // Update player bullets
        for(Bullet b : bullets)
            b.update();

        // Update enemy bullets
        for(EnemyBullet eb : enemyBullets)
            eb.update();

        // Spawn enemies if boss is inactive
        if(!bossActive && !levelPause && bossIntroFrames <= 0){

            spawnCounter++;

            // Enemy spawn speed depends on level
            int spawnDelay = 60;

            if(level == 2) spawnDelay = 45;
            if(level == 3) spawnDelay = 30;

            // Spawn new enemy
            if(spawnCounter > spawnDelay){

                spawnCounter = 0;

                Enemy e = Enemy.random();

                // Faster enemies on higher levels
                if(level == 2) e.speed += 1;
                if(level == 3) e.speed += 2;

                enemies.add(e);
            }
        }

        // Update enemy movement
        for(Enemy e : enemies)
            e.update();

        // Handle boss intro
        if(!bossActive && level == 4){

            // Count down intro timer
            if(bossIntroFrames > 0){

                bossIntroFrames--;

                // Start boss fight
            }else if(boss == null){

                startBossFight();
            }
        }

        // Update boss
        if(bossActive && boss != null){

            boss.update(enemyBullets);

            // Player wins if boss dies
            if(boss.health <= 0){

                bossActive = false;
                gameOver = true;
                playerWon = true;

                resetButton.setVisible(false);

                loop.stop();

                SwingUtilities.invokeLater(this::openWinSequence);
            }
        }

        // Check all collisions
        checkCollisions();

        // Remove inactive bullets
        bullets.removeIf(b -> !b.alive);
        enemyBullets.removeIf(eb -> !eb.alive);

        // Reduce instruction timer
        if(instructionFrames > 0){
            instructionFrames--;
        }
    }

    // Checks collisions between objects
    void checkCollisions(){

        // Player bullets hit enemies
        for(Iterator<Bullet> bi = bullets.iterator(); bi.hasNext();){

            Bullet b = bi.next();

            for(Iterator<Enemy> ei = enemies.iterator(); ei.hasNext();){

                Enemy e = ei.next();

                // Collision detected
                if(Utils.hit(b.getBounds(), e.getBounds())){

                    bi.remove();
                    ei.remove();

                    // Add score
                    score += 10;

                    // Count destroyed enemies
                    enemiesDestroyed++;

                    // Check level progress
                    updateLevelProgress();

                    break;
                }
            }
        }

        // Player bullets hit boss
        if(bossActive && boss != null){

            for(Iterator<Bullet> bi2 = bullets.iterator(); bi2.hasNext();){

                Bullet b2 = bi2.next();

                if(Utils.hit(b2.getBounds(), boss.getBounds())){

                    bi2.remove();

                    // Damage boss
                    boss.health -= 5;
                }
            }
        }

        // Enemies hit player
        for(Enemy e : enemies){

            if(Utils.hit(player.getBounds(), e.getBounds())){

                // Reduce player health
                player.health--;

                // End game if player dies
                if(player.health <= 0){

                    gameOver = true;
                    playerWon = false;

                    resetButton.setVisible(true);

                    loop.stop();
                }
            }
        }

        // Enemy bullets hit player
        for(Iterator<EnemyBullet> it = enemyBullets.iterator(); it.hasNext();){

            EnemyBullet eb = it.next();

            if(Utils.hit(player.getBounds(), eb.getBounds())){

                it.remove();

                // Reduce player health
                player.health--;

                // End game if player dies
                if(player.health <= 0){

                    gameOver = true;
                    playerWon = false;

                    resetButton.setVisible(true);

                    loop.stop();
                }
            }
        }
    }

    // Handles level progression
    void updateLevelProgress(){

        // Go to level 2
        if(level == 1 && enemiesDestroyed >= 15){

            level = 2;

            levelPause = true;

            levelMessageFrames = 60 * 2;

            levelMessageText = "LEVEL 2 - MEDIUM";

            saveCheckpoint();

            // Go to level 3
        }else if(level == 2 && enemiesDestroyed >= 35){

            level = 3;

            levelPause = true;

            levelMessageFrames = 60 * 2;

            levelMessageText = "LEVEL 3 - HARD";

            saveCheckpoint();

            // Start boss level
        }else if(level == 3 && enemiesDestroyed >= 60){

            level = 4;

            // Show boss intro
            bossIntroFrames = 60 * 4;

            bossIntroLine1 = "MR ANDERSON...";
            bossIntroLine2 = "WELCOME BACK TO THE MATRIX";

            saveCheckpoint();
        }
    }

    // Saves checkpoint data
    void saveCheckpoint(){

        checkpointLevel = level;
        checkpointEnemiesDestroyed = enemiesDestroyed;
        checkpointScore = score;
    }

    // Restores game from checkpoint
    void resetFromCheckpoint(){

        // Reset game state
        gameOver = false;
        playerWon = false;

        // Hide reset button
        resetButton.setVisible(false);

        // Restore saved values
        level = checkpointLevel;
        enemiesDestroyed = checkpointEnemiesDestroyed;
        score = checkpointScore;

        // Clear all objects
        bullets.clear();
        enemies.clear();
        enemyBullets.clear();

        // Reset player
        player.x = 280;
        player.y = 700;
        player.health = 1;

        // Reset boss
        boss = null;
        bossActive = false;

        // Reset messages
        levelPause = false;
        levelMessageFrames = 0;
        bossIntroFrames = 0;

        // Show intro again at boss checkpoint
        if(level == 4){

            bossIntroFrames = 60 * 3;

            bossIntroLine1 = "THIS IS YOUR LAST CHANCE...";
            bossIntroLine2 = "DODGE THE BULLETS, MR ANDERSON";
        }

        // Restart game loop
        if(loop != null){
            loop.start();
        }

        // Enable keyboard input again
        requestFocusInWindow();
    }

    // Starts the boss fight
    void startBossFight(){

        bossActive = true;

        // Create boss at top center
        boss = new AgentSmithBoss(230, 80);
    }

    // Opens the win screen
    void openWinSequence(){

        WinSequencePanel win = new WinSequencePanel(parent);

        parent.setContentPane(win);

        parent.revalidate();

        win.requestFocusInWindow();
    }

    // Draws everything on screen
    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        // Draw player
        player.draw(g);

        // Draw player bullets
        for(Bullet b : bullets)
            b.draw(g);

        // Draw enemies
        for(Enemy e : enemies)
            e.draw(g);

        // Draw enemy bullets
        for(EnemyBullet eb : enemyBullets)
            eb.draw(g);

        // Draw score
        g.setColor(new Color(120, 255, 120));

        g.drawString("Score: " + score, 10,20);

        // Draw current level
        g.drawString("Level: " + (bossActive ? "BOSS" : level), 10,40);

        // Draw level message
        if(levelPause && levelMessageFrames > 0){

            // Create blinking effect
            if((levelMessageFrames / 8) % 2 == 0){

                g.setFont(new Font("Monospaced", Font.BOLD, 28));

                g.drawString(levelMessageText, 110, 250);
            }

            levelMessageFrames--;

            // End pause
            if(levelMessageFrames <= 0){
                levelPause = false;
            }
        }

        // Draw boss and health bar
        if(bossActive && boss != null){

            boss.draw(g);

            // Background of health bar
            g.setColor(Color.DARK_GRAY);
            g.fillRect(150, 10, 300, 10);

            // Current boss health
            g.setColor(Color.RED);

            int barWidth = (int)(300 * (boss.health / (float)boss.maxHealth));

            g.fillRect(150, 10, barWidth, 10);

            // Boss name
            g.setColor(new Color(120, 255, 120));

            g.drawString("AGENT SMITH", 230, 35);
        }

        // Draw boss intro
        if(!bossActive && level == 4 && bossIntroFrames > 0){

            g.setColor(new Color(0,0,0,180));
            g.fillRoundRect(40, 260, 520, 130, 20, 20);

            g.setColor(new Color(120, 255, 120));
            g.setFont(new Font("Monospaced", Font.BOLD, 22));

            int y = 310;

            g.drawString(bossIntroLine1, 80, y);

            y += 30;

            g.drawString(bossIntroLine2, 80, y);
        }

        // Draw controls at game start
        if(instructionFrames > 0){

            g.setColor(new Color(0,0,0,170));
            g.fillRoundRect(70, 300, 460, 120, 20, 20);

            g.setColor(new Color(120, 255, 120));
            g.setFont(new Font("Monospaced", Font.PLAIN, 16));

            g.drawString("Controls:", 90, 330);
            g.drawString("ARROWS = move   SPACE = shoot", 90, 355);
            g.drawString("Avoid red blocks and shoot them for points.", 90, 380);
        }

        // Draw game over screen
        if(gameOver){

            g.setFont(new Font("Monospaced", Font.BOLD, 40));

            // Win screen
            if(playerWon){

                g.drawString("SYSTEM CRASHED",120,340);

                g.setFont(new Font("Monospaced", Font.BOLD, 26));

                g.drawString("YOU ARE THE ONE",160,380);

                g.setFont(new Font("Monospaced", Font.PLAIN, 18));

                g.drawString("There is no spoon.", 220, 410);
                g.drawString("Zion is safe... for now.", 190, 435);

                // Lose screen
            }else{

                g.drawString("GAME OVER",150,380);

                g.setFont(new Font("Monospaced", Font.PLAIN, 18));

                g.drawString("The Matrix got you.", 210, 410);
            }
        }
    }

    // Called when a key is pressed
    public void keyPressed(KeyEvent e){

        int k = e.getKeyCode();

        if(k == 37) left = true;
        if(k == 39) right = true;
        if(k == 38) up = true;
        if(k == 40) down = true;

        if(k == 32) shoot = true;
    }

    // Called when a key is released
    public void keyReleased(KeyEvent e){

        int k = e.getKeyCode();

        if(k == 37) left = false;
        if(k == 39) right = false;
        if(k == 38) up = false;
        if(k == 40) down = false;

        if(k == 32) shoot = false;
    }

    // Not used here
    public void keyTyped(KeyEvent e){}
}
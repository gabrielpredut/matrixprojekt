package matrixshmup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
 Main menu screen of the game.
 Shows the Matrix intro and starts the game.
*/

public class MenuPanel extends JPanel implements KeyListener {

    // Main game window
    JFrame parent;

    // Intro text shown at the beginning
    String introMessage =
            "Wake up, student...\n" +
                    "The Matrix has you...\n" +
                    "Follow the white rabbit...\n\n" +
                    "Take the RED PILL to wake up.\n" +
                    "Take the BLUE PILL to stay asleep.\n";

    // Instructions shown after taking the red pill
    String instructionsMessage =
            "GAME INSTRUCTIONS:\n" +
                    "- Move with the ARROW KEYS\n" +
                    "- Shoot with the SPACE BAR\n" +
                    "- Avoid the red code blocks (enemies)\n" +
                    "- Shoot them to gain points\n\n" +
                    "After you are ready:\n" +
                    "PRESS SPACE TO JACK IN...\n";

    // Current text that is being displayed
    String message = introMessage;

    // Text already shown on screen
    String shown = "";

    // Current typing position
    int index = 0;

    // Timer for typing animation
    Timer typingTimer;

    // Timer for blinking text
    Timer blinkTimer;

    // Controls blinking visibility
    boolean showPressSpace = false;

    /*
     Menu states:
     0 = typing intro
     1 = waiting for pill choice
     2 = typing instructions
     3 = waiting for SPACE
    */
    int state = 0;

    // Menu buttons
    JButton redButton;
    JButton blueButton;

    // Creates the menu panel
    public MenuPanel(JFrame parent){

        this.parent = parent;

        // Set background color
        setBackground(Color.BLACK);

        // Use free positioning
        setLayout(null);

        // Enable keyboard input
        setFocusable(true);
        addKeyListener(this);

        // Create buttons
        redButton = new JButton("Red Pill");
        blueButton = new JButton("Blue Pill");

        // Set button positions
        redButton.setBounds(150,650,140,45);
        blueButton.setBounds(320,650,140,45);

        // Style red button
        redButton.setBackground(new Color(200, 40, 40));
        redButton.setForeground(Color.WHITE);
        redButton.setFocusPainted(false);
        redButton.setBorderPainted(false);
        redButton.setFont(new Font("Monospaced", Font.BOLD, 14));

        // Style blue button
        blueButton.setBackground(new Color(40, 80, 200));
        blueButton.setForeground(Color.WHITE);
        blueButton.setFocusPainted(false);
        blueButton.setBorderPainted(false);
        blueButton.setFont(new Font("Monospaced", Font.BOLD, 14));

        // Hide buttons at the start
        redButton.setVisible(false);
        blueButton.setVisible(false);

        // Red pill shows instructions
        redButton.addActionListener(e -> showInstructions());

        // Blue pill closes the game
        blueButton.addActionListener(e -> System.exit(0));

        add(redButton);
        add(blueButton);
    }

    // Starts the typing animation
    public void startTyping(){

        // Slow typing at the beginning
        typingTimer = new Timer(120, e -> {

            // Add next character
            if(index < message.length()){

                shown += message.charAt(index);

                index++;

                repaint();

                // Increase typing speed later
                if(index == 80){
                    typingTimer.setDelay(70);
                }

                if(index == 200){
                    typingTimer.setDelay(40);
                }

            }else{

                // Stop typing when text is finished
                typingTimer.stop();

                // Show buttons after intro
                if(state == 0){

                    state = 1;

                    redButton.setVisible(true);
                    blueButton.setVisible(true);

                    // Wait for SPACE after instructions
                }else if(state == 2){

                    state = 3;

                    startBlinking();
                }
            }
        });

        typingTimer.start();
    }

    // Starts the actual game
    void startGame(){

        // Stop blinking effect
        if(blinkTimer != null){
            blinkTimer.stop();
        }

        // Create game panel
        GamePanel game = new GamePanel(parent, this);

        // Replace menu with game
        parent.setContentPane(game);

        parent.revalidate();

        // Enable keyboard controls
        game.requestFocus();

        // Start game loop
        game.startGame();
    }

    // Shows the instruction screen
    void showInstructions(){

        // Hide menu buttons
        redButton.setVisible(false);
        blueButton.setVisible(false);

        // Reset typing values
        shown = "";
        index = 0;

        state = 2;

        // Change current text
        message = instructionsMessage;

        // Stop old typing timer
        if(typingTimer != null){
            typingTimer.stop();
        }

        // Start new typing animation
        startTyping();
    }

    // Starts blinking "PRESS SPACE" text
    void startBlinking(){

        blinkTimer = new Timer(400, e -> {

            // Toggle visibility
            showPressSpace = !showPressSpace;

            repaint();
        });

        blinkTimer.start();
    }

    // Draws text on the screen
    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        // Set Matrix-style text color
        g.setColor(new Color(120, 255, 120));

        g.setFont(new Font("Monospaced", Font.PLAIN, 18));

        int y = 80;

        // Draw each line separately
        for(String line : shown.split("\n")){

            g.drawString(line, 40, y);

            y += 25;
        }

        // Draw blinking start message
        if(state == 3 && showPressSpace){

            g.setFont(new Font("Monospaced", Font.BOLD, 20));

            g.drawString("PRESS SPACE TO JACK IN", 120, 580);
        }
    }

    // Called when a key is pressed
    public void keyPressed(java.awt.event.KeyEvent e){

        int k = e.getKeyCode();

        // Start game when SPACE is pressed
        if(state == 3 && k == KeyEvent.VK_SPACE){

            startGame();
        }
    }

    // Not used
    public void keyReleased(java.awt.event.KeyEvent e){}

    // Not used
    public void keyTyped(java.awt.event.KeyEvent e){}
}
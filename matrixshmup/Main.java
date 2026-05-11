package matrixshmup;

import javax.swing.JFrame;

/*
 Main class of the game.
 The program starts here.
*/

public class Main {

    public static void main(String[] args) {

        // Create the main game window
        JFrame window = new JFrame(
                "01001101 01100001 01110100 01110010 01101001 01111000 00100000 01010011 01101000 01101101 01110101 01110000"
        );

        // Set window size
        window.setSize(600, 800);

        // Close program when the window is closed
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable resizing
        window.setResizable(false);

        // Create the menu screen
        MenuPanel menu = new MenuPanel(window);

        // Show the menu inside the window
        window.setContentPane(menu);

        // Make the window visible
        window.setVisible(true);

        // Start menu animation
        menu.startTyping();
    }
}
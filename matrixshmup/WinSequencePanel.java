package matrixshmup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

/*
 Win screen shown after the player defeats the boss.

 The screen presents two “trick” buttons.
 They move away from the mouse a few times before they can be clicked.

 After both are accepted, a video is played and the game closes.
*/

public class WinSequencePanel extends JPanel {

    // Main window reference
    JFrame parentFrame;

    // First choice button
    JButton firstButton;

    // Second choice button
    JButton secondButton;

    // How many times each button can escape before it becomes clickable
    int[] firstEscapesLeft = { 3 };
    int[] secondEscapesLeft = { 3 };

    // Title text
    JLabel titleLabel;

    // Used to detect if mouse is already near a button
    boolean wasNearFirstButton = false;
    boolean wasNearSecondButton = false;

    // Creates win screen
    public WinSequencePanel(JFrame parentFrame){

        this.parentFrame = parentFrame;

        setLayout(null);
        setBackground(Color.BLACK);

        // Title text
        titleLabel = new JLabel("One last choice, Neo...");
        titleLabel.setForeground(new Color(120, 255, 120));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        titleLabel.setBounds(120, 120, 400, 30);
        add(titleLabel);

        // First button
        firstButton = new JButton("Are you the one?");
        styleGreenButton(firstButton);
        firstButton.setBounds(200, 350, 200, 45);
        firstButton.addActionListener(e -> onFirstButtonAction());
        add(firstButton);

        // Second button (hidden at start)
        secondButton = new JButton("Are you sure?");
        styleGreenButton(secondButton);
        secondButton.setBounds(200, 350, 200, 45);
        secondButton.setVisible(false);
        secondButton.addActionListener(e -> onSecondButtonAction());
        add(secondButton);

        // Mouse movement detection (for “button runs away” effect)
        addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseMoved(MouseEvent e){

                Point p = e.getPoint();

                // First button behavior
                if(firstButton.isVisible()){

                    boolean near = isNearButton(firstButton, p);

                    // If mouse just entered danger zone
                    if(near && !wasNearFirstButton && firstEscapesLeft[0] > 0){

                        firstEscapesLeft[0]--;

                        // Move button away
                        moveButtonToRandomSpot(firstButton);
                    }

                    wasNearFirstButton = near;

                }else{
                    wasNearFirstButton = false;
                }

                // Second button behavior
                if(secondButton.isVisible()){

                    boolean near2 = isNearButton(secondButton, p);

                    if(near2 && !wasNearSecondButton && secondEscapesLeft[0] > 0){

                        secondEscapesLeft[0]--;

                        moveButtonToRandomSpot(secondButton);
                    }

                    wasNearSecondButton = near2;

                }else{
                    wasNearSecondButton = false;
                }
            }
        });

        setFocusable(true);
    }

    // Styles buttons in green Matrix theme
    void styleGreenButton(JButton b){

        b.setBackground(new Color(20, 80, 20));
        b.setForeground(new Color(120, 255, 120));
        b.setFocusPainted(false);
        b.setFont(new Font("Monospaced", Font.BOLD, 14));
    }

    // Checks if mouse is close to a button
    boolean isNearButton(JButton button, Point mouseOnPanel){

        Rectangle r = button.getBounds();

        // Extra area around button (danger zone)
        int pad = 70;

        Rectangle danger = new Rectangle(
                r.x - pad,
                r.y - pad,
                r.width + 2 * pad,
                r.height + 2 * pad
        );

        return danger.contains(mouseOnPanel);
    }

    // Moves button to a random position on screen
    void moveButtonToRandomSpot(JButton button){

        int w = button.getWidth();
        int h = button.getHeight();

        int maxX = Math.max(20, getWidth() - w - 20);
        int maxY = Math.max(20, getHeight() - h - 20);

        int nx = 20 + (int)(Math.random() * maxX);
        int ny = 20 + (int)(Math.random() * maxY);

        button.setLocation(nx, ny);
    }

    // First button click logic
    void onFirstButtonAction(){

        // If still escapes left, it runs away again
        if(firstEscapesLeft[0] > 0){

            firstEscapesLeft[0]--;

            moveButtonToRandomSpot(firstButton);

            return;
        }

        // Otherwise move to second button
        firstButton.setVisible(false);

        secondEscapesLeft[0] = 3;

        secondButton.setBounds(200, 350, 200, 45);
        secondButton.setVisible(true);
    }

    // Second button click logic
    void onSecondButtonAction(){

        // Still escaping
        if(secondEscapesLeft[0] > 0){

            secondEscapesLeft[0]--;

            moveButtonToRandomSpot(secondButton);

            return;
        }

        // Final action
        playVideoThenExit();
    }

    /*
     Plays a video file and then closes the game.
     Uses external media player.
    */
    void playVideoThenExit(){

        // Path to video file
        File video = new File(
                System.getProperty("user.dir"),
                "src/videos/click.mp4"
        );

        // If video is missing
        if(!video.exists()){

            JOptionPane.showMessageDialog(
                    this,
                    "Video not found:\n" + video.getAbsolutePath() +
                            "\n\nPut click.mp4 in src/videos/",
                    "Missing video",
                    JOptionPane.WARNING_MESSAGE
            );

            System.exit(0);
            return;
        }

        // Run video in background thread
        new SwingWorker<Void, Void>(){

            @Override
            protected Void doInBackground(){

                boolean played = false;

                // Try Windows PowerShell method first
                try{

                    String path = video.getAbsolutePath().replace("'", "''");

                    ProcessBuilder pb = new ProcessBuilder(
                            "powershell.exe",
                            "-NoProfile",
                            "-Command",
                            "Start-Process -FilePath '" + path + "' -Wait"
                    );

                    Process p = pb.start();
                    p.waitFor();

                    played = true;

                }catch(Exception ignored){
                    // If it fails, try fallback
                }

                // Fallback method using Desktop API
                if(!played){

                    try{

                        if(Desktop.isDesktopSupported()){

                            Desktop.getDesktop().open(video);

                            // Wait so user can watch video
                            Thread.sleep(15000);
                        }

                    }catch(Exception ignored2){
                    }
                }

                return null;
            }

            @Override
            protected void done(){

                // Exit game after video ends
                System.exit(0);
            }

        }.execute();
    }
}
package matrixshmup;

import java.awt.*;

/*
 Helper class with small utility functions.
*/

public class Utils {

    /*
     Checks if two objects touch each other.
     Used for collision detection.
    */
    public static boolean hit(Rectangle a, Rectangle b){

        // Returns true if rectangles overlap
        return a.intersects(b);
    }
}
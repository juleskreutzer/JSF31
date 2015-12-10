/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author juleskreutzer
 */
public class Obser implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        // Cast arguments to Edge object
        Edge edge = (Edge)arg;
        
        // Print coordinates
        System.out.print("Start: (" + edge.X1 + "," + edge.Y1 + "), End: (" + edge.X2 + "," + edge.Y2 + ")");
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import calculate.KochManager.GeneratePart;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author juleskreutzer
 */
public class Runner implements Runnable {
    
    final KochFractal koch;
    final KochManager manager;
    final GeneratePart part;
    
    public Runner(int level,KochManager manager, GeneratePart part)
    {
        koch = new KochFractal();
        koch.setLevel(level);
        this.manager = manager;
        this.part = part;
    }
    
    @Override
    public void run() {
        try {
            if(Thread.interrupted())
                throw new InterruptedException("Oops.. Thread interrupted");
            
            switch(part)
            {
                case BOTTOM:
                    koch.generateBottomEdge();
                    break;
                case RIGHT:
                    koch.generateRightEdge();
                    break;
                case LEFT:
                    koch.generateLeftEdge();
                    break;
                default:
                    throw new Exception("Hmm.. Switch screwed up!");
            }
            synchronized(manager) {
            if(manager.addCount() >= 3)
                manager.notify();
            } 
        }
        catch(Exception e)
        {
            System.out.print(e.toString());
        }
    }
    
}

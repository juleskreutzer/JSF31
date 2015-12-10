/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import calculate.KochManager.GeneratePart;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import javafx.concurrent.Task;

/**
 *
 * @author juleskreutzer
 */

//Not used anymore, has been replaced by calcTask
public class Runner extends Task<List<Edge>> implements Observer {
    
    final KochFractal koch;
    final GeneratePart part;
    final CyclicBarrier barrier;
    private List<Edge> edges;
    
    public Runner(int level, GeneratePart part, CyclicBarrier barrier)
    {
        this.koch = new KochFractal();
        this.koch.setLevel(level);
        this.koch.addObserver((Observer) this);
        this.edges = new ArrayList<>();
        this.part = part;
        this.barrier = barrier;
    }
    
    

    @Override
    public void update(Observable o, Object arg) {
        synchronized(this)
        {
            edges.add((Edge)arg);
        }
    }

    @Override
    public List<Edge> call() throws Exception {
        try{
            if(Thread.interrupted())
                System.out.print("Thread screwed up!");
            
            switch(part)
            {
                case BOTTOM:
                    koch.generateBottomEdge();
                    break;
                case LEFT:
                    koch.generateLeftEdge();
                    break;
                case RIGHT:
                    koch.generateRightEdge();
                    break;
                default:
                    throw new Exception("Which edge did you try?");
            }
            
            barrier.await();
        }
        catch(InterruptedException ex)
        {
            System.out.print(ex.toString());
        }
        catch(Exception ex)
        {
            System.out.print(ex.toString());
        }
        
        return edges;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author juleskreutzer
 */
public class KochManager implements Observer {

    private JSF31KochFractalFX mContext;
    private List<Edge> edgeList;
    private int count;
    private KochFractal koch;
    private KochFractal koch1;
    private KochFractal koch2;
    private KochFractal koch3;
    //private List<Thread> threads;
    private CyclicBarrier barrier;
    private ExecutorService executorService;
    
    
    public KochManager(JSF31KochFractalFX mContext)
    {
        this.mContext = mContext;
        edgeList = new ArrayList<>();
        //threads = new ArrayList<>();
        
        this.koch = new KochFractal();
        this.koch.addObserver(this);
                
        this.count = 0;
        this.barrier = new CyclicBarrier(3);
        this.executorService = Executors.newFixedThreadPool(3);
    }
    
    public void ChangeLevel(int nextLevel)
    {
        //clearAllThreads();
        mContext.clearKochPanel();
        koch.setLevel(nextLevel);
        
        this.edgeList.clear();
 
        // new threads
        final Future<List<Edge>> bottom = executorService.submit(new Runner(koch.getLevel(), GeneratePart.BOTTOM, barrier));
        final Future<List<Edge>> left = executorService.submit(new Runner(koch.getLevel(), GeneratePart.LEFT, barrier));
        final Future<List<Edge>> right = executorService.submit(new Runner(koch.getLevel(), GeneratePart.RIGHT, barrier));
        
        Thread end = new Thread(new Runnable() {
            @Override public void run() {
                synchronized(getCurrentInstance())
                {
                    try {
                        final TimeStamp ts = new TimeStamp();
                        ts.setBegin();
                        edgeList.addAll(bottom.get());
                        edgeList.addAll(left.get());
                        edgeList.addAll(right.get());
                        ts.setEnd();
                        
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                mContext.setTextCalc(ts.toString());
                            }
                        });
                        mContext.requestDrawEdges();
                    }
                    catch(Exception ex)
                    {
                        System.out.print(ex.toString());
                    }
                }
            }
        });
        end.start();
    }
    
    public void drawEdges()
    {
        
            mContext.clearKochPanel();
            
            TimeStamp timer = new TimeStamp();
            timer.setBegin();
            
            for(Edge edge : edgeList)
            {
                mContext.drawEdge(edge);
            }
            timer.setEnd();
            mContext.setTextDraw(timer.toString());
            mContext.setTextNrEdges(String.format("%d", this.koch.getNrOfEdges()));
        
    }

    @Override
    public void update(Observable o, Object arg) {
//        Edge edge = (Edge) arg;
//        edgeList.add(edge);
//        System.out.print("Start: (" + edge.X1 + "," + edge.Y1 + "), End: (" + edge.X2 + "," + edge.Y2 + ")");
        synchronized(this) {
            this.edgeList.add((Edge)arg);
            
        }

    }
    
    /**
     * Let all threads disappear - DIE!
    
    public synchronized void clearAllThreads()
    {
        for(Thread t : threads)
        {
            t.interrupt();
        }
        
        threads.clear();
    }
    * */
    
    /**
     * Set count back to 0
    */
    public synchronized void resetCount()
    {
        this.count = 0;
    }
    
    /**
     * Increase count by 1
     * @return current count
    */
    public synchronized int addCount()
    {
        this.count++;
        return this.count;
    }
    
    /**
     * Get the current instance
     * @return current KochManager instance
     */
    public synchronized KochManager getCurrentInstance()
    {
        return this;
    }
    
    /**
     * To make things penuts
     */
    public static enum GeneratePart
    {
        BOTTOM,
        LEFT,
        RIGHT;
    }
    
}

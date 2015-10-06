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
    
    private KochFractal koch;
    private KochFractal koch1;
    private KochFractal koch2;
    private KochFractal koch3;
    private int count;
    private List<Thread> threads;
    
    public KochManager(JSF31KochFractalFX mContext)
    {
        this.mContext = mContext;
        edgeList = new ArrayList<>();
        threads = new ArrayList<>();
        
        this.koch = new KochFractal();
        this.koch.addObserver(this);
        this.koch1 = new KochFractal();
        this.koch1.addObserver(this);
        this.koch2 = new KochFractal();
        this.koch2.addObserver(this);
        this.koch3 = new KochFractal();
        this.koch3.addObserver(this);
        this.count = 0;
        
        
    }
    
    public void ChangeLevel(int nextLevel)
    {
        clearAllThreads();
        resetCount();
        koch.setLevel(nextLevel);
        koch1.setLevel(nextLevel);
        koch2.setLevel(nextLevel);
        koch3.setLevel(nextLevel);
        
        this.edgeList.clear();
 
        
        Thread end = new Thread(new Runnable() {
            @Override public void run() {
                synchronized(getCurrentInstance())
                {
                    try {
                        final TimeStamp ts = new TimeStamp();
                        ts.setBegin();
                        getCurrentInstance().wait();
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
        
        threads.add(end);
        
        Thread t1 = new Thread(new Runner(koch1, this, GeneratePart.BOTTOM));
        threads.add(t1);
        Thread t2 = new Thread(new Runner(koch2, this, GeneratePart.LEFT));
        threads.add(t2);
        Thread t3 = new Thread(new Runner(koch3, this, GeneratePart.RIGHT));
        threads.add(t3);
        
        end.start();
        t1.start();
        t2.start();
        t3.start();
    }
    
    public void drawEdges()
    {
        if(count >= 3)
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
     */
    public synchronized void clearAllThreads()
    {
        for(Thread t : threads)
        {
            t.interrupt();
        }
        
        threads.clear();
    }
    
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

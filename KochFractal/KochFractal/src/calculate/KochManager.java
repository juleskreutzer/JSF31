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
import javafx.concurrent.Task;
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
    //Three tasks for the edges
    private Task taskLeft = null;
    private Task taskBottom = null;
    private Task taskRight = null;
    TimeStamp tsDraw = new TimeStamp();
    
    
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
    
    public int getLevel() {
        return koch.getLevel();
    }
    
    public void ChangeLevel(int nextLevel)
    {
        //clearAllThreads();
        mContext.clearKochPanel();
        koch.setLevel(nextLevel);
        
        this.edgeList.clear();
 
        // new threads
//        final Task<List<Edge>> bottom = executorService.submit(new Runner(koch.getLevel(), GeneratePart.BOTTOM, barrier));
//        final Future<List<Edge>> left = executorService.submit(new Runner(koch.getLevel(), GeneratePart.LEFT, barrier));
//        final Future<List<Edge>> right = executorService.submit(new Runner(koch.getLevel(), GeneratePart.RIGHT, barrier));
        
        TimeStamp ts = new TimeStamp();
        ts.setBegin();
        
        if (taskLeft != null && taskRight != null && taskBottom != null)
        {
            taskLeft.cancel();
            taskRight.cancel();
            taskBottom.cancel();
        }
        
        createAllTasks();
        tsDraw.setBegin();
        startTasks();


        ts.setEnd();
        mContext.setTextCalc(ts.toString());
    }
    
    /**
     * Three tasks have been created, are now started.
     */
    public void startTasks() {
        Thread thLeft = new Thread(taskLeft);
        Thread thRight = new Thread(taskRight);
        Thread thBottom = new Thread(taskBottom);

        executorService.submit(thLeft);
        executorService.submit(thRight);
        executorService.submit(thBottom);


    }
    
    /**
     * Create three tasks, and in darkness bind them.
     */
    public void createAllTasks()
    {
        if (taskLeft != null) {
            mContext.getProgressBarLeft().progressProperty().unbind();
            mContext.getLblLeftCalc().textProperty().unbind();
        }
        if (taskRight != null) {
            mContext.getProgressBarRight().progressProperty().unbind();
            mContext.getLblRightCalc().textProperty().unbind();
        }
        if (taskBottom != null) {
            mContext.getProgressBarBottom().progressProperty().unbind();
            mContext.getLblBottomCalc().textProperty().unbind();
        }

        taskLeft = new calcTask("Left", this);
        taskRight = new calcTask("Right", this);
        taskBottom = new calcTask("Bottom", this);

        mContext.getProgressBarLeft().setProgress(0);
        mContext.getProgressBarLeft().progressProperty().bind(taskLeft.progressProperty());
        mContext.getLblLeftCalc().textProperty().bind(taskLeft.messageProperty());

        mContext.getProgressBarRight().setProgress(0);
        mContext.getProgressBarRight().progressProperty().bind(taskRight.progressProperty());
        mContext.getLblRightCalc().textProperty().bind(taskRight.messageProperty());

        mContext.getProgressBarBottom().setProgress(0);
        mContext.getProgressBarBottom().progressProperty().bind(taskBottom.progressProperty());
        mContext.getLblBottomCalc().textProperty().bind(taskBottom.messageProperty());
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
    
    public void drawEdge(Edge e) {
        mContext.drawWhiteEdge(e);
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
    
    public Task getTaskLeft() {
        return taskLeft;
    }
    public Task getTaskRight() {
        return taskRight;
    }
    public Task getTaskBottom() {
        return taskBottom;
    }
    
    public synchronized void signalEnd() {
        count++;
        if (count >= 3) {
            mContext.requestDrawEdges();
            count = 0;
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
    
    public synchronized void addEdge(Edge e) {
        edgeList.add(e);
    }
    
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

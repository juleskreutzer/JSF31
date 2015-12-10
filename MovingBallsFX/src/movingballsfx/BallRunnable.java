/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package movingballsfx;

import javafx.scene.paint.Color;

/**
 *
 * @author Peter Boots
 */
public class BallRunnable implements Runnable {

    private Ball ball;
    private RWMonitor monitor;
    private boolean stop = false;

    public BallRunnable(Ball ball, RWMonitor monitor) {
        this.ball = ball;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                
                if(ball.isEnteringCs())
                {
                    if(ball.getColor() == Color.BLUE)
                    {
                        monitor.enterWriter();
                    }
                    else
                    {
                        monitor.enterReader();
                    }
                }
                
                
                if(ball.isLeavingCs())
                {
                    if(ball.getColor() == Color.BLUE)
                    {
                        monitor.exitWriter();
                    }
                    else
                        monitor.exitReader();
                }                    
                ball.move();
                Thread.sleep(ball.getSpeed());
                
                
            } catch (InterruptedException ex) {
                Thread.currentThread().isInterrupted();
            }
        }
    }
}

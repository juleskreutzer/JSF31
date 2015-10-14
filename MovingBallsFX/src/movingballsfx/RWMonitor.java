/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movingballsfx;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author juleskreutzer
 */
public class RWMonitor {
    private int readersActive;
    private int writersActive;
    private int readersWaiting;
    private int writersWaiting;
    Lock monLock = new ReentrantLock();
    Condition okToRead = monLock.newCondition();
    Condition okToWrite = monLock.newCondition();


    
    public RWMonitor(){
        readersActive = 0;
        writersActive = 0;
        readersWaiting = 0;
    }
    
    public void enterReader() throws InterruptedException{
        monLock.lock();
        try {
            while (writersActive > 0){
            readersWaiting++;
            okToRead.await(); 
            readersWaiting--;
        }    
            readersActive++;
        }  
        finally {
            monLock.unlock();  
        }
    }
    public void exitReader() {
        monLock.lock();
        try{
            readersActive--;
            if (readersActive == 0){
                okToWrite.signal();
            }
        }
        finally{
            monLock.unlock();
        }
    }
    public void enterWriter() throws InterruptedException{
        monLock.lock();
        try{
            while (writersActive > 0 || readersActive > 0)
            {
                writersWaiting++;
                okToWrite.await();
                writersWaiting--;
            }
            writersActive++;
        }
        finally{
            monLock.unlock();
        }
    }
    
    public void exitWriter(){
        monLock.lock();
        try{
            writersActive--;
            if (writersWaiting > 0) okToWrite.signal();
            else okToRead.signal();
        }
        finally{
            monLock.unlock();
        }
    }
}

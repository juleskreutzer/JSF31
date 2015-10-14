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
    private int activeReaders;
    private int activeWriters;
    private int waitingWriters;
    private int waitingReaders;
    private Lock monLock = new ReentrantLock();
    private Condition okToRead = monLock.newCondition();
    private Condition okToWrite = monLock.newCondition();
    
    public RWMonitor()
    {
        activeReaders = 0;
        activeWriters = 0;
        waitingWriters = 0;
        waitingReaders = 0;
        
    }
    
    public void enterReader() throws InterruptedException
    {
        monLock.lock();
        try{
            while(activeWriters > 0)
            {
                waitingReaders++;
                okToRead.await();
                waitingReaders--;
            }
            activeReaders++;
        }
        finally{
            monLock.unlock();
        }
    }
    
    public void exitReader()
    {
        monLock.lock();
        try{
            activeReaders--;
            while(activeReaders == 0)
            {
                okToWrite.signal();
            }
        }
        finally
        {
            monLock.unlock();
        }
    }
    
    public void enterWriter() throws InterruptedException
    {
        monLock.lock();
        try{
            while(activeWriters > 0 || activeReaders > 0)
            {
                waitingWriters++;
                okToWrite.await();
                waitingWriters--;
            }
            activeWriters++;
        }
        finally
        {
            monLock.unlock();
        }
    }
    
    public void exitWriter()
    {
        monLock.lock();
        try{
            activeWriters--;
            if(waitingWriters > 0)
            {
                okToRead.signal();
            }
            else
            {
                okToWrite.signal();
            }
        }
        finally
        {
            monLock.unlock();
        }
    }
}

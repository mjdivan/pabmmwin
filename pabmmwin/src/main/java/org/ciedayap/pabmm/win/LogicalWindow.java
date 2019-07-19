/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.ciedayap.utils.StringUtils;

/**
 * It keeps as a queue of Strings the arrived measures under CINCAMI/MIS
 * @author Mario Diván
 * @version 1.0
 */
public class LogicalWindow extends ObservableTarget{
    /**
     * The max limit related to the CINCAMI/MIS queue
     */
    private final Long maxQueueSize;
    /**
     * The queue of CINCAMIMIS messages as a String
     */
    private final ConcurrentLinkedQueue<String> queue;
    
    /**
     * Default constructor
     */
    public LogicalWindow()
    {
        maxQueueSize=1000L;
        queue=new ConcurrentLinkedQueue();
    }
    
    /**
     * Constructor which allow defining the max queue and project sizes
     * @param queueSize The max queue size
     */
    public LogicalWindow(Long queueSize)
    {
        maxQueueSize=(queueSize==null || queueSize<1000)?1000:queueSize;
        queue=new ConcurrentLinkedQueue();        
    }
    
    /**
     * It creates a new instance of LogicalWindow
     * @return The new instance ready to be used
     */
    public synchronized static LogicalWindow create()
    {
        return new LogicalWindow();
    }

    /**
     * It creates the new instance of LogicalWindow with a given queue size.
     * @param queueSize The max queue size    
     * @return A new instance able to manage the given number of projects using a queue limited by queueSize.
     */
    public synchronized static LogicalWindow create(Long queueSize)
    {
        return new LogicalWindow(queueSize);
    }    

    /**
     * @return the maxQueueSize
     */
    public Long getMaxQueueSize() {
        return maxQueueSize;
    }

    /**
     * Add a new CINCAMI/MIS message in the queue organized under JSON or XML data formats.
     * The queue is circular. It implies that when the max limit is reached, a new message
     * will involve discarding the oldest element for incorporating the new one.
     * @param cincamimis The CINCAMI/MIS message organized under XML or JSON data formats
     * @return TRUE when the message was incorporated to the queue to be processed, FALSE when it was discarded.
     */
    public synchronized boolean add(String cincamimis)
    {
        if(StringUtils.isEmpty(cincamimis)) {
            System.out.println("Cincamimis null");
            return false;
        }
        
        if((queue.size()+1)>maxQueueSize)
        {
            queue.poll();
        }
        
        boolean rdo=queue.add(cincamimis);
        if(rdo)
        {
            super.setChanged();//It is very important!!!
            String toNotify=cincamimis;
            super.notifyObservers(toNotify);
        }
        
        return rdo;            
    }
    
    /**
     * It provides the current queue's head (the oldest element) when it is available.
     * @return The oldest element in the queue, null when there not exist element in the queue.
     */
    public synchronized String next()
    {
        if(queue==null) return null;
        
        return queue.poll();
    }
    
    /**
     * It removes all elements in the ArrayList.
     */
    public synchronized void clear()
    {
        if(queue!=null)
        {
            queue.clear();
        }
    }
}

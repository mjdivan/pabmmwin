/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ciedayap.cincamimis.Cincamimis;
import org.ciedayap.cincamimis.Measurement;
import org.ciedayap.cincamimis.MeasurementItem;
import org.ciedayap.utils.StringUtils;
import org.ciedayap.utils.TranslateJSON;
import org.ciedayap.utils.TranslateXML;

/**
 * It is responsible for registering and monitoring on each Logical Window.
 * From each Registered logical Window, the measures will be provided
 * to the ProjectMonitor Instance.
 * @author Mario Diván
 */
public class WindowManager implements Runnable{
    private final ProjectMonitor monitor;
    private final ArrayList<LogicalWindow> queues;
    private boolean receiving;
    private boolean shutdown;
    private long sequence;

    public WindowManager(ProjectMonitor m,ArrayList<LogicalWindow> q) throws PAbMMWindowException
    {
        if(q==null) throw new PAbMMWindowException("There are not local windows to be inspected");
        if(m==null) throw new PAbMMWindowException("There is not available a ProjectMonitor instance");
        
        queues=q;
        monitor=m;
        receiving=true;
        shutdown=false;
        sequence=0;
    }
    
    /**
     * A default factory method. It creates a ProjectMonitor instance able to monitor 10 projects, while
     * the QueuesManager is able to manage 10 queue.
     * @return A new instance
     * @throws PAbMMWindowException It is raised when the ProjectMonitor instance or Queues could not be created.
     */
    public static synchronized WindowManager create() throws PAbMMWindowException
    {
        return new WindowManager(ProjectMonitor.create(10),new ArrayList(10));
    }
    
    /**
     * It creates a WindowManager able to manage "capacity" projects, ans monitoring
     * a set of "queueSize" queues.
     * @param capacity The initial capacity relateed to the projects
     * @param queueSize The initial number of queues to be monitored
     * @return A new instance
     * @throws PAbMMWindowException It is raised when the ProjectMonitor instance or Queues could not be created.
     */
    public static synchronized WindowManager create(int capacity,int queueSize) throws PAbMMWindowException
    {
        int qs=(queueSize<5)?5:queueSize;
        return new WindowManager(ProjectMonitor.create(capacity),new ArrayList(qs));
    }
    
    /**
     * It creates a new instance of WindowManager giving the ProjectMonitor instance jointly with the queues to be monitored.
     * @param pm The projecMonitor instance to keep the data in memory
     * @param pq The queues to be monitored
     * @return A new instance with the given ProjectMonitor and Queues
     * @throws PAbMMWindowException It is raised when the ProjectMonitor or Queues instance is null
     */
    public static synchronized WindowManager create(ProjectMonitor pm, ArrayList<LogicalWindow> pq) throws PAbMMWindowException
    {
        if(pm==null || pq==null) throw new PAbMMWindowException("The ProjectMonitor or Queues instances are null");
        
        return new WindowManager(pm,pq);
    }

    /**
     * @return the monitor
     */
    public ProjectMonitor getMonitor() {
        return monitor;
    }

    /**
     * @return the queues
     */
    public ArrayList<LogicalWindow> getQueues() {
        return queues;
    }

    /**
     * @return the receiving
     */
    public boolean isReceiving() {
        return receiving;
    }

    /**
     * @param receiving the receiving to set
     */
    public void setReceiving(boolean receiving) {
        this.receiving = receiving;
    }

    /**
     * @return the shutdown
     */
    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * @param shutdown the shutdown to set
     */
    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
    
    /**
     * It adds a new LogicalWindow instance to the set of Logical Windows to be monitored
     * @param lw The logical window to be added
     * @return TRUE when the logical window was added, FALSE otherwise.
     */
    public synchronized boolean addLogicalWindow(LogicalWindow lw)
    {
        if(lw==null) return false;
        
        return this.queues.add(lw);
    }

    /**
     * It removes the window from the queue
     * @param lw The logical window to be deleted
     * @return TRUE when the logical window is adequately removed, FALSE otherwise.
     */
    public synchronized boolean removeLogicalWindow(LogicalWindow lw)
    {
        if(lw==null) return false;
        
        return this.queues.remove(lw);
    }
    
    /**
     * It ends the each LogicalWindow in the queue and then, the queue is cleaned. 
     */
    public void shutdown()
    {
        if(!shutdown) return;
        if(queues!=null)
        {
            queues.stream().forEach(LogicalWindow::clear);            
            queues.clear();
        }
    }        
    
    @Override
    public void run() 
    {
        while(!isShutdown())
        {
            while(isReceiving())
            {
                String message=getNextMessage();
                if(message!=null)
                {System.out.println("Processing Message...");
                    Cincamimis obj;
                    if(message.contains("{"))
                    {//JSON
                        obj=(Cincamimis) TranslateJSON.toObject(Cincamimis.class, message);
                    }
                    else
                    {//XML
                        obj=(Cincamimis) TranslateXML.toObject(Cincamimis.class, message);
                    }
                    
                    if(obj!=null)
                    {//The message has been converted
                        System.out.println("Cincamimis translated...");
                        String maID=obj.getDsAdapterID();
                        ArrayList<MeasurementItem> mlist=obj.getMeasurements().getMeasurementItems();
                        mlist.forEach((MeasurementItem mi) -> 
                        {
                            String prjID=mi.getProjectID();
                            String ecID=mi.getEntityCategoryID(); 
                            String enID=mi.getIdEntity();
                            String dsID=mi.getDataSourceID();
                            //Context
                            if (mi.getContext()!=null) {
                                ArrayList<Measurement> cp_measures=mi.getContext().getMeasurements();
                                if(cp_measures!=null && !cp_measures.isEmpty())
                                {
                                    cp_measures.stream()
                                            .forEach(p->
                                            {
                                                
                                                String metricID=p.getIdMetric();
                                                
                                                MeasuringData md;
                                                try {
                                                    md = MeasuringData.create(p.getMeasure(), dsID, maID, this.incrementSequence(), p.getDatetime());
                                                    monitor.add(prjID, ecID, enID, metricID, md);
                                                        
                                                } catch (PAbMMWindowException ex)
                                                {
                                                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            });                                                                                
                                }
                            }
                            //Measures
                            if (mi.getMeasurement()!=null) {
                                String metricID=mi.getMeasurement().getIdMetric();
                                
                                MeasuringData md;
                                try
                                {
                                    md = MeasuringData.create(mi.getMeasurement().getMeasure(), dsID, maID, this.incrementSequence(), 
                                            mi.getMeasurement().getDatetime());
                                    monitor.add(prjID, ecID, enID, metricID, md);
                                } catch (PAbMMWindowException ex)
                                {
                                    Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
                                }                                                            
                            }
                        });
                    }else System.out.println("Discarded Message...");//else the message is discarded
                    
                }
            }

            try {
                java.util.concurrent.TimeUnit.MILLISECONDS.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WindowManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    /**
     * It seeks the first available message among the queues, and when it is found, it is returned. 
     * @return A String with the first available message among the queues. NULL when there is not available message.
     */
    protected synchronized String getNextMessage()
    {
        if(queues==null || queues.isEmpty()) return null;
        
        for(int i=0;i<queues.size();i++)
        {
            //It gets the head of the queue and will return it (It is removed from the logicalWindow)
            String message=queues.get(i).next();
            if(!StringUtils.isEmpty(message)) return message;
        }
        
        return null;
    }
    
    /**
     * It keeps a circular sequence number. When the sequence reaches the 
     * maximum value for a long data type, it restarts in zero.
     * @return The next value for the sequence;
     */
    protected synchronized Long incrementSequence()
    {
        try{
            sequence=Math.addExact(sequence, 1);
        }catch(Exception e)
        {
            sequence=0L;
        }
        
        return sequence;
    }
    
}

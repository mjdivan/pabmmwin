/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ciedayap.pabmm.pd.MeasurementProject;
import static org.ciedayap.pabmm.win.KeyUtils.ENTITY_ID;
import org.ciedayap.pabmm.win.layers.BasicColumnarMeasure;
import org.ciedayap.pabmm.win.layers.BasicColumnarMeasures;
import org.ciedayap.pabmm.win.layers.BasicDynamicTuple;
import org.ciedayap.utils.StringUtils;

/**
 * It is responsible for keeping in memory the project metadata, and its associated last state.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class ProjectMetadata extends ObservableTarget implements Runnable{
    private final MeasurementProject currentProjectDefinition;
    private final ZonedDateTime lastChanging;
    
    private final ConcurrentHashMap<String,CollectedMetricData> collectedMetricData;
    private final ConcurrentHashMap<String,MetricState> metricStates;      
    
    /**
     * The max queue size related to each metric
     */
    private final Long maxQueueSize;
    /**
     * the maximum time in milliseconds between the statistic updating.
     */
    private final Long sleepingTime;
    /**
     * Maximum number of threads by project
     */
    private final Short nThreadsByProject;
    /**
     * It determines the moment in which the active monitoring on the queues is enabled
     */
    private boolean enabledMonitoring;
    
    /**
     * Default Constructor
     * @param m The Measurement Project Definition to be associated.
     * @param mQueueSize The max limit related to the queue of each metric. It must be upper than 1000 (Default: 1000)
     * @param stime The sleepint time between statistic updating (default: 1000 ms)
     * @param nt Number of threads by each project
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the Project Definition is incomplete
     */
    public ProjectMetadata(MeasurementProject m,Long mQueueSize,Long stime,Short nt) throws PAbMMWindowException
    {
        if(!m.isDefinedProperties()) throw new PAbMMWindowException("The Measurement Project Definition is incomplete");
        if(mQueueSize==null || mQueueSize<1000L)
        {
            maxQueueSize=1000L;
        }
        else
        {
            maxQueueSize=mQueueSize;
        }
        
        if(stime==null || stime<1000)
        {
            sleepingTime=1000L;
        }
        else
        {
            sleepingTime=stime;
        }
        
        if(nt==null || nt<3)
        {
            nThreadsByProject=3;
        }
        else
        {
            nThreadsByProject=nt;
        }
        
        int att,ctxp;
        try{
            att=m.getInfneed().getSpecifiedEC().getDescribedBy().getCharacteristics().size();
            ctxp=m.getInfneed().getCharacterizedBy().getDescribedBy().getContextProperties().size();                   
        }catch(java.lang.NullPointerException npe)
        {
            throw new PAbMMWindowException("The Measurement Project Definition is incomplete. The number of attributes and context properties were not successfully obtained");
        }
        
        if(att<1 || ctxp<0) throw new PAbMMWindowException("The minimum number of attributes is 1");
        
        currentProjectDefinition=m;
        lastChanging=ZonedDateTime.now();
        
        collectedMetricData=new ConcurrentHashMap(att+ctxp);
        metricStates=new ConcurrentHashMap(att+ctxp);            
        
        enabledMonitoring=true;
    }
    
    /**
     * It is a default factory method
     * @param m The measurement project definition to be associated
     * @return A new instance
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the measurement project definition is incomplete
     */
    public synchronized static ProjectMetadata create(MeasurementProject m) throws PAbMMWindowException
    {
        return new ProjectMetadata(m,1000L,1000L,(short)3);
    }

    /**
     * @return the current Project Definition
     */
    public MeasurementProject getCurrentProjectDefinition() {
        return currentProjectDefinition;
    }

    /**
     * @return the last Changing of the definition
     */
    public ZonedDateTime getLastChanging() {
        return lastChanging;
    }
    
    /**
     * It add a new measure in the quere related to (Project, Entity Category, Entity, Metric).
     * If there not exists the queue, then it will be created and the measure added.
     * @param prjID The project ID
     * @param ecID The entity category ID
     * @param eID The entity ID
     * @param mID the metric ID
     * @param md The measuring data to be added
     * @return TRUE when the measure was added to the queue, FALSE otherwise.
     */
    public synchronized boolean add(String prjID,String ecID,String eID,String mID,MeasuringData md) 
    {
        if(StringUtils.isEmpty(prjID) || StringUtils.isEmpty(ecID) || 
                StringUtils.isEmpty(eID) || StringUtils.isEmpty(mID))
        {
            System.out.println("PRJID: "+prjID+" EC: "+ecID+" Ent: "+eID+" metric: "+mID);
            return false;
        }
        if(md==null || !md.isConsistent()){
            System.out.println((md==null)?"Null":"Not consistent");
            return false;
        }
        
       String key=KeyUtils.generateMetricKey(prjID, ecID, eID, mID);
       if(key==null) {
           System.out.println("KeyNull prj "+prjID+" ecid "+ ecID +" eid "+ eID +" mid "+ mID);
           return false;
       }
       
       if(!collectedMetricData.containsKey(key))
       {
           CollectedMetricData cmd;
           
           try{
               cmd=CollectedMetricData.create(prjID, ecID, eID, mID, this.maxQueueSize);
           }catch(PAbMMWindowException p)
           {
               System.out.println("Exception ProjectMetadata: "+p.getMessage());
               return false;
           }
           
           collectedMetricData.put(key, cmd);
       }       
       
       //Add the measuring in the queue
       boolean rdo=collectedMetricData.get(key).add(md);             
       
       if(rdo)
       {
            //It Notifies to the observers
           BasicColumnarMeasure bcm=BasicColumnarMeasure.createFullInformation(prjID, ecID, eID, mID, md);
           if(bcm==null) return rdo;
            
           super.setChanged();//It is very important!!!
           this.notifyObservers(bcm);
       }
       
       return rdo;
    }
    
    /**
     * It removes the measuring queue from memory
     * @param prjID The project ID
     * @param ecID The entity category ID
     * @param eID The entity ID
     * @param mID The metric ID
     * @return TRUE when the queue was removed, FALSE otherwise
     */
    public synchronized boolean remove(String prjID,String ecID,String eID,String mID)  
    {
        String key=KeyUtils.generateMetricKey(prjID, ecID, eID, mID);
        if(key==null) return false;

        CollectedMetricData md=this.collectedMetricData.remove(key);
        
        return (md!=null);
    }
    
    /**
     * @return the maxQueueSize
     */
    public Long getMaxQueueSize() {
        return maxQueueSize;
    }
    
    /**
     * It returns the number of queues available on the hashmap
     * @return The number of queues (0 when the hashmap is empty)
     */
    public int getNumberOfQueues()
    {
        if(collectedMetricData==null) return 0;
        
        return collectedMetricData.size();
    }

    /**
     * It generates a new instance of ConcurrentLinkedQueue containing the available metric keys at the moment in which it is invoked
     * @return A list with the available keys, NULL otherwise.
     */
    protected ConcurrentLinkedQueue getAvailableKeys()
    {
        if(getNumberOfQueues()==0) return null;
        
        java.util.concurrent.ConcurrentLinkedQueue mkeys=new java.util.concurrent.ConcurrentLinkedQueue();
        Enumeration<String> keys=this.collectedMetricData.keys();
        while(keys!=null && keys.hasMoreElements())
        {
            mkeys.offer(keys);
        }
        
        return mkeys;
    }
    
    /**
     * It gets the MetricStates instance from the HashMap
     * @param key The key to be reached
     * @return The MetricStates Instance when it is found, NULL otherwise
     */
    public MetricState getMetricState(String key)
    {
        if(metricStates==null) return null;
        
        return metricStates.get(key);
    }
    
    /**
     * Updates the MetricState instance in the StateHashMap
     * @param ms The new MetricState instance
     * @param key The corresponding key 
     * @return TRUE when the state has been updated, FALSE otherwise.
     */
    public boolean updateMetricState(MetricState ms, String key)
    {
        if(metricStates==null) return false;
        
        metricStates.put(key, ms);
        return true;        
    }       
    
    /**
     * It returns the set of available values in an ordered way
     * @param key The Key
     * @return A set of double values currently available.
     */
    public double[] currentValuesForMetric(String key)
    {
        if(key==null) return null;
        ArrayList<Double> values=null;
        
        CollectedMetricData queue= collectedMetricData.get(key);
        
        return queue.getOrderedMeasures();      
    }
    
    @Override
    public void run() {
        while(enabledMonitoring)
        {
            //Obtaining the Available Keys
            ConcurrentLinkedQueue availableKeys=getAvailableKeys();
            StatesWorker logic;
            try {
                //Create the instances for monitoring
                logic=StatesWorker.create(this, availableKeys);
            } catch (PAbMMWindowException ex) {
                Logger.getLogger(ProjectMetadata.class.getName()).log(Level.SEVERE, null, ex);
                logic=null;
            }
            
            /**
             * Updating the Statistics related to the Metrics
             */
            if(logic!=null && availableKeys!=null && !availableKeys.isEmpty())
            {
                //Fork in parallel based on the available metrics
                ExecutorService executor=Executors.newFixedThreadPool(nThreadsByProject);
                for(int i=0;i<nThreadsByProject;i++)
                { 
                    executor.execute(logic);
                }        

                while(!availableKeys.isEmpty())
                {
                    //Keep waiting up to the queue is empty
                }

                executor.shutdown();            

                while(!executor.isTerminated()){}   
            }
            
            /**
             * Updating the Project Information
             */
            
            
            try {
                TimeUnit.MILLISECONDS.sleep(sleepingTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProjectMetadata.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Stop the thread and clean all the data related to the metrics and statistics
     */
    public synchronized void clearAndStopAll()
    {
        //Finishing the Thread
        this.enabledMonitoring=false;
        
        //Cleaning each Metric related to the project
        Collection<CollectedMetricData> col=collectedMetricData.values();
        col.stream().forEach(CollectedMetricData::clear);

        //Clean the MetricStates
        metricStates.clear();
        
        //Clean the HashMap
        collectedMetricData.clear();        
    }
    
    /**
     * It surfs by each metric in the project and returns a row with the last known value for each metric
     * @param projectID The projectID
     * @param entityCategoryID The corresponding entityCategoryID
     * @param entityID The corresponding entityID
     * @return A tuple with the last known value for each metric
     */
    public BasicDynamicTuple getMostRecentTuple(String projectID,String entityCategoryID,String entityID)
    {
        if(collectedMetricData==null || collectedMetricData.isEmpty()) return null;
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || StringUtils.isEmpty(entityID)) return null;
        BasicDynamicTuple row=new BasicDynamicTuple();
        
        String searchfor=KeyUtils.generateSearchPatternUpTo(projectID,entityCategoryID,entityID,"m1",ENTITY_ID);//m1 is idle
        if(searchfor==null) return null;
        
        ArrayList<String> filteredKeys=new ArrayList();
        collectedMetricData.forEachKey(3, p -> {
            if(p.regionMatches(true, 0, searchfor, 0, searchfor.length())) filteredKeys.add(p);
            });

        if(filteredKeys==null || filteredKeys.isEmpty()) return null;
        
//It Incorporates each metric related to the searched pattern into the row
        for(int i=0; i<filteredKeys.size();i++)            
        {
            String xkey=filteredKeys.get(i);
            
                if(xkey!=null && collectedMetricData.get(xkey)!=null)
                {
                    row.add(collectedMetricData.get(xkey).getLastOne());
                }
        }
        
        return (row!=null && row.getNumberOfColumns()>0)?row:null;
    }
    
    /**
     * It returns the key managed by this ProjectMetadata instance
     * @return An enumeration including managed keys.
     */
    public Enumeration<String> managedKeys()
    {
        return collectedMetricData.keys();        
    }
    
    public BasicColumnarMeasure[] getCopiedRawData(String key)
    {
        CollectedMetricData item=collectedMetricData.get(key);
        
        return (item!=null)?item.getACopy():null;
    }

    public BasicColumnarMeasures getCopiedColumnarData(String key)
    {
        CollectedMetricData item=collectedMetricData.get(key);
        
        return (item!=null)?item.getColumnarCopy():null;
    }
}

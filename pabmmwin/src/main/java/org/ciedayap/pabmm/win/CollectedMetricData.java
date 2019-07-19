/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.ciedayap.cincamimis.Measure;
import org.ciedayap.pabmm.win.layers.BasicColumnarMeasure;
import org.ciedayap.pabmm.win.layers.BasicColumnarMeasures;
import org.ciedayap.pabmm.win.layers.BasicMeasure;
import org.ciedayap.utils.StringUtils;

/**
 * It contains the queue of measures for a given metric
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class CollectedMetricData extends ObservableTarget implements Serializable{
    /**
     * It cotains the measures in the insertion order
     */
    private ConcurrentLinkedQueue<MeasuringData> measures;
    /**
     * It identifies the project
     */
    private String projectID;
    /**
     * It identifies the related entity category (e.g. an outpatient)
     */
    private String entityCategoryID;
    /**
     * It identifies the entity belonging to an entity category (e.g. John, which is an outpatient)
     */
    private String entityID;
    /**
     * The metric ID related to the entity
     */
    private String metricID;
    /**
     * The max limit related to the number of measures for the metric
     */
    private final Long maxQueueSize;
    /**
     * It represents the accumulated number of measures received by this metric's queue
     */
    private Long accumulatedMeasures=0L;
    /**
     * It indicates the last time in which the accumulator was changed
     */
    private ZonedDateTime lastChangingAccumulator=null;
    /**
     * For reasons related to performance, it keeps in memory the last measure added to the queue
     */
    private BasicColumnarMeasure lastOne;
    
    /**
     * Default Constructor
     * 
     * @param pid The Measurement Project ID
     * @param ecid The Entity Category ID
     * @param eid The Entity ID
     * @param mid The Metric ID related to the measures to be kept in memory
     * @param max Max Queue Size
     * @throws PAbMMWindowException It is raised when some ID is not specified
     */
    public CollectedMetricData(String pid,String ecid,String eid,String mid,Long max) throws PAbMMWindowException
    {
        if(StringUtils.isEmpty(mid) || StringUtils.isEmpty(pid) ||
                StringUtils.isEmpty(eid) ||StringUtils.isEmpty(ecid))
        {
            throw new PAbMMWindowException("The IDs are incomplete or not defined");
        }
        
        if(max<0) throw new PAbMMWindowException("The max limit is invalid. It must be a positive value");
        
        this.projectID=pid;
        this.entityCategoryID=ecid;
        this.entityID=eid;
        this.metricID=mid;
        maxQueueSize=max;
        
        measures=new ConcurrentLinkedQueue();
    }
    
    /**
     * A basic factory method
     * @param pid The Measurement Project ID
     * @param ecid The Entity Category ID
     * @param eid The Entity ID
     * @param mid The Metric ID related to the measures to be kept in memory
     * @param max The max queue size
     * @return A new instance
     * @throws PAbMMWindowException It is raised when some ID is not specified
     */
    public static synchronized CollectedMetricData create(String pid,String ecid,String eid,String mid,Long max) throws PAbMMWindowException
    {
        return new CollectedMetricData(pid,ecid,eid,mid,max);
    }
    
    /**
     * It incorporates a measuring data in a Circular FIFO Queue. When the maxQueueSize is reached, each new element will be incorporated at the end
     * while the queue's head will be removed.
     * @param m The measure
     * @param sequence The sequence ID
     * @param measurementInstant The timestamp related to the measurement
     * @param dsID The data source ID
     * @param maID The measurement Adapter ID
     * @return TRUE when the new data could be incorporated in the CIRCULAR FIFO queue to be consumed. In such case, the observers are notified with 
     * a copy of the new measurement as an argument
     */
    public synchronized boolean add(Measure m,Long sequence,ZonedDateTime measurementInstant,String dsID, String maID)
    {
        if(m==null) {
            System.out.println("CollectedMetricData: Measure is null");
            return false;
        }
        if(m.getQuantitative()==null){
            System.out.println("CollectedMetricData: Quantitative is null");
            return false;
        }
        if(m.getQuantitative().getDeterministicValue()==null && m.getQuantitative().getLikelihoodDistribution()==null) 
        {
            System.out.println("CollectedMetricData: Not quantitative not likelihooddistribution");
            return false;
        }
        if(measures==null) 
        {
            System.out.println("CollectedMetricData: Measures (Array) is null");
            return false;
        }
        
        MeasuringData item;
        try{
            item=MeasuringData.create(m, dsID, maID, sequence, measurementInstant);
        }catch(PAbMMWindowException ex)
        {
            System.out.println("CollectedMetricData: Exception: "+ex.getMessage());
            return false;
        }
        
        boolean rdo;
        if((measures.size()-1)<this.getMaxQueueSize()) rdo = measures.offer(item);
        else
        {
            measures.poll();
            rdo=measures.offer(item);
        }
        
        if(rdo) 
        {
            //It increases the accumulator up to the Long's max limit
            try{
                this.accumulatedMeasures=Math.addExact(accumulatedMeasures, 1L);
            }catch(java.lang.ArithmeticException ar)
            {
                accumulatedMeasures=0L;
                lastChangingAccumulator=ZonedDateTime.now();
            }
            
           lastOne=BasicColumnarMeasure.createRawFullInformation(this.getProjectID(),
                   this.getEntityCategoryID(), this.getEntityID(), this.getMetricID(), 
                   measurementInstant,dsID,maID,m.getQuantitative());

           
            //It Notifies to thee observers
           BasicColumnarMeasure bcm=BasicColumnarMeasure.createRawFullInformation(this.getProjectID(),
                   this.getEntityCategoryID(), this.getEntityID(), this.getMetricID(), 
                   measurementInstant,dsID,maID,m.getQuantitative());
           if(bcm==null) return rdo;
            
           super.setChanged();//It is very important!!!
           this.notifyObservers(bcm);
        }
        
        return rdo;
    }
    
    /**
     * It incorporates a measuring data to the Circular FIFO Queue
     * @param md The measure to be incorporated
     * @return TRUE when the new data could be incorporatede in the CIRCULAR FIFO queue to be consumed. 
     */
    public synchronized boolean add(MeasuringData md)
    {
        if(md==null || !md.isConsistent()){
            System.out.println("ColleectedMetricData "+((md==null)?"md null":"md is inconsistent"));
            return false;
        }
        
        return add(md.getMeasure(),md.getSequence(),md.getMeasurementInstant(),md.getDataSourceID(),md.getMaID());
    }
    
    /**
     * It cleans all the measures in the queue, leaving in zero its size.
     * @return TRUE when the queue could be cleaned, FALSE otherwise
     */
    public synchronized boolean clear()
    {
        if(measures==null) return false;
        measures.clear();
        
        super.setChanged();//It is very important!!!
        this.notifyObservers();
        return true;
    }

    /**
     * @return the projectID
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * @param projectID the projectID to set
     */
    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    /**
     * @return the entityCategoryID
     */
    public String getEntityCategoryID() {
        return entityCategoryID;
    }

    /**
     * @param entityCategoryID the entityCategoryID to set
     */
    public void setEntityCategoryID(String entityCategoryID) {
        this.entityCategoryID = entityCategoryID;
    }

    /**
     * @return the entityID
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * @param entityID the entityID to set
     */
    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    /**
     * @return the metricID
     */
    public String getMetricID() {
        return metricID;
    }

    /**
     * @param metricID the metricID to set
     */
    public void setMetricID(String metricID) {
        this.metricID = metricID;
    }

    /**
     * @return the maxQueueSize
     */
    public Long getMaxQueueSize() {
        return maxQueueSize;
    }
    
    /**
     * It evaluates the intrinsic properties of the instance
     * @return TRUE when the instance has value for all their properties, FALSE otherwise
     */
    public boolean isConsistent()
    {
        return !(this.projectID==null || this.entityCategoryID==null || this.entityID==null || this.metricID==null 
                || maxQueueSize==null || measures==null);                
    }

    /**
     * @return It returns the total number of processed measures relative to the accumulator's last changing. In case
     * the accumulator's last changing would be NULL, it implies that Long's maximum value never was reached before.
     */
    public Long getAccumulatedMeasures() {
        return accumulatedMeasures;
    }

    /**
     * @return In case accumulator's overflow, it keeps the instant related to the last changing.
     */
    public ZonedDateTime getLastChangingAccumulator() {
        return this.lastChangingAccumulator;
    }
    
    /**
     * It returns a new Array ordered by value. It is a copy of the current values under a deterministic way.
     * The estimated values are converted using the mathematical expectation.
     * @return A list with the current values
     */
    public double[] getOrderedMeasures()
    {
        if(measures==null || measures.isEmpty()) return null;
        double[] toArray;
        toArray = measures.stream()
                .filter(MeasuringData::hasMeasuredValue)
                .mapToDouble(MeasuringData::getMeasuredValue)                
                .sorted()
                .toArray();
        
        return toArray;
    }

    /**
     * @return the lastOne
     */
    public BasicColumnarMeasure getLastOne() {
        return lastOne;
    }
    
    /**
     * It returns a safe copy of the list of measures in the proper order
     * @return The current list of measures 
     */
    public BasicColumnarMeasure[] getACopy()
    {
        Object current[]=measures.toArray();
        if(current==null || current.length==0) return null;
        
        BasicColumnarMeasure result[]=new BasicColumnarMeasure[current.length];
        
        for(int i=0;i<current.length;i++)
        {
            result[i]=BasicColumnarMeasure.createFullInformation(projectID, entityCategoryID, entityID, metricID, (MeasuringData)current[i]);
        }
     
        return result;
    }

    public BasicColumnarMeasures getColumnarCopy()
    {
        Object current[]=measures.toArray();
        if(current==null || current.length==0) return null;
                
        BasicColumnarMeasures bcm=BasicColumnarMeasures.createWithMetricInformation(projectID, entityCategoryID, entityID, metricID);
        
        for (Object current1 : current) {
            BasicMeasure bm = BasicMeasure.create((MeasuringData) current1);
            if(bm!=null) bcm.add(bm);
        }
     
        return bcm;
    }
    
}

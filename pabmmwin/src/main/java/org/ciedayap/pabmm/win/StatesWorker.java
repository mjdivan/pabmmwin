/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.ciedayap.utils.Statistic;

/**
 * This class is responsible for the statistic computing related to
 * each measure into a metric's queue.
 * 
 * @author Mario Diván
 */
public class StatesWorker implements Runnable{
    /**
     * The synchronized list which is updated for ProjectMetadata class for updating the metrics to inspect
     */
    private ConcurrentLinkedQueue<String> availableProjectKeys;
    /**
     * The object to be monitored
     */
    private ProjectMetadata monitored;
    /**
     * It keeps the threads inspecting the queue
     */
    private boolean enabled;
    
    /**
     * The default constructor related to the State Manager
     * @param pm The ProjectMetadata instance to be monitored
     * @param keys The keys to be inspected
     * @throws PAbMMWindowException An exception is raised when the ProjectMetadata instance is not defined.
     */
    public StatesWorker(ProjectMetadata pm,ConcurrentLinkedQueue keys) throws PAbMMWindowException
    {
        if(pm==null) throw new PAbMMWindowException("The ProjectMetadata instance is not defined");
        
        monitored=pm;
        availableProjectKeys=keys;
    }
        
    /**
     * Default Factory method related to 
     * @param pm The project to be monitored
     * @param keys The keys to be inspected
     * @return A new StatesWorker
     * @throws PAbMMWindowException It is raised when the ProjectMetadata instance is not defined.
     */
    public static synchronized StatesWorker create(ProjectMetadata pm,ConcurrentLinkedQueue keys) throws PAbMMWindowException
    {
        return new StatesWorker(pm,keys);
    } 
    
    @Override
    public void run() {
        while(availableProjectKeys!=null && !availableProjectKeys.isEmpty())
        {
            String key=availableProjectKeys.poll();//It Retrieves and removes the head from the queue
            
            if(key!=null)
            {
                MetricState current=monitored.getMetricState(key);
                MetricState nState;
                try{
                    nState=MetricState.create(current.getProjectID(), current.getEntityCategoryID(), current.getEntityID(), current.getMetricID());
                }catch(PAbMMWindowException e)
                {
                    nState=null;
                }
                
                double values[]=monitored.currentValuesForMetric(key);
                if(values!=null && nState!=null)
                {
                    //Computing the statistics
                    nState.setCount(new Long(values.length));
                    
                    //Max
                    OptionalDouble od=Arrays.stream(values).max();
                    if(od!=null && od.isPresent())
                        nState.setMax(BigDecimal.valueOf(od.getAsDouble()));
                    else
                        nState.setMax(null);
                    
                    //Min
                    od=Arrays.stream(values).min();
                    if(od!=null && od.isPresent())
                        nState.setMin(BigDecimal.valueOf(od.getAsDouble()));
                    else
                        nState.setMin(null);
                    
                    //AVG
                    od=Arrays.stream(values).average();
                    if(od!=null && od.isPresent())
                        nState.setMean(BigDecimal.valueOf(od.getAsDouble()));
                    else
                        nState.setMean(null);

                    //Sum
                    double sum=Arrays.stream(values).sum();
                    nState.setSum(new BigDecimal(sum));
                    
                    //Median & RIQ
                    double ret[]=Statistic.percentileQ1Q2Q3(values);
                    nState.setQ1(new BigDecimal(ret[0]));
                    nState.setMedian(new BigDecimal(ret[1]));
                    nState.setQ3(new BigDecimal(ret[2]));
                    nState.setRIQ(new BigDecimal(ret[2]-ret[0]));
                    
                    //sd
                    double sum2=Arrays.stream(values).map(x->x*x).sum();
                    double sd=Math.sqrt((sum2-(Math.pow(sum, 2)/values.length))/(values.length-1));
                    nState.setSd(new BigDecimal(sd));
                                        
                    monitored.updateMetricState(nState, key);//Update the Statistics on the metric using the current data in the queue
                }
            }
        }
    } 
}

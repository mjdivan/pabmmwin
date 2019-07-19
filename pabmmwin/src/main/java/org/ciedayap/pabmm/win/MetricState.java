/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.math.BigDecimal;
import org.ciedayap.utils.StringUtils;

/**
 * It keeps in memory the last known statistics related to each metric.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class MetricState {
    /**
     * It represents the project ID
     */
    private final String projectID;
    /**
     * It represents the entity category ID (e.g. an outpatient)
     */
    private final String entityCategoryID;
    /**
     * It represents the entity ID (e.g. John)
     */
    private final String entityID;
    /**
     * It represents the metric ID (e.g. Value of the corporal temperature).
     * The tetra-tuple (projectID, entityCategoryID, entityID, metricID) identifies specifically each metric along projects.
     */
    private final String metricID;
    /**
     * The max value reached between the available values
     */
    private BigDecimal max;
    /**
     * The min value reached between the available values
     */
    private BigDecimal min;
    /**
     * The current mean between the available values
     */
    private BigDecimal mean;
    /**
     * The current sum
     */
    private BigDecimal sum;
    /**
     * The current standard deviation
     */
    private BigDecimal sd;
    /**
     * First quartile
     */
    private BigDecimal Q1;
    /**
     * Third Quartile
     */
    private BigDecimal Q3;
    /**
     * The current interquartile range
     */
    private BigDecimal RIQ;
    /**
     * The current median
     */
    private BigDecimal median;
    /**
     * The current number of measures
     */
    private Long count;  
    
    /**
     * Default Constructor
     * @param pID The project ID
     * @param ecID The entity category ID
     * @param eID The entity ID
     * @param mID The metric ID
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the identificatory information is incomplete
     */
    public MetricState(String pID, String ecID, String eID, String mID) throws PAbMMWindowException
    {
        if(StringUtils.isEmpty(pID) || StringUtils.isEmpty(ecID) || StringUtils.isEmpty(eID) || StringUtils.isEmpty(mID))
            throw new PAbMMWindowException("The metric state instance must be the identificatory data");
        projectID=pID;
        entityCategoryID=ecID;
        entityID=eID;
        metricID=mID;
        max=min=mean=sum=sd=RIQ=median=Q1=Q3=BigDecimal.ZERO;
        count=0L;
    }
    
    /**
     * Default factory method
     * @param pID The project ID
     * @param ecID The entity category ID
     * @param eID The entity ID
     * @param mID The metric ID
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the identificatory information is incomplete
     * @return A new class's instance
     */
    public static synchronized MetricState create(String pID, String ecID, String eID, String mID) throws PAbMMWindowException
    {
        return new MetricState(pID,ecID,eID,mID);
    }

    /**
     * @return the projectID
     */
    public String getProjectID() {
        return projectID;
    }

    /**
     * @return the entityCategoryID
     */
    public String getEntityCategoryID() {
        return entityCategoryID;
    }

    /**
     * @return the entityID
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * @return the metricID
     */
    public String getMetricID() {
        return metricID;
    }

    /**
     * @return the max
     */
    public BigDecimal getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(BigDecimal max) {
        this.max = max;
    }

    /**
     * @return the min
     */
    public BigDecimal getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(BigDecimal min) {
        this.min = min;
    }

    /**
     * @return the mean
     */
    public BigDecimal getMean() {
        return mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(BigDecimal mean) {
        this.mean = mean;
    }

    /**
     * @return the sum
     */
    public BigDecimal getSum() {
        return sum;
    }

    /**
     * @param sum the sum to set
     */
    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    /**
     * @return the sd
     */
    public BigDecimal getSd() {
        return sd;
    }

    /**
     * @param sd the sd to set
     */
    public void setSd(BigDecimal sd) {
        this.sd = sd;
    }

    /**
     * @return the RIQ
     */
    public BigDecimal getRIQ() {
        return RIQ;
    }

    /**
     * @param RIQ the RIQ to set
     */
    public void setRIQ(BigDecimal RIQ) {
        this.RIQ = RIQ;
    }

    /**
     * @return the median
     */
    public BigDecimal getMedian() {
        return median;
    }

    /**
     * @param median the median to set
     */
    public void setMedian(BigDecimal median) {
        this.median = median;
    }

    /**
     * @return the count
     */
    public Long getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * @return the Q1
     */
    public BigDecimal getQ1() {
        return Q1;
    }

    /**
     * @param Q1 the Q1 to set
     */
    public void setQ1(BigDecimal Q1) {
        this.Q1 = Q1;
    }

    /**
     * @return the Q3
     */
    public BigDecimal getQ3() {
        return Q3;
    }

    /**
     * @param Q3 the Q3 to set
     */
    public void setQ3(BigDecimal Q3) {
        this.Q3 = Q3;
    }
}

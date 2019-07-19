/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.ciedayap.cincamimis.LikelihoodDistributionException;
import org.ciedayap.cincamimis.Measure;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.utils.QuantitativeUtils;
import org.ciedayap.utils.StringUtils;

/**
 * It represents the minimum information to keep maintained in memory for each metric
 * @author Mario Diván
 * @version 1.0
 */
public final class MeasuringData {
    /**
     * It is the measure itself, which could be deterministic or estimated.
     */
    private Measure measure;
    /**
     * It represents the device ID responsible for getting the measure
     */
    private String dataSourceID;
    /**
     * It represents the measurement adapter ID responsible for translating from the raw data formant to CINCAMI/MIS
     */
    private String maID;
    /**
     * It is a sequence number given for internal ordering in the window
     */
    private Long sequence;
    /**
     * It represents the instant in which the measure was obatined.
     */
    private ZonedDateTime measurementInstant;
    
    /**
     * Default Constructor
     */
    public MeasuringData()
    {
        
    }
    
    /**
     * A Copy constructor for soft copy
     * @param another The instance to be copied
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised is some property is incomplete
     */
    public MeasuringData(MeasuringData another) throws PAbMMWindowException            
    {
       if(another.getMeasure()==null || StringUtils.isEmpty(another.getDataSourceID()) || 
               StringUtils.isEmpty(another.getMaID()) || another.getSequence()==null || another.getMeasurementInstant()==null)
            throw new PAbMMWindowException("The measuring data require data for: quantitative, data source, measurement adapter, sequence, and Zoned Data Time");
         
       setDataSourceID(another.getDataSourceID());
       setMaID(another.getMaID());
       setMeasure(another.getMeasure());
       setMeasurementInstant(another.getMeasurementInstant());
       setSequence(another.getSequence());       
    }
    
    /**
     * It is a default factory method
     * @param m The quantitative measure
     * @param did The data source ID
     * @param mid The measurement adapter ID
     * @param seq The sequence number
     * @param zdt The Zoned Date Time related to the measurement
     * @return A new instance
     * @throws PAbMMWindowException It is raised when some data is incomplete. All the parameters are required.
     */
    public static synchronized MeasuringData create(Measure m, String did, String mid, Long seq,ZonedDateTime zdt) throws PAbMMWindowException            
    {
        if(m==null || StringUtils.isEmpty(did) || StringUtils.isEmpty(mid) || seq==null || zdt==null)
            throw new PAbMMWindowException("The measuring data require data for: quantitative, data source, measurement adapter, sequence, and Zoned Data Time");
        
        MeasuringData me=new MeasuringData();
        me.setMeasure(m);
        me.setDataSourceID(did);
        me.setMaID(mid);
        me.setSequence(seq);
        me.setMeasurementInstant(zdt);
        
        return me;
    }

    /**
     * A soft copy method
     * @param another The instance to be copied
     * @return A new instance with the information coming from the "another" instance
     * @throws PAbMMWindowException It is raised when some data is incomplete. All the parameters are required.
     */
    public static synchronized MeasuringData copy(MeasuringData another) throws PAbMMWindowException
    {
        return new MeasuringData(another);
    }
    
    /**
     * It is true when all the properties are defined, false otherwise.
     * @return TRUE when all the properties are defined, FALSE otherwise.
     */
    public synchronized boolean isConsistent()
    {
        return !(measure==null || StringUtils.isEmpty(this.dataSourceID) || 
                StringUtils.isEmpty(this.maID) || this.sequence==null || 
                this.measurementInstant==null);
    }
    
    /**
     * @return the measure
     */
    public Measure getMeasure() {
        return measure;
    }

    /**
     * @param measure the measure to set
     */
    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    /**
     * @return the dataSourceID
     */
    public String getDataSourceID() {
        return dataSourceID;
    }

    /**
     * @param dataSourceID the dataSourceID to set
     */
    public void setDataSourceID(String dataSourceID) {
        this.dataSourceID = dataSourceID;
    }

    /**
     * @return the measuremeent adapter ID
     */
    public String getMaID() {
        return maID;
    }

    /**
     * @param maID the measurment adapter ID to set
     */
    public void setMaID(String maID) {
        this.maID = maID;
    }

    /**
     * @return the sequence
     */
    public Long getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the measurementInstant
     */
    public ZonedDateTime getMeasurementInstant() {
        return measurementInstant;
    }

    /**
     * @param measurementInstant the measurementInstant to set
     */
    public void setMeasurementInstant(ZonedDateTime measurementInstant) {
        this.measurementInstant = measurementInstant;
    }
    
    /**
     * It converts the Quantitative instance to a double value.
     * When it is deterministic convert the data kind from BigDecimal to double, but 
     * when we are in fron of an estimated value, the returned value will be the
     * mathematical expectation.
     * @return A given value (deterministic or mathematical expectation) when it could
     * be computed, NULL otherwise.
     */
    public Double getMeasuredValue()
    {
        if(measure==null) return null;
        Quantitative val=measure.getQuantitative();
        if(val==null) return null;
        
        if(val.getDeterministicValue()!=null)
            return val.getDeterministicValue().doubleValue();
        
        BigDecimal me;
        
        try{
            me=QuantitativeUtils.mathematicalExpectation(val);
        }catch(LikelihoodDistributionException e)
        {
            return null;
        }

        return (me==null)?null:me.doubleValue();
    }
    
    /**
     * It indicates whether the conversion is feasible or not
     * @return TRUE when the conversion is feasible, FALSE otherwise.
     */
    public boolean hasMeasuredValue()
    {
        if(measure==null) return false;
        if(measure.getQuantitative()==null) return false;
        
        if(measure.getQuantitative().getDeterministicValue()!=null)
            return true;
        
        BigDecimal me;
        
        try{
            me=QuantitativeUtils.mathematicalExpectation(measure.getQuantitative());
        }catch(LikelihoodDistributionException e)
        {
            return false;
        }

        return (me != null);
    }
    
}

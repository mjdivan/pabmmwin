/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.ciedayap.cincamimis.LikelihoodDistributionException;
import org.ciedayap.cincamimis.Quantitative;
import org.ciedayap.cincamimis.adapters.ZonedDateTimeAdapter;
import org.ciedayap.pabmm.win.MeasuringData;
import org.ciedayap.utils.QuantitativeUtils;

/**
 * It represent the basic measure for a given metric
 * @author Mario Diván
 * @version 1.0
 */
@XmlRootElement(name="BasicMeasure")
@XmlType(propOrder={"maID","dataSourceID","measure","measurementInstant","estimated"})
public class BasicMeasure implements Serializable{
    private String maID;
    private String dataSourceID;
    private BigDecimal measure;
    private ZonedDateTime measurementInstant;
    private boolean estimated;
   
    public BasicMeasure()
    {
        maID=dataSourceID=null;
        measure=null;
        measurementInstant=null;
        estimated=true;
    }

    public static synchronized BasicMeasure createEmpty()
    {
        return new BasicMeasure();
    }

    public static synchronized BasicMeasure create(MeasuringData item)
    {
        if(item==null || !item.isConsistent()) return null;
        if(item.getMeasure().getQuantitative()==null) return null;//There is not value
        
        BasicMeasure bm=BasicMeasure.createEmpty();
        if(bm==null) return null;
        
        bm.setDataSourceID(item.getDataSourceID());
        bm.setMaID(item.getMaID());
        bm.setMeasurementInstant(item.getMeasurementInstant());
        
        Quantitative val=item.getMeasure().getQuantitative();
        if(QuantitativeUtils.isDeterministic(val))
        {
            bm.setEstimated(false);
            bm.setMeasure(val.getDeterministicValue());
        }
        else
        {
            bm.setEstimated(true);
            try {
                bm.setMeasure(QuantitativeUtils.mathematicalExpectation(val));
            } catch (LikelihoodDistributionException ex) {
                return null; //The conversion has failed
            }
        }
        
        return bm;        
    }

    /**
     * @return the maID
     */
    @XmlElement(name="maID")
    public String getMaID() {
        return maID;
    }

    /**
     * @param maID the maID to set
     */
    public void setMaID(String maID) {
        this.maID = maID;
    }

    /**
     * @return the dataSourceID
     */
    @XmlElement(name="dataSourceID")
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
     * @return the measure
     */
    @XmlElement(name="measure")
    public BigDecimal getMeasure() {
        return measure;
    }

    /**
     * @param measure the measure to set
     */
    public void setMeasure(BigDecimal measure) {
        this.measure = measure;
    }

    /**
     * @return the measurementInstant
     */
    @XmlElement(name="measurementInstant")
    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)     
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
     * @return the estimated
     */
    @XmlElement(name="estimated")
    public boolean isEstimated() {
        return estimated;
    }

    /**
     * @param estimated the estimated to set
     */
    public void setEstimated(boolean estimated) {
        this.estimated = estimated;
    }
}

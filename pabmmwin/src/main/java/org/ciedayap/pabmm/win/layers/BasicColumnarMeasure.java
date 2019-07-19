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
import org.ciedayap.utils.StringUtils;
import org.ciedayap.utils.TranslateJSON;
import org.ciedayap.utils.TranslateXML;

/**
 * It is a transitive class to convert from the object model to a plain way easily communicable by JSON or XML data formats.
 * @author Mario Diván
 * @version 1.0
 */
@XmlRootElement(name="BasicColumnarMeasure")
@XmlType(propOrder={"projectID","entityCategoryID","entityID","metricID","maID","dataSourceID",
    "measure","measurementInstant","estimated"})
public class BasicColumnarMeasure implements Serializable{
    private String projectID;
    private String entityCategoryID;
    private String entityID;
    private String metricID;
    private String maID;
    private String dataSourceID;
    private BigDecimal measure;
    private ZonedDateTime measurementInstant;
    private boolean estimated;
    
    public BasicColumnarMeasure()
    {
        projectID=entityCategoryID=entityID=metricID=maID=dataSourceID=null;
        measure=null;
        measurementInstant=null;
        estimated=true;
    }

    public static synchronized BasicColumnarMeasure createEmpty()
    {
        return new BasicColumnarMeasure();
    }
    
    public static synchronized BasicColumnarMeasure createWithMetricInformation(String projectID,String entityCategoryID,String entityID,String metricID)
    {
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || StringUtils.isEmpty(entityID) ||
                StringUtils.isEmpty(metricID)) return null;
        
        BasicColumnarMeasure bcm=new BasicColumnarMeasure();
        bcm.setProjectID(projectID);
        bcm.setEntityCategoryID(entityCategoryID);
        bcm.setEntityID(entityID);
        bcm.setMetricID(metricID);
        
        return bcm;
    }
    
    public static synchronized BasicColumnarMeasure createFullInformation(String projectID,String entityCategoryID,String entityID,String metricID,
            MeasuringData item)
    {
        if(item==null || !item.isConsistent()) return null;
        if(item.getMeasure().getQuantitative()==null) return null;//There is not value
        
        BasicColumnarMeasure bcm=BasicColumnarMeasure.createWithMetricInformation(projectID, entityCategoryID, entityID, metricID);
        if(bcm==null) return null;
        
        bcm.setDataSourceID(item.getDataSourceID());
        bcm.setMaID(item.getMaID());
        bcm.setMeasurementInstant(item.getMeasurementInstant());
        Quantitative val=item.getMeasure().getQuantitative();
        if(QuantitativeUtils.isDeterministic(val))
        {
            bcm.setEstimated(false);
            bcm.setMeasure(val.getDeterministicValue());
        }
        else
        {
            bcm.setEstimated(true);
            try {
                bcm.setMeasure(QuantitativeUtils.mathematicalExpectation(val));
            } catch (LikelihoodDistributionException ex) {
                return null; //The conversion has failed
            }
        }
        
        return bcm;
    }

    public static synchronized BasicColumnarMeasure createRawFullInformation(String projectID,String entityCategoryID,String entityID,String metricID,
            ZonedDateTime zdt, String dsID,String maID, Quantitative val)
    {
        if(zdt==null || val==null) return null;
        if(StringUtils.isEmpty(maID) || StringUtils.isEmpty(dsID)) return null;
        
        BasicColumnarMeasure bcm=BasicColumnarMeasure.createWithMetricInformation(projectID, entityCategoryID, entityID, metricID);
        if(bcm==null) return null;
        
        bcm.setDataSourceID(dsID);
        bcm.setMaID(maID);
        bcm.setMeasurementInstant(zdt);
        
        if(QuantitativeUtils.isDeterministic(val))
        {
            bcm.setEstimated(false);
            bcm.setMeasure(val.getDeterministicValue());
        }
        else
        {
            bcm.setEstimated(true);
            try {
                bcm.setMeasure(QuantitativeUtils.mathematicalExpectation(val));
            } catch (LikelihoodDistributionException ex) {
                return null; //The conversion has failed
            }
        }
        
        return bcm;
    }

    /**
     * @return the projectID
     */
    @XmlElement(name="projectID")
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
    @XmlElement(name="entityCategoryID")
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
    @XmlElement(name="entityID")
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
    @XmlElement(name="metricID")
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
 
    public static void main(String args[])
    {
        BasicColumnarMeasure bcm=BasicColumnarMeasure.createEmpty();
        bcm.setDataSourceID("ds1");
        //bcm.setEntityCategoryID("ec1");
        //bcm.setEntityID("eID");
        bcm.setEstimated(false);
        bcm.setMaID("MA1");
        bcm.setMeasure(BigDecimal.valueOf(2.33));
        bcm.setMeasurementInstant(ZonedDateTime.now());
        //bcm.setMetricID("metric1");
        //bcm.setProjectID("prj1");

        BasicColumnarMeasure bcm2=BasicColumnarMeasure.createEmpty();
        bcm2.setDataSourceID("ds1");
        //bcm.setEntityCategoryID("ec1");
        //bcm.setEntityID("eID");
        bcm2.setEstimated(false);
        bcm2.setMaID("MA1");
        bcm2.setMeasure(BigDecimal.valueOf(5.53));
        bcm2.setMeasurementInstant(ZonedDateTime.now());
        //bcm.setMetricID("metric1");
        //bcm.setProjectID("prj1");
        
        BasicDynamicTuple tuple=new BasicDynamicTuple();
        tuple.add(bcm);
        tuple.add(bcm2);
        
        System.out.println(TranslateXML.toXml(tuple));
        System.out.println(TranslateJSON.toJSON(bcm));       
    }
}

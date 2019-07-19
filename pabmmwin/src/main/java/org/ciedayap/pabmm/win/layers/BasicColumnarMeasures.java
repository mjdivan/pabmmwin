/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.ciedayap.pabmm.win.MeasuringData;
import org.ciedayap.utils.StringUtils;

/**
 * It is a transitive class to convert from the object model to a plain way easily communicable by JSON or XML data formats.
 * @author Mario Diván
 * @version 1.0
 */
@XmlRootElement(name="BasicColumnarMeasures")
@XmlType(propOrder={"projectID","entityCategoryID","entityID","metricID","measures"})
public class BasicColumnarMeasures implements Serializable{
    private String projectID;
    private String entityCategoryID;
    private String entityID;
    private String metricID;
    private final ArrayList<BasicMeasure> measures;
    
    public BasicColumnarMeasures()
    {
        projectID=entityCategoryID=entityID=metricID=null;
        measures=new ArrayList<>();
    }

    public static synchronized BasicColumnarMeasures createEmpty()
    {
        return new BasicColumnarMeasures();
    }
    
    public static synchronized BasicColumnarMeasures createWithMetricInformation(String projectID,String entityCategoryID,String entityID,String metricID)
    {
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || StringUtils.isEmpty(entityID) ||
                StringUtils.isEmpty(metricID)) return null;
        
        BasicColumnarMeasures bcm=new BasicColumnarMeasures();
        bcm.setProjectID(projectID);
        bcm.setEntityCategoryID(entityCategoryID);
        bcm.setEntityID(entityID);
        bcm.setMetricID(metricID);
        
        return bcm;
    }
    
    public static synchronized BasicColumnarMeasures createFullInformation(String projectID,String entityCategoryID,String entityID,String metricID,
            ArrayList<MeasuringData> items)
    {
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || StringUtils.isEmpty(entityID) ||
                StringUtils.isEmpty(metricID)) return null;
                
        BasicColumnarMeasures bcm=BasicColumnarMeasures.createWithMetricInformation(projectID, entityCategoryID, entityID, metricID);
        if(bcm==null) return null;
        
        if(items==null || items.isEmpty()) return bcm;
        
        items.stream().forEach(item->
        {
            BasicMeasure bm=BasicMeasure.create(item);
            if(bm!=null)bcm.add(bm);
        });
        
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

    public boolean add(BasicMeasure item)
    {
        if(item==null) return false;

        return this.getMeasures().add(item);
    }
    
    public boolean clear()
    {
        if(getMeasures()!=null) getMeasures().clear();
        
        return true;
    }
    
    public boolean removeAt(int index)
    {
        if(index<0 || index>=getMeasures().size()) return false;
        
        return (getMeasures().remove(index)!=null);
    }

    public BasicMeasure getAt(int index)
    {
        if(index<0 || index>=getMeasures().size()) return null;
        
        return getMeasures().get(index);
    }
    
    public boolean remove(BasicMeasure item)
    {
        if(item==null) return false;
        
        return getMeasures().remove(item);
    }

    /**
     * @return the measures
     */
    @XmlElement(name="measures")
    public ArrayList<BasicMeasure> getMeasures() {
        return measures;
    }

}

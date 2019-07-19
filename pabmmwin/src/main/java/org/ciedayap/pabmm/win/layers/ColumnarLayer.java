/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.util.Observable;
import org.ciedayap.pabmm.win.KeyUtils;
import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;
import org.ciedayap.utils.StringUtils;
import org.ciedayap.utils.TranslateJSON;

/**
 * It interprets the layer as exclusive for a given metric. In this sense, it 
 * can analyze individually a given metric. It is useful when you want to keep the
 * parsimony in a model and focus on a very important metric.
 * 
 * @author mjdivan
 */
public class ColumnarLayer extends BoundedLayer{
    /**
     * It represents the specific metric to be monitored in an isolated way
     */
    private final String metricID;
    /**
     * It indicates whether the key's information (ProjectID, EntityCategoryID, EntityID, and MetricID) will be included or not in the message
     */    
    private boolean informKey;
    /**
     * It indicates whether the source's information (data source and measurement adapter) will be included or not in the message
     */
    private boolean informSource;
    /**
     * It indicates whether the measure will be transmitted or discarded
     */
    private boolean activated;

    /**
     * The constructor 
     * @param carrier The carrier to be used
     * @param projectID The projectID to be monitored
     * @param ecatID The ID of the entity category to be analyzed
     * @param eID The specific entity ID to be monitored
     * @param metricID The particular "metricID" to be focused on the given metric.
     * @throws PAbMMWindowException It is raised when some of the constructor's parameters are null
     */
    public ColumnarLayer(Carrier carrier, String projectID, String ecatID, String eID, String metricID) throws PAbMMWindowException 
    {
        super(carrier,projectID, ecatID, eID);
        if(StringUtils.isEmpty(metricID)) throw new PAbMMWindowException("The metricID is null");
        
        this.metricID=metricID;
        informSource=true;
        informKey=true;
        activated=true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!activated) return;
        if(arg==null || !(arg instanceof BasicColumnarMeasure)) return;
        
        BasicColumnarMeasure item=(BasicColumnarMeasure)arg;
        
        String key=KeyUtils.generateMetricKey(this.getProjectID(), this.getEntityCategoryID(), this.getEntityID(), this.getMetricID());
        String keyItem=KeyUtils.generateMetricKey(item.getProjectID(), item.getEntityCategoryID(), item.getEntityID(), item.getMetricID());
        if(key==null || keyItem==null) return;
        
        if(!key.equalsIgnoreCase(keyItem)) return;
        
        if(!this.isInformKey()) 
        {
            item.setProjectID(null);
            item.setEntityCategoryID(null);
            item.setEntityID(null);
            item.setMetricID(null);
        }
        
        if(!this.isInformSource())
        {
            item.setDataSourceID(null);
            item.setMaID(null);            
        }
        
        String message=TranslateJSON.toJSON(item);
        if(StringUtils.isEmpty(message)) return;
        
        carrier.sendJSON(message);        
    }

    @Override
    public boolean activate() {
        activated=true;
        return true;
    }

    @Override
    public boolean deactivate() {
        activated=false;
        return true;
    }

    /**
     * @return the metricID
     */
    public String getMetricID() {
        return metricID;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }
    
    /**
     * Default factory method
     * @param carrier The carrier to be used
     * @param projectID The project ID
     * @param ecatID The entity category ID
     * @param eID The entity ID
     * @param metricID The specific metric ID
     * @return A new ColumnarLayer Instance
     * @throws PAbMMWindowException It is raised when some of the constructor's parameters are null
     */
    public synchronized static ColumnarLayer create(Carrier carrier,String projectID, String ecatID, String eID, String metricID) throws PAbMMWindowException
    {
        return new ColumnarLayer(carrier,projectID,ecatID,eID,metricID);
    }

    /**
     * @return the informKey
     */
    public boolean isInformKey() {
        return informKey;
    }

    /**
     * @param informKey the informKey to set
     */
    public void setInformKey(boolean informKey) {
        this.informKey = informKey;
    }

    /**
     * @return the informSource
     */
    public boolean isInformSource() {
        return informSource;
    }

    /**
     * @param informSource the informSource to set
     */
    public void setInformSource(boolean informSource) {
        this.informSource = informSource;
    }

    /**
     * @return the activated
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * @param activated the activated to set
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}

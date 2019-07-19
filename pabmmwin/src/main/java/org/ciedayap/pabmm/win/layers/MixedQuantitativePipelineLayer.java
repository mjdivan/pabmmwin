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
 * It allows sending any Quantitative Measure at the time in which it is 
 * incorporated at the queue. Of course, it is limited to a given projectID,
 * entity category, and entity. 
 * @author Mario Diván
 * @version 1.0
 */
public class MixedQuantitativePipelineLayer extends BoundedLayer{
    /**
     * It indicates whether the layer derives measures to the carrier or not
     */
    private boolean activated;
    /**
     * It indicates whether the key's information (ProjectID, EntityCategoryID, EntityID, and MetricID) will be included or not in the message
     */    
    private boolean informKey;
    /**
     * It indicates whether the source's information (data source and measurement adapter) will be included or not in the message
     */
    private boolean informSource;    
    
    /**
     * Default constructor
     * @param carrier The carrier to be used
     * @param projectID The project ID
     * @param ecatID The entity category ID
     * @param eID The entity ID
     * @throws PAbMMWindowException It is raised when some parameter is null
     */
    public MixedQuantitativePipelineLayer(Carrier carrier, String projectID, String ecatID, String eID) throws PAbMMWindowException {
        super(carrier,projectID, ecatID, eID);
        activated=true;
        informKey=true;
        informSource=true;
    }

    /**
     * Default factory method
     * @param carrier The carrier to be used
     * @param projectID The project ID
     * @param ecatID The entity category ID
     * @param eID The entity ID
     * @return A new MixedQuantitativePipelineLayer instance
     * @throws PAbMMWindowException It is raised when some parameter is null
     */
    public synchronized static MixedQuantitativePipelineLayer create(Carrier carrier,String projectID, String ecatID, String eID) throws PAbMMWindowException {
        return new MixedQuantitativePipelineLayer(carrier,projectID,ecatID,eID);
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

    @Override
    public void update(Observable o, Object arg) {
        if(!activated) return;
        if(arg==null || !(arg instanceof BasicColumnarMeasure)) return;
        
        BasicColumnarMeasure item=(BasicColumnarMeasure)arg;
        
        String key=KeyUtils.generateEntityKey(this.getProjectID(), this.getEntityCategoryID(), this.getEntityID());
        String keyItem=KeyUtils.generateEntityKey(item.getProjectID(), item.getEntityCategoryID(), item.getEntityID());
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
    public boolean isEnabled() {
        return activated;
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
    
}

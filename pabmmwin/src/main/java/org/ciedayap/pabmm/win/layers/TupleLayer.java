/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.util.Observable;
import org.ciedayap.pabmm.win.KeyUtils;
import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.ProjectMetadata;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;
import org.ciedayap.utils.StringUtils;
import org.ciedayap.utils.TranslateJSON;

/**
 * It represents the layer focusing on all metrics for a given projectID, entityCategory, and entityID parameters.
 * Each data in this layer is a tuple, that is to say, a row with all the attributes and context properties. Each one will have a value or not
 * depending on each particular situation for the given time instant.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class TupleLayer extends BoundedLayer{
    private boolean activated;
    /**
     * Default constructor
     * @param carrier The carrier to be used
     * @param projectID The project ID
     * @param ecatID The entity category ID
     * @param eID The entity ID
     * @throws PAbMMWindowException It is raised when some parameter is null
     */
    public TupleLayer(Carrier carrier,String projectID, String ecatID, String eID) throws PAbMMWindowException {
        super(carrier,projectID, ecatID, eID);
        activated=true;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(!isActivated()) return;
        if(o==null || !(o instanceof ProjectMetadata)) return;
        
        ProjectMetadata prj=(ProjectMetadata)o;
        
        BasicDynamicTuple row=prj.getMostRecentTuple(this.getProjectID(),this.getEntityCategoryID(),this.getEntityID());
        if(row==null) return;        
        
        String message=TranslateJSON.toJSON(row);
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

    @Override
    public boolean isEnabled() {
        return activated;
    }
    
    /**
     * Default factory method
     * @param carrier The carrier to be used
     * @param projectID The project ID
     * @param ecatID The entity category ID
     * @param eID The entity ID
     * @return A new TupleLayer instance
     * @throws PAbMMWindowException It is raised when some parameter is null
     */
    public synchronized static TupleLayer create(Carrier carrier,String projectID, String ecatID, String eID) throws PAbMMWindowException {
        return new TupleLayer(carrier,projectID,ecatID,eID);
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

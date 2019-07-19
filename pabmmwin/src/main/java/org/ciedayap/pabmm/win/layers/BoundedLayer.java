/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;
import org.ciedayap.utils.StringUtils;

/**
 * It represents a set of layers specifically bound to a given project, entity category, and entity under monitoring.
 * @author Mario Diván
 * @version 1.0
 */
public abstract class BoundedLayer extends LayerByItem{
    /**
     * The project ID to be monitored
     */
    private final String projectID;
    /**
     * The ID related to the entity category into the projectID
     */
    private final String entityCategoryID;
    /**
     * The specific entityID to be monitored into the projectID given the entityCategoryID
     */
    private final String entityID;
    
    /**
     * The constructor responsible for initializing the data for bonding.
     * @param carrier The carrier to be used
     * @param projectID The project ID
     * @param ecatID The ID related to the entity category
     * @param eID  The entity ID
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when at least one of projectdID, entityCategoryID, or entityID parameters are not defined.
     */
    public BoundedLayer(Carrier carrier, String projectID, String ecatID,String eID) throws PAbMMWindowException
    {
        super(carrier);
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(eID) || StringUtils.isEmpty(ecatID))
        {
            throw new PAbMMWindowException("At least one of projectID, entityCategoryID, or entityID parameters are not defined");
        }
        
        this.projectID=projectID;
        this.entityCategoryID=ecatID;
        this.entityID=eID;
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
}

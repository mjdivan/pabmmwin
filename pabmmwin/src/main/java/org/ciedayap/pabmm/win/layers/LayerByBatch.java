/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.ProjectMetadata;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;

/**
 * It allows building new structures from the data using data batch processing each a given time period.
 * It does not react in front of the new data arrives, but the reaction happens in relation to a given lifespan.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public abstract class LayerByBatch extends Layer implements Runnable{
    private final ProjectMetadata target;
    
    /**
     * Default constructor
     * @param carrier the carrier to be used
     * @param target The target to be monitored
     * @throws PAbMMWindowException It is raised when the target or carrier is null, or the carrier is unavailable.
     */
    public LayerByBatch(Carrier carrier,ProjectMetadata target) throws PAbMMWindowException
    {
        super(carrier);
        
        if(target==null) throw new PAbMMWindowException("The indicated target is null");

        this.target=target;
    }

    /**
     * @return the target
     */
    public ProjectMetadata getTarget() {
        return target;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;


/**
 * It represents the abstract class for the different viewpoints related to the layers.
 * A layer can not modify in any way the origin. The layer just is useful to see or 
 * observe the data, read them and to make posterior actions are derived just from 
 * the reading.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public abstract class Layer {
    protected final Carrier carrier;
    
    /**
     * Default constructor
     * @param carrier The carrier associated with the layer
     * @throws PAbMMWindowException It is raised when the carrier is not defined or unavailable
     */
    public Layer(Carrier carrier) throws PAbMMWindowException
    {
        if(carrier==null || !carrier.isAvailable())
            throw new PAbMMWindowException("The carrier is null or unavailable");
        
        this.carrier=carrier;
    }
    /**
     * It is responsible for activating the layer jointly with the necessary resources
     * @return TRUE when it was activated, FALSE otherwise.
     */
    public abstract boolean activate();
    /**
     * It is responsible for cleaning all used resources.
     * @return TRUE when resources were released, FALSE otherwise
     */
    public abstract boolean deactivate();
    /**
     * It indicates whether the expected behaviour related to the layer is enabled or not.
     * @return TRUE it is activated, FALSE otherwise.
     */
    public abstract boolean isEnabled();            
}

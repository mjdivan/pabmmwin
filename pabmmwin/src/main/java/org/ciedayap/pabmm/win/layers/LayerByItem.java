/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.util.Observer;
import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;

/**
 * It represents layers in which each action is taken based on the change of an element when it happens.
 * @author Mario Diván
 * @version 1.0
 */
public abstract class LayerByItem extends Layer implements Observer{
    
    /**
     * Default constructor
     * @param carrier The carrier to be used
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the carrier is null or unavailable
     */
    public LayerByItem(Carrier carrier) throws PAbMMWindowException
    {
        super(carrier);
    }
}

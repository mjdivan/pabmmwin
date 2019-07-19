/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.util.Observable;
import org.ciedayap.pabmm.win.LogicalWindow;
import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;

/**
 * It manages each CINCAMI/MIS message at the time in which the message has arrived at
 * the window. In this layer, the perspective is not limited to a given project because
 * of measures about different projects could be provided.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class MessageLayer extends LayerByItem
{
    public boolean activated;
    /**
     * Default constructor
     * @param carrier The carrier to be used
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the carrier is null or unavailable
     */
    public MessageLayer(Carrier carrier) throws PAbMMWindowException
    {
        super(carrier);
        activated=true;
    }
    
    /**
     * It creates a new MessageLayer instance
     * @param carrier The carrier to be used
     * @return A new MessageLayer instance
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when the carrier is null or unavailable
     */
    public synchronized static MessageLayer create(Carrier carrier) throws PAbMMWindowException
    {
        return new MessageLayer(carrier);
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
        if(!activated) 
        {
            System.out.println("MessageLayer: Not Activated");
            return;
        }
        
        if(arg==null || !(arg instanceof String)) 
        {
            System.out.println("MessageLayer: "+((arg==null)?"arg null":"arg not String"));
            return;
        }
        if(!(o instanceof LogicalWindow)){
            System.out.println("MessageLayer: o is not LogicalWinsow is "+o.getClass());
            return;
        }
        
        
        String mes=(String)arg;        
        
        if(mes.contains("{")) carrier.sendJSON(mes);
        else carrier.sendXML(mes);
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }
    
}

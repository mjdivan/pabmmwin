/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.util.Observable;
import java.util.Observer;
import org.ciedayap.pabmm.win.layers.LayerByItem;

/**
 * It represents the abstract class associated with elements able to be monitored and translated based on the 
 * logic related to each layer. Here, the common responsibilities and properties are established in order to 
 * share it between the derived classes.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public abstract class ObservableTarget extends Observable{
    /**
     * Default constructor
     */
    public ObservableTarget()
    {
        super();
    }
    
    /**
     * It adds a new Observer, but the Observer must belong to a class inherited from org.ciedayap.pabmm.win.layers.LayerByItem.class.
     * @param o An observer which belongs to a Class inherited from org.ciedayap.pabmm.win.layers.LayerByItem.class
     */
    @Override
    public synchronized void addObserver(Observer o)
    {
        if(o==null) return;
        
        if(!LayerByItem.class.isAssignableFrom(o.getClass())) return; //Is Layer.class assignable from o.getClass If yes, it implies that Layer is a superclass
            
        super.addObserver(o);
    }
}

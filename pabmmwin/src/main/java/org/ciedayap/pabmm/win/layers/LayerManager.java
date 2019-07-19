/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.util.ArrayList;

/**
 * It allows managing the created layers
 * @author Mario Diván
 * @version 1.0
 */
public class LayerManager {
    private final ArrayList<Layer> layers;
    
    public LayerManager()
    {
        layers=new ArrayList<>();
    }
    
    /**
     * It activates the layer and incorporate it into the list. 
     * @param layer The layer to be activated and incorporated into the list.
     * @return TRUE when the layer was activated and incorporated into the list, FALSE otherwise.
     */
    public boolean activate(Layer layer)
    {
        if(layer==null) return false;
        if(!layer.isEnabled()) layer.activate();
        
        if(layer.isEnabled())
        {//The layer is now enabled
            if(layers.contains(layer)) return true;
            else return layers.add(layer);
        }
        
        return false;
    }
    
    /**
     * It deactivates the layer and remove it from the list
     * @param layer The layer to be deactivated and removed from the list
     * @return TRUE when the layer was deactivated and removed from the list, FALSE otherwise
     */
    public boolean deactivate(Layer layer)
    {
        if(layer==null) return false;
        if(layer.isEnabled()) layer.deactivate();
        
        if(!layer.isEnabled())
        {
            return layers.remove(layer);
        }
        
        return false;
    }
    
    /**
     * It tries to deactivate all the layers and remove it from the list.
     * @return TRUE when all the layers have been deactivated and removed from the list, FALSE otherwise.
     */
    public boolean deactivateAll()
    {
        layers.stream().forEach(Layer::deactivate);
        
        return layers.isEmpty();
    }
    
    /**
     * It retrieves the layer at the index
     * @param index The wished index
     * @return The Layer located at the given index, NULL when the index is out of range.
     */
    public Layer getAt(int index)
    {
        if(index<0 || index>=layers.size()) return null;
        
        return layers.get(index);
    }
    
    /**
     * It return the size of the list associated with the active layers.
     * @return The number of active layers in the list
     */
    public int size()
    {
        return layers.size();
    }
    
    /**
     * It indicates whether the list has active layers or not.
     * @return TRUE when there at least exists one active layer, FALSE none of layers is available and activated.
     */
    public boolean isEmpty()
    {
       return layers.isEmpty();
    }
}

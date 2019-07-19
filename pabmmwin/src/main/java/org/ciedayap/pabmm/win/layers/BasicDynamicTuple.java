/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * It groups a set of BasicColumnarMeasure instances for informing them as a tuple
 * @author Mario Diván
 * @version 1.0
 */
@XmlRootElement(name="BasicDynamicTuple")
@XmlType(propOrder={"row"})
public class BasicDynamicTuple implements Serializable{
    private ArrayList<BasicColumnarMeasure> row;
    
    public BasicDynamicTuple()
    {
        row=new ArrayList();
    }

    /**
     * @return the row
     */
    @XmlElement(name="row")    
    public ArrayList<BasicColumnarMeasure> getRow() {
        return row;
    }

    public int getNumberOfColumns()
    {
        return (row==null)?0:row.size();
    }
    
    /**
     * @param row the row to set
     */
    public void setRow(ArrayList<BasicColumnarMeasure> row) {
        this.row = row;
    }
    
    public boolean add(BasicColumnarMeasure item)
    {
        if(item==null) return false;

        return row.add(item);
    }
    
    public boolean clear()
    {
        if(row!=null) row.clear();
        
        return true;
    }
    
    public boolean removeAt(int index)
    {
        if(index<0 || index>=row.size()) return false;
        
        return (row.remove(index)!=null);
    }
    
    public boolean remove(BasicColumnarMeasure item)
    {
        if(item==null) return false;
        
        return row.remove(item);
    }
}

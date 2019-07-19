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
@XmlRootElement(name="BasicDynamicTable")
@XmlType(propOrder={"table"})
public class BasicDynamicTable implements Serializable{
    private ArrayList<BasicDynamicTuple> table;
    
    public BasicDynamicTable()
    {
        table=new ArrayList();
    }

    /**
     * @return the row
     */
    @XmlElement(name="table")    
    public ArrayList<BasicDynamicTuple> getTable() {
        return table;
    }

    /**
     * @param table the row to set
     */
    public void setTable(ArrayList<BasicDynamicTuple> table) {
        this.table = table;
    }
    
    public boolean add(BasicDynamicTuple item)
    {
        if(item==null) return false;

        return table.add(item);
    }
    
    public boolean clear()
    {
        if(table!=null) table.clear();
        
        return true;
    }
    
    public boolean removeAt(int index)
    {
        if(index<0 || index>=table.size()) return false;
        
        return (table.remove(index)!=null);
    }
    
    public boolean remove(BasicDynamicTuple item)
    {
        if(item==null) return false;
        
        return table.remove(item);
    }
}

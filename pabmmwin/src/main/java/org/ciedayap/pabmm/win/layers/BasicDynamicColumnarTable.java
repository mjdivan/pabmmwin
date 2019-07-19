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
@XmlRootElement(name="BasicDynamicColumnarTable")
@XmlType(propOrder={"columnarTable"})
public class BasicDynamicColumnarTable implements Serializable{
    private ArrayList<BasicColumnarMeasures> columnarTable;
    
    public BasicDynamicColumnarTable()
    {
        columnarTable=new ArrayList();
    }

    public synchronized static BasicDynamicColumnarTable create()
    {
        return new BasicDynamicColumnarTable();
    }
    
    /**
     * @return the row
     */
    @XmlElement(name="column")    
    public ArrayList<BasicColumnarMeasures> getColumnarTable() {
        return columnarTable;
    }

    /**
     * @param columnarTable the columnar to set
     */
    public void setColumnarTable(ArrayList<BasicColumnarMeasures> columnarTable) {
        this.columnarTable = columnarTable;
    }
    
    public boolean add(BasicColumnarMeasures item)
    {
        if(item==null) return false;

        return columnarTable.add(item);
    }
    
    public boolean clear()
    {
        if(columnarTable!=null){
            columnarTable.stream().forEach(BasicColumnarMeasures::clear);
            columnarTable.clear();
        }
        
        return true;
    }
    
    public boolean removeAt(int index)
    {
        if(index<0 || index>=columnarTable.size()) return false;
        
        return (columnarTable.remove(index)!=null);
    }
    
    public boolean remove(BasicColumnarMeasures item)
    {
        if(item==null) return false;
        
        return columnarTable.remove(item);
    }
        
}

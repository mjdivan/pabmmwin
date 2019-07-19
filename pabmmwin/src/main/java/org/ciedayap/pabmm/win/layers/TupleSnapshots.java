/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.storm.utils.Time;
import org.ciedayap.pabmm.win.PAbMMWindowException;
import org.ciedayap.pabmm.win.ProjectMetadata;
import org.ciedayap.pabmm.win.layers.carrier.Carrier;
import org.ciedayap.utils.TranslateJSON;
import org.ciedayap.utils.TranslateXML;
import org.ciedayap.utils.ZipUtil;

/**
 * It generates and keeps in memory snapshots related to the available data of the indicated target.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public class TupleSnapshots extends LayerByBatch {    
    public static final short XML=0;
    public static final short JSON=1;
    
    private boolean activated;
    /**
     * It represents the Snapshot's lifespan. Each lifespan expressed in milliseconds, the spapshot will be updated 
     * using the current data. The minimum lifespan is 5000 ms.
     */
    private final long lifespan;
    /**
     * It indicates whether the snapshot will be kept in a compressed way or not.
     */
    private final boolean compressed;
    /**
     * It indicates whether the generated snapshot will be organized under the XML or JSON data format.
     */
    private final short dataFormat;
    /**
     * The last snapshot expressed as a set of bytes. It could be compressed or not and under XML or JSON depending on 
     * chosen options.
     */
    private byte[] lastSnapshot;
    /**
     * The last Updating related to 
     */
    private ZonedDateTime lastUpdating;
    
    /**
     * Default constructor
     * @param myTarget The target to be read
     * @param carrier The carrier to be used
     * @param lifespan The time period in which the snapshot is kept in memory before generating a new snapshot
     * @param compressed It indicates whether the snapshot will be compressed or not
     * @param dataFormat It indicates the way in which the content will be organized (i.e. JSON or XML)
     * @throws PAbMMWindowException  It is raised when some parameter is invalid or null
     */
    public TupleSnapshots(ProjectMetadata myTarget, Carrier carrier,
            long lifespan, boolean compressed, short dataFormat) throws PAbMMWindowException {
        super(carrier,myTarget);
        
        if(lifespan<5000) this.lifespan=5000;
        else this.lifespan=lifespan;
        
        this.compressed=compressed;
        switch(dataFormat)
        {
            case XML:
            case JSON:
                this.dataFormat=dataFormat;
                break;
            default:
                throw new PAbMMWindowException("Invalid Data Format");
        }
        
        activated=true;
    }

    /**
     * Default factory method
     * @param myTarget The target to be read
     * @param carrier The carrier to be used
     * @param lifespan The time period in which the snapshot is kept in memory before generating a new snapshot
     * @param compressed It indicates whether the snapshot will be compressed or not
     * @param dataFormat It indicates the way in which the content will be organized (i.e. JSON or XML)
     * @return A new TupleSnapshots instance
     * @throws PAbMMWindowException It is raised when some parameter is invalid or null
     */
    public synchronized static TupleSnapshots create(ProjectMetadata myTarget,Carrier carrier,
            long lifespan, boolean compressed, short dataFormat) throws PAbMMWindowException {
        return new TupleSnapshots(myTarget,carrier,lifespan,compressed,dataFormat);
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
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public void run() {
        while(activated)
        {
            System.out.println("[Start] "+System.nanoTime());
            //Recorrer de lo más nuevo a lo más viejo, cuando falta, utilizar lo más nuevo para completar
            String generated=createSnapshot();
            
            if(compressed)
            {
                byte[] cmessage;
                try {
                     cmessage=ZipUtil.compressGZIP(generated);
                     System.out.println("[Bytes] "+cmessage.length);
                     carrier.sendZIP(cmessage);
                } catch (Exception ex) {
                   System.out.println("TupleSnapshots - GZIP: "+ex.getMessage());
                }                
            }
            else
            {
                System.out.println("[Bytes] "+generated.getBytes().length);
                switch(dataFormat)
                {
                    case XML:
                        carrier.sendXML(generated);
                        break;
                    case JSON:
                        carrier.sendJSON(generated);
                        break;
                }             
            }
            System.out.println("[End] "+System.nanoTime());            
            try {
                TimeUnit.MILLISECONDS.sleep(lifespan);
            } catch (InterruptedException ex) {                
                Logger.getLogger(ProjectMetadata.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @return the lifespan
     */
    public long getLifespan() {
        return lifespan;
    }

    /**
     * @return the compressed
     */
    public boolean isCompressed() {
        return compressed;
    }

    /**
     * @return the dataFormat
     */
    public short getDataFormat() {
        return dataFormat;
    }

    /**
     * @return the lastSnapshot
     */
    public byte[] getLastSnapshot() {
        return lastSnapshot;
    }

    /**
     * @param lastSnapshot the lastSnapshot to set
     */
    public void setLastSnapshot(byte[] lastSnapshot) {
        this.lastSnapshot = lastSnapshot;
    }

    /**
     * @return the lastUpdating
     */
    public ZonedDateTime getLastUpdating() {
        return lastUpdating;
    }

    /**
     * @param lastUpdating the lastUpdating to set
     */
    public void setLastUpdating(ZonedDateTime lastUpdating) {
        this.lastUpdating = lastUpdating;
    }

    /**
     * @return the activated
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * @param activated the activated to set
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    private String createSnapshot() {
        if(this.getTarget()==null) return null;
        
        Enumeration<String> keys=getTarget().managedKeys();
        if(keys==null) return null;
        
        BasicDynamicColumnarTable bdct=BasicDynamicColumnarTable.create();
        if(bdct==null) return null;       
        
        while(keys.hasMoreElements())
        {
            String thekey=keys.nextElement();
            BasicColumnarMeasures bcm=getTarget().getCopiedColumnarData(thekey);

            if(bcm!=null)            
            {
                bdct.add(bcm);
            }
        }                   

        String message;
        switch(dataFormat)
        {
            case XML:
                message=TranslateXML.toXml(bdct);
                break;
            case JSON:
                message=TranslateJSON.toJSON(bdct);
                break;
            default:
                return null;
        }
        
        bdct.clear();//It releases the memory
        
        lastUpdating=ZonedDateTime.now();
        
        return message;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers.carrier;

/**
 * It allows carrying an element or message to another platform (e.g. Apache Storm).
 * It acts by reactions. That is to say, it transports the element when some send method is invoked.
 * 
 * @author Mario Diván
 * @version 1.0
 */
public abstract class Carrier {
    /**
     * It sends to some platform the string o, which corresponds with a JSON data format
     * @param o The JSON message to be sent
     * @return TRUE when the message has been transmitted, FALSE otherwise.
     */
    public abstract boolean sendJSON(String o);
    /**
     * It sends to some platform the string o, which corresponds with a XML data format
     * @param o The XML message to be sent
     * @return TRUE when the message has been transmitted, FALSE otherwise.
     */    
    public abstract boolean sendXML(String o);
    /**
     * It indicates the availability of the carrying method
     * @return TRUE when it is available, FALSE otherwise
     */
    public abstract boolean isAvailable();
    
    /**
     * It sends a compressed message using ZIP compression. The message could be organized both JSON or XML data format.
     * @param o The ZIP compressed message
     * @return TRUE when the message was sent, FALSE otherwise.
     */
    public abstract boolean sendZIP(byte[] o);
            
}

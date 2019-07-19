/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.layers.carrier;

import org.ciedayap.utils.ZipUtil;

/**
 * It is a basic class for sending  messages to System.out
 * @author Mario Diván
 * @version 1.0
 */
public class TestCarrier extends Carrier{

    @Override
    public boolean sendJSON(String o) {
        System.out.println(" ** JSON ["+System.nanoTime()+"] ");
        return true;
    }

    @Override
    public boolean sendXML(String o) {
        System.out.println(" ** XML ["+System.nanoTime()+"] ");
        return true;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean sendZIP(byte[] o) {
        String message;
        try {
            message=ZipUtil.decompressGZIP(o);
        } catch (Exception ex) {
            return false;
        }
        
        
        System.out.println(" ** ZIP["+System.nanoTime()+"] ");
        return true;
    }
    
}

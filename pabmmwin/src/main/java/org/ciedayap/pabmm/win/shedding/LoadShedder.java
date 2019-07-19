/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win.shedding;

import org.ciedayap.pabmm.win.MeasuringData;
import org.ciedayap.pabmm.win.ProjectMonitor;

/**
 * It establishes the basic aspects to be immplemented by a load shedder
 * @author Mario Diván
 * @version 1.0
 */
public interface LoadShedder {
    public boolean setPolicy(String policy);
    public boolean isProcessable(ProjectMonitor pm,MeasuringData md);
    public boolean shutdown();
    
}

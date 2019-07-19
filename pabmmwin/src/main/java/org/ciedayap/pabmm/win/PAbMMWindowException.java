/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

/**
 * Kind of Exception associated with the PAbMM Windows
 * @author Mario Diván
 * @version 1.0
 */
public class PAbMMWindowException extends Exception {

    public PAbMMWindowException() 
    {
        super();
    }
    
    public PAbMMWindowException(String mes) {
        super(mes);
    }
    
}

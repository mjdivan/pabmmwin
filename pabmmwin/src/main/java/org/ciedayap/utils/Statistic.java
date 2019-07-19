/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.utils;

import java.util.Arrays;

/**
 *
 * @author mjdivan
 */
public class Statistic {
    
    public static double median(double[] values)
    {
       return percentileQ1Q2Q3(values)[1];
    }
    

    public static double riq(double[] values)
    {        
        double ret[]=percentileQ1Q2Q3(values);
        
        return ret[2]-ret[0];
    }
    
    public static double[] percentileQ1Q2Q3(double[] values)
    {
        values=Arrays.stream(values).sorted().toArray();
        
        double ret[]=new double[3];
        ret[0]=values[((int)Math.ceil(0.25*(double)values.length))-1];
        ret[2]=values[((int)Math.ceil(0.75*(double)values.length))-1];
        if((values.length%2)==0)
         {
             double n=values[values.length/2];//Fitted considering start index at 0
             double nb=values[(values.length/2)-1];//Fitted considering start index at 0

              ret[1]=(n+nb)/2;                        
         }
         else
         {
              ret[1]=values[((values.length+1)/2)-1];
         }
        
        
        return ret;
        
    }
    
}

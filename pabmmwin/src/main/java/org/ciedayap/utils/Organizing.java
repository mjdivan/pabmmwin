/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author mjdivan
 */
public class Organizing {
    public static void main(String args[])
    {
        //Inputs
        String provinces[]={"Capital Federal", "Buenos Aires","Córdoba","Santa Fe","Mendoza","Tucumán","Salta","Entre Ríos","Rio Negro","Neuquén",
        "Corrientes","San Juan","Misiones","Chaco","Formosa","Jujuy","Catamarca","Santiago del Estero","Santa Cruz","Chubut","La Pampa",
        "San Luis","La Rioja","Tierra del Fuego"};
        Double[] inhabitants={2890151.0,15625084.0,3066801.0,3000701.0,1579651.0,1338523.0,1079051.0,1158147.0,552822.0,474155.0,930991.00,620023.00,
            965522.00,984446.00,486559.00, 611888.00,367828.00,804457.00,196958.00,413237.00,299294.00,367933.00,289983.00,101079.00};
        Double[] advertisement={ 227888316.65,59733604.98,15677076.99,12162241.07,10345441.94,4440538.41,3306110.74,3172466.48,2850127.47,
            2561344.31,2352576.48,2149455.99,1934083.61,1845836.23,1504772.70,1504220.00,1406081.76,1294474.35,1285290.55,1234845.41,
            1065700.82,922199.31,766518.13,536643.36};
        
        double maxTolerance=0.5;//USD/person
        //
        
        segmenter(provinces,inhabitants,advertisement,maxTolerance);
    }
    
    public static double segmenter(String provinces[], Double inhabitants[],Double advertisement[],double maxTolerance)
    {
        Double total_inhabitants=Arrays.stream(inhabitants).parallel().reduce(0.0, (a,b)->a+b);
        Double total_expenditure=Arrays.stream(advertisement).parallel().reduce(0.0, (a,b)->a+b);
        
        Double global_rate=total_expenditure/total_inhabitants;
        
        System.out.println("Inhabitants: "+total_inhabitants+" Advertisement: "+total_expenditure+" Global Rate: "+global_rate);

        
        Double rates[]=new Double[provinces.length];
        Double maxRate=null,minRate=null;
        for(int i=0;rates!=null && i<rates.length;i++) 
        {
            rates[i]=advertisement[i]/inhabitants[i];
            if(maxRate==null) maxRate=rates[i];
            else maxRate=(maxRate<rates[i])?rates[i]:maxRate;
            if(minRate==null) minRate=rates[i];
            else minRate=(minRate>rates[i])?rates[i]:minRate;            
        }
             
        System.out.println("Max Rate: "+maxRate+" Min Rate:"+ minRate);
        long count=Arrays.stream(rates).filter(d->Math.abs(d-global_rate)>maxTolerance).count();
        if(count>0) System.out.println("There is not homogeneity: "+count+" ("+(100*count/rates.length)+"%)");
        else System.out.println("There is homogeneity.");
        
        System.out.println("MaxTolerance: "+maxTolerance+" Groups: "+(maxRate-minRate)/maxTolerance);
        
        double kgroups=Math.floor((maxRate-minRate)/maxTolerance);
        System.out.println(kgroups);
        
        //Grouping
        java.util.HashMap map=new java.util.HashMap<>();
        for(int i=0;i<rates.length;i++)
        {         
            int g=(int) Math.floor((rates[i]-minRate)/maxTolerance);
            if(map.containsKey(g))
                ((ArrayList)map.get(g)).add(i);
            else
            {
                ArrayList list=new ArrayList();
                list.add(i);
                map.put(g, list);
            }
        }
         
        //It shows the Grouping
        Iterator keymap=map.keySet().iterator();
        double accumulatedWide=0.0;
        while(keymap.hasNext())
        {            
            Object key=keymap.next();
            ArrayList list=(ArrayList) map.get(key);
            double acu=0.0;
            System.out.println("GROUP: "+key);
            
            double mmax=-1,mmin=-1;
            for(int i=0;i<list.size();i++)
            {                
                System.out.println(provinces[(int)list.get(i)]+" - Rate: "+rates[(int)list.get(i)]);
                double val=((double)rates[(int)list.get(i)]);
                if(mmax==-1) mmax=val;
                else mmax=(mmax<val)?val:mmax;
                
                if(mmin==-1) mmin=val;
                else mmin=(mmin>val)?val:mmin;
                
                if(i==(list.size()-1))
                {
                    accumulatedWide+=(mmax-mmin);
                    System.out.println(" ["+mmin+" ; "+mmax+"] Range: "+(mmax-mmin)+ " Threshold: "+(maxTolerance>=(mmax-mmin)));
                }
            }                                                            
            System.out.println();
        }        
        
        return accumulatedWide;
     //The weight will goes from minimum to max up to each group has a minimum quantity or zero
    }
}

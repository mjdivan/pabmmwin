/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.ciedayap.utils.StringUtils;

/**
 * A utility class related to the key generation
 * @author Mario Diván
 * @version 1.0
 */
public class KeyUtils {
    public static short PROJECT_ID=0;
    public static short ENTITY_CATEGORY_ID=1;
    public static short ENTITY_ID=2;
    public static short METRIC_ID=3;
    /**
     * It generates a String concatenating the parameters in a specific way
     * @param projectID The project ID
     * @param entityCategoryID The entity category ID
     * @param entityID The entity ID
     * @param metricID The metric ID
     * @return The new key when it was generated, null otherwise
     */
    public static String generateMetricKey(String projectID,String entityCategoryID,String entityID,String metricID)
    {
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || 
                StringUtils.isEmpty(entityID) || StringUtils.isEmpty(metricID)) return null;
        
        StringBuilder sb=new StringBuilder();
        sb.append("[")
                .append(projectID.trim()).append("|")
                .append(entityCategoryID.trim()).append("|")
                .append(entityID.trim()).append("|")
                .append(metricID.trim()).append("]");
        
        return sb.toString();                
    }
    
    /**
     * It generates a searching pattern 
     * @param projectID The project ID
     * @param entityCategoryID The entity category ID
     * @param entityID The entity ID
     * @param metricID The metric ID
     * @param limit The upper limit to the pattern
     * @return The new key pattern when it was generated, null otherwise
     */
    public static String generateSearchPatternUpTo(String projectID,String entityCategoryID,String entityID,String metricID,short limit)
    {
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || 
                StringUtils.isEmpty(entityID) || StringUtils.isEmpty(metricID)) return null;
        
        StringBuilder sb=new StringBuilder();
        sb.append("[").append(projectID.trim()).append("|");
        
        if(limit==PROJECT_ID) return sb.toString();
        
        sb.append(entityCategoryID.trim()).append("|");
        if(limit==ENTITY_CATEGORY_ID) return sb.toString();
        
        sb.append(entityID.trim()).append("|");
        if(limit==ENTITY_ID) return sb.toString();
        
        sb.append(metricID.trim()).append("]");
        
        return sb.toString();                
    }
    
    /**
     * It obtains the key component from a string
     * @param s The generatede keey under the schema indicated in the generateMetricKey method
     * @return A String Array with the components ordered as projectID, entityCategoryID, eentityID, and metricID. null otherwise
     */
    public static String[] obtainMetricKeyFrom(String s)
    {
        if(StringUtils.isEmpty(s)) return null;
        
        String rdo[]=new String[4];
        
        int sIdx=s.indexOf("[",0);
        int fpipe=s.indexOf("|", 0);
        if(sIdx<0 || fpipe<0) return null;
        
        rdo[0]=s.substring(sIdx+1,fpipe);
                
        int spipe=s.indexOf("|",fpipe+1);
        if(spipe<0) return null;
        rdo[1]=s.substring(fpipe+1, spipe);
        
        int tpipe=s.indexOf("|", spipe+1);
        if(tpipe<0) return null;
        rdo[2]=s.substring(spipe+1, tpipe);
        
        rdo[3]=s.substring(tpipe+1, s.length()-1);
        
        return rdo;
    }
    
    /**
     * It generates an String concatenating the parameters in a specific way
     * @param projectID The project ID
     * @param entityCategoryID The entity category ID
     * @param entityID The entity ID
     * @return The new key when it was generated, null otherwise
     */
    public static String generateEntityKey(String projectID,String entityCategoryID,String entityID)
    {
        if(StringUtils.isEmpty(projectID) || StringUtils.isEmpty(entityCategoryID) || 
                StringUtils.isEmpty(entityID)) return null;
        
        StringBuilder sb=new StringBuilder();
        sb.append("[")
                .append(projectID.trim()).append("|")
                .append(entityCategoryID.trim()).append("|")
                .append(entityID.trim()).append("]");
        
        return sb.toString();                
    }
    
    /**
     * It obtains the key component from a string
     * @param s The generatede keey under the schema indicated in the generateMetricKey method
     * @return A String Array with the components ordered as projectID, entityCategoryID, eentityID, and metricID. null otherwise
     */
    public static String[] obtainEntityKeyFrom(String s)
    {
        if(StringUtils.isEmpty(s)) return null;
        
        String rdo[]=new String[3];
        
        int sIdx=s.indexOf("[",0);
        int fpipe=s.indexOf("|", 0);
        if(sIdx<0 || fpipe<0) return null;
        
        rdo[0]=s.substring(sIdx+1,fpipe);
                
        int spipe=s.indexOf("|",fpipe+1);
        if(spipe<0) return null;
        rdo[1]=s.substring(fpipe+1, spipe);
                
        rdo[2]=s.substring(spipe+1, s.length()-1);
        
        return rdo;
    }
    
    /**
     * Example
     * @param args 
     */
    public static void main(String args[])
    {
        String key=KeyUtils.generateMetricKey("p2","ec1","eid","metric1");
        System.out.println("Key: "+key);
        String ret[]=KeyUtils.obtainMetricKeyFrom(key);
        System.out.println("Components: ");
        System.out.println("Project_ID:"+ret[KeyUtils.PROJECT_ID]);
        System.out.println("EC_ID:"+ret[KeyUtils.ENTITY_CATEGORY_ID]);
        System.out.println("Entity_ID:"+ret[KeyUtils.ENTITY_ID]);
        System.out.println("MetricID:"+ret[KeyUtils.METRIC_ID]);
        
        String keyEnt=KeyUtils.generateEntityKey("p2","ec1","eid");
        System.out.println("KeyEnt: "+keyEnt);
        String retEnt[]=KeyUtils.obtainEntityKeyFrom(keyEnt);
        System.out.println("Components: ");
        for(int i=0;retEnt!=null & i<retEnt.length;i++) System.out.println(retEnt[i]);        
        
        System.out.println("pattern prj: "+KeyUtils.generateSearchPatternUpTo("p2","ec1","eid","metric1",PROJECT_ID));
        System.out.println("pattern EC: "+KeyUtils.generateSearchPatternUpTo("p2","ec1","eid","metric1",ENTITY_CATEGORY_ID));
        System.out.println("pattern E: "+KeyUtils.generateSearchPatternUpTo("p2","ec1","eid","metric1",ENTITY_ID));
        System.out.println("pattern MID: "+KeyUtils.generateSearchPatternUpTo("p2","ec1","eid","metric1",METRIC_ID));
       
        ConcurrentHashMap <String,String>mio=new ConcurrentHashMap<>();
        for(int i=0;i<10;i++)
        {
            String pkey;
            pkey = KeyUtils.generateMetricKey("prj"+((i%2==0)?"1":"2"), "ec1", "ent1", "m"+i);
            mio.put(pkey, "value of "+pkey);
        }
        
        String searchfor=KeyUtils.generateSearchPatternUpTo("prj2","ec1","ent1","m1",ENTITY_ID);
        ArrayList<String> result=new ArrayList();
        mio.forEachKey(3, p -> {
            if(p.regionMatches(true, 0, searchfor, 0, searchfor.length())) result.add(p);
            });
        
        mio.searchKeys(3, p->(p.regionMatches(true, 0, searchfor, 0, searchfor.length())?p:null));
        for(int i=0;i<result.size();i++)
            System.out.println("s:"+result.get(i));
    }
}

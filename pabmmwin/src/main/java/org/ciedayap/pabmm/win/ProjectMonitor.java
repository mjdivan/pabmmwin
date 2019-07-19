/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ciedayap.pabmm.win;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.ciedayap.pabmm.pd.MeasurementProject;
import org.ciedayap.pabmm.win.shedding.LoadShedder;
import org.ciedayap.utils.StringUtils;

/**
 * It is responsible for monitoring and updating all projects
 * @author Mario Diván
 * @version 1.0
 */
public class ProjectMonitor{
    public static final short HIGH_PRIORITY=1;
    public static final short NORMAL_PRIORITY=0;
    public static final short LOW_PRIORITY=-1;
    
    /**
     * It contains the current priority of each project
     */
    private final ConcurrentHashMap<String,Short> priorities;
    
    /**
     * The load shedder. It is based on the priority of each project
     */
    private LoadShedder loadShedder;
        
    /**
     * The active projects receiving data
     */
    private final ConcurrentHashMap<String,ProjectMetadata> activeProjects;
    
    /**
     * It indicates the end of the active Thread
     */
    private boolean shutdown=false;
    /**
     * It determines the lowest level for a given project in order to receive their data
     */
    private short fromPolicyLevelUptoHigh;
    
    /**
     * Default Constructor
     * @param actives The active projects to be monitored
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when active projects is null
     */
    public ProjectMonitor(ConcurrentHashMap<String,ProjectMetadata> actives) throws PAbMMWindowException
    {
        if(actives==null) throw new PAbMMWindowException("There is not active projects");
        
        activeProjects=actives;
        priorities=new ConcurrentHashMap();
        shutdown=false;
    }
    
    /**
     * DEfault factory method
     * @param capacity The initial capacity for active projects
     * @return A new instance of ProjectMonitor
     * @throws org.ciedayap.pabmm.win.PAbMMWindowException It is raised when active projects could not be created
     */
    public synchronized static final ProjectMonitor create(int capacity) throws PAbMMWindowException
    {
        int pcap=(capacity<10)?10:capacity;
        return new ProjectMonitor(new ConcurrentHashMap(pcap));
    }
    
    /**
     * It return the ProjectMetadata instance related to the indicated projectID 
     * @param projectID The projectID to be reached
     * @return  The ProjectMetadata instance related to the projectID's value, NULL otherwise.-
     */
    public synchronized ProjectMetadata get(String projectID)
    {
        if(StringUtils.isEmpty(projectID)) return null;
        
        return activeProjects.get(projectID);
    }
    
    /**
     * It loads the project and prepares the memory structures for receiving data
     * @param definition The new project definition
     * @param priority The priority to be assigned to the project (See @HIGH_PRIORITY
     * @return TRUE when the project was loaded and the memory structure was initialized
     * @throws PAbMMWindowException 
     */
    public synchronized boolean loadAProject(MeasurementProject definition,Short priority) throws PAbMMWindowException
    {
        if(definition ==null) return false;
        if(!definition.isDefinedProperties()) return false;
        switch(priority)
        {
            case ProjectMonitor.HIGH_PRIORITY:
            case ProjectMonitor.LOW_PRIORITY:
            case ProjectMonitor.NORMAL_PRIORITY:
                break;
            default:
                throw new PAbMMWindowException("The indicated priority is invalid");                    
        }
        
        ProjectMetadata pm=ProjectMetadata.create(definition);
        
        if(pm==null) return false;
        
        ProjectMetadata previous=activeProjects.put(definition.getID(), pm);
        if(previous!=null) previous.clearAndStopAll();
        priorities.put(definition.getID(), priority);
        
        return true;
    }
    
    /**
     * It add the measuring data in the respective slot related to the project
     * @param prjID The project ID
     * @param ecID The entity category ID
     * @param eID The entity ID
     * @param mID The metric ID
     * @param md The measuring data itself
     * @return TRUE when the measure was incorporated, FALSE otherwise.
     */
    public synchronized boolean add(String prjID,String ecID,String eID,String mID,MeasuringData md) 
    {
        if(activeProjects==null || activeProjects.isEmpty()) {
            System.out.println("Not Active Projects");
            return false;
        }
        if(!activeProjects.containsKey(prjID)){
            System.out.println("PRJ_ID: "+prjID+" does not exist");
            return false;
        }
        
        return activeProjects.get(prjID).add(prjID, ecID, eID, mID, md);
    }
    
    /**
     * @return the shutdown
     */
    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * @param shutdown the shutdown to set
     */
    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    /**
     * @return the fromPolicyLevelUptoHigh
     */
    public short getFromPolicyLevelUptoHigh() {
        return fromPolicyLevelUptoHigh;
    }

    /**
     * @param fromPolicyLevelUptoHigh the fromPolicyLevelUptoHigh to set
     */
    public void setFromPolicyLevelUptoHigh(short fromPolicyLevelUptoHigh) {
        this.fromPolicyLevelUptoHigh = fromPolicyLevelUptoHigh;
    }
    
    /**
     * It clean all the in-memory structures when shutdown has been indicated (shutdown=true)
     */
    protected void clearAll()
    {
        if(!shutdown) return;
        
        if(priorities!=null) priorities.clear();
        if(this.loadShedder!=null) loadShedder.shutdown();
        if(activeProjects!=null)
        {
            Collection<ProjectMetadata> col=activeProjects.values();
            col.stream().forEach(ProjectMetadata::clearAndStopAll);
        }      
    }
}

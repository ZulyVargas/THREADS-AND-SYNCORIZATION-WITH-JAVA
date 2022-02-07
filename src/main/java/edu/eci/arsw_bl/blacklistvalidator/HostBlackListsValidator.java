/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw_bl.blacklistvalidator;

import edu.eci.arsw_bl.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicMenuBarUI;
import javax.xml.stream.events.StartDocument;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private CopyOnWriteArrayList<Integer> blackListOcurrences = new CopyOnWriteArrayList<Integer>();
    private AtomicInteger ocurrencesCount;
    private AtomicInteger checkedListsCount;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int numberOfThreads){
          
        //int ocurrencesCount=0;
        ocurrencesCount = new AtomicInteger(0);
        checkedListsCount = new AtomicInteger(0);
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
    	//List of threads
        ArrayList<BlackListValidator> threads = new ArrayList<BlackListValidator>();
    	
    	//Entire part, decimal part and residue
        
        int entirePart = skds.getRegisteredServersCount()/numberOfThreads;
        int residue = skds.getRegisteredServersCount()%numberOfThreads;
        
        
        //Creation of threads with their corresponding arguments
        int initial_server = 0;
        int final_server = 0;
        
            	
        for (int n = 0; n < numberOfThreads && ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT;n++){	
        	if (n == numberOfThreads - 1) {
        		final_server = initial_server + entirePart + residue - 1 ;
        	}else final_server = initial_server + entirePart - 1;
        	
        	BlackListValidator thread = new BlackListValidator(initial_server, final_server,ipaddress, skds, blackListOcurrences, ocurrencesCount,checkedListsCount);
        	threads.add(thread);	
        	initial_server = final_server + 1;
        	thread.start();
        }
        
        for (BlackListValidator blv: threads) {
        	try {
				blv.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

        
        /*int i = 0;
        for (BlackListValidator blv: threads) {
        	ocurrencesCount = ocurrencesCount + blv.getOcurrencesCount();
        	blackListOcurrences.addAll(blv.getBlackListOcurrences());
        	checkedListsCount = checkedListsCount + blv.getCheckedListsCount();
        	i++;
        }*/
        
        
        
        if (ocurrencesCount.get() >= BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
   
}

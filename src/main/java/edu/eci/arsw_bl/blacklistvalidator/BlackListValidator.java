package edu.eci.arsw_bl.blacklistvalidator;

import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw_bl.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

/**
 * 
 * 
 * @author Camilo Pichimata, Zuly Vargas 
 *
 */
public class BlackListValidator extends Thread {
	
	private static final int BLACK_LIST_ALARM_COUNT=5;
	private int initial_server, final_server;
	private AtomicInteger ocurrencesCount;
	private String ipaddress; 
	private HostBlacklistsDataSourceFacade skds;
	private CopyOnWriteArrayList<Integer> blackListOcurrences = new CopyOnWriteArrayList();
	//private LinkedList<Integer> blackListOcurrences;
	private AtomicInteger checkedListsCount;
	
	
	public BlackListValidator (int initial_server, int final_server, String ipaddress, HostBlacklistsDataSourceFacade skds, CopyOnWriteArrayList blackListOcurrences, AtomicInteger ocurrencesCount, AtomicInteger checkedListsCount){
		this.initial_server = initial_server;
		this.final_server = final_server;
		this.ocurrencesCount = ocurrencesCount ;
		this.ipaddress = ipaddress;
		this.skds = skds;
		this.blackListOcurrences = blackListOcurrences;
		this.checkedListsCount = checkedListsCount; 
	}
	
	/**
	 * Performs the search for the ip address in the malicious server lists.
	 *   
	 */
	public void run() {
		for (int i = initial_server ; i <= final_server && ocurrencesCount.get() < BLACK_LIST_ALARM_COUNT ; i++) {
			checkedListsCount.getAndIncrement();
			if (skds.isInBlackListServer(i, ipaddress)){         
				blackListOcurrences.add(i);                
			    ocurrencesCount.getAndIncrement();
			}	
		}
	}
	
	/**
	 * Returns the number of occurrences of malicious servers.
	 * @return ocurrencesNumber - integer with the number of occurrences of malicious servers.
	 */
	public AtomicInteger getOcurrencesCount() {
		return (AtomicInteger) ocurrencesCount;
	}

	public void setOcurrencesCount(AtomicInteger ocurrencesNumber) {
		this.ocurrencesCount = ocurrencesNumber;
	}
	
	/**
	 * Returns the list of blacklists where the ip address was found.
	 * @return blackListOcurrences - list of blacklists where the ip address was found.
	 */
	public CopyOnWriteArrayList<Integer> getBlackListOcurrences() {
		return blackListOcurrences;
	}

	public void setBlackListOcurrences(CopyOnWriteArrayList<Integer> blackListOcurrences) {
		this.blackListOcurrences = blackListOcurrences;
	}

	public AtomicInteger getCheckedListsCount() {
		return checkedListsCount;
	}
}

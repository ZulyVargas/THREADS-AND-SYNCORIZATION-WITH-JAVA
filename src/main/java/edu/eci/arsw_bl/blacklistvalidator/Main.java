/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw_bl.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
    	long start = System.currentTimeMillis();
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        List<Integer> blackListOcurrences=hblv.checkHost("202.24.34.55", 20);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        long end = System.currentTimeMillis(); 
        //Ejecution time:
        double time = (end-start);
        System.out.println("Solution time  " + time);
        //System.out.println(""+Runtime.getRuntime().availableProcessors());
        }
}


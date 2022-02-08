package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    //Atomic para que sea actualizada una vez a la vez
    //private int health;
    private AtomicInteger health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());
    
    private boolean paused = false;
    
    public boolean dead = false;




	public Immortal(String name, List<Immortal> immortalsPopulation, AtomicInteger health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {
        while (!dead) {
            Immortal im;
            //Pausado (desde el método del botón)
            while (paused) {
            	try {
            		synchronized(this) {
            			this.wait();
            		}
            	}catch(InterruptedException e) {
            		e.printStackTrace();
            	}
            }
            
            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i2) {
    		Immortal immortal1;
    		Immortal immortal2;
    		
    		//El primero a bloquear es el menor
    		immortal1 = this.getId() < i2.getId() ? this : i2;
    		immortal2 = this.getId() > i2.getId() ? this : i2;
    		
    		if (this.getHealth().get() <= 0) {
    			this.dead = true;
    			immortalsPopulation.remove(this);
    		}
    		else if (i2.getHealth().get() > 0) {
    			synchronized (immortal1) {
    				synchronized (immortal2) {
	    				i2.changeHealth(i2.getHealth().get() - defaultDamageValue);
	    				//this.health += defaultDamageValue;
    					this.health.getAndAdd(defaultDamageValue);
    					updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
    				}
    			}
	        }else {
	        		updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
	        }
    }

    public void changeHealth(int v) {
    	//Cambia a lo que llega
        health.getAndSet(v);
    }

    public AtomicInteger getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
    public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}

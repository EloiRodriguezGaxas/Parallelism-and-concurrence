package exer_03;

import java.util.concurrent.Semaphore;

public class MTTT_SEM {
	
	
	private static final int INSTANCES = 10;
	
	// launcher;
	public static void main (String [] args) throws InterruptedException  {
		
		Tic tics [] = new Tic[INSTANCES];
		Tac tacs [] = new Tac[INSTANCES];
		Toe toes [] = new Toe[INSTANCES];
		
		Semaphore semTic = new Semaphore(1);
		Semaphore semTac = new Semaphore(0);
		Semaphore semToe = new Semaphore(0);
		
		Semaphore two_tac = new Semaphore(0, true);
		
		Shared shared = new Shared();
		
		/* COMPLETE */
		
		for (int i=0; i<INSTANCES; i++) {
			tics[i] = new Tic(semTic, semTac, two_tac, i);
			tacs[i] = new Tac(semTac, semToe, two_tac, i, shared);
			toes[i] = new Toe(semToe, semTic, i);
			tacs[i].start();
			toes[i].start();
			tics[i].start();
		}
		
		Thread.sleep(10000);
		
		for (int i=0; i<INSTANCES; i++) {
			tacs[i].stop();
			tics[i].stop();
			toes[i].stop();
		}		
	}
}

class Shared {
	
	private volatile int lastTac = -1000;

	public int getLastTac() {
		return lastTac;
	}

	public void setLastTac(int lastTac) {
		this.lastTac = lastTac;
	}
	
	
}

class Tic extends Thread {
	
	Semaphore semTic;
	Semaphore semTac;
	Semaphore mutex;
	int id;
	
	public Tic(Semaphore semTic, Semaphore semTac, Semaphore mutex, int id) {
		super();
		this.semTic = semTic;
		this.semTac = semTac;
		this.mutex = mutex;
		this.id = id;
	}
	
	public void run() {
		while(true) {
			
			try { this.semTic.acquire(); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
			System.out.print("TIC("+this.id+")-");
			
			this.mutex.release();
			this.semTac.release();
			
		}
	}
	
}

class Tac extends Thread {
	
	Semaphore semTac;
	Semaphore semToe;
	Semaphore two_tac;
	int id;
	Shared shared;
	
	public Tac(Semaphore semTac, Semaphore semToe, Semaphore two_tac, int id, Shared shared) {
		super();
		this.semTac = semTac;
		this.semToe = semToe;
		this.two_tac = two_tac;
		this.id = id;
		this.shared = shared;
	}
	
	public void run() {
		while(true) {
			
			try { this.semTac.acquire(); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
			if(this.two_tac.tryAcquire()) {
				
				System.out.print("TAC("+this.id+")");
				this.semTac.release();
				this.shared.setLastTac(this.id);
				
			} else {
				if(this.shared.getLastTac() != this.id) {
				
					System.out.print("TAC("+this.id+")");
					this.semToe.release();
				
				} else {
					this.semTac.release();
				}
			
			}
			
		}
	}
		
}

class Toe extends Thread {
	
	Semaphore semToe;
	Semaphore semTic;
	int id;
	
	public Toe(Semaphore semToe, Semaphore semTic, int id) {
		super();
		this.semToe = semToe;
		this.semTic = semTic;
		this.id = id;
	}
	
	public void run() {
		while(true) {
			
			try { this.semToe.acquire(); } 
			catch (InterruptedException e) { e.printStackTrace(); }
			
			System.out.println("-TOE("+this.id+")");
			
			this.semTic.release();
			
		}
	}
	
}

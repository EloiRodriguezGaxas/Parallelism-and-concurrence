package exer_02;

import java.util.concurrent.atomic.*;

public class MTTT_TS {
	
	public static final int LET_TIC = 1;
	public static final int LET_TAC = 2;
	public static final int LET_TOE = 3;
	public static final int WRITING = -1000;
	
	private static final int INSTANCES = 10;
	
	// launcher;
	public static void main (String [] args) throws InterruptedException  {
		
		Tic tics [] = new Tic[INSTANCES];
		Tac tacs [] = new Tac[INSTANCES];
		Toe toes [] = new Toe[INSTANCES];
		
		/* COMPLETE */
		
		AtomicInteger sync = new AtomicInteger(MTTT_TS.LET_TIC);
		AtomicInteger counter = new AtomicInteger(0);
		
		for (int i=0; i<INSTANCES; i++) {
			tics[i] = new Tic(sync, i);
			tacs[i] = new Tac(sync, i, counter);
			toes[i] = new Toe(sync, i);
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

class Tic extends Thread {
	
	private AtomicInteger sync;
	private int i;
	
	public Tic(AtomicInteger sync, int i) {
		super();
		this.sync = sync;
		this.i = i;
	}
	
	public void run() {
		while(true){
			
			while(!this.sync.compareAndSet(MTTT_TS.LET_TIC, MTTT_TS.WRITING)) { yield(); }
			
			System.out.print("TIC("+this.i+")-");
			
			this.sync.set(MTTT_TS.LET_TAC);
			
		}
	}
}

class Tac extends Thread {
	
	private AtomicInteger sync;
	private int i;
	private AtomicInteger counter;
	
	public Tac(AtomicInteger sync, int i, AtomicInteger counter) {
		super();
		this.sync = sync;
		this.i = i;
		this.counter = counter;
	}
	
	public void run() {
		while(true) {
			
			while(!this.sync.compareAndSet(MTTT_TS.LET_TAC, MTTT_TS.WRITING)) { yield(); }
				
			if(this.counter.compareAndSet(0, 1)) {
				
				System.out.print("TAC("+i+")");
				this.sync.set(MTTT_TS.LET_TAC);
			
			} else {
				
				if(this.counter.compareAndSet(1, 2)) {
				
					System.out.print("TAC("+i+")");
					this.sync.set(MTTT_TS.LET_TOE);
					this.counter.set(0);
				
				}
				
			}
				
		}
	}
	
}

class Toe extends Thread {

	private AtomicInteger sync;
	private int i;
	
	public Toe(AtomicInteger sync, int i) {
		super();
		this.sync = sync;
		this.i = i;
	}
	
	public void run() {
		while(true) {
			while(!this.sync.compareAndSet(MTTT_TS.LET_TOE, MTTT_TS.WRITING)) { yield(); }
			System.out.println("-TOE("+i+")");
			this.sync.set(MTTT_TS.LET_TIC);
		}
	}
	
}

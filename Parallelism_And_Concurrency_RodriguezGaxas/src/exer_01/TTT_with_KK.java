package exer_01;


// TIC-TACTAC-TOE with a KIT-KAT. Test and Set version

public class TTT_with_KK {
	
	// launcher
	public static void main (String [] args) throws InterruptedException {
		
		Thread[] threads = new Thread[4];
		Sync sync = new Sync();
		
		threads[0] = new Tic(sync);
		threads[1] = new Tac(sync);
		threads[2] = new Toe(sync);
		threads[3] = new KitKat(sync);
		
		for (Thread t : threads) t.start();
		
		Thread.sleep(30000);
		
		for (Thread t : threads) t.stop();
	}

}


class Sync {
	/* COMPLETE */
	
	private volatile boolean letTic = true;
	private volatile boolean letTac = false;
	private volatile boolean letToe = false;
	private volatile boolean letKitKat = false;
	
	private volatile boolean wantKitKat;
	
	public boolean getWantKitKat() {
		return wantKitKat;
	}
	public void setWantKitKat(boolean wantKitKat) {
		this.wantKitKat = wantKitKat;
	}
	public boolean getLetKitKat() {
		return letKitKat;
	}
	public void setLetKitKat(boolean letKitKat) {
		this.letKitKat = letKitKat;
	}
	public boolean getLetTic() {
		return letTic;
	}
	public void setLetTic(boolean letTic) {
		this.letTic = letTic;
	}
	public boolean getLetTac() {
		return letTac;
	}
	public void setLetTac(boolean letTac) {
		this.letTac = letTac;
	}
	public boolean getLetToe() {
		return letToe;
	}
	public void setLetToe(boolean letToe) {
		this.letToe = letToe;
	}
	
}

class Tic extends Thread {
	private Sync sync;
	
	public Tic (Sync sync) {
		this.sync = sync;
	}
	
	public void run () {
		while (true) {
			
			//this.sync.setWantKitKat(false);
			
			while(!this.sync.getLetTic() || this.sync.getWantKitKat()) { yield(); }
			
			System.out.print("TIC-");
			
			this.sync.setLetKitKat(false);
			this.sync.setLetTic(false);
			this.sync.setLetTac(true);
		}
	}
}

class Tac extends Thread {
	private Sync sync;
	private int times=0;
	
	public Tac (Sync sync) {
		this.sync = sync;
	}
	
	public void run () {
		while (true) {
			while(!this.sync.getLetTac()) { yield(); }
			
			if(this.times == 2) {
				this.times = 0;
				this.sync.setLetTac(false);
				this.sync.setLetKitKat(true);
				this.sync.setLetToe(true);
			}
			else{
				System.out.print("TAC");
				this.times++;
			}
		}
	}
}

class Toe extends Thread {

	private Sync sync;
	
	public Toe (Sync sync) {
		this.sync = sync;
	}
	
	public void run () {
		while (true) {
			
			//this.sync.setWantKitKat(false);
			
			while(!this.sync.getLetToe() || this.sync.getWantKitKat()) { yield(); }
			
			System.out.println("-TOE");
			
			this.sync.setLetKitKat(true);
			this.sync.setLetToe(false);
			this.sync.setLetTic(true);
			
		}
	}
	
}



class KitKat extends Thread {

	private Sync sync;
	
	public KitKat (Sync sync) {
		this.sync = sync;
	}
	
	public void run () {
		while (true) {
			
			try {this.sleep(200);} 
			catch (InterruptedException e) {e.printStackTrace();}
			
			this.sync.setWantKitKat(true);
			
			while(!this.sync.getLetKitKat() || !this.sync.getWantKitKat()) { yield(); }

			System.out.println();
			System.out.print("	Kit-Kat:");
			for(int i = 9; i>=0; i--){
				
				System.out.print(" "+i);
				
				try {this.sleep(200);} 
				catch (InterruptedException e) {e.printStackTrace();}
				
			}
			
			System.out.println();
			
			this.sync.setLetKitKat(false);
			this.sync.setLetToe(false);
			this.sync.setWantKitKat(false);
			this.sync.setLetTic(true);
		}
	}
	
}



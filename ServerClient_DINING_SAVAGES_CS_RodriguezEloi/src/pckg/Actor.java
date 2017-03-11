package pckg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Actor extends Thread {
	
	public static void main (String [] args) {
		Cook cook = new Cook();
		Savage [] savages = new Savage[5];
		for (int i=0; i<savages.length; i++) {
			savages[i] = new Savage(i);
			savages[i].start();
		}
		cook.start();
	}
	

	protected Socket connection;
	protected BufferedReader inputChannel;
	protected PrintWriter outputChannel;

	protected void connect() throws IOException {
		this.connection = new Socket("localhost", 4444);
		inputChannel = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		outputChannel = new PrintWriter(connection.getOutputStream(), true);
	}

	protected void disconnect() throws IOException {
		this.inputChannel.close();
		this.outputChannel.close();
		this.connection.close();
	}

	protected void receiveReply() throws IOException {
		this.inputChannel.readLine();
	}

	protected void sendRequest(String request) throws IOException {
		this.outputChannel.println(request);
		this.outputChannel.flush();
	}
}

class Savage extends Actor {
	
	private int id;
	
	public Savage (int id) {
		this.id = id;
	}
	
	public void run () {
		try {
			innerRun();
		}
		catch(IOException ioex) {ioex.printStackTrace(System.err);}
	}
	
	private void innerRun () throws IOException {
		while (true) {
			try{Thread.sleep(1000);}catch(InterruptedException iex) {}

			this.connect();
			this.sendRequest("WANT_TO_EAT");
			System.out.println("Savage("+id+") has been granted access to the pot");
			this.disconnect();
			
			this.connect();
			this.sendRequest("HELP_YOURSELF");
			this.receiveReply();
			System.out.println("Savage("+id+") has been given a serving");
			this.disconnect();
		}
	}
	
}

class Cook extends Actor {
	public void run () {
		try {
			innerRun();
		}
		catch(IOException ioex) {ioex.printStackTrace(System.err);}
	}
	
	private void innerRun () throws IOException {
		while (true) {
			try{Thread.sleep(1000);}catch(InterruptedException iex) {}
			this.connect();
			System.out.println("Cook will ask permission to refill the pot");
			this.sendRequest("REFILL");
			this.receiveReply();
			System.out.println("Cook has just refilled the pot");
			this.disconnect();
		}
	}
}

package pckg;

import java.io.*;
import java.net.*;
import java.util.*;

public class PotMonitor extends Thread {

	
	public static void main (String [] args) {
		PotMonitor pm = new PotMonitor();
		pm.start();
	}

	private ServerSocket serverSocket;
	private Socket connection;
	private BufferedReader inputChannel;
	private PrintWriter outputChannel;

	private String request;
	
	private static final int CAPACITY = 3;
	private int servings = CAPACITY;
	private boolean pleaseRefill = false;
	private boolean potIsFree = true;
	private Queue<ClientRep> potIsFreeQueue = new LinkedList<ClientRep>();
	private Queue<ClientRep> potIsNotEmptyQueue = new LinkedList<ClientRep>();
	private Queue<ClientRep> potIsEmptyQueue = new LinkedList<ClientRep>();
	
	public void run () {
		try {
			innerRun();
		}
		catch(IOException ioex) {ioex.printStackTrace(System.err);}
	}
	
	private void innerRun() throws IOException {
		serverSocket = new ServerSocket(4444);
	    System.out.println("Pot monitor server running and listening to port 4444");
		while (true) {
			acceptConnection();
			request = receiveRequest();
			switch(request) {
				case "WANT_TO_EAT": wantToEat(); break;
				case "HELP_YOURSELF": helpYourself(); break;
				case "REFILL": refill(); break;
				default: System.out.println("got a BAD request");
			}
		}
	}
	
	private void wantToEat() throws IOException {
		System.out.println("Processing WANT_TO_EAT request");
		if (!potIsFree) {
			System.out.println("    ... pot is NOT free. Savage must wait");
			
			this.potIsFreeQueue.add(new ClientRep(connection, inputChannel, outputChannel));
			
		}
		else {
			potIsFree = false;
			System.out.println("    ... pot is FREE. Savage can proceed");
			this.sendReply();
			this.disconnect();
			
		}
		System.out.println("exiting WANT_TO_EAT request\n");
	}
	
	private void helpYourself() throws IOException {
		System.out.println("Processing HELP_YOURSELF request");
		if (this.servings==0) {
			// awaken cook and wait until cook has done its job
			System.out.println("    ...Savage finds pot empty...");
			pleaseRefill = true;
			this.potIsNotEmptyQueue.add(new ClientRep(connection, inputChannel, outputChannel));
			
			if(!this.potIsEmptyQueue.isEmpty()){
				this.restoreConnection(this.potIsEmptyQueue.poll());
				this.refill();
			}
		}
		else {
			System.out.println("    A savage gets a serving from the pot");

			/* COMPLETE 4 */
			
			this.servings--;
			this.sendReply();
			this.disconnect();
			this.potIsFree = true;
			if (!this.potIsFreeQueue.isEmpty()) {
				restoreConnection(potIsFreeQueue.poll());
				wantToEat(); // reenter operation so that awakened savage can help himself
			}
		}
		System.out.println("exiting HELP_YOURSELF request\n");
	}
	
	private void refill() throws IOException {
		System.out.println("Processing REFILL request");
		if (!pleaseRefill) {
			System.out.println("    ...pot has servings yet. Make cook wait");
			
			this.potIsEmptyQueue.add(new ClientRep(connection, inputChannel, outputChannel));
			
		}
		else {
			System.out.println("    ...Pot seems empty ("+servings+") refilling");
			pleaseRefill = false;
			servings = CAPACITY;
			sendReply();
			disconnect();
			if (!this.potIsNotEmptyQueue.isEmpty()) {
				System.out.println("    ...Savage waiting for refilling awakened");
				restoreConnection(potIsNotEmptyQueue.poll());
				this.helpYourself();
				
			}
		}
		System.out.println("exiting REFILL request\n");
	}
	
	private String receiveRequest() throws IOException {
		return this.inputChannel.readLine();
	}

	private void sendReply() throws IOException {
		this.outputChannel.println("proceed...");
		this.outputChannel.flush();
	}

	private void acceptConnection() throws IOException {
		this.connection = this.serverSocket.accept();
		this.inputChannel = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
		this.outputChannel = new PrintWriter(this.connection.getOutputStream(), true);
	}

	private void disconnect() throws IOException {
		inputChannel.close();
    	outputChannel.close();
    	connection.close();
	}

	private void restoreConnection(ClientRep cr) {
		this.connection = cr.connection;
		this.inputChannel = cr.inputChannel;
		this.outputChannel = cr.outputChannel;
	}
}


class ClientRep {
	// utility class to store a connection and its channels
	public Socket connection;
	public BufferedReader inputChannel;
	public PrintWriter outputChannel;

	public ClientRep(Socket co, BufferedReader iCh, PrintWriter oCh) {
		this.connection = co;
		this.inputChannel = iCh;
		this.outputChannel = oCh;
	}

}


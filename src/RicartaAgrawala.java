import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;



public class RicartaAgrawala extends Thread{


	private int processID;
	private String state;
	private Queue<InetAddress> requestQ;
	private int noOfReplies;
	private InetAddress[] others;
	static final int N=5;
	static InetAddress CSIP;
	Socket sock[];

	public RicartaAgrawala(Socket s[]) {
		processID = new Random().nextInt(100);
		state = "RELEASED";
		requestQ = new LinkedList<InetAddress>();
		others = new InetAddress[N-1];
		sock = s;
		noOfReplies=0;

	}

	
	public InetAddress[] getOthers() {
		return others;
	}



	public void setOthers(InetAddress[] others) {
		this.others = others;
	}



	public String getStates() {
		return state;
	}



	public void setState(String state) {
		this.state = state;
	}



	public int getNoOfReplies() {
		return noOfReplies;
	}



	public void setNoOfReplies(int noOfReplies) {
		this.noOfReplies = noOfReplies;
	}



	public int getProcessID() {
		return processID;
	}



	public Queue<InetAddress> getRequestQ() {
		return requestQ;
	}



	public boolean requestCriticalSection()
	{

		state = "WANTED";
		for(int i=0; i<sock.length; i++)
		{
			try {
				sendRequest(sock[i]);

			} catch (IOException e) {
				try {
					sock[i].close();
				} catch (IOException e1) {
					System.out.println("Exception at closing socket at requestCriticalSection");
					e1.printStackTrace();
				}
				System.out.println("Exception at requestCriticalSection");
				e.printStackTrace();
			}
		}
		System.out.println("Waiting for replies from other processes...");
		while(noOfReplies != N-1)
		{
			//Wait
		}
		state = "HELD";

		return true;

	}

	public void releaseCriticalSection() throws IOException
	{
		state = "RELEASED";
		System.out.println("Released CS");
		InetAddress ip=null;
		while(!requestQ.isEmpty())
		{
			ip = requestQ.remove();
			for(int i=0; i<sock.length; i++)
			{
				if(ip.equals(sock[i].getInetAddress()))
				{
					sendReply(sock[i]);
				}
			}
			
		}
	}

	public void sendRequest(Socket s) throws IOException
	{
		PrintWriter send=null;
		try {
			send = new PrintWriter(s.getOutputStream(),true);
			System.out.println("Sending WANTED request to "+s.getInetAddress().getHostAddress());
			send.println("WANTED");
			send.println(InetAddress.getLocalHost().getHostAddress());
			send.println(processID);

		} catch (IOException e) {
			send.close();
			System.out.println("Exception");
			e.printStackTrace();
		}

	}

	public void getRequest(InetAddress ip,Socket s, int pid) throws IOException
	{
		if(state.equalsIgnoreCase("HELD") || (state.equalsIgnoreCase("WANTED") && this.processID > pid))
		{
			System.out.println("Queueing request from "+ip.getHostAddress());
			requestQ.add(ip);
		}
		else
		{
			sendReply(s);
		}
	}


	public void sendReply(Socket send) throws IOException
	{

		System.out.println("Sending reply to "+send.getInetAddress().getHostAddress());
		PrintWriter out = new PrintWriter(send.getOutputStream(),true);
		out.println("REPLY");
	}

	public void getReply()
	{
		noOfReplies++;
	}

	public void enterCriticalSection(InetAddress ip) throws IOException
	{
		PrintWriter outfile;
		boolean flag=false;
		for(int i=0; i<sock.length; i++)
		{
			if(ip.equals(sock[i].getInetAddress()))
			{
				flag=true;
				outfile = new PrintWriter(sock[i].getOutputStream(),true);
				System.out.println("Accessing Critical Section on "+sock[i].getInetAddress().getHostAddress());
				outfile.println("ACCESS");
				outfile.println(InetAddress.getLocalHost().getHostAddress()+" Writing to critical section...");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					System.out.println("Exception at Sleep");
					e.printStackTrace();
				}
				break;
			}
		}
		if(flag==false)
		{
			System.out.println("Accessing Critical Section on this system");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Exception at Sleep");
				e.printStackTrace();
			}
			BufferedWriter outputfile = new BufferedWriter(new FileWriter("CS.txt", true));
			outputfile.newLine();
			outputfile.write(ip.getHostAddress()+" Writing to Critical section...");
			outputfile.flush();
			outputfile.close();
		}
		
		releaseCriticalSection();

	}


}

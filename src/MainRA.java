import java.io.*;
import java.net.*;
import java.util.Scanner;



public class MainRA {

	PrintWriter out[];
	BufferedReader in[];
	InetAddress ipadd[];
	ServerSocket serverskt[]; 
	Socket skt[];
	RicartaAgrawala ra;
	int no;
	int port=7000;
	static final int N=5;

	public MainRA(InetAddress ip[], int n) {
		no = n;
		out = new PrintWriter[N-1];
		in = new BufferedReader[N-1];
		
		skt = new Socket[N-1];
		ipadd = ip;
		createSockets();
		createStreams();
	}

	public void createSockets()
	{
		if(no == 1)
		{
			serverskt = new ServerSocket[N-no];
			System.out.println("Node 1");
			for(int i=0; i<serverskt.length; i++)
			{
				try {
					serverskt[i] = new ServerSocket(port+i);
				} catch (IOException e) {
					try {
						serverskt[i].close();
					} catch (IOException e1) {
						System.out.println("Exception at closing ServerSocket "+i);
						e1.printStackTrace();
					}
					System.out.println("Exception at ServerSocket "+i);
					e.printStackTrace();
				}
			}
			for(int i=0; i<serverskt.length; i++)
			{
				try {
					skt[i] = serverskt[i].accept();
				} catch (IOException e) {
					try {
						skt[i].close();
					} catch (IOException e1) {
						System.out.println("Exception at closing Socket "+i);
						e1.printStackTrace();
					}
					System.out.println("Exception at ServerSocket accept"+i);
					e.printStackTrace();
				}
			}

		}
		else if(no == 2)
		{
			System.out.println("Node 2");
			serverskt = new ServerSocket[N-no];
			process(no, 0);
		}
		else if(no == 3)
		{
			serverskt = new ServerSocket[N-no];
			System.out.println("Node 3");
			process(no, 1);
		}
		else if(no == 4)
		{
			serverskt = new ServerSocket[N-no];
			System.out.println("Node 4");
			process(no, 2);
		}
		else if(no == 5)
		{
			serverskt = new ServerSocket[N-no];
			System.out.println("Node 5");
			process(no, 3);
		}

		System.out.println("Created Sockets");
		ra = new RicartaAgrawala(skt);
	}

	public void createStreams()
	{
		for(int i=0; i<skt.length; i++)
		{
			try {
				in[i] = new BufferedReader(new InputStreamReader(skt[i].getInputStream()));
				out[i] = new PrintWriter(skt[i].getOutputStream(), true);
			} catch (Exception e) {
				System.out.println("Exception at stream");
				e.printStackTrace();
			}
		}
		
	}
	
	public void process(int no, int p)
	{
		
		for(int i=0; i<ipadd.length; i++)
		{
			try {
				skt[i] = new Socket(ipadd[i],port+p);
			} catch (IOException e) {
				try {
					skt[i].close();
				} catch (IOException e1) {
					System.out.println("Exception at closing Client Socket "+i);
					e1.printStackTrace();
				}
				System.out.println("Exception at Client Socket "+i);
				e.printStackTrace();
			}
		}
		int k=0;
		for(int i=0; i<serverskt.length; i++)
		{
			
			try {
				serverskt[i] = new ServerSocket(port+k+no-1);
				k++;
			} catch (IOException e) {
				try {
					serverskt[i].close();
				} catch (IOException e1) {
					System.out.println("Exception at Server Socket "+i);
					e1.printStackTrace();
				}
				System.out.println("Exception at ServerSocket "+i);
				e.printStackTrace();
			}
		}
		int m=0;
		for(int i=ipadd.length; i<skt.length; i++)
		{
			try {
				skt[i] = serverskt[m].accept();
				m++;
			} catch (IOException e) {
				try {
					skt[i].close();
				} catch (IOException e1) {
					System.out.println("Exception at closing Socket "+i);
					e1.printStackTrace();
				}
				System.out.println("Exception at ServerSocket accept"+i);
				e.printStackTrace();
			}
		}
	}
	
	public void startThreads()
	{
		MessageHandlerRA mh[] =new MessageHandlerRA[N-1];
		for(int i=0; i<mh.length; i++)
		{
			try {
				mh[i] = new MessageHandlerRA(skt[i]);
				mh[i].start();
			} catch (IOException e) {
				System.out.println("Exception at startThreads");
				e.printStackTrace();
			}
		}
	}
	static String csip;
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub

		int n = Integer.parseInt(args[0]);
		
		InetAddress ip[]=null;
		
		if(args.length > 1){
			csip = args[1];
			ip = new InetAddress[args.length-1];
			for(int i=1; i<args.length; i++)
			{
				try {
					ip[i-1] = InetAddress.getByName(args[i]);
					
				} catch (UnknownHostException e) {
					System.out.println("Exception at InetAddress");
					e.printStackTrace();
				}
			}
		}
		else{
			csip = InetAddress.getLocalHost().getHostAddress();
		}
		
		MainRA m = new MainRA(ip, n);
		m.startThreads();
		
		Scanner in = new Scanner(System.in);
		String input;
		
		System.out.println("Enter command 1. REQUEST (To enter CS) 2.STOP");
		while(!(input = in.nextLine()).equalsIgnoreCase("stop"))
		{
			if(input.equalsIgnoreCase("REQUEST"))
			{
				m.ra.requestCriticalSection();
				m.ra.enterCriticalSection(InetAddress.getByName(csip));
				
			}
		}
		in.close();
		System.exit(0);
		
	}


class MessageHandlerRA extends Thread
{
	Socket con;
	BufferedReader in;
	PrintWriter out;
	
	
	public MessageHandlerRA(Socket s) throws IOException {
		con = s;
		
		try {
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			out = new PrintWriter(con.getOutputStream(), true);
		} catch (IOException e) {
			in.close();
			out.close();
			System.out.println("Exception at constructor");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		
		String msg;
		
		try {
			while((msg = in.readLine()) != null)
			{
				System.out.println("Message recieved from "+con.getInetAddress().getHostAddress()+" "+msg);
				
				if(msg.equalsIgnoreCase("REPLY"))
				{
					ra.getReply();
					if(ra.getNoOfReplies() == N-1)
					{
						ra.setState("HELD");
						ra.enterCriticalSection(InetAddress.getByName(csip));
					}
				}
				if(msg.equalsIgnoreCase("WANTED"))
				{
					String ip = in.readLine();
					int pid = Integer.parseInt(in.readLine());
					InetAddress ipadd = InetAddress.getByName(ip);
					for(int i=0; i<ra.sock.length; i++)
					{
						if(ipadd.equals(ra.sock[i].getInetAddress()))
						{
							ra.getRequest(ipadd, ra.sock[i], pid);
						}
					}
				
				}
				if(msg.equalsIgnoreCase("ACCESS"))
				{
					System.out.println("Entered CS");
					String s = in.readLine();
					System.out.println(s);
					synchronized(this){
					BufferedWriter outputfile = new BufferedWriter(new FileWriter("CS.txt", true));
					outputfile.newLine();
					outputfile.write(s);
					outputfile.newLine();
					outputfile.flush();
					outputfile.close();
					}
				}
			}
		} catch (IOException e) {
			try {
				in.close();
			} catch (IOException e1) {
				System.out.println("exception at closing socket");
				e1.printStackTrace();
			}
			System.out.println("Exception at reading message");
			e.printStackTrace();
		}
	}
}

}
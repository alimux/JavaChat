package dnr2i.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread extends Thread 
{

	private ChatServer server;
	private Socket socket;
	private String username;
	private int x;
	private int y;
	private BufferedReader streamIn;
	private PrintWriter streamOut;
	
	public ChatServerThread(ChatServer server, Socket socket, String loginMessage) throws IOException
	{
		super();
		this.server = server;
		this.socket = socket;
		
		String[] data = loginMessage.split(",");
		
		if(data.length == 3) {
			System.out.println("The message shape of Directive LOGIN is correct, connexion almost done.");
			this.username = data[0];
			this.x = Integer.parseInt(data[1]);
			this.y = Integer.parseInt(data[2]);
		} else {
			System.out.println("The message shape of Directive LOGIN is incorrect, avorting.");
			this.close();
		}
		
	}
	
	public void open() throws IOException
	{
		System.out.println("Opening thread listening");
		this.streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.streamOut = new PrintWriter(socket.getOutputStream());
	}
	
	public void close() throws IOException
	{
		System.out.println("Terminating thread.");
		if (this.socket != null) {
			this.socket.close();
		}
		if (this.streamIn != null) {
			this.streamIn.close();
		}
		if (this.streamOut != null) {
			this.streamOut.close();
		}
	}
	
	@Override
	public void run() 
	{
		System.out.println("Server thread " + this.username + " is running.");
		
		while(true) {
			try {
				String clientDirective = this.streamIn.readLine();
				System.out.println("New client " + this.username + " directive received: " + clientDirective);
				
				switch(clientDirective) {
					case "SET_MSG":
						String input = this.streamIn.readLine();
						System.out.println("Message content received: " + input);
						this.server.handleClientMessage(this.username, input);
						break;
					case "LOGOUT":
						//TODO
						break;
					default:
						System.out.println("Unknown directive received: " + clientDirective);
				}
			} catch(IOException e) {
				System.out.println(this.username + " Error reading incoming message: " + e.getMessage());
				server.removeClient(this.username);
				this.interrupt();
			}
		}
	}
	
	public void send(String msg)
	{
		System.out.println("SEND to " + this.username + " message: " + msg);
		this.streamOut.println(msg);
		this.streamOut.flush();
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}

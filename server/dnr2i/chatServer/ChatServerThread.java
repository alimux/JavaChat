package dnr2i.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread implements Runnable
{

	private ChatServer server;
	private Socket socket;
	private String username;
	private int x, y;
	private BufferedReader streamIn;
	private PrintWriter streamOut;
	private Thread t2, t3;
	
	
	public ChatServerThread(ChatServer server, Socket socket) throws IOException
	{
		System.out.println("New thread creation");
		this.server = server;
		this.socket = socket;
		this.streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.streamOut = new PrintWriter(socket.getOutputStream());
		
		String clientDirective = this.streamIn.readLine();
		if (clientDirective.equals("LOGIN")) {
			System.out.println("Directive LOGIN received, handling the request...");
			String input = this.streamIn.readLine();
			this.handleLogin(input);
		} else {
			System.out.println("The connection directive is incorrect: " + clientDirective + ", avorting...");
			this.close();
		}
	
	}
	
	private void handleLogin(String loginMessage) throws IOException
	{
		String[] data = loginMessage.split(",");
		
		if(data.length == 3) {
			System.out.println("The message shape of Directive LOGIN is correct, connexion almost done.");
			this.username = data[0];
			if(!this.server.userExist(username)) {
				this.x = Integer.parseInt(data[1]);
				this.y = Integer.parseInt(data[2]);
				this.server.addClient(this);
				System.out.println("Connexion complete!");
			} else {
				System.out.println("This username already exist, avorting...");
				this.close();
			}
		} else {
			System.out.println("The message shape of Directive LOGIN is incorrect, avorting...");
			this.close();
		}
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
		try {
			while(true) {
				System.out.println("Waiting for new client directive");
			
				String clientDirective = this.streamIn.readLine();
				System.out.println("New client " + this.username + " directive received: " + clientDirective);
				
				switch(clientDirective) {
					case "SET_MSG":
						String input = this.streamIn.readLine();
						System.out.println("Directive SET_MSG received, handling the request...");
						this.server.handleClientMessage(this.username, input);
						break;
					case "GET_USERS_LIST":
						System.out.println("Directive GET_USERS_LIST received, handling the request...");
						this.streamOut.println("SET_USERS_LIST");
						this.streamOut.println(this.server.getUsersList());
						this.streamOut.flush();
						System.out.println("Response sent");
					case "GET_NB":
						System.out.println("Directive GET_NB received, handling the request...");
						this.streamOut.println("SET_NB");
						this.streamOut.println(this.server.getNbClients());
						this.streamOut.flush();
						System.out.println("Response sent");
					case "LOGOUT":
						//TODO
						break;
					default:
						System.out.println("Unknown directive received: " + clientDirective);
				}
			}
		} catch(IOException e) {
			System.out.println(this.username + " Error reading incoming message: " + e.getMessage());
		} //finally { //most of the time for client deconexion
//				try {
//					System.out.println("Le client " + this.username + " c'est déconnecté");
//					server.removeClient(this.username);
//					this.socket.close();
//				} catch(IOException e) {
//					System.out.println("Unable to disconnect client " + this.username + ": " + e.getMessage());
//				}
				
			//}
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

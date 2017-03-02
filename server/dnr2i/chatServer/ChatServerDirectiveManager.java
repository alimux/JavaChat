package dnr2i.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A class for managing directive from the connected client
 * @author plabadille, Alexandre DUCREUX
 * @since February, 2017
 */
public class ChatServerDirectiveManager implements Runnable
{
	private ChatServer server;
	private Socket socket;
	private String username;
	private int x, y;
	private BufferedReader streamIn;
	private PrintWriter streamOut;	
	
	/**
	 * Manage directive flow (client/server) for a specific client (one instance by client)
	 * @param server
	 * @param socket
	 * @throws IOException
	 */
	public ChatServerDirectiveManager(ChatServer server, Socket socket) throws IOException
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
	
	/**
	 * Check if the content of LOGIN directive is correct and handle it.
	 * If the LOGIN directive content isn't correct the thread is destroy.
	 * @param loginMessage (content of the LOGIN directive)
	 * @throws IOException
	 */
	private void handleLogin(String loginMessage) throws IOException
	{
		String[] data = loginMessage.split("<END/>");
		
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

	/**
	 * Prepare the clean death of the thread.
	 * @throws IOException
	 */
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
	
	/**
	 * Wait for client directive to execute action.
	 * @see java.lang.Runnable#run()
	 * @throws IOException
	 */
	@Override
	public void run()
	{
		System.out.println("Server thread " + this.username + " is running.");
		try {
			while(true) {
				System.out.println("Waiting for new client directive");
			
				String clientDirective = this.streamIn.readLine();
				System.out.println("New client " + this.username + " directive received: " + clientDirective);
				
				String input;
				switch(clientDirective) {
					case "SET_MSG":
						input = this.streamIn.readLine();
						System.out.println("Directive SET_MSG received, handling the request...");
						this.server.handleClientMessage(this.username, input);
						break;
					case "GET_NB":
						System.out.println("Directive GET_NB received, handling the request...");
						this.streamOut.println("SET_NB");
						this.streamOut.println(this.server.getNbClients());
						this.streamOut.flush();
						System.out.println("Response sent");
						break;
					case "SET_COORDINATE":
						System.out.println("Directive SET_COORDINATE received, handling the request...");
						input = this.streamIn.readLine();
						this.changeCoordinate(input);
						this.server.handleNewCoordinate(input);
						break;
					case "LOGOUT":
						System.out.println("Directive LOGOUT received, handling the request...");
						input = this.streamIn.readLine();
						this.server.removeClient(input);
						this.close();
						break;
					default:
						System.out.println("Unknown directive received: " + clientDirective);
				}
			}
		} catch(IOException e) {
			System.out.println(this.username + " Error reading incoming message: " + e.getMessage());
		}
	}
	
	
	/**
	 * Send to the client a GET_MSG directive.
	 * Broadcast a message to the user associated with this thread
	 * @param output
	 */
	public void send(String output)
	{
		System.out.println("SEND to " + this.username + " message: " + output);
		this.streamOut.println("GET_MSG");
		this.streamOut.println(output);
		this.streamOut.flush();
	}
	
	/**
	 * Handle SET_COORDINATE directive send by the client
	 * Check if the content of SET_COORDINATE directive is correct and update coordinate.
	 * @param input
	 */
	private void changeCoordinate(String input)
	{
		String[] part = input.split("<END/>");
		if (part.length == 3) {
			System.out.println("The coordinate for user " + this.username + " have been change to " + part[1] + "/" + part[2]);
			this.setX(Integer.parseInt(part[1]));
			this.setY(Integer.parseInt(part[2]));
		} else {
			System.out.println("The shape of SET_COORDINATE directive received is not correct.");
		}
	}
	
	
    /**
     * Send to the client a SET_NEW_USER directive.
     * @param output
     */
    public void notifyNewUser(String output)
    {
    	System.out.println("Server notify client: "+ this.getUsername() + " join to connected users: " + output);
    	this.streamOut.println("SET_NEW_USER");
		this.streamOut.println(output);
		this.streamOut.flush();
    }
    
    /**
     * Send to the client a SET_USERS_LIST directive.
     * @param output
     */
    public void notifyUserList(String output)
    {
    	System.out.println("Server notify new client of connected user: " + output);
    	this.streamOut.println("SET_USERS_LIST");
		this.streamOut.println(output);
		this.streamOut.flush();
    }
    
    /**
     * Send to the client a SET_OLD_USER directive.
     * @param output
     */
    public void notifyOldUser(String output)
    {
    	System.out.println("Server notify client quit to connected users: " + output);
    	this.streamOut.println("SET_OLD_USER");
		this.streamOut.println(output);
		this.streamOut.flush();
    }
    
    /**
     * Send to the client a SET_NEW_COORDINATE directive.
     * @param output
     */
    public void notifyNewCoordinate(String output)
    {
    	System.out.println("Server notify client move to connected users: " + output);
    	this.streamOut.println("SET_NEW_COORDINATE");
		this.streamOut.println(output);
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
		System.out.println("update x: "+x );
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		System.out.println("update y: "+y );
		this.y = y;
	}
	
}

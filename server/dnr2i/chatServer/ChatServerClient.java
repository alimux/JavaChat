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
public class ChatServerClient implements Runnable
{

	private ChatServer server;
	private Socket socket;
	private String username;
	private int x, y;
	private BufferedReader streamIn;
	private PrintWriter streamOut;	
	
	/**
	 * Build a new client thread
	 * @param server
	 * @param socket
	 * @throws IOException
	 */
	public ChatServerClient(ChatServer server, Socket socket) throws IOException
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
					case "GET_USERS_LIST":
						System.out.println("Directive GET_USERS_LIST received, handling the request...");
						this.streamOut.println("SET_USERS_LIST");
						this.streamOut.println(this.server.getUsersList());
						this.streamOut.flush();
						System.out.println("Response sent");
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
						break;
					case "LOGOUT":
						System.out.println("Directive LOGOUT received, handling the request...");
						this.server.removeClient(this.username);
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
	 * Broadcast a message to the user associated with this thread
	 * @param msg
	 */
	public void send(String msg)
	{
		System.out.println("SEND to " + this.username + " message: " + msg);
		this.streamOut.println(msg);
		this.streamOut.flush();
	}
	
	/**
	 * Check if the content of SET_COORDINATE directive is correct and handle it.
	 * @param input
	 */
	private void changeCoordinate(String input)
	{
		String[] part = input.split(",");
		if (part.length == 2) {
			System.out.println("The coordinate for user " + this.username + " have been change to " + part[0] + "/" + part[1]);
			this.setX(Integer.parseInt(part[0]));
			this.setY(Integer.parseInt(part[1]));
		} else {
			System.out.println("The shape of SET_COORDINATE directive received is not correct.");
		}
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
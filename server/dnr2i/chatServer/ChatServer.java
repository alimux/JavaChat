package dnr2i.chatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/** 
 * Build a new instance of a multi-user chat server.
 * @author plabadille, Alexandre DUCREUX
 * @since February, 2017
 */
public class ChatServer 
{

	private HashMap<String, ChatServerClient> clients;
	private int nbClients = 0;
	
	/**
	 * Constructor of multi-user chat server using a port number.
	 * @param port
	 */
	public ChatServer(int port)
	{	
		this.clients = new HashMap<String, ChatServerClient>();
		ServerSocket serverSocket = null;
		try { 
			System.out.println("Trying to bind port " + port + ", please wait  ...");
			serverSocket = new ServerSocket(port);
			System.out.println("Server sucessfuly started: " + serverSocket);
			
			Thread t = new Thread(new ChatServerConnection(serverSocket, this));
			t.start();
		} catch(IOException e) {
			System.out.println("Can't bind port " + port + ": " + e.getMessage());
		}
	}
	
	/**
	 * Used to add a new client thread to the clients HashMap
	 * @param clientThread
	 * @throws IOException
	 */
	public synchronized void addClient(ChatServerClient clientThread) throws IOException
	{
		System.out.println("Adding new client: " + clientThread.getUsername() + " to client list");
		
		if (this.nbClients != 0) { 
			Set<Entry<String, ChatServerClient>> set = this.clients.entrySet();
			Iterator<Entry<String, ChatServerClient>> i = set.iterator();
			
			System.out.println("User connected: " + this.clients.size());
			
			//Notify others client:
			while(i.hasNext()) {
				Entry<String, ChatServerClient> me = i.next();
				me.getValue().notifyNewUser(clientThread.getUsername() + "," + clientThread.getX() + "," + clientThread.getY());
			}
			//Send new client the users list:
			clientThread.notifyUserList(this.getUsersList());
		}
		
		this.clients.put(clientThread.getUsername(),clientThread); 
		this.nbClients++;
	}
  
	/**
	 * Used to remove a client thread from the clients HashMap
	 * @param username
	 * @throws IOException
	 */
	public synchronized void removeClient(String username) throws IOException
	{
		System.out.println("Removing client: " + username + " from client list");
		
		if (this.nbClients > 1) {
			Set<Entry<String, ChatServerClient>> set = this.clients.entrySet();
			Iterator<Entry<String, ChatServerClient>> i = set.iterator();
			
			System.out.println("User connected: " + this.clients.size());
			
			//Notify others client:
			while(i.hasNext()) {
				Entry<String, ChatServerClient> me = i.next();
				if (me.getKey() != username) {
					me.getValue().notifyOldUser(username);
				}
			}
		}
		
		this.clients.remove(username);
		this.nbClients--;
		   
	}
	
	/**
	 * Used to send a message from an user to the others at proximity
	 * @param username
	 * @param message
	 */
	public synchronized void handleClientMessage(String username, String content)
	{
		System.out.println("handling message: " + content + " from: " + username);
		//We use an iterator to send the message to other client
		Set<Entry<String, ChatServerClient>> set = this.clients.entrySet();
		Iterator<Entry<String, ChatServerClient>> i = set.iterator();
		
		String out = username + "<END/>" + content;
		System.out.println("User connected: " + this.clients.size());
		while(i.hasNext()) {
			Entry<String, ChatServerClient> me = i.next();
			if (me.getKey() != username) {
				//TODO: check the location
				me.getValue().send(out);
			}
		}
	}
	
	public synchronized void handleNewCoordinate(String input)
	{
		Set<Entry<String, ChatServerClient>> set = this.clients.entrySet();
		Iterator<Entry<String, ChatServerClient>> i = set.iterator();
		
		while(i.hasNext()) {
			Entry<String, ChatServerClient> me = i.next();
			if (me.getKey() != input.split(",")[0]) {
				me.getValue().notifyNewCoordinate(input);
			}
		}
	}
	
	/**
	 * Use to get the prepared response for sending to the client the usersList
	 * @return response
	 */
	private String getUsersList()
	{
		System.out.println("Generating usersList...");
		Set<Entry<String, ChatServerClient>> set = this.clients.entrySet();
		Iterator<Entry<String, ChatServerClient>> i = set.iterator();
		String response = "";
   
		while(i.hasNext()) {
			Entry<String, ChatServerClient> me = i.next();
			response += me.getKey() + "," + me.getValue().getX() + "," + me.getValue().getY() + ";";
		}
		
		return response;
	}
	
	/**
	 * Use to get the number of client connected
	 * @return nbClients
	 */
	public int getNbClients()
	{
		return this.nbClients;
	}
  
	/**
	 * Check if a client username doesn't already exist
	 * @param username
	 * @return boolean
	 */
	public boolean userExist(String username)
	{
		return this.nbClients != 0 ? this.clients.containsKey(username) : false;
	}
	
}

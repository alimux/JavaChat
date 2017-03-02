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
	private final int RADIUS = 75;
	private HashMap<String, ChatServerDirectiveManager> clients;
	private int nbClients = 0;
	
	/**
	 * Constructor of multi-user chat server using a port number.
	 * @param port
	 */
	public ChatServer(int port)
	{	
		this.clients = new HashMap<String, ChatServerDirectiveManager>();
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
	public synchronized void addClient(ChatServerDirectiveManager clientThread) throws IOException
	{
		System.out.println("Adding new client: " + clientThread.getUsername() + " to client list");
		
		if (this.nbClients != 0) { 
			Set<Entry<String, ChatServerDirectiveManager>> set = this.clients.entrySet();
			Iterator<Entry<String, ChatServerDirectiveManager>> i = set.iterator();
			
			System.out.println("User connected: " + this.clients.size());
			
			//Notify others client:
			while(i.hasNext()) {
				Entry<String, ChatServerDirectiveManager> me = i.next();
				me.getValue().notifyNewUser(clientThread.getUsername() + "<END/>" + clientThread.getX() + "<END/>" + clientThread.getY());
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
			Set<Entry<String, ChatServerDirectiveManager>> set = this.clients.entrySet();
			Iterator<Entry<String, ChatServerDirectiveManager>> i = set.iterator();
			
			System.out.println("User connected: " + this.clients.size());
			
			//Notify others client:
			while(i.hasNext()) {
				Entry<String, ChatServerDirectiveManager> me = i.next();
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
		Set<Entry<String, ChatServerDirectiveManager>> set = this.clients.entrySet();
		Iterator<Entry<String, ChatServerDirectiveManager>> i = set.iterator();
		
		String out = username + "<END/>" + content;
		System.out.println("User connected: " + this.clients.size());
		
		ChatServerDirectiveManager centerUser = this.clients.get(username);
		int xCenter = centerUser.getX();
		int yCenter = centerUser.getY();
		while(i.hasNext()) {
			Entry<String, ChatServerDirectiveManager> me = i.next();
			if (me.getKey() != username) {
				//We check if this user is close enough to the sender
				System.out.println(xCenter +" "+ yCenter +" "+ me.getValue().getX() +" "+ me.getValue().getY());
				if (this.canEar(xCenter, yCenter, me.getValue().getX(), me.getValue().getY())) {
					me.getValue().send(out);
				}
			}
		}
	}
	
	public synchronized void handleNewCoordinate(String input)
	{
		String[] split = input.split("<END/>");
		
		Set<Entry<String, ChatServerDirectiveManager>> set = this.clients.entrySet();
		Iterator<Entry<String, ChatServerDirectiveManager>> i = set.iterator();
		
		while(i.hasNext()) {
			Entry<String, ChatServerDirectiveManager> me = i.next();
			if (me.getKey() != split[0]) { //we tell the other the change
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
		Set<Entry<String, ChatServerDirectiveManager>> set = this.clients.entrySet();
		Iterator<Entry<String, ChatServerDirectiveManager>> i = set.iterator();
		String response = "";
   
		while(i.hasNext()) {
			Entry<String, ChatServerDirectiveManager> me = i.next();
			response += me.getKey() + "<END/>" + me.getValue().getX() + "<END/>" + me.getValue().getY() + "<USER/>";
		}
		
		return response;
	}
	
	/**
	 * Check if a user is in the earing zone of another one.
	 * @param x_center
	 * @param y_center
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canEar(int x_center, int y_center, int x, int y)
	{	
		//the earing zone is a circle, we used pythagoras theorem to do the math.
		double compute = Math.pow((x - x_center), 2) + Math.pow((y - y_center), 2);
		double radiusSquared = Math.pow(this.RADIUS, 2);
		
		System.out.println("compute: " + compute + " radiusSquared: " + radiusSquared);
		System.out.print(compute <= radiusSquared ? true : false);
		//if you don't want the person in the circle line to match use '<' instead of '=='
		return compute <= radiusSquared ? true : false;
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

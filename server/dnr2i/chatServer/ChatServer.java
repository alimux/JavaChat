package dnr2i.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ChatServer implements Runnable
{
	
	private HashMap<String, ChatServerThread> clients;
	private ServerSocket server;
	private Thread thread;
	
	public ChatServer(int port) 
	{
		try {
			System.out.println("Trying to bind port " + port + ", please wait  ...");
			this.server = new ServerSocket(port);
			System.out.println("Server sucessfuly started: " + this.server);
			this.start();
		} catch(IOException e) {
			System.out.println("Can't bind port " + port + ": " + e.getMessage());
		}
		
		this.clients = new HashMap<String, ChatServerThread>();
	}
	
	@Override
	public void run()
	{
		while(this.thread != null) {
			try {
				System.out.println("Waiting for client directive ..."); 
	            this.manageGlobalClientDirective(this.server.accept());
			} catch(IOException e) {
				System.out.println("Server accept error: " + e); 
				this.stop();
			}
		}
	}
	
	public void start()  
	{ 
      if (this.thread == null) {  
    	 this.thread = new Thread(this); 
         this.thread.start();
      }
   }
	
   public void stop()   
   { 
      if (this.thread != null) {  
    	 this.thread.interrupt(); 
         this.thread = null;
      }
   }
   
   public synchronized void removeClient(String username)
   {
	   ChatServerThread toTerminate = this.clients.get(username);
	   System.out.println("Removing client thread " + toTerminate.getUsername());
	   this.clients.remove(username);
	   
	   try {
		   toTerminate.close();
	   } catch(IOException e) {
		   System.out.println("Error closing thread: " + e);
		   toTerminate.interrupt();
	   }
   }
   
   public synchronized void handleClientMessage(String username, String input)
   {   
	   // We use an iterator to send the message to other client
	   Set<Entry<String, ChatServerThread>> set = this.clients.entrySet();
	   Iterator<Entry<String, ChatServerThread>> i = set.iterator();
	   String message = username + "<END/>" + input;
	   
	   while(i.hasNext()) {
		   Entry<String, ChatServerThread> me = i.next();
		   if (me.getKey() != username) {
			   //TODO: check the location
			   me.getValue().send(message);
		   }
	   }
   }
   
   private void manageGlobalClientDirective(Socket socket) throws IOException
   {
	   BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	   PrintWriter out = new PrintWriter(socket.getOutputStream());
	   
	   String clientDirective = in.readLine();
	   System.out.println("New client directive received: " + clientDirective + ". Processing directive...");
	   
	   switch(clientDirective) {
	   		case "LOGIN":
	   			System.out.println("LOGIN directive received: processing...");
				String directiveContent = in.readLine();
				this.addClientThread(socket, directiveContent);
				break;
	   		case "GET_USERS_LIST":
				System.out.println("GET_USERS_LIST directive received: processing...");
				this.sendUsersList(socket, out);
				break;
			default:
				System.out.println("Unknown directive received: " + clientDirective);
	   }
   }
   
   private void addClientThread(Socket socket, String directiveContent) throws IOException
   {
	   System.out.println("User information received: " + directiveContent);
	   
	   System.out.println("Client connexion processing....: " + socket);
	   ChatServerThread newThread = new ChatServerThread(this, socket, directiveContent);
	   
	   //check if user doesn't already exist
	   if(!this.clients.containsKey(newThread.getUsername())) {
		   try {
			   newThread.open();
			   newThread.run();
			   this.clients.put(newThread.getUsername(),newThread);
		   } catch(IOException e) {
			   System.out.println("Error opening thread: " + e);
		   }
	   } else {
		   System.out.println("This username is already taken: ... avorting");
	   }
	   
	     
   }
   
   private void sendUsersList(Socket socket, PrintWriter out)
   {
	   Set<Entry<String, ChatServerThread>> set = this.clients.entrySet();
	   Iterator<Entry<String, ChatServerThread>> i = set.iterator();
	   String response = "";
	   
	   while(i.hasNext()) {
		   Entry<String, ChatServerThread> me = i.next();
		   response += me.getKey() + "," + me.getValue().getX() + "," + me.getValue().getY() + ";";
	   }
	   
	   System.out.println("Sending userList: " + response);
	   out.print(response);
	   out.flush();
   }

}

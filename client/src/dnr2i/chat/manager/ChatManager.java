package dnr2i.chat.manager;

import dnr2i.chat.gui.UsersPanel;
import dnr2i.chat.gui.socket.Connection;
import dnr2i.chat.user.User;
import dnr2i.util.event.ListenableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A class for managing interaction between this client and the chat server.
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class ChatManager extends ListenableModel 
{

	private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    public Thread t1;
   
    private String eventDirective;
    private UsersPanel graphicsController;
    private ArrayList<Message> message;
    private HashMap<String, User> userList;
    private User currentUser;

    
    /**
     * Build a chat manager instance, have a socket connection to the chat server and a thread to listen to server directive.
     */
    public ChatManager() 
    {
        this.userList = new HashMap<String, User>();
        this.message = new ArrayList<Message>();
        
        System.out.println("Connection in progress...");
        Connection connection = new Connection();
        this.socket = connection.getSocket();
        
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream());
            System.out.println("Connection to server established");
        } catch(IOException e) {
        	System.out.println("Connection to server failed: " + e.getMessage());
        }
            

        System.out.println("Starting thread for listening server directives...");
        this.t1 = new Thread(new ClientIncomingDirective(this, this.input));
        this.t1.start();
        
    }
    
    /**
     * Login method for connecting the user to the server.
     * Send a LOGIN directive to the server and handle welcoming message.
     * @param loginName
     * @param x
     * @param y
     */
    public void login(String loginName, int x, int y) 
    {
        this.currentUser = new User(loginName, x, y);
        this.userList.put(loginName, this.currentUser);

        //send login to server
        System.out.println("Sending LOGIN directive to the server...");
        String login = loginName + "<END/>" + x + "<END/>" + y;
        output.println("LOGIN");
        output.println(login);
        output.flush();
        System.out.println("LOGIN directive send to the server: " + login);
        
        Message message = new Message("Bienvenu " + loginName + "!", "System", this.getTime());
    	this.message.add(message);
        
        this.eventDirective = "LOGIN";
        fireChanged();
    }
    
    /**
     * Send a SET_COORDINATE directive to the server to update the user position
     */
    public void sendCoordinate() 
    {
    	System.out.println("Sending SET_COORDINATE directive to the server...");
    	String coordinate = currentUser.getUserName() + "<END/>" + currentUser.getxPosition() + "<END/>" + currentUser.getyPosition();
        output.println("SET_COORDINATE");
        output.println(coordinate);
        output.flush();
        System.out.println("New coordinate sent to the server: " + coordinate);
    }

    /**
     * Send a SET_MESSAGE directive to the server to send the user message
     * Called by an eventListener, shape the directive content, create the message instance and fireChanged locally to update the client view.
     * @param ocMessage (content of the message)
     */
    public void sendMessage(String ocMessage)
    { 
    	System.out.println("Sending SET_MSG directive to the server...");
    	Message message = new Message(ocMessage, this.currentUser.getUserName(), this.getTime());
    	this.message.add(message);
    	
    	String request = ocMessage + "<END/>" + this.getLastMessage().getTime();
        output.println("SET_MSG");
        output.println(request);
        output.flush();
        System.out.println("New message sent to the server : " + request);
        
        this.eventDirective = "NEW_OUT_MESSAGE";
        fireChanged();
    }

   
    /**
     * Handle GET_MSG directive from the server
     * Decode the content directive, create the Message instance then fire changed to update the view
     * @param incomingMessage
     */
    public void retrieveMessage(String incomingMessage) 
    {
        System.out.println("Handling the server response for directive GET_MSG: " + incomingMessage);

        String[] splitMessage = incomingMessage.split("<END/>");
        
        Message message = new Message(splitMessage[1], splitMessage[0], splitMessage[2]);
    	this.message.add(message);
        
        this.eventDirective = "NEW_IN_MESSAGE";
        fireChanged();
    }
    
    /**
     * Handle a SET_NEW_USER directive from the server
     * Decode the directive content, update the user list then fireChange to update the view
     * @param response
     */
    public void addUserToList(String response)
    {
    	String[] userSplit = response.split("<END/>");
    	if(!this.userExist(userSplit[0])) {
    		User newUser = new User(userSplit[0], Integer.parseInt(userSplit[1]), Integer.parseInt(userSplit[2]));
        	this.userList.put(userSplit[0], newUser);
        	
        	if (this.eventDirective != "ALREADY_CONNECTED") { //the currentUser is older than the new one
        		this.eventDirective = "WELCOME";
        		Message message = new Message(userSplit[0] + " s'est connecté!", "System", this.getTime());
            	this.message.add(message);
        		fireChanged();
        		this.changeUserCoordinate(response);
        	} else { //the currentUser is newer than the new one
        		fireChanged();
        		this.eventDirective = "WELCOME";
        	}            
    	}
    }
    
    /**
     * Handle a SET_OLD_USER directive from the server
     * Remove the oldUser instance, add a new system message then update the view.
     * @param oldUser
     */
    public void deleteUserFromList(String oldUser)
    {
    	if(this.userExist(oldUser)) {
    		this.userList.remove(oldUser);
    		
    		Message message = new Message(oldUser + " s'est déconnecté!", "System", this.getTime());
        	this.message.add(message);
    		
    		this.eventDirective = "BYE";
            fireChanged(); //message-userList view
            this.graphicsController.fireGraphicsChange(this.userList); //map view
    	}
    }

    /**
     * Handle a SET_USERS_LIST directive from the server
     * Send after a LOGIN directive and let us know every connected user
     * Just called after the user connection
     * @param userList
     */
    public void retrieveUsersList(String userList)
    {
        String[] userSplit = userList.split("<USER/>");
        for (int i = 0; i < userSplit.length; i++) {
        	this.eventDirective = "ALREADY_CONNECTED";
        	this.addUserToList(userSplit[i]);
        }
    }
    
    /**
     * Handle a SET_NEW_COORDINATE directive from the server
     * Update the coordinate of a given user (from directive content)
     * @param response
     */
    public void changeUserCoordinate(String response)
    {
    	//called by SET_NEW_COORDINATE directive
    	String[] responseSplit = response.split("<END/>");
    	User user = this.userList.get(responseSplit[0]);
    	user.setxPosition(Integer.parseInt(responseSplit[1]));
    	user.setyPosition(Integer.parseInt(responseSplit[2]));
    	
    	this.graphicsController.fireGraphicsChange(this.userList);
    }
    
    /**
     * Send a LOGOUT directive to the server
     * Used when the current user close the chat window. Also turnoff properly the thread and the socket.
     */
    public void logout()
    {
    	System.out.println("Sending LOGOUT directive to the server...");
    	output.println("LOGOUT");
        output.println(this.currentUser.getUserName());
        output.flush();
        
        //clean close:
        try {
        	this.output.close();
            this.input.close();
            this.t1.interrupt();
			this.socket.close();
		} catch (IOException e) {
			System.out.print("Error closing the socket: " + e.getMessage());
		}
    }
    
    /**
     * Check if a username exist in the userArray
     * @param username
     * @return boolean
     */
    public boolean userExist(String username)
    {
    	return this.userList.containsKey(username);
    }
    
    
    /**
     * Return the current time (HH:mm:ss) in a String
     * @return time
     */
    private String getTime()
    {
    	Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    /**
     * Return the last message added.
     * @return Message
     */
    public Message getLastMessage()
    {
    	return this.message.get(this.message.size() -1);
    }
    
    public User getCurrentUser() {
        return currentUser;

    }

    public HashMap<String, User> getUserList() {
        return this.userList;
    }
    
    public String getEventDirective()
    {
    	return this.eventDirective;
    }
    
    public void setGraphicsController(UsersPanel gc)
    {
    	this.graphicsController = gc;
    }

}

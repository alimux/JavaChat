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
 * Class which manages sending & retrieving message
 * @author Alexandre DUCREUX & plabadille
 * @since February, 2017
 */
public class ChatManager extends ListenableModel 
{

	private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    public Thread t1;
    
    private ArrayList<Message> message;
    private HashMap<String, User> userList;
    
    //Used by modelChanged
    private String eventDirective;
    private UsersPanel graphicsController;
  
    private User currentUser;

    /**
     * constructor
     *
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
     * Method which is called on login, send login and position to server
     *
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
        String login = loginName + "," + x + "," + y;
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
     * method wich update the coordinates of the current user
     */
    public void sendCoordinate() 
    {
    	System.out.println("Sending SET_COORDINATE directive to the server...");
    	String coordinate = currentUser.getUserName() + "," + currentUser.getxPosition() + "," + currentUser.getyPosition();
        output.println("SET_COORDINATE");
        output.println(coordinate);
        output.flush();
        System.out.println("New coordinate sent to the server: " + coordinate);
    }

    /**
     * send message method
     *
     * @param ocMessage
     */
    public void sendMessage(String ocMessage)
    { 
    	System.out.println("Sending SET_MSG directive to the server...");
    	Message message = new Message(ocMessage.split(",")[0], this.currentUser.getUserName(), this.getTime());
    	this.message.add(message);
    	
    	String request = ocMessage + "," + this.getLastMessage().getTime();
        output.println("SET_MSG");
        output.println(request);
        output.flush();
        System.out.println("New message sent to the server : " + request);
        
        this.eventDirective = "NEW_OUT_MESSAGE";
        fireChanged();
    }

    /**
     * retrieve message method
     *
     * @param out
     */
    public void retrieveMessage(String incomingMessage) 
    {
        System.out.println("Handling the server response for directive GET_MSG: " + incomingMessage);

        String[] splitMessage = incomingMessage.split("<END/>");
        
        Message message = new Message(splitMessage[1].split(",")[0], splitMessage[0], splitMessage[1].split(",")[1]);
    	this.message.add(message);
        
        this.eventDirective = "NEW_IN_MESSAGE";
        fireChanged();
    }
    
    public void addUserToList(String response)
    {
    	//called by SET_NEW_USER directive
    	String[] userSplit = response.split(",");
    	if(!this.userExist(userSplit[0])) {
    		User newUser = new User(userSplit[0], Integer.parseInt(userSplit[1]), Integer.parseInt(userSplit[2]));
        	this.userList.put(userSplit[0], newUser);
        	
        	if (this.eventDirective != "ALREADY_CONNECTED") {
        		this.eventDirective = "WELCOME";
        		Message message = new Message(userSplit[0] + " s'est connecté!", "System", this.getTime());
            	this.message.add(message);
        		fireChanged();
        		this.changeUserCoordinate(response);
        	} else {
        		fireChanged();
        		this.eventDirective = "WELCOME";
        	}            
    	}
    }
    
    public void deleteUserFromList(String oldUser)
    {
    	//called by SET_OLD_USER directive
    	if(this.userExist(oldUser)) {
    		this.userList.remove(oldUser);
    		
    		Message message = new Message(oldUser + " s'est déconnecté!", "System", this.getTime());
        	this.message.add(message);
    		
    		this.eventDirective = "BYE";
            fireChanged();
            this.graphicsController.fireGraphicsChange(this.userList);
    	}
    }
    
    public boolean userExist(String username)
    {
    	return this.userList.containsKey(username);
    }

    /**
     * Method which retrieves the list of the Tchatters
     * Just called after the user connection
     * @return ArrayList
     */
    public void retrieveUsersList(String userList)
    {
    	//called by SET_USERS_LIST directive
        String[] userSplit = userList.split(";");
        for (int i = 0; i < userSplit.length; i++) {
        	this.eventDirective = "ALREADY_CONNECTED";
        	this.addUserToList(userSplit[i]);
        }
    }
    
    public void changeUserCoordinate(String response)
    {
    	//called by SET_NEW_COORDINATE directive
    	String[] responseSplit = response.split(",");
    	User user = this.userList.get(responseSplit[0]);
    	user.setxPosition(Integer.parseInt(responseSplit[1]));
    	user.setyPosition(Integer.parseInt(responseSplit[2]));
    	
    	this.graphicsController.fireGraphicsChange(this.userList);
    }
    
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
    
    private String getTime()
    {
    	Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    /**
     * return the current user
     *
     * @return
     */
    public User getCurrentUser() {
        return currentUser;

    }

    /**
     * getter user list
     *
     * @return
     */
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
    
    public Message getLastMessage()
    {
    	return this.message.get(this.message.size() -1);
    }

}
